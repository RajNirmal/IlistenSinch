package com.example.nirmal.ilistensinch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sinch.android.rtc.SinchClient;

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
    public static String UserName = "Username";
    public static final String SharedPrefName = "SinchListen";
    public static final String phpUserName = "UserName";
    public static final String phpUserNickName = "NickName";
    public static final String phpUserPassword = "UserPassword";
    public static final String phpUserFirebaseToken = "FireBaseId";
    public static final String phpUserProfession = "userprofession";
    public static final String phpUserexpertise = "userexpertise";
    public static final String phpUserBirthYear = "birthdate";
    public static final String phpUserZip = "zipcode";
    public static final String phpUserGender = "sex";
    public static Integer lastDataCount = 0;
    public static final String phpMeetingName = "MeetingName";
    public static final String phpMeetingTime = "MeetingStartTime";
    public static final String phpMeetingDuration = "MeetingDuration";
    public static final String phpMeetingParticipants = "Participant";
    public static final String phpMeetingCreator = "CreatorId";
    public static final String phpMeetingCategory = "concategory";
    public static final String phpMeetingDesc = "condesc";
    public static final String phpMeetingId = "MeetingId";
//    static final String TAG = SinchService.class.getSimpleName();
    public void setUpIncomingClient(){
//        myClient.getCallClient().addCallClientListener(new SinchCallClientListenerMine());
    }
//    public class SinchCallClientListenerMine implements CallClientListener {
//        @Override
//        public void onIncomingCall(CallClient callClient, Call call) {
//            Log.d(TAG, "Incoming call");
//            Intent intent = new Intent(SinchHolders.this, IncomingCallScreenActivity.class);
//            intent.putExtra(CALL_ID, call.getCallId());
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            SinchHolders.this.startActivity(intent);
//        }
//    }
//
//    private SinchService.StartFailedListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
