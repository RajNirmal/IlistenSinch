package com.example.nirmal.ilistensinch;

import android.content.Intent;
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

import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

import static com.example.nirmal.ilistensinch.SinchHolders.myClient;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    public int mainFragmentHolder;
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
                    xfragmentTransaction.replace(R.id.containerView, new NavDrawFrag1()).commit();
                }

                if (menuItem.getItemId() == R.id.collections) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new NavDrawFrag1()).commit();

                }

                if (menuItem.getItemId() == R.id.help) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new NavDrawFrag1()).commit();

                }

                if (menuItem.getItemId() == R.id.exit) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new NavDrawFrag1()).commit();

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
            case R.id.action_call:
               // Toast.makeText(getApplicationContext(),"Call Icon Has been clicked",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this,SinchLoginActivity.class);
                startActivity(i);
                break;
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