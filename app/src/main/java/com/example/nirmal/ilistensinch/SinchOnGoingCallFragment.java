package com.example.nirmal.ilistensinch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
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
    TextView UserNameinTextView,mCallDuration;
    Button acceptButton, rejectButton;
    private long mCallStart = 0;
    AudioPlayer mAudioPlayer;
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
        View subView = inflater.inflate(R.layout.sinch_incoming_call, container, false);
        UserNameinTextView = (TextView) subView.findViewById(R.id.caller_name);
        acceptButton = (Button) subView.findViewById(R.id.answerthecall);
        rejectButton = (Button) subView.findViewById(R.id.rejectthecall);
        mCallDuration = (TextView) subView.findViewById(R.id.calling_time);
       // String whatUserToCall = (((SinchMainActivity) getActivity()).getTheUsertoCall());
        mAudioPlayer = new AudioPlayer(getActivity());
        setTheOnClickListeners();
        mCallStart = System.currentTimeMillis();
        /*Intent i = gUsetIntent();
        String CallerIdentifier = i.getStringExtra(SinchHolders.CALL_ID);*/
        if (!((SinchMainActivity) getActivity()).whatToDo) {
            String CallerIdentifier = ((MainActivity) getActivity()).mainCall.getCallId();
            call = SinchHolders.myClient.getCallClient().getCall(CallerIdentifier);
            CallingUsersName = call.getRemoteUserId();
            UserNameinTextView.setText(CallingUsersName);
            mAudioPlayer.playRingtone();
            updateTheView(false);
        } else {
            CallingUsersName = (((MainActivity) getActivity()).getTheUsertoCall());
            try {
                call = SinchHolders.myClient.getCallClient().callConference(CallingUsersName);
                call.addCallListener(new SinchCallListener());

            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
            }
            UserNameinTextView.setText(CallingUsersName);
            updateTheView(true);
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
        try {
            call.answer();
            acceptButton.setVisibility(View.INVISIBLE);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
        }
        updateTheView(true);
        }   });
        rejectButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick (View view){
            call.hangup();
        mAudioPlayer.stopRingtone();
        mAudioPlayer.stopProgressTone();
                /*Intent i = new Intent(In.this,SinchMainActivity.class);
                startActivity(i);*/
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.detach(SinchOnGoingCallFragment.this);
        ft.replace(R.id.mainholderforsinchcalling, new SinchPlaceCallFragment());
        ft.commit();
        //updateTheView(false);
        }    });
    }

    private void updateTheView(boolean x){
        //Send false if to show two buttons and send true to show one button

        if(x) {
            acceptButton.setVisibility(View.GONE);
            rejectButton.setGravity(Gravity.CENTER);
        }else{
            acceptButton.setVisibility(View.VISIBLE);
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
    class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(com.sinch.android.rtc.calling.Call endedCall) {
            //call ended by either party
            CallEndCause cause = endedCall.getDetails().getEndCause();
            mAudioPlayer.stopProgressTone();
            getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + endedCall.getDetails().toString();
            Toast.makeText(getActivity(), endMsg, Toast.LENGTH_LONG).show();
            mAudioPlayer.stopProgressTone();
            updateTheView(false);
            rejectButton.performClick();
        }

        @Override
        public void onCallEstablished(com.sinch.android.rtc.calling.Call establishedCall) {
            //incoming call was picked up
            mAudioPlayer.stopProgressTone();
            mCallStart = System.currentTimeMillis();
            getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(com.sinch.android.rtc.calling.Call progressingCall) {
            //call is ringing
            mAudioPlayer.playProgressTone();
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

}
