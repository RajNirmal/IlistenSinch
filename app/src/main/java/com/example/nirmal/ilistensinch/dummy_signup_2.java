package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.sinch.android.rtc.Sinch;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 24/1/17.
 */

public class dummy_signup_2 extends Activity implements AdapterView.OnItemSelectedListener {
    String uBirth, ugender, uZip, uName, uNick, uPass, uFCM, uUserProf, uUserExp;
    EditText zipCode;
    Spinner BirthYear;
    RadioGroup userSex;
    Button finish;
    TextView testing;
    String years[] = {"1970","1971","1972","1973","1974","1975","1976","1977","1978","1979","1980","1981","1982","1983","1984","1985","1986","1987","1988","1989","1990","1991",
            "1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002"," 2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013"};
    public final String URL = "http://www.mazelon.com/iListen/ilisten_Save_User.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout_2);
        zipCode = (EditText)findViewById(R.id.dummy_zipcode);
        BirthYear = (Spinner)findViewById(R.id.spinnerbirthyear);
        userSex = (RadioGroup)findViewById(R.id.radioGroup);
        finish = (Button)findViewById(R.id.dummy_finishBtn);
        testing = (TextView)findViewById(R.id.NullText);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_item,years);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.useryear,R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BirthYear.setAdapter(adapter);
        BirthYear.setOnItemSelectedListener(this);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uZip = zipCode.getText().toString().trim();
                int selectedId = userSex.getCheckedRadioButtonId();
                if(selectedId == R.id.radioButton){
                    ugender = "M";
                }else{
                    ugender = "F";
                }
                boolean flag = checkIfDataisValid();
                if(flag){
                    writeDatatoSharedPrefs();
                    getDatafromSharedPrefs();
                    ToastData();
                    writeDatatoDB();
                    changActivity();
                }
            }
        });
    }
    private void changActivity(){
        Intent i = new Intent(dummy_signup_2.this,MainActivity.class);
        startActivity(i);
    }
    private void getDatafromSharedPrefs(){
        SharedPreferences prefs = getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        final String DefaultValue = "xyz";
        try {
            uName = prefs.getString(SinchHolders.phpUserName,DefaultValue);
            uNick = prefs.getString(SinchHolders.phpUserNickName,DefaultValue);
            uPass = prefs.getString(SinchHolders.phpUserPassword,DefaultValue);
            uFCM = prefs.getString(SinchHolders.phpUserFirebaseToken,DefaultValue);
            uUserProf = prefs.getString(SinchHolders.phpUserProfession,DefaultValue);
            uUserExp = prefs.getString(SinchHolders.phpUserexpertise,DefaultValue);
         //   Toast.makeText(getApplicationContext(),uName + "  "+uNick,Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    private void writeDatatoSharedPrefs(){
        SharedPreferences prefs = getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(SinchHolders.phpUserGender,ugender);
        edit.putString(SinchHolders.phpUserZip,uZip);
        edit.putString(SinchHolders.phpUserBirthYear,uBirth);
        edit.commit();
    }
    private void ToastData(){
       Intent i = new Intent(dummy_signup_2.this,SinchLoginActivity.class);
       startActivity(i);;
    }
    private void writeDatatoDB(){

        StringRequest sr = new StringRequest(Request.Method.POST,URL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
             //   Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occured try again later",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> maps = new HashMap<>();
                maps.put(SinchHolders.phpUserName,uName);
                maps.put(SinchHolders.phpUserNickName,uNick);
                maps.put(SinchHolders.phpUserPassword,uPass);
                maps.put(SinchHolders.phpUserFirebaseToken,uFCM);
                maps.put(SinchHolders.phpUserProfession,uUserProf);
                maps.put(SinchHolders.phpUserexpertise,uUserExp);
                maps.put(SinchHolders.phpUserGender,ugender);
                maps.put(SinchHolders.phpUserZip,uZip);
                maps.put(SinchHolders.phpUserBirthYear,uBirth);
                return maps;

            }
        };
        RequestQueue rq = Volley.newRequestQueue(dummy_signup_2.this);
        rq.add(sr);
    }


    private boolean checkIfDataisValid(){
        if(uZip.isEmpty()){
            Toast.makeText(getApplicationContext(),"Enter Zip Code",Toast.LENGTH_SHORT).show();
            return false;
        }else if(uZip.length() != 6){
            Toast.makeText(getApplicationContext(),"Enter valid Zip Code",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(BirthYear.equals("2017")){
            Toast.makeText(getApplicationContext(),"Enter valid year",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        uBirth = (String)adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        uBirth = "2017";
    }
}
