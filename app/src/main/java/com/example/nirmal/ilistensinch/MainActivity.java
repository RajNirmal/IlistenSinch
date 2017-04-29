package com.example.nirmal.ilistensinch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4 ;
    private int[] tabIcons = {
            R.drawable.hom,
            R.drawable.tab2,
            R.drawable.tab3
    };

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
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainActivity.MyAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                setupTabIcons();
            }
        });

        /**
         *Setup the DrawerLayout and NavigationView
         */
        getSharedfs();
        buildClient(SinchHolders.UserName);

        myClient.getCallClient().addCallClientListener(new MainActivity.SinchIncomingCallListener());
        updateTheTokeninHostinger();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
//        mainFragmentHolder = R.id.containerView;
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
//        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.home) {
                    viewPager.setCurrentItem(0,true);
                    /*FragmentTransaction xfragmentTransaction = fragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/

                }

                if (menuItem.getItemId() == R.id.save_orders) {
                    viewPager.setCurrentItem(1,true);
                    /*FragmentTransaction xfragmentTransaction = fragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/
                }
                if (menuItem.getItemId() == R.id.returns) {
                    viewPager.setCurrentItem(2,true);
                    /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/

                }
                if (menuItem.getItemId() == R.id.collections) {
                    viewPager.setCurrentItem(3,true);
                    /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/

                }

                if (menuItem.getItemId() == R.id.help) {
//                    viewPager.setCurrentItem(0,true);
                    /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/
                    String Title = "Contact Us";
                    String Body = "ilisten@mazelon.com";
                    AlertBuilder(Title,Body);

                }

                if (menuItem.getItemId() == R.id.exit) {
//                    viewPager.setCurrentItem(0,true);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(intent);
                    /*FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();*/

                }

                if(menuItem.getItemId() == R.id.terms){
                    String Title = "Terms and Conditions";
                    String Body = getString(R.string.t1)+"\n"+getString(R.string.t2)+"\n"+getString(R.string.t3)+"\n"+getString(R.string.t4)+"\n"+getString(R.string.t5)+"\n"+getString(R.string.t6)+"\n";
                    AlertBuilder(Title,Body);
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
    public void AlertBuilder(String AlertTitle, String AlertBody){
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(AlertTitle);
        alert.setMessage(AlertBody);
        alert.setCancelable(false);
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Close the app.

            }
        });
        alert.show();
    }
    public void getSharedfs(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        SinchHolders.UserName = sp.getString(SinchHolders.phpUserName,"Randoms");
    }
    public void switchToThirdFragment(){
        viewPager.setCurrentItem(0,true);
        viewPager.setCurrentItem(2,true);
    }
    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new Fragment1();
                case 1 : return new Fragment2();
                case 2 : return new Fragment3();
                case 3 : return new Fragment4();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "";
                case 1 :
                    return "New Invite";
                case 2 :
                    return "My Invite";
                case 3 :
                    return "My Participation";
            }
            return null;
        }

    }
    public void setTheAlarm(final int MeetID, final long TimeRemaining, final String MeetName){
        Intent i = new Intent(MainActivity.this,myBroadcastReceiver.class);
        i.putExtra(SinchHolders.phpMeetingId,MeetID);
        i.putExtra(SinchHolders.phpMeetingName, MeetName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),MeetID,i,0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ TimeRemaining, pendingIntent);
//        Toast.makeText(this, "Alarm set in "+TimeRemaining+" seconds",Toast.LENGTH_SHORT).show();
    }
    public void startTheCall(String x){
        Intent i = new Intent(MainActivity.this,SinchMainActivity.class);
        i.putExtra(SinchHolders.phpMeetingName,x);
        startActivity(i);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
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