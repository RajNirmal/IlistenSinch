package com.example.nirmal.ilistensinch;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.text.InputType.TYPE_NULL;

public class Fragment2 extends Fragment implements View.OnClickListener{

    EditText meetingTitle,meetingDesc,meetingDate,meetingTime;
    Spinner meetingDuration;
    TextView FinishSettingUpMeeting;
    String stringTitle,stringDesc,stringDuration,stringDate,userName,Category,stringTime;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    AlertDialog.Builder alertDialog;
    SimpleDateFormat meetingDateString;
    ProgressDialog progressDialog;
    public Fragment2() {
        // TODO Auto-generated constructor stub
    }

    public static Fragment2 newInstance() {
        return new Fragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("iListen");
        alertDialog.setMessage("Meeting Successfully Created");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.listfrag2,container,false);
        meetingTitle = (EditText)subView.findViewById(R.id.title_meeting);
        meetingDuration = (Spinner)subView.findViewById(R.id.duration_meeting);
        meetingDate = (EditText)subView.findViewById(R.id.dt_meeting);
        meetingTime = (EditText)subView.findViewById(R.id.time_meeting);
        meetingDesc = (EditText)subView.findViewById(R.id.desc_meeting);
        FinishSettingUpMeeting = (TextView) subView.findViewById(R.id.submit_meeting);
        meetingDateString = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        meetingDate.setInputType(TYPE_NULL);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Creating Meeting");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        setDateTimeField();
        meetingDuration.setOnItemSelectedListener(myListener);
        List<Integer> categories = new ArrayList<>();
        categories.add(10);
        categories.add(20);
        categories.add(30);
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(inflater.getContext(), R.layout.spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        meetingDuration.setAdapter(dataAdapter);
        FinishSettingUpMeeting.setOnClickListener(this);
        meetingDate.setOnClickListener(this);
        meetingTime.setOnClickListener(this);
        meetingDesc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null&& (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(textView.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    // Must return true here to consume event
                    return true;
            }else{
                    return false;
                }
            }});
        meetingTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(textView.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    // Must return true here to consume event
                    return true;
                }else{
                    return false;
                }
            }});

        return subView;
    }

    private void setDateTimeField(){
        Calendar newCalendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                meetingDate.setText(meetingDateString.format(newDate.getTime()));
                stringDate = meetingDateString.format(newDate.getTime());
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    AdapterView.OnItemSelectedListener myListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // On selecting a spinner item
            String item = adapterView.getItemAtPosition(i).toString();
            stringDuration = item;
            // Showing selected spinner item
//            Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            stringDuration = "10";
        }
    };

    private void setTimeField(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                stringTime = hourOfDay+ " : "+minute;
                meetingTime.setText(stringTime);
            }
        }, 0, 0, true);
        timePickerDialog.show();
    }

    private void getSharedPrefsData(){
        SharedPreferences prefs = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
        try{
            userName = prefs.getString(SinchHolders.phpUserName,"NoData");
            Category = prefs.getString(SinchHolders.phpUserexpertise,"NoData");
        }catch (NullPointerException e){
            Toast.makeText(getActivity(),"Please wait until token initialisation",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPushToAllUsers(final String ConfName , final String confTime, final String confdur){
        final String URL = "https://sfbpush.herokuapp.com/push";
        final String Body = "The Conference is being held on " + confTime + " for " + confdur + " minutes ";
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                ((MainActivity)getActivity()).switchToThirdFragment();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                ((MainActivity)getActivity()).switchToThirdFragment();
            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("body",Body);
                map.put("title",ConfName);
                return map;
            }
        };
        RequestQueue r = Volley.newRequestQueue(getActivity());
        r.add(sr);
    }

    @Override
    public void onClick(View view) {
        if (view == FinishSettingUpMeeting){
            stringTitle = meetingTitle.getText().toString().trim();
            stringDesc = meetingDesc.getText().toString().trim();
            String finalDateandTime = stringDate+" "+stringTime;
            if((!(stringTitle.isEmpty())&&(!(stringDesc.isEmpty()))&&(!(stringDuration.isEmpty())))){
                progressDialog.show();
                getSharedPrefsData();
                sendTheDataToHostinger(stringTitle, finalDateandTime, stringDuration, stringDesc);
                sendPushToAllUsers(stringTitle, stringDate, stringDuration);
                setTheAlarm();

            }else{
                Toast.makeText(getActivity().getApplicationContext(),"Please enter all the details",Toast.LENGTH_LONG).show();
            }
        }else if (view == meetingDate){
            datePicker.show();
        }else if(view == meetingTime){
            setTimeField();
        }
    }

    public void setTheAlarm(){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH : mm");
            String dat1 = formatter.format(new Date());
            Date date1 = formatter.parse(dat1);
            String dat2 = stringDate+" "+stringTime;;
            Date date2 = formatter.parse(dat2);
            Random r = new Random();
            int Low = 1000;
            int High = 5000;
            int Result = r.nextInt(High-Low) + Low;
            if(date1.compareTo(date2)<0){
                long timeRemaining = getDifferenceInMilliSeconds(date1,date2);
                ((MainActivity)getActivity()).setTheAlarm(Result,timeRemaining,stringTitle);
//                            Toast.makeText(getActivity(),"Time has not arrived",Toast.LENGTH_SHORT).show();
            }
        }catch (ParseException e){
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
        }
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

    private void sendTheDataToHostinger(final String confName, final String confTime, final String confDuration, final String confDescription){
        final String HostingerURL = "http://www.mazelon.com/iListen/ilisten_Save_Meeting.php";
        //TestingMeeting.setText(putotText);
        StringRequest stringreqs = new StringRequest(Request.Method.POST, HostingerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                if(response.toString().trim().equals("Success")){
//                    Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
                    meetingDate.setText("");
                    meetingTime.setText("");
                    meetingTitle.setText("");
//                    meetingDuration.setText("");
                    meetingDesc.setText("");
                    alertDialog.show();
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
                map.put(SinchHolders.phpMeetingName,confName);
                map.put(SinchHolders.phpMeetingTime,confTime);
                map.put(SinchHolders.phpMeetingDuration,confDuration);
                map.put(SinchHolders.phpMeetingCategory,Category);
                map.put(SinchHolders.phpMeetingCreator,userName);
                map.put(SinchHolders.phpMeetingDesc,confDescription);
                map.put(SinchHolders.phpMeetingParticipants,"10");
                return map;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(stringreqs);
    }

}

