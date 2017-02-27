package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import static com.example.nirmal.ilistensinch.SinchHolders.APP_KEY;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_SECRET;
import static com.example.nirmal.ilistensinch.SinchHolders.ENVIRONMENT;
import static com.example.nirmal.ilistensinch.SinchHolders.SharedPrefName;
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
        Intent intent = getIntent();
        if(intent.hasExtra("StartaClient")){
            boolean flag = intent.getBooleanExtra("StartaClient",true);
            if(flag)
                buildClient();
        }
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
    private void buildClient(){
//        showSpinner();
        SharedPreferences spefs = getApplication().getSharedPreferences(SharedPrefName,MODE_PRIVATE);
        String UserName = spefs.getString(SinchHolders.phpUserName,"DefaultVal");
        myClient = Sinch.getSinchClientBuilder().context(this).userId(UserName).applicationKey(APP_KEY).applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();
        myClient.setSupportCalling(true);
        myClient.startListeningOnActiveConnection();
        myClient.setSupportActiveConnectionInBackground(true);
        //  myClient.getCallClient().addCallClientListener(new SinchCallClientListenerMine());
        myClient.addSinchClientListener(new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                // Toast.makeText(getApplicationContext(),"Client is connected",Toast.LENGTH_SHORT).show();
//                spinnerLog.dismiss();
                /*Intent i = new Intent(SinchLoginActivity.this,MainActivity.class);
                startActivity(i);*/
            }

            @Override
            public void onClientStopped(SinchClient sinchClient) {
                Toast.makeText(getApplicationContext(),"Client is disconnected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
                Toast.makeText(getApplicationContext(),"Connection failed. Try again after sometime",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

            }

            @Override
            public void onLogMessage(int i, String s, String s1) {

            }
        });
        myClient.start();
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
