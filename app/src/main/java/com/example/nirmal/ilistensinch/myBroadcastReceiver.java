package com.example.nirmal.ilistensinch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nirmal on 22/2/17.
 */

public class myBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;

    @Override
    public void onReceive(Context context, Intent intent) {
        mp = MediaPlayer.create(context,R.raw.alrm);
        mp.start();
        Toast.makeText(context, "Alarm is ringing",Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();
        Intent nextAct = new Intent(context,handleBroadcast.class);
        if(extras.containsKey(SinchHolders.phpMeetingId)){
            int MeetingId = (int)extras.get(SinchHolders.phpMeetingId);
//            getTheMeetingDataFromHostinger(MeetingId, context);
            String MeetingName = extras.getString(SinchHolders.phpMeetingName);
            nextAct.putExtra(SinchHolders.phpMeetingId,MeetingId);
            nextAct.putExtra(SinchHolders.phpMeetingName, MeetingName);
        }
        nextAct.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(nextAct);
    }


}
