package com.example.nirmal.ilistensinch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nirmal.ilistensinch.DBPackage.MeetingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 26/2/17.
 */

public class handleBroadcast extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Integer MeetID ;
        final String MeetName;
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Intent intents = getIntent();
        if (intents.hasExtra(SinchHolders.phpMeetingId)) {
            MeetID = intents.getIntExtra(SinchHolders.phpMeetingId, -56);
            MeetName = intents.getStringExtra(SinchHolders.phpMeetingName);
        }else {
            MeetName = null;
            MeetID = -56;
        }

        if (MeetID != -56) {
//            getTheMeetingDataFromHostinger(MeetID);
            builder.setMessage("Are you sure you want to Join " + MeetName +" ?").setCancelable(false).
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(handleBroadcast.this,SinchMainActivity.class);
                            i.putExtra(SinchHolders.phpMeetingName,MeetName);
                            i.putExtra(SinchHolders.phpMeetingId, MeetID);
                            i.putExtra("StartaClient",true);
                            startActivity(i);
                            dialog.cancel();
                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            builder.setMessage("Some error occured in the meeting").setCancelable(false).
                    setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).
                    setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    private void getTheMeetingDataFromHostinger(final int Id){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_meetings_by_id.php";
        StringRequest stringRequestr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                final MeetingList myList ;
                String tMeetingName,tConCategory,tConDesc,tTime,tDuration,tPresenter;
                int tMeetingID;
                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray jsonArray = obj.getJSONArray("result");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    tMeetingName = jsonObject.getString("MeetingName");
                    tConCategory = jsonObject.getString("ConCategory");
                    tConDesc = jsonObject.getString("ConDesc");
                    tTime = "Time : " + jsonObject.getString("Time");
                    tDuration = jsonObject.getString("Duration");
                    tPresenter = jsonObject.getString("PID");
                    tMeetingID = jsonObject.getInt("MeetingID");
                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put(SinchHolders.phpMeetingId,String.valueOf(Id));
                return map;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(stringRequestr);
    }
}
