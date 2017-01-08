package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import static com.example.nirmal.ilistensinch.SinchHolders.myClient;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_KEY;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_SECRET;
import static com.example.nirmal.ilistensinch.SinchHolders.ENVIRONMENT;



/**
 * Created by nirmal on 23/12/16.
 */

public class SinchLoginActivity extends Activity {
    Button logButton;
    EditText userName;
    String uNameString;
    ProgressDialog spinnerLog;
    FirebaseDatabase fireDatabase;
    DatabaseReference fireReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinch_login_screen);
        logButton = (Button)findViewById(R.id.sinch_login);
        userName = (EditText)findViewById(R.id.user_name_sinch);
        FirebaseApp.initializeApp(getApplicationContext());
        fireDatabase = FirebaseDatabase.getInstance();
        fireReference = fireDatabase.getReference("MyApp");
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myClient.getCallClient().callUser("Nirmal");
                uNameString = userName.getText().toString().trim();

                if(!uNameString.isEmpty()) {
                    buildClient(uNameString);
                    updateDatainFirebase();
                }
                else
                    Toast.makeText(getApplicationContext(),"Please Enter a UserName",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateDatainFirebase(){
        SinchUserData myUser = new SinchUserData(uNameString);
        fireReference.child(SinchUserData.getCanonicalClassName()).child(SinchUserData.UserBaseName()).setValue(myUser.ReturnUserName());
    }
    private void buildClient(String x){
        showSpinner();
        myClient = Sinch.getSinchClientBuilder().context(this).userId(x).applicationKey(APP_KEY).applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();
        myClient.setSupportCalling(true);
        myClient.startListeningOnActiveConnection();
        myClient.setSupportActiveConnectionInBackground(true);
      //  myClient.getCallClient().addCallClientListener(new SinchCallClientListenerMine());

        myClient.addSinchClientListener(new SinchClientListener() {
            @Override
            public void onClientStarted(SinchClient sinchClient) {
                Toast.makeText(getApplicationContext(),"Client is connected",Toast.LENGTH_SHORT).show();
                spinnerLog.dismiss();
                Intent i = new Intent(SinchLoginActivity.this,SinchMainActivity.class);
                startActivity(i);
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
    private void showSpinner(){
        spinnerLog = new ProgressDialog(SinchLoginActivity.this);
        spinnerLog.setTitle("Trying to log in");
        spinnerLog.setMessage("Please Wait");
        spinnerLog.show();
    }


}
