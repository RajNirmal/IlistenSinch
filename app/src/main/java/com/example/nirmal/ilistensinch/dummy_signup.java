package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinch.android.rtc.Sinch;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 13/1/17.
 */

public class dummy_signup extends Activity implements AdapterView.OnItemSelectedListener{
    EditText uName, nName, uPass, uProfession;
    String sName, snName, sPass, sFireBaseToken, sUserExpert, sUserProf;
    Button signUpButton;
    TextView dummyToast;
    Spinner Category;
    FirebaseDatabase fireDatabase;
    DatabaseReference fireReference;
    public final String HeloUrl = "http://gocode.esy.es/Save_User.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup_dummy);
        uName = (EditText)findViewById(R.id.dummy_fullName);
        nName = (EditText)findViewById(R.id.dummy_nickname);
        uPass = (EditText)findViewById(R.id.dummy_password);
        uProfession = (EditText)findViewById(R.id.dummy_userprofession);
        signUpButton = (Button)findViewById(R.id.dummy_signUpBtn);
        dummyToast = (TextView)findViewById(R.id.dummy_toast);
        Category = (Spinner)findViewById(R.id.spinner);
        FirebaseApp.initializeApp(getApplicationContext());
        fireDatabase = FirebaseDatabase.getInstance();
        fireReference = fireDatabase.getReference("MyApp");
        SinchHolders.FirebaseToken = FirebaseInstanceId.getInstance().getToken();
        sFireBaseToken = SinchHolders.FirebaseToken;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dummyToast.setVisibility(View.GONE);
                sName = uName.getText().toString().trim();
                snName = nName.getText().toString().trim();
                sPass = uPass.getText().toString().trim();
                sUserProf = uProfession.getText().toString().trim();
                if((sName.isEmpty())||(snName.isEmpty())||(sPass.isEmpty())){
                    dummyToast.setVisibility(View.VISIBLE);
                    dummyToast.setText("Enter Valid Details");
                }
                else{
                   //sendDataToServer();
                    writeDataInSharedPrefs();
                   // Toast.makeText(getApplicationContext(),"All Details are correct",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(dummy_signup.this,dummy_signup_2.class);
                    startActivity(i);
                }

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.userchoices,R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(adapter);
        Category.setOnItemSelectedListener(this);
    }
    private void writeDataInSharedPrefs(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(SinchHolders.phpUserName,sName);
        edit.putString(SinchHolders.phpUserNickName,snName);
        edit.putString(SinchHolders.phpUserPassword,sPass);
        edit.putString(SinchHolders.phpUserFirebaseToken,sFireBaseToken);
        edit.putString(SinchHolders.phpUserexpertise,sUserExpert);
        edit.putString(SinchHolders.phpUserProfession,sUserProf);
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
                else;
                    ChangeActivity();
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
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
                myMap.put(SinchHolders.phpUserFirebaseToken,sFireBaseToken);
                return myMap;
            }
        };
        RequestQueue rs = Volley.newRequestQueue(getApplicationContext());
        rs.add(rq);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        sUserExpert = (String)adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        sUserExpert = "User did not select";
    }
}
