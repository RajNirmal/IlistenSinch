package com.example.nirmal.ilistensinch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import static com.example.nirmal.ilistensinch.SinchHolders.APP_KEY;
import static com.example.nirmal.ilistensinch.SinchHolders.APP_SECRET;
import static com.example.nirmal.ilistensinch.SinchHolders.ENVIRONMENT;
import static com.example.nirmal.ilistensinch.SinchHolders.myClient;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    public int mainFragmentHolder;
    ProgressDialog spinnerLog;
    public FragmentManager fragmentManager;
    FragmentTransaction mFragmentTransaction;
    android.support.v7.widget.Toolbar toolbar;
    String theUsertoCall;
    public Call mainCall;
    AudioPlayer mAudioPlayer;
    public boolean whatToDo = false;//set to true if I am making a call else set to false for incoming call


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioPlayer = new AudioPlayer(this);
        if(getIntent().getExtras() != null){
            for(String key: getIntent().getExtras().keySet()){
                Object value = getIntent().getExtras().get(key);
            }
        }
        /**
         *Setup the DrawerLayout and NavigationView
         */
        buildClient(SinchHolders.phpUserNickName);
        myClient.getCallClient().addCallClientListener(new MainActivity.SinchIncomingCallListener());
        updateTheTokeninHostinger();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        mainFragmentHolder = R.id.containerView;
        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.blank,R.string.blank);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.home) {
                    FragmentTransaction xfragmentTransaction = fragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.save_orders) {
                    FragmentTransaction xfragmentTransaction = fragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.collections) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

                }
                if (menuItem.getItemId() == R.id.returns) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

                }
                if (menuItem.getItemId() == R.id.help) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

                }

                if (menuItem.getItemId() == R.id.exit) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

                }

                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }
    public void startTheCall(String x){
        Intent i = new Intent(MainActivity.this,SinchMainActivity.class);
        i.putExtra(SinchHolders.phpMeetingName,x);
        startActivity(i);
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
                // Toast.makeText(getApplicationContext(),"Client is connected",Toast.LENGTH_SHORT).show();
                spinnerLog.dismiss();
                /*Intent i = new Intent(SinchLoginActivity.this,MainActivity.class);
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
        spinnerLog = new ProgressDialog(MainActivity.this);
        spinnerLog.setTitle("Trying to log in");
        spinnerLog.setMessage("Please Wait");
        spinnerLog.show();
    }

    public void showActionBar(){
        getSupportActionBar().show();
    }
    public void hideActionBar(){
        getSupportActionBar().hide();
    }
    class SinchIncomingCallListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            // Log.d(TAG, "Incoming call");
            FragmentTransaction fr = fragmentManager.beginTransaction();
            mainCall = call;
            whatToDo = false;
            if(call!=null)
                fr.replace(R.id.mainholderforsinchcalling,new SinchOnGoingCallFragment()).commit();
            //fr.commit();
            /*Intent intent = new Intent(SinchMainActivity.this, SinchOnGoingCallFragment.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        }
    }
    public void setTheUsertoCall(String x){
        theUsertoCall = x;
        whatToDo = true;
        FragmentTransaction makeaCall = fragmentManager.beginTransaction();
        hideActionBar();
        makeaCall.replace(mainFragmentHolder,new SinchOnGoingCallFragment()).commit();
    }
    public void goBackToMain(){
        Intent i = new Intent(MainActivity.this,MainActivity.class);
        Toast.makeText(getApplicationContext(),"Conference Successfully setup",Toast.LENGTH_SHORT).show();
        startActivity(i);

    }
    public String getTheUsertoCall(){return theUsertoCall;}


    private void updateTheTokeninHostinger(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){

        }
        return true;
    }

    boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;
			}
		}, 2000);
	}
}