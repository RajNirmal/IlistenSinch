package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.example.nirmal.ilistensinch.SinchHolders.myClient;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_KEY;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_SECRET;
import static com.example.nirmal.ilistensinch.SinchHolders.ENVIRONMENT;



/**
 * Created by nirmal on 23/12/16.
 */

public class SinchLoginActivity extends Activity {
    final String URL = "https://sfbpush.herokuapp.com/hello";
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
                sendDatatoHeroku(uNameString);
                if(!uNameString.isEmpty()) {
                    buildClient(uNameString);
                    updateDatainFirebase();
                }
                else
                    Toast.makeText(getApplicationContext(),"Please Enter a UserName",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendDatatoHeroku(final String name){
        StringRequest sr = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"The Server returned error",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("test",name);
                return map;
            }
        };
        RequestQueue r = Volley.newRequestQueue(getApplicationContext());
        r.add(sr);
    }
    private void sendJSONDatatoHeroku(final String names){
        //Toast.makeText(getApplicationContext(),"Sending Data",Toast.LENGTH_SHORT).show();
        JSONObject jsonObj = new JSONObject();
        try{
            jsonObj.put("test",names);
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(),e.toString()+"error parsing json",Toast.LENGTH_SHORT).show();
        }
        final String JSONString = jsonObj.toString();
        JsonObjectRequest str = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString()+"error returned",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {
                try {
                    return JSONString == null ? null : JSONString.getBytes("utf-8");
                }catch (UnsupportedEncodingException x){
                    Toast.makeText(getApplicationContext(),x.toString()+"error in JSONVolley",Toast.LENGTH_SHORT).show();
                    return null;
                }
            }
/*
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("test",names);
                return params;
            }*/
        };
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(str);
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
                /*Intent i = new Intent(SinchLoginActivity.this,SinchMainActivity.class);
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
    private void showSpinner(){
        spinnerLog = new ProgressDialog(SinchLoginActivity.this);
        spinnerLog.setTitle("Trying to log in");
        spinnerLog.setMessage("Please Wait");
        spinnerLog.show();
    }


}
