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
                            Intent i = new Intent(handleBroadcast.this,MainActivity.class);
                            startActivity(i);
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
}
