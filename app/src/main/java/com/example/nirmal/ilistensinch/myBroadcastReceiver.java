package com.example.nirmal.ilistensinch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

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

    }


}
