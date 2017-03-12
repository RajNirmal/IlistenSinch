package com.example.nirmal.ilistensinch;

import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchOnGoingCallFragment extends Fragment {
    public com.sinch.android.rtc.calling.Call call;
    String CallingUsersName;
    private UpdateCallDurationTask mDurationTask;
    private Timer mTimer;
    TextView UserNameinTextView,mCallDuration,mConferenceParticipants;
    Button rejectButton;
    MediaPlayer mp;
    final String URL ="https://callingapi.sinch.com/v1/conferences/id/" ;
    final String AppKey = "762e9944-0918-4a8a-9f64-efbbbd93f0c1";
    final String AppSecretKey = "WFzBgsADy0uFHmkGDwXqDQ==";
    ImageButton acceptButton,muteButton;
    private long mCallStart = 0;
    AudioPlayer mAudioPlayer;
    Runnable handlerForParticipantsCount;
    Handler mHandler;
    RequestQueue rq ;
    boolean flag;//set as true if user is mute
    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mp = MediaPlayer.create(inflater.getContext(),R.raw.alrm);
        flag = false;
        rq = Volley.newRequestQueue(getActivity(),new okhttpstack());
        View subView = inflater.inflate(R.layout.sinch_conference_fragment, container, false);
        UserNameinTextView = (TextView) subView.findViewById(R.id.conferencename);
        acceptButton = (ImageButton) subView.findViewById(R.id.rejectconferencebutton);
        muteButton = (ImageButton)subView.findViewById(R.id.button3);
//        rejectButton = (Button) subView.findViewById(R.id.rejectthecall);
        mCallDuration = (TextView) subView.findViewById(R.id.conferenceduration);
        mCallDuration.setVisibility(View.INVISIBLE);
        mConferenceParticipants = (TextView)subView.findViewById(R.id.conferenceparticipation);
        mConferenceParticipants.setVisibility(View.INVISIBLE);
        mHandler = new Handler();
//        String whatUserToCall = (((SinchMainActivity) getActivity()).getTheUsertoCall());
        mAudioPlayer = new AudioPlayer(getActivity());
        setTheOnClickListeners();
        mCallStart = System.currentTimeMillis();
        handlerForParticipantsCount = new Runnable() {
            @Override
            public void run() {
                try{
                    getTheParticipantCount();
                }finally {
                    mHandler.postDelayed(handlerForParticipantsCount,20000);
                }
            }
        };
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MuteTheParticipants();
            }
        });

        if (!((SinchMainActivity) getActivity()).whatToDo) {
            String CallerIdentifier = ((SinchMainActivity) getActivity()).mainCall.getCallId();
            call = SinchHolders.myClient.getCallClient().getCall(CallerIdentifier);
            CallingUsersName = call.getRemoteUserId();
            UserNameinTextView.setText(CallingUsersName);
            mAudioPlayer.playRingtone();
        } else {
            CallingUsersName = (((SinchMainActivity) getActivity()).getTheUsertoCall());
            try {
                call = SinchHolders.myClient.getCallClient().callConference(CallingUsersName);
                call.addCallListener(new SinchCallListener());

            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
            }
            UserNameinTextView.setText(CallingUsersName);
        }
        return subView;
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }
    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
    private void setTheOnClickListeners() {
        acceptButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View view){
        call.addCallListener(new SinchCallListener());
        call.hangup();
        mAudioPlayer.stopRingtone();
        mAudioPlayer.stopProgressTone();
            ((SinchMainActivity)getActivity()).letsGoBackToMainActivity();

        }   });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "You may now answer the call", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "This application needs permission to use your microphone to function properly.", Toast
                    .LENGTH_LONG).show();
        }
    }

    class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            //call ended by either party
            CallEndCause cause = endedCall.getDetails().getEndCause();
            mAudioPlayer.stopProgressTone();
            getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            mAudioPlayer.stopProgressTone();
            mHandler.removeCallbacks(handlerForParticipantsCount);
        }
        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call establishedCall) {
            //incoming call was picked up
            mp.start();
            getTheParticipantCount();
            mCallStart = System.currentTimeMillis();
            mCallDuration.setVisibility(View.VISIBLE);
            getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            handlerForParticipantsCount.run();
        }
        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call progressingCall) {
            //call is ringing
            mp.start();
            mCallDuration.setVisibility(View.VISIBLE);
        }
        @Override
        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> pushPairs) {
            //don't worry about this right now
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    public void getTheParticipantCount(){
        String finalURL = URL+CallingUsersName;
        StringRequest sr = new StringRequest(Request.Method.GET, finalURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jobj = new JSONObject(response);
                    JSONArray jArray = jobj.getJSONArray("participants");
                    StringBuilder sb =new StringBuilder();
                    for(int i=0 ; i<jArray.length() ; i++){
                        JSONObject jsonObject = jArray.getJSONObject(i);
                        sb.append(jsonObject.toString());
                    }
                    mConferenceParticipants.setText("Participants :" + jArray.length());
                    mConferenceParticipants.setVisibility(View.VISIBLE);
                }catch (JSONException e){
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mConferenceParticipants.setText(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",AppKey,AppSecretKey);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(8000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

    public void MuteTheParticipants(){
        String userName = call.getCallId();
        final String muteURL = URL+CallingUsersName+"/"+userName;
        StringRequest sr = new StringRequest(Request.Method.PATCH, muteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(flag)
                    muteButton.setBackgroundResource(R.drawable.microphone);
                else
                    muteButton.setBackgroundResource(R.drawable.microphone_off
                    );

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",AppKey,AppSecretKey);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
//                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                if(!flag) {
                    params.put("command", "mute");
                    flag = true;
                }else {
                    params.put("command", "unmute");
                    flag = false;
                }
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(8000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
    }

}
