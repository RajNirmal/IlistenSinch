package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 13/1/17.
 */

public class dummy_signup extends Activity {
    EditText uName, nName, uPass;
    String sName, snName, sPass;
    Button signUpButton;
    TextView dummyToast;
    public final String HeloUrl = "http://gocode.esy.es/Save_User.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup_dummy);
        uName = (EditText)findViewById(R.id.dummy_fullName);
        nName = (EditText)findViewById(R.id.dummy_nickname);
        uPass = (EditText)findViewById(R.id.dummy_password);
        signUpButton = (Button)findViewById(R.id.dummy_signUpBtn);
        dummyToast = (TextView)findViewById(R.id.dummy_toast);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dummyToast.setVisibility(View.GONE);
                sName = uName.getText().toString().trim();
                snName = nName.getText().toString().trim();
                sPass = uPass.getText().toString().trim();
                if((sName.isEmpty())||(snName.isEmpty())||(sPass.isEmpty())){
                    dummyToast.setVisibility(View.VISIBLE);
                    dummyToast.setText("Enter Valid Details");
                }
                else{
                    sendDataToServer();
                    writeDataInSharedPrefs();
                   // Toast.makeText(getApplicationContext(),"All Details are correct",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void writeDataInSharedPrefs(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(SinchHolders.phpUserName,sName);
        edit.putString(SinchHolders.phpUserNickName,snName);
        edit.putString(SinchHolders.phpUserName,sPass);
        edit.commit();
    }
    private void ChangeActivity(){
        Intent i = new Intent(dummy_signup.this,MainActivity.class);
        startActivity(i);
    }
    private void sendDataToServer(){
        StringRequest rq = new StringRequest(Request.Method.POST, HeloUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().equals("Failure"))
                    Toast.makeText(getApplicationContext(), "NickName already taken", Toast.LENGTH_SHORT).show();
                else
                    ChangeActivity();
                //Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> myMap = new HashMap<String,String>();
                myMap.put(SinchHolders.phpUserName,sName);
                myMap.put(SinchHolders.phpUserNickName,snName);
                myMap.put(SinchHolders.phpUserPassword,sPass);
                return myMap;
            }
        };
        RequestQueue rs = Volley.newRequestQueue(getApplicationContext());
        rs.add(rq);

    }

}
