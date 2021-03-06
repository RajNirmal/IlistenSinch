package com.example.nirmal.ilistensinch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nirmal.ilistensinch.DBPackage.DBHandler;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Fragment1 extends Fragment {
    private String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[],TimeinString[];
    private int MeetingID[],Status[];
    public String UserNameInSharedPrefs;
    private TextView nothingToShow;
    private ImageView con;
    private EditText searchBar;
    private static int flag = 0;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    private String uName;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    private DBHandler db;
    AlertDialog.Builder alertDialog;
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    RequestQueue requestQueue;
    public Fragment1() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getAllMeetingsFromHostingerDB();
        mRootView = inflater.inflate(R.layout.listfrag1, container, false);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        nothingToShow = (TextView)mRootView.findViewById(R.id.nothingtoshow);
        con=(ImageView)mRootView.findViewById(R.id.con);
        searchBar = (EditText)mRootView.findViewById(R.id.searchmeetings);
        recyclerView.setHasFixedSize(false);
        db = new DBHandler(getActivity());
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<DataModel1>();
        alertDialog = new AlertDialog.Builder(getActivity());
        getTheUserNameFromSharedPrefs();
        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
//                Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
            }
        });

        try{
            SharedPreferences sp = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
            uName = sp.getString(SinchHolders.phpUserName,"-1");
        }catch (NullPointerException e){
            Toast.makeText(getActivity().getApplicationContext(),"User data not found",Toast.LENGTH_SHORT).show();
            uName = "UserName";
        }
        flag =1;

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        return mRootView;
    }
    public void filter(String text){
        ArrayList<DataModel1> temp = new ArrayList();
        for(DataModel1 d: data){
            //or use .contains(text)
            if(d.getTit().contains(text)){
                temp.add(d);
            }
        }

        //update recyclerview
        adapter = new CustomAdapter1(temp, Fragment1.this);
        recyclerView.setAdapter(adapter);
//        adapter.updateList(temp);
    }

    /*private void checkTheAcceptStatus(int x){
        int status = db.getMeetingStatus(x);
        Toast.makeText(getActivity(),String.valueOf(status),Toast.LENGTH_SHORT).show();
        if(status == 1){

        }
    }
    */
    public void getTheUserNameFromSharedPrefs(){
        SharedPreferences prefs = getActivity().getSharedPreferences(SinchHolders.SharedPrefName,Context.MODE_PRIVATE);
        UserNameInSharedPrefs = prefs.getString(SinchHolders.phpUserName,"xyz");
    }
    private void getDBdata(int id){
        MeetingList myList = db.getMeeting(id);
//        Toast.makeText(getActivity(),"Meeting Status = "+myList.getStatus()+" Meeting Name = "+myList.getMeetingName(),Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
///        getHostingerData();
    }
    public void showToast(String x){
        Integer MeetId = Integer.parseInt(x);
        getTheMeetingDataFromHostinger(MeetId);

    }

    private long writeinLocalDB(int i){
        long out = db.addMeeting(new MeetingList(MeetingID[i],MeetingName[i],ConDesc[i],Time[i],Duration[i],Time[i],Presenter[i],Status[i]));
//        getDBdata(i);
        return out;
   }

    public long getDifferenceInMilliSeconds(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        long theSecondsRemaining = ((elapsedSeconds * 1000) + (elapsedMinutes * 60000) + (elapsedHours * 60000 * 60) + (elapsedDays * 60000 * 60 * 24));
        return theSecondsRemaining;
    }

    private void getTheMeetingDataFromHostinger(final int Id){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_meetings_by_id.php";
        StringRequest stringRequestr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                final MeetingList myList ;
                String tMeetingName,tConCategory,tConDesc,tTime,tDuration,tPresenter;
                int tMeetingID;
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.getJSONArray("result");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    tMeetingName = jsonObject.getString("MeetingName");
                    tConCategory = jsonObject.getString("ConCategory");
                    tConDesc = jsonObject.getString("ConDesc");
                    tDuration = jsonObject.getString("Duration");
                    tPresenter = jsonObject.getString("PID");
                    tMeetingID = jsonObject.getInt("MeetingID");
                    Date date;
                    try {
                        String DateinGMT = jsonObject.getString("Time");
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH : mm");
                        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                        Date GMTDate = formatter.parse(DateinGMT);
                        formatter.setTimeZone(TimeZone.getDefault());
                        String LocalDateString = formatter.format(GMTDate);
                        Date LocalDate = formatter.parse(LocalDateString);
                        formatter.setTimeZone(TimeZone.getDefault());
                        Date date2 = LocalDate;
                        date = LocalDate;
                        String dat2 = formatter.format(new Date());
                        Date date1 = formatter.parse(dat2);
                        if(date1.compareTo(date2)<0){
                            alertDialog.setTitle("Meeting Scheduled");
                            alertDialog.setMessage("You can attend \""+tMeetingName+"\" Meeting");
                            long timeRemaining = getDifferenceInMilliSeconds(date1,date2);
                            ((MainActivity)getActivity()).setTheAlarm(Id,timeRemaining,tMeetingName);
                        }else if(date1.compareTo(date2)>0){
                            alertDialog.setTitle("Meeting over");
                            alertDialog.setMessage("\""+tMeetingName+"\" is already over");
                        }
                    }catch (ParseException e){
                        date = null;
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    int years = ((date.getYear())%100)+2000;
                    String shert = date.getDate()+"-"+(date.getMonth()+1)+"-"+years+" "+date.getHours()+" : "+date.getMinutes();
                    tTime = "Time : "+ shert;
                    myList = new MeetingList(tMeetingID,tMeetingName,tConDesc,tTime,tDuration,tTime,tPresenter,1);
                    getDBdata(tMeetingID);
                    long out = db.updateMeetingStatus(myList);
                    getDBdata(tMeetingID);
                    alertDialog.show();
                }catch (JSONException e){
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put(SinchHolders.phpMeetingId,String.valueOf(Id));
                return map;
            }
        };
        requestQueue.add(stringRequestr);
    }

    private void getAllMeetingsFromHostingerDB(){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_all_meetings.php";
//        final String URL = "http://www.mazelon.com/iListen/ilisten_get_all_meetings_by_category.php";
        StringRequest sr =new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jArray = obj.getJSONArray("result");
                    MeetingName = new String[jArray.length()];
                    ConCategory = new String[jArray.length()];
                    ConDesc = new String[jArray.length()];
                    Time = new String[jArray.length()];
                    TimeinString = new String[jArray.length()];
                    Duration = new String[jArray.length()];
                    Presenter = new String[jArray.length()];
                    MeetingID = new int[jArray.length()];
                    Status = new int[jArray.length()];
                   if(jArray.length()==0){
//                       Toast.makeText(getActivity().getApplicationContext(),"Length is 0",Toast.LENGTH_SHORT).show();
                       con.setVisibility(View.VISIBLE);
                        nothingToShow.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }else if(jArray.length() == SinchHolders.lastDataCount){
                        //Nothing has changed since the last time so no need to update the DB just keep the recycler view persistent
                       recyclerView.setVisibility(View.VISIBLE);
                       for (int i =0; i < jArray.length(); i++) {
                           JSONObject jobj = jArray.getJSONObject(i);
                           MeetingName[i] = jobj.getString("MeetingName");
                           ConCategory[i] = jobj.getString("ConCategory");
                           ConDesc[i] = jobj.getString("ConDesc");
                           Date date;
                           String shert,shert1;
                           int years;
                           try {
                               String DateinGMT = jobj.getString("Time");
                               SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH : mm");
                               formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                               Date GMTDate = formatter.parse(DateinGMT);
                               formatter.setTimeZone(TimeZone.getDefault());
                               String LocalDateString = formatter.format(GMTDate);
                               Date LocalDate = formatter.parse(LocalDateString);
                               formatter.setTimeZone(TimeZone.getDefault());
                               date = LocalDate;
                               years = ((date.getYear())%100)+2000;
                               String monthInString = returnmonthString(date.getMonth()+1);
                               shert = date.getDate()+"-"+(date.getMonth()+1)+"-"+years+" "+date.getHours()+" : "+date.getMinutes();
                               shert1 = date.getDate()+"-"+monthInString+"-"+years+" "+date.getHours()+" : "+date.getMinutes();
                           }catch (ParseException e){
                               shert = "Some error occured try again later";
                               shert1 = "Some error occured try again later";
                               Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                           }
                           Time[i] = "Time : " + shert;
                           TimeinString[i] = "Time : " + shert1;
                           Duration[i] = jobj.getString("Duration");
                           Presenter[i] = jobj.getString("PID");
                           MeetingID[i] = jobj.getInt("MeetingID");
//                           writeinLocalDB(i);
                           Status[i] = db.getMeetingStatus(MeetingID[i]);

                       }
                       for (int i = 0;i<(MeetingName.length-1);i++) {
                           if(!(UserNameInSharedPrefs.equals(Presenter[i])))
                               if(!Time[i].isEmpty())
                                if(Status[i] != 1) {
                                       boolean flag = isTheMeetingOver(Time[i]);
                                       if (flag)
                                           data.add(new DataModel1(Presenter[i], String.valueOf(Status[i]), MeetingName[i], ConCategory[i], ConDesc[i], TimeinString[i], MeetingID[i]));
                                }
                       }
                   }else {
//                      New data has been found in the server so call the local Db and update it accordingly
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jobj = jArray.getJSONObject(i);
                            MeetingName[i] = jobj.getString("MeetingName");
                            ConCategory[i] = jobj.getString("ConCategory");
                            ConDesc[i] = jobj.getString("ConDesc");
                            Date date;
                            int years;
                            String shert,shert1;
                            try {
                                String DateinGMT = jobj.getString("Time");
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH : mm");
                                formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
                                Date GMTDate = formatter.parse(DateinGMT);
                                formatter.setTimeZone(TimeZone.getDefault());
                                String LocalDateString = formatter.format(GMTDate);
                                Date LocalDate = formatter.parse(LocalDateString);
                                formatter.setTimeZone(TimeZone.getDefault());
                                date = LocalDate;
                                years = ((date.getYear())%100)+2000;
                                String monthInString = returnmonthString(date.getMonth()+1);
                                shert = date.getDate()+"-"+(date.getMonth()+1)+"-"+years+" "+date.getHours()+" : "+date.getMinutes();
                                shert1 = date.getDate()+"-"+monthInString+"-"+years+" "+date.getHours()+" : "+date.getMinutes();
                            }catch (ParseException e){
                                date = null;
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                                shert = "Some error occured try again later";
                                shert1 = "Some error occured try again later";
                            }
                            Time[i] = "Time : " + shert;
                            TimeinString[i] = "Time : " + shert1;
                            Duration[i] = jobj.getString("Duration");
                            Presenter[i] = jobj.getString("PID");
                            MeetingID[i] = jobj.getInt("MeetingID");
                            writeinLocalDB(i);
                            Status[i] = db.getMeetingStatus(MeetingID[i]);
//                            Toast.makeText(getActivity(),String.valueOf(SuccessorNot),Toast.LENGTH_SHORT).show();
                        }

                       for (int i = 0;i<(MeetingName.length-1);i++){
                            if(!(UserNameInSharedPrefs.equals(Presenter[i])))
                                if(Status[i] != 1) {
                                    boolean flag = isTheMeetingOver(Time[i]);
                                    if (flag)
                                        data.add(new DataModel1(Presenter[i], String.valueOf(Status[i]), MeetingName[i], ConCategory[i], ConDesc[i], TimeinString[i], MeetingID[i]));
                                }
                        }


                    }
                    if(data.isEmpty()){
                        recyclerView.setVisibility(View.INVISIBLE);
                        nothingToShow.setVisibility(View.VISIBLE);
                        con.setVisibility(View.VISIBLE);
                    }else {
                        adapter = new CustomAdapter1(data, Fragment1.this);
                        recyclerView.setAdapter(adapter);
                        SinchHolders.lastDataCount = jArray.length();
                    }
              //      Toast.makeText(getActivity().getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                }catch (JSONException e){
                    Toast.makeText(getActivity().getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                SharedPreferences prefs = getActivity().getSharedPreferences(SinchHolders.SharedPrefName,Context.MODE_PRIVATE);
                String userExpertField;
                try{
                    userExpertField = prefs.getString(SinchHolders.phpUserexpertise,"xyz");
                }catch (NullPointerException e){
                    userExpertField = "xyz";
                }
                map.put(SinchHolders.phpUserexpertise,userExpertField);
                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(8000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(sr);
    }

    public boolean isTheMeetingOver(String MeetingTime){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH : mm");
        String[] split = MeetingTime.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < split.length; i++) {
            sb.append(split[i] + " ");
//                Toast.makeText(getActivity(),split[i],Toast.LENGTH_SHORT).show();
        }
        try {
            Date date1, date2;
            String dat1 = formatter.format(new Date());
            date1 = formatter.parse(dat1);
            String MeetingDate = sb.toString();
            date2 = formatter.parse(MeetingDate);
//            Toast.makeText(getActivity(),"The current date is "+date1+" The Meeting date is "+date2,Toast.LENGTH_LONG).show();
            if(date2.before(date1)){
//                Toast.makeText(getActivity(), "The date now is "+date2+" False", Toast.LENGTH_SHORT).show();
                return false;
            }else {
//                Toast.makeText(getActivity(), "The date now is "+date2+" True", Toast.LENGTH_SHORT).show();
                return true;
            }

        }catch (ParseException e){
            return true;
        }
    }

    public String returnmonthString(int monthNumber){
        String monthName;
        switch(monthNumber){
            case 1:
                monthName = "Jan";
                break;
            case 2:
                monthName = "Feb";
                break;
            case 3:
                monthName = "Mar";
                break;
            case 4:
                monthName = "Apr";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "Jun";
                break;
            case 7:
                monthName = "Jul";
                break;
            case 8:
                monthName = "Aug";
                break;
            case 9:
                monthName = "Sep";
                break;
            case 10:
                monthName = "Oct";
                break;
            case 11:
                monthName = "Nov";
                break;
            case 12:
                monthName = "Dec";
                break;
            default:
                monthName = "Invalid month";
                break;
        }
//        Toast.makeText(getActivity(),monthName,Toast.LENGTH_LONG).show();
        return monthName;
    }

}

