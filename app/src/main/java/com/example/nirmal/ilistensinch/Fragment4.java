package com.example.nirmal.ilistensinch;

import android.content.DialogInterface;
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
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nirmal.ilistensinch.DBPackage.DBHandler;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Fragment4 extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<MeetingList> data;
    TextView noShow4;
    public final static String TAG = Fragment1.class.getSimpleName();
    private View mRootView;
    private DBHandler db;
    AlertDialog.Builder alert;
    String MeetingDate;
    public Fragment4() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment1 newInstance() {
        return new Fragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void startMeeting(final String MeetName,String MeetingTime) {
//        ((MainActivity)getActivity()).startTheCall(MeetName);
        alert = new AlertDialog.Builder(getActivity());
//        Toast.makeText(getActivity(),MeetingTime,Toast.LENGTH_SHORT).show();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh : mm");
            String[] split = MeetingTime.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < split.length; i++) {
                sb.append(split[i] + " ");
//                Toast.makeText(getActivity(),split[i],Toast.LENGTH_SHORT).show();
            }
                Date date1,date2;
                String dat1 = formatter.format(new Date());
                date1 = formatter.parse(dat1);
                MeetingDate = sb.toString();
                date2 = formatter.parse(MeetingDate);
//                Toast.makeText(getActivity(),"The Meeting date is " + MeetingDate +" The meeting time in DATE is "+String.valueOf(date2) + "The current date is "+ date1,Toast.LENGTH_SHORT).show();
// The meeting is not yet Over
            if (date1.compareTo(date2) < 0) {
                String timeDifference = printDifference(date1, date2);
                if (timeDifference.contains(" Hour")) {
                    alert.setTitle(timeDifference + " Remaining");
//                    Toast.makeText(getActivity(),"Has hour field",Toast.LENGTH_SHORT).show();
                    alert.setMessage("You cannot join \"" + MeetName + "\" Meeting right now");
                    alert.setCancelable(true);
                    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /*String meetingName = MeetName.replace(" ", "");
                            ((MainActivity)getActivity()).startTheCall(meetingName);*/
                            dialogInterface.dismiss();
                        }
                    });
                } else if (timeDifference.contains(" True")) {
//                  Only some minutes are remaining
                    String timeArray[] = timeDifference.split(" ");
                    int x = Integer.valueOf(timeArray[1]);
                    if(x <= 5){
                        alert.setTitle(timeDifference + " Minutes Remaining");
//                    Toast.makeText(getActivity(),"Has hour field",Toast.LENGTH_SHORT).show();
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
        } catch (ParseException e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        alert.show();
    }
/*
        String[] split = MeetingTime.split("\\s+");
        int count = split.length;
        int i = count-3;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c.getTime());
        String[] currentTime = formattedDate.split(":");
        for(String x : currentTime)
            Toast.makeText(getActivity(),x,Toast.LENGTH_SHORT).show();
        if((Integer.parseInt(currentTime[0]) < Integer.parseInt(split[i]))||(Integer.parseInt(currentTime[0]) > Integer.parseInt(split[i++]))){
            //Meeting Hour has not reached yet
            alert.setTitle(MeetName);
            alert.setMessage("The time has not yet come. Are you sure you want to attend");
            alert.show();
        }else if((Integer.parseInt(currentTime[1]) < Integer.parseInt(split[i]))||(Integer.parseInt(currentTime[1]) > Integer.parseInt(split[i++]))){
            //Meeting Minutes has not been reached yet
            alert.setTitle(MeetName);
            alert.setMessage("The time has not yet come. Are you sure you want to attend");
            alert.show();
        }
*/

    private void getSinchConferenceDetails(){
        final String URL = "https://sfbpush.herokuapp.com/getRequest";
//        final String Body = "The Conference is being held on " + confTime + " for " + confdur + " minutes ";
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                  Toast.makeText(getActivity(),response.toString()+" returned from node.js server",Toast.LENGTH_SHORT).show();
                //  ((SinchMainActivity)getActivity()).goBackToMain();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                 Toast.makeText(getActivity(),error.toString()+"The Server returned error",Toast.LENGTH_SHORT).show();
                // ((MainActivity)getActivity()).goBackToMain();
            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
//                map.put("body",Body);
                map.put("title","HelloWorld");
                return map;
            }
        };
        RequestQueue r = Volley.newRequestQueue(getActivity());
        r.add(sr);
    }
    public String printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();
        /*System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);
        DateTimeFormatter format = DateTimeFormat.forPattern("MMM");
        DateTime instance        = format.withLocale(Locale.ENGLISH).parseDateTime(dates[1]);
        int month_number         = instance.getMonthOfYear();
        instance = format.withLocale(Locale.ENGLISH).parseDateTime(dates2[1]);
        int month_number_2 = instance.getMonthOfYear();

*/
        String StringDate = String.valueOf(startDate);
        String dates [] = StringDate.split(" ");
        String StringDate2 = StringDate.valueOf(endDate);
        String dates2 [] = StringDate2.split(" ");
        Date date,date2;
//        long Date = startDate.getMonth();
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.listfrag4, container, false);
        noShow4 = (TextView) mRootView.findViewById(R.id.nothingtoshowfrag4);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        db = new DBHandler(getActivity());
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setTheTimings();
        getAllMeetings();
        return mRootView;
    }
    public void getAllMeetings(){
        data = db.getAllMeetings();
        if(data.isEmpty()){
            noShow4.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else {
            adapter = new CustomAdapter4(data,Fragment4.this);
            recyclerView.setAdapter(adapter);
        }
//        Toast.makeText(getActivity(),meetingsList.toString(),Toast.LENGTH_SHORT).show();
    }
    private void setTheTimings(){
        data = db.getAllMeetings();
        for(int i = 0; i<data.size() ; i++) {
            MeetingList SingleMeeting =  data.get(i);
            String time = SingleMeeting.getTime();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh : mm");
            String[] split = time.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for(int j=2;j<split.length;j++)
                sb.append(split[j]+" ");
            try {
                String dat1 = formatter.format(new Date());
                Date date1 = formatter.parse(dat1);
                String dat2 = sb.toString();
                Date date2 = formatter.parse(dat2);
                if(date1.compareTo(date2)>0){
                    MeetingList myList = new MeetingList(SingleMeeting.getId(),SingleMeeting.getMeetingName(),SingleMeeting.getConferenceDesc(),SingleMeeting.getTime(),SingleMeeting.getDuration(),SingleMeeting.getTime(),SingleMeeting.getPresenter(),2);
                    db.updateMeetingStatus(myList);
                }
            }catch (ParseException e){
                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
            }

        }
        data.clear();
    }
}
/*
        data = new ArrayList<DataModel4>();
        if(MyData.nick.length == 0){
        mRootView.setVisibility(View.INVISIBLE);
        noShow4.setVisibility(View.VISIBLE);
        }else {
        for (int i = 0; i < MyData.nick.length; i++) {
        data.add(new DataModel4(MyData.nick[i], MyData.stat[i], MyData.tit[i], MyData.cat[i], MyData.desc[i], MyData.dt[i], MyData.part[i]));
        }
        }

        adapter = new CustomAdapter4(data);
        recyclerView.setAdapter(adapter);
        */

