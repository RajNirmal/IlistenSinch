package com.example.nirmal.ilistensinch;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchPlaceCallFragment extends Fragment {
    EditText callerName;
    static TextView statusOfCall;
    private AudioPlayer mAudioPlayer;
    Button callButton;
    String callingUsersName;
    FirebaseDatabase FireDB;
    DatabaseReference FBReference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subView = inflater.inflate(R.layout.sinch_place_call_fragment, container, false);
        callButton = (Button) subView.findViewById(R.id.sinch_calling);
        statusOfCall = (TextView) subView.findViewById(R.id.call_status);
        callerName = (EditText) subView.findViewById(R.id.call_name_sinch);
        mAudioPlayer = new AudioPlayer(getActivity());
        FireDB = FirebaseDatabase.getInstance();
        FBReference = FireDB.getReference("MyApp");
        SinchHolders.FirebaseToken = FirebaseInstanceId.getInstance().getToken();
        FBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("UserData").child("name").getValue(String.class);
                Toast.makeText(getActivity(),value,Toast.LENGTH_SHORT).show();
                callerName.setText(SinchHolders.FirebaseToken);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callingUsersName = callerName.getText().toString().trim();

                /*if (!callingUsersName.isEmpty())
                    //callTheUser();
                    ((SinchMainActivity)getActivity()).setTheUsertoCall(callingUsersName);

                else
                    Toast.makeText(getActivity(), "Please Enter a User Name", Toast.LENGTH_SHORT).show();*/
            }
        });
        return subView;
    }
/*
    private void callTheUser() {
        try {
            //Call call = SinchHolders.myClient.getCallClient().callUser(callingUsersName);
            //SinchMainActivity.SinchOutGoingCallListener = new SinchMainActivity.SinchOutGoingCallListener();
           // call.addCallListener(new SinchCallListener());
        }catch (MissingPermissionException e){
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
        }
    }*/

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
