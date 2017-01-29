package com.example.nirmal.ilistensinch;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sinch.android.rtc.Sinch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 28/1/17.
 */

public class FirebaseInst extends FirebaseInstanceIdService {
    String Nick;
    final String URL = "http://www.mazelon.com/iListen/ilisten_update_fcmtoken.php";
    RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
    @Override
    public void onTokenRefresh() {
        final String x = FirebaseInstanceId.getInstance().getToken();
        if( x.equals(SinchHolders.FirebaseToken)){
            //No need to change the data
            Toast.makeText(getApplicationContext(),"No need to change the data",Toast.LENGTH_SHORT).show();
        }else{
            StringRequest str = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences spf = getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
                    try{
                        Nick = spf.getString(SinchHolders.phpUserNickName,"123");
                    }catch (NullPointerException e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                    HashMap<String,String> maps = new HashMap<>();
                    maps.put(SinchHolders.phpUserFirebaseToken,x);
                    maps.put(SinchHolders.phpUserNickName,Nick);
                    return maps;
                }
            };
            rq.add(str);
        }

    }
}
