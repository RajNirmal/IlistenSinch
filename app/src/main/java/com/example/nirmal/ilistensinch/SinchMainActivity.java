package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import static com.example.nirmal.ilistensinch.SinchHolders.myClient;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchMainActivity extends FragmentActivity {
    FragmentManager fragmentManager;
    int mainFragmentHolder;
    public Call mainCall;
    String theUsertoCall;
    AudioPlayer mAudioPlayer;
    Bundle extra;
    public boolean whatToDo = false;//set to true if I am making a call else set to false for incoming call
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinch_call_activity);
        fragmentManager = this.getSupportFragmentManager();
        mainFragmentHolder = R.id.mainholderforsinchcalling;
        mAudioPlayer = new AudioPlayer(this);
        FragmentTransaction fr = fragmentManager.beginTransaction();
        fr.replace(mainFragmentHolder,new SinchPlaceCallFragment());
        fr.commit();
        myClient.getCallClient().addCallClientListener(new SinchIncomingCallListener());
        extra = getIntent().getExtras();
        if(extra == null){
            String User = "Default";
        }else {
            String Conferencename = extra.getString(SinchHolders.phpMeetingName);
            setTheUsertoCall(Conferencename);
        }
    }
    public void letsGoBackToMainActivity(){
        Intent i = new Intent(SinchMainActivity.this,MainActivity.class);
        startActivity(i);
    }

    class SinchIncomingCallListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            // Log.d(TAG, "Incoming call");
            FragmentTransaction fr = fragmentManager.beginTransaction();
            mainCall = call;
            whatToDo = false;
            if(call!=null)
                fr.replace(R.id.mainholderforsinchcalling,new SinchOnGoingCallFragment()).commit();
            //fr.commit();
            /*Intent intent = new Intent(SinchMainActivity.this, SinchOnGoingCallFragment.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        }
    }
    public void setTheUsertoCall(String x){
        theUsertoCall = x;
        whatToDo = true;
        FragmentTransaction makeaCall = fragmentManager.beginTransaction();
        makeaCall.replace(mainFragmentHolder,new SinchOnGoingCallFragment()).commit();
    }
    public void goBackToMain(){
        Intent i = new Intent(SinchMainActivity.this,MainActivity.class);
        Toast.makeText(getApplicationContext(),"Conference Successfully setup",Toast.LENGTH_SHORT).show();
        startActivity(i);

    }
    public String getTheUsertoCall(){return theUsertoCall;}


}
