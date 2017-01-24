package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by nirmal on 24/1/17.
 */

public class dummy_signup_2 extends Activity {
    String uBirth, ugender, uZip;
    EditText zipCode;
    Spinner BirthYear;
    RadioGroup userSex;
    Button finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout_2);
        zipCode = (EditText)findViewById(R.id.dummy_zipcode);
        BirthYear = (Spinner)findViewById(R.id.spinnerbirthyear);
        userSex = (RadioGroup)findViewById(R.id.radioGroup);
        finish = (Button)findViewById(R.id.dummy_finishBtn);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uZip = zipCode.getText().toString().trim();
                userSex
                boolean flag = checkIfDataisValid();
            }
        });
    }
    private boolean checkIfDataisValid(){
        if(uZip.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Zip Code",Toast.LENGTH_SHORT).show();
            return false;
        }else if(uZip.length() != 6){
            Toast.makeText(getApplicationContext(),"Enter valid Zip Code",Toast.LENGTH_SHORT).show();
            return false;
        }else

    }
}
