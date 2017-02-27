package com.example.nirmal.ilistensinch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nirmal.ilistensinch.DBPackage.DBHandler;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;
import com.rebtel.repackaged.com.google.gson.JsonObject;
import com.sinch.android.rtc.Sinch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fragment1 extends Fragment {
    private String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[];
    private int MeetingID[],Status[];
    public String UserNameInSharedPrefs;
    private TextView nothingToShow;
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
        return mRootView;
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
//        getHostingerData();
    }
    public void showToast(String x){
        Integer MeetId = Integer.parseInt(x);
        getTheMeetingDataFromHostinger(MeetId);

    }
    private void setTheAlarmForTheUser(){
//        Intent i = new Intent((MainActivity)getActivity().this)
    }
    private long writeinLocalDB(int i){
        long out = db.addMeeting(new MeetingList(MeetingID[i],MeetingName[i],ConDesc[i],Time[i],Duration[i],Time[i],Presenter[i],Status[i]));

//        Toast.makeText(getActivity(),String.valueOf(out),Toast.LENGTH_SHORT).show();
//        getDBdata(i);
        return out;
   }
    public long getDifferenceInMilliSeconds(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
//        Toast.makeText(getActivity(),"startDate : " + startDate+"endDate : "+ endDate+"different : " + different,Toast.LENGTH_SHORT).show();
        /*System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);
*/
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
     /*   String remaining;
        if((elapsedDays == 0)&&(elapsedHours == 0)){
            remaining = String.valueOf(elapsedMinutes)+"True";
        }else {
            remaining = elapsedDays+" Days "+elapsedHours+" Hours "+elapsedMinutes+" Minutes";
        }
        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);
*/
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
                    tTime = "Time : " + jsonObject.getString("Time");
                    tDuration = jsonObject.getString("Duration");
                    tPresenter = jsonObject.getString("PID");
                    tMeetingID = jsonObject.getInt("MeetingID");
                    myList = new MeetingList(tMeetingID,tMeetingName,tConDesc,tTime,tDuration,tTime,tPresenter,1);
                    getDBdata(tMeetingID);
                    long out = db.updateMeetingStatus(myList);
//                    String[] split = jsonObject.getString("Time").split("\\s+");
                    try {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy hh : mm");
                        String dat1 = formatter.format(calendar.getTime());
                        Date date1 = formatter.parse(dat1);
                        String dat2 = jsonObject.getString("Time");
                        Date date2 = formatter.parse(dat2);
                        if(date1.compareTo(date2)<0){
                            alertDialog.setTitle("Meeting Scheduled");
                            alertDialog.setMessage("You can attend \""+tMeetingName+"\" Meeting");
                            long timeRemaining = getDifferenceInMilliSeconds(date1,date2);
                            ((MainActivity)getActivity()).setTheAlarm(Id,timeRemaining,tMeetingName);
//                            Toast.makeText(getActivity(),"Time has not arrived",Toast.LENGTH_SHORT).show();
                        }else if(date1.compareTo(date2)>0){
                            alertDialog.setTitle("Meeting over");
                            alertDialog.setMessage("\""+tMeetingName+"\" is already over");
//                            Toast.makeText(getActivity(),"Time has Passed",Toast.LENGTH_SHORT).show();
                        }
                    }catch (ParseException e){
                        Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
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
        StringRequest sr =new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray jArray = obj.getJSONArray("result");
                    MeetingName = new String[jArray.length()];
                    ConCategory = new String[jArray.length()];
                    ConDesc = new String[jArray.length()];
                    Time = new String[jArray.length()];
                    Duration = new String[jArray.length()];
                    Presenter = new String[jArray.length()];
                    MeetingID = new int[jArray.length()];
                    Status = new int[jArray.length()];
                   if(jArray.length()==0){
//                       Toast.makeText(getActivity().getApplicationContext(),"Length is 0",Toast.LENGTH_SHORT).show();
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
                           Time[i] = "Time : " + jobj.getString("Time");
                           Duration[i] = jobj.getString("Duration");
                           Presenter[i] = jobj.getString("PID");
                           MeetingID[i] = jobj.getInt("MeetingID");
                           Status[i] = db.getMeetingStatus(MeetingID[i]);
                       }
                       for (int i = 0; i < MeetingName.length; i++) {
                           if(!(UserNameInSharedPrefs.equals(Presenter[i])))
                            data.add(new DataModel1(Presenter[i], String.valueOf(Status[i]), MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                       }
                   }else {
//                      New data has been found in the server so call the local Db and update it accordingly
                        recyclerView.setVisibility(View.VISIBLE);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jobj = jArray.getJSONObject(i);
                            MeetingName[i] = jobj.getString("MeetingName");
                            ConCategory[i] = jobj.getString("ConCategory");
                            ConDesc[i] = jobj.getString("ConDesc");
                            Time[i] = "Time : " + jobj.getString("Time");
                            Duration[i] = jobj.getString("Duration");
                            Presenter[i] = jobj.getString("PID");
                            MeetingID[i] = jobj.getInt("MeetingID");
                            writeinLocalDB(i);
                            Status[i] = db.getMeetingStatus(MeetingID[i]);
//                            Toast.makeText(getActivity(),String.valueOf(SuccessorNot),Toast.LENGTH_SHORT).show();
                        }
                        for (int i = 0; i < MeetingName.length; i++) {
                            if(!(UserNameInSharedPrefs.equals(Presenter[i])))
                                data.add(new DataModel1(Presenter[i], String.valueOf(Status[i]), MeetingName[i], ConCategory[i], ConDesc[i], Time[i],MeetingID[i]));
                        }

                    }
                    adapter = new CustomAdapter1(data,Fragment1.this);
                    recyclerView.setAdapter(adapter);
                    SinchHolders.lastDataCount = jArray.length();
              //      Toast.makeText(getActivity().getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                }catch (JSONException e){
                    Toast.makeText(getActivity().getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(sr);
    }

}

