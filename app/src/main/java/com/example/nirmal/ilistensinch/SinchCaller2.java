package com.example.nirmal.ilistensinch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchCaller2 extends BaseActivity implements SinchService.StartFailedListener {

    @Override
    protected void onServiceConnected() {
      //  mLoginButton.setEnabled(true);
        Toast.makeText(getApplicationContext(),"Servie Connected in Login",Toast.LENGTH_LONG).show();
        getSinchServiceInterface().setStartListener(this);
        spinnerLog.dismiss();
    }

    @Override
    protected void onPause() {
     /*   if (mSpinner != null) {
            mSpinner.dismiss();
        }*/
        Toast.makeText(getApplicationContext(),"Service Paused",Toast.LENGTH_LONG).show();
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        /*if (mSpinner != null) {
            mSpinner.dismiss();
        }*/
        Toast.makeText(getApplicationContext(),"Service failed Connected in Login",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        Toast.makeText(getApplicationContext(),"Change teh Activity",Toast.LENGTH_LONG).show();
    }

    Button logButton;
    EditText userName;
    String uNameString;
    ProgressDialog spinnerLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinch_login_screen);
        logButton = (Button)findViewById(R.id.sinch_login);
        userName = (EditText)findViewById(R.id.user_name_sinch);
//        spinnerLog.show();
        showSpinner();
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myClient.getCallClient().callUser("Nirmal");
                uNameString = userName.getText().toString().trim();
                if(!uNameString.isEmpty())
                    loginClicked();
                    //buildClient(uNameString);
                else
                    Toast.makeText(getApplicationContext(),"Please Enter a UserName",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loginClicked(){
        getSinchServiceInterface().setStartListener(this);
        getSinchServiceInterface().startClient(uNameString);
    }
    private void showSpinner(){
        spinnerLog = new ProgressDialog(SinchCaller2.this);
        spinnerLog.setTitle("Trying to log in");
        spinnerLog.setMessage("Please Wait");
        spinnerLog.show();
    }
}
