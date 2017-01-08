package com.example.nirmal.ilistensinch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

/**
 * Created by nirmal on 24/12/16.
 */

public class SinchHolders extends Service{
    public static SinchClient myClient;
    public static final String APP_KEY = "762e9944-0918-4a8a-9f64-efbbbd93f0c1";
    public static final String APP_SECRET = "WFzBgsADy0uFHmkGDwXqDQ==";
    public static final String ENVIRONMENT = "sandbox.sinch.com";
    public static final String CALL_ID = "CALL_ID";
    public static String FirebaseToken;
    static final String TAG = SinchService.class.getSimpleName();
    public void setUpIncomingClient(){
        myClient.getCallClient().addCallClientListener(new SinchCallClientListenerMine());
    }
    public class SinchCallClientListenerMine implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
            Intent intent = new Intent(SinchHolders.this, IncomingCallScreenActivity.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchHolders.this.startActivity(intent);
        }
    }

    private SinchService.StartFailedListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}