package com.example.nirmal.ilistensinch;

import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchPlaceCallFragment extends Fragment {
    String Category,Desc,userFCM;
    final String URL = "https://sfbpush.herokuapp.com/push";
    final String HostingerURL = "http://www.mazelon.com/iListen/ilisten_Save_Meeting.php";
    EditText ConferenceName,ConferenceDuration;
    TimePicker ConferenceStartTime;
    static TextView statusOfCall;
    TextView someRandomData,TestingMeeting;
    private AudioPlayer mAudioPlayer;
    Button setUpConference,joinConference;
    String callingUsersName;
    FirebaseDatabase FireDB;
    DatabaseReference FBReference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.sinch_place_call_fragment, container, false);
        setUpConference = (Button) subView.findViewById(R.id.sinch_setup_meeting);
        statusOfCall = (TextView) subView.findViewById(R.id.call_status);
        ConferenceName = (EditText) subView.findViewById(R.id.call_name_sinch);
        joinConference = (Button) subView.findViewById(R.id.sinch_start_meeting);
        ConferenceStartTime = (TimePicker) subView.findViewById(R.id.call_time);
        ConferenceDuration = (EditText) subView.findViewById(R.id.call_duration);
        someRandomData = (TextView)subView.findViewById(R.id.call_participants);
        TestingMeeting = (TextView)subView.findViewById(R.id.testingmeeting);
        mAudioPlayer = new AudioPlayer(getActivity());
        FireDB = FirebaseDatabase.getInstance();
        FBReference = FireDB.getReference("MyApp");
        SinchHolders.FirebaseToken = FirebaseInstanceId.getInstance().getToken();
        FBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("UserData").child("name").getValue(String.class);
                FirebaseMessaging.getInstance().subscribeToTopic("ilisten");
                /*Toast.makeText(getActivity(),value,Toast.LENGTH_SHORT).show();
                callerName.setText(SinchHolders.FirebaseToken);*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        joinConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callingUsersName = ConferenceName.getText().toString().trim();
                if (!callingUsersName.isEmpty()) {
                    ((SinchMainActivity) getActivity()).setTheUsertoCall(callingUsersName);
                    //callTheUser();
                }else
                    Toast.makeText(getActivity(), "Please Enter a User Name", Toast.LENGTH_SHORT).show();
            }
        });
        setUpConference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callingUsersName = ConferenceName.getText().toString().trim();
                Integer hour = ConferenceStartTime.getCurrentHour();
                Integer mins = ConferenceStartTime.getCurrentMinute();
                String confTime = hour.toString() +" : "+ mins.toString()+" : "+"00";
                String confDuration = ConferenceDuration.getText().toString().trim();
                if((!callingUsersName.isEmpty())&&(!confTime.isEmpty())&&(!confDuration.isEmpty())){
                    getSharedPrefsData();
                    sendTheDataToHostinger(callingUsersName,confTime,confDuration);
                    sendPushToAllUsers(callingUsersName,confTime,confDuration);
                }else{
                    Toast.makeText(getActivity(),"Enter Valid Details",Toast.LENGTH_SHORT).show();
                }

            }
        });
        return subView;
    }
    private void getSharedPrefsData(){
        SharedPreferences prefs = getActivity().getSharedPreferences(SinchHolders.SharedPrefName, Context.MODE_PRIVATE);
        try{
            userFCM = prefs.getString(SinchHolders.phpUserFirebaseToken,"NoData");
            Category = prefs.getString(SinchHolders.phpUserexpertise,"NoData");
            Desc = "Lets add this field later on";
        }catch (NullPointerException e){
            Toast.makeText(getActivity(),"Please wait until token initialisation",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendPushToAllUsers(final String ConfName , final String confTime, final String confdur){
        final String Body = "The Conference is being held on " + confTime + " for " + confdur + " minutes ";
        StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Toast.makeText(getActivity(),response.toString()+" returned from node.js server",Toast.LENGTH_SHORT).show();
            ((SinchMainActivity)getActivity()).goBackToMain();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(),error.toString()+"The Server returned error",Toast.LENGTH_SHORT).show();
            ((SinchMainActivity)getActivity()).goBackToMain();

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

    private void sendTheDataToHostinger(final String confName, final String confTime, final String confDuration){
        String putotText = SinchHolders.phpMeetingName+"="+confName +SinchHolders.phpMeetingTime+"="+confTime+SinchHolders.phpMeetingDuration+"="+confDuration+SinchHolders.phpMeetingCategory+"="+Category+
                SinchHolders.phpMeetingCreator+"="+userFCM+SinchHolders.phpMeetingDesc+"="+Desc;
        TestingMeeting.setText(putotText);
        StringRequest stringreqs = new StringRequest(Request.Method.POST, HostingerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(),response.toString(),Toast.LENGTH_SHORT).show();
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
                map.put(SinchHolders.phpMeetingCreator,userFCM);
                map.put(SinchHolders.phpMeetingDesc,Desc);
                map.put(SinchHolders.phpMeetingParticipants,"10");
                return map;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(stringreqs);
    }

    private void callTheUser() {
        try {
            //Call call = SinchHolders.myClient.getCallClient().callUser(callingUsersName);
            //SinchMainActivity.SinchOutGoingCallListener = new SinchMainActivity.SinchOutGoingCallListener();
           // call.addCallListener(new SinchCallListener());
        }catch (MissingPermissionException e){
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    public static void CallStatus(int x) {
        statusOfCall.setTextColor(Color.parseColor("#000000"));
        switch (x) {
            case 1:
                //Call is ringing;
                statusOfCall.setText("Call is ringing");
                break;
            case 2:
                //Call is picked
                statusOfCall.setText("Call is Picked");
                break;
            case 3:
                //Call is cancelled
                statusOfCall.setText("Call is Cancelled");
                break;
            case 4:
                //We will get back to it later;
                break;
        }
    }
/*

    class SinchCallListener implements CallListener {
       // FragmentTransaction fr = getActivity().getFragmentManager().beginTransaction();
        @Override
        public void onCallEnded(Call endedCall) {
            //call ended by either party
            CallEndCause cause = endedCall.getDetails().getEndCause();
            mAudioPlayer.stopProgressTone();
            getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + endedCall.getDetails().toString();
            Toast.makeText(getActivity(), endMsg, Toast.LENGTH_LONG).show();
//            mAudioPlayer.stopProgressTone();
            SinchPlaceCallFragment.CallStatus(3);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            //incoming call was picked up
            mAudioPlayer.stopProgressTone();
            getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            SinchPlaceCallFragment.CallStatus(2);

        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            //call is ringing
          //  fr.replace(R.id.mainholderforsinchcalling,new SinchOnGoingCallFragment()).commit();
            ((SinchMainActivity)getActivity()).ChangingFragments(1);
            SinchPlaceCallFragment.CallStatus(1);
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            //don't worry about this right now
        }
    }*/
}
