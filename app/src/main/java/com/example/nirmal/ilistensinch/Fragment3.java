package com.example.nirmal.ilistensinch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.sinch.android.rtc.Sinch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fragment3 extends Fragment {
    private String MeetingName[],ConCategory[],ConDesc[], Time[],Duration[],Presenter[];
    private int MeetingID[];
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DataModel1> data;
    TextView noShow2;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    public String UserName;
    AlertDialog.Builder alert;
    public Fragment3() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.listfrag3, container, false);
        noShow2 = (TextView) mRootView.findViewById(R.id.nothingtoshowfrag3);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        noShow2.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(false);
        data = new ArrayList<DataModel1>();
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.INVISIBLE);
//        noShow2.setVisibility(View.VISIBLE);
        getSharedPrefsUserName();
        getMeetingsCreatedByUserFromHostinger();
        return mRootView;
    }
    private void getSharedPrefsUserName(){
        SharedPreferences sp = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
        UserName = sp.getString(SinchHolders.phpUserName,"xyz");
    }
    private void getMeetingsCreatedByUserFromHostinger(){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_meetings_by_user.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
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
                    for(int i=0;i<jArray.length();i++){
                        JSONObject jobj = jArray.getJSONObject(i);
                        MeetingName[i] = jobj.getString("MeetingName");
                        ConCategory[i] = jobj.getString("ConCategory");
                        ConDesc[i] = jobj.getString("ConDesc");
                        Time[i] = "Time = " + jobj.getString("Time");
                        Duration[i] = jobj.getString("Duration");
                        Presenter[i] = jobj.getString("PID");
                        MeetingID[i] = jobj.getInt("MeetingID");

                    }
                    for(int i=0 ; i < MeetingName.length; i++) {
                        data.add(new DataModel1(Presenter[i], "Active", MeetingName[i], ConCategory[i], ConDesc[i], Time[i], MeetingID[i]));
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new CustomAdapter3(data,Fragment3.this);
                    recyclerView.setAdapter(adapter);
//                    noShow2.setVisibility(View.INVISIBLE);
                    if(jArray.length() == 0){
                        noShow2.setVisibility(View.VISIBLE);
                    }
                }catch (JSONException e){
                    Toast.makeText(getActivity(),"Try again after some time",Toast.LENGTH_SHORT).show();
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
                map.put(SinchHolders.phpUserName,UserName);
                return map;
            }
        };
        RequestQueue rs = Volley.newRequestQueue(getActivity());
        rs.add(stringRequest);
    }
    public String printDifference(Date startDate, Date endDate){

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
        String remaining;
        if((elapsedDays == 0)&&(elapsedHours == 0)){
            remaining = String.valueOf(elapsedMinutes)+"True";
        }else {
            remaining = elapsedDays+" Days "+elapsedHours+" Hours "+elapsedMinutes+" Minutes";
        }
        return remaining;
        /*System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);*/

    }

    public void startMeeting(final String MeetName,String MeetingTime) {
        alert = new AlertDialog.Builder(getActivity());
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh : mm");
            String[] split = MeetingTime.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < split.length; i++)
                sb.append(split[i] + " ");
            String dat1 = formatter.format(new Date());
            Date date1 = formatter.parse(dat1);
            String dat2 = sb.toString();
            Date date2 = formatter.parse(dat2);
            if (date1.compareTo(date2) < 0) {
                String timeDifference = printDifference(date1, date2);
                if (timeDifference.contains(" Hour")) {
                    alert.setTitle(timeDifference + " Remaining");
                    alert.setMessage("You cannot join \"" + MeetName + "\" Meeting right now");
                    alert.setCancelable(false);
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else if (timeDifference.contains(" True")) {
//                  Only some minutes are remaining
                    String timeArray[] = timeDifference.split(" ");
                    int x = Integer.valueOf(timeArray[1]);
                    if(x <= 5){
                        alert.setTitle(timeDifference + " Minutes Remaining");
                        alert.setMessage("You cannot join \"" + MeetName + "\" Meeting right now");
                        alert.setCancelable(false);
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String meetingName = MeetName.replace(" ", "");
                                ((MainActivity)getActivity()).startTheCall(meetingName);
                                //                        getSinchConferenceDetails();
                            }
                        });
                    }else{
                        alert.setTitle(timeDifference + " Minutes Remaining");
//                    Toast.makeText(getActivity(),"Has hour field",Toast.LENGTH_SHORT).show();
                        alert.setMessage("You cannot join \"" + MeetName + "\" Meeting right now");
                        alert.setCancelable(false);
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

//                            Toast.makeText(getActivity(),"Time has not arrived",Toast.LENGTH_SHORT).show();
                    }
                }}else
//            The meeting is over already
                if (date1.compareTo(date2) > 0) {
                    alert.setTitle("Meeting over");
                    alert.setMessage("\"" + MeetName + "\" is already over");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
//                            Toast.makeText(getActivity(),"Time has Passed",Toast.LENGTH_SHORT).show();
                }
            /*if (date1.compareTo(date2) < 0) {
                String timeDifference = printDifference(date1, date2);
                if (timeDifference.contains(" Hour")) {
                    alert.setTitle(timeDifference + " Remaining");
//                    Toast.makeText(getActivity(),"Has hour field",Toast.LENGTH_SHORT).show();
                } else if (timeDifference.contains(" True")) {
                    alert.setTitle(timeDifference + " Minutes Remaining");
//                    Toast.makeText(getActivity(),"Has only minutes",Toast.LENGTH_SHORT).show();
                }


                alert.setMessage("Would You like to join \"" + MeetName + "\" Meeting right now");
                alert.setCancelable(false);
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String meetingName = MeetName.replace(" ", "");
                        ((MainActivity)getActivity()).startTheCall(meetingName);
//                        getSinchConferenceDetails();
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
//                            Toast.makeText(getActivity(),"Time has not arrived",Toast.LENGTH_SHORT).show();
            } else if (date1.compareTo(date2) > 0) {
                alert.setTitle("Meeting over");
                alert.setMessage("\"" + MeetName + "\" is already over");
                alert.setCancelable(false);
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
//                            Toast.makeText(getActivity(),"Time has Passed",Toast.LENGTH_SHORT).show();
            }*/
        } catch (ParseException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        alert.show();
    }

}
        /*data = new ArrayList<DataModel3>();
        Toast.makeText(getActivity(),MyData.nick.length,Toast.LENGTH_SHORT).show();
        if(MyData.nick.length == 0){
        else {
            for (int i = 0; i < MyData.nick.length; i++) {
                data.add(new DataModel3(MyData.nick[i], MyData.stat[i], MyData.tit[i], MyData.cat[i], MyData.desc[i], MyData.dt[i]));
            }

        adapter = new CustomAdapter3(data);
        recyclerView.setAdapter(adapter);
        */

