package com.example.nirmal.ilistensinch;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        // Setting Dialog Title
        alertDialog.setTitle("iListen");

        // Setting Dialog Message
        alertDialog.setMessage("Meeting Successfully Created");

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
//                Toast.makeText(getContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
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
        /*timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
                stringTime = Hour+ " : "+Minute;
            }
        },newCalendar.get(Calendar.HOUR_OF_DAY),newCalendar.get(Calendar.MINUTE));*/
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
              //  Toast.makeText(getActivity(),response.toString()+" returned from node.js server",Toast.LENGTH_SHORT).show();
                //  ((SinchMainActivity)getActivity()).goBackToMain();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getActivity(),error.toString()+"The Server returned error",Toast.LENGTH_SHORT).show();
                // ((MainActivity)getActivity()).goBackToMain();
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
//            stringDuration = meetingDuration.getText().toString().trim();
            String finalDateandTime = stringDate+" "+stringTime;
//            Toast.makeText(getActivity(),finalDateandTime,Toast.LENGTH_SHORT).show();
            if((!(stringTitle.isEmpty())&&(!(stringDesc.isEmpty()))&&(!(stringDuration.isEmpty())))){
                getSharedPrefsData();
                sendTheDataToHostinger(stringTitle, finalDateandTime, stringDuration, stringDesc);
                sendPushToAllUsers(stringTitle, stringDate, stringDuration);
                ((MainActivity)getActivity()).switchToThirdFragment();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"Please enter all the details",Toast.LENGTH_LONG).show();
            }
        }else if (view == meetingDate){
            datePicker.show();
        }else if(view == meetingTime){
            setTimeField();
        }
    }

    private void sendTheDataToHostinger(final String confName, final String confTime, final String confDuration, final String confDescription){
        final String HostingerURL = "http://www.mazelon.com/iListen/ilisten_Save_Meeting.php";
        String putotText = SinchHolders.phpMeetingName+"="+confName +SinchHolders.phpMeetingTime+"="+confTime+SinchHolders.phpMeetingDuration+"="+confDuration+SinchHolders.phpMeetingCategory+"="+Category+
                SinchHolders.phpMeetingCreator+"="+userName+SinchHolders.phpMeetingDesc+"="+confDescription;
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

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.listfrag2, container, false);


        recyclerView = (RecyclerView) mRootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel2>();
        for (int i = 0; i <1; i++) {
            data.add(new DataModel2( MyData.tit[i], MyData.cat[i], MyData.desc[i], MyData.dt[i]));
        }


        adapter = new CustomAdapter2(data);
        recyclerView.setAdapter(adapter);
        return mRootView;
    }
*/
}

