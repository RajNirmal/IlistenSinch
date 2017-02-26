package com.example.nirmal.ilistensinch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rebtel.repackaged.com.google.gson.JsonObject;
import com.sinch.android.rtc.Sinch;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

	EditText emailid, password;
	Button loginButton;
	TextView forgotPassword, signUp;
	CheckBox show_hide_password;
	LinearLayout loginLayout;
	Animation shakeAnimation;
    String sName, snName, sPass, sFireBaseToken, sUserExpert, sUserProf,sGender,sZip,sBirth;
    ProgressDialog spinnerLog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		updateToken();
        checkIfDataAlreadyexists();
		initViews();
		setListeners();
	}
	private void updateToken(){

	}
    private void showSpinner(){
        spinnerLog = new ProgressDialog(Login.this);
        spinnerLog.setTitle("Trying to log in");
        spinnerLog.setMessage("Please Wait");
        spinnerLog.show();
    }
    private void checkIfDataAlreadyexists(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        try{
            String name = prefs.getString(SinchHolders.phpUserName,"1");
            String nick = prefs.getString(SinchHolders.phpUserNickName,"2");
            if((name.equals("1"))&&(nick.equals("2"))){
             //   Toast.makeText(getApplicationContext(),"No user data found",Toast.LENGTH_SHORT).show();
            }else {
				//Toast.makeText(getApplicationContext(),"Came here from login" + name,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this,MainActivity.class);
                startActivity(i);
            }
        }catch (NullPointerException e){
           // Toast.makeText(getApplicationContext(),"No user data found",Toast.LENGTH_SHORT).show();
        }
    }
	// Initiate Views
	private void initViews() {

		emailid = (EditText) findViewById(R.id.login_emailid);
		password = (EditText) findViewById(R.id.login_password);
		loginButton = (Button) findViewById(R.id.loginBtn);
		forgotPassword = (TextView) findViewById(R.id.forgot_password);
		signUp = (TextView) findViewById(R.id.createAccount);
		show_hide_password = (CheckBox)
				findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(this,
				R.anim.shake);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			forgotPassword.setTextColor(csl);
			show_hide_password.setTextColor(csl);
			signUp.setTextColor(csl);
		} catch (Exception e) {
		}

		loginButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
//                ContextThemeWrapper ctw = new ContextThemeWrapper(TestActivity.this, R.style.AppDialog);
                final AlertDialog.Builder alert = new AlertDialog.Builder(Login.this,R.style.AppTheme_Dark_Dialog);
                String email = emailid.getText().toString().trim();
				String pass = password.getText().toString().trim();
                if(email.isEmpty()) {
                    alert.setTitle("Please Fill all the details");
                    alert.setMessage("Enter the name");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    Toast.makeText(getApplicationContext(), "Enter valid details", Toast.LENGTH_SHORT).show();
                    alert.show();
                }else if(pass.isEmpty()) {
                    alert.setTitle("Please Fill all the details");
                    alert.setMessage("Enter the password");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                    Toast.makeText(getApplicationContext(), "Password is Blank", Toast.LENGTH_SHORT).show();
                }else
				    checkForLoginDetails(email,pass);
//					Toast.makeText(getApplicationContext(),"User Details not found, Please Sign up",Toast.LENGTH_SHORT).show();

			}
		});

		forgotPassword.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), Forgot.class);
				startActivity(intent);
			}
		});

		signUp.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent1 = new Intent(getApplicationContext(), dummy_signup.class);
				startActivity(intent1);
			}
		});
	}


	//Check if user data already exists
	private void checkForLoginDetails(final String theUserName,final String theUserPassword){
//		Toast.makeText(getApplicationContext(),theUserName+theUserPassword,Toast.LENGTH_SHORT).show();

        final AlertDialog.Builder buildAlert = new AlertDialog.Builder(Login.this,R.style.AppTheme_Dark_Dialog);
		final String URL = "http://www.mazelon.com/iListen/ilisten_login_script.php";
		StringRequest sr = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
//				Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                String UserPass = response.toString();
                if(UserPass.equals(theUserPassword)){
                    //The Password is correct
                    showSpinner();
//                    Toast.makeText(getApplicationContext(),"Passwords match",Toast.LENGTH_SHORT).show();
                    getTheDataAndWriteInSharedPrefs(theUserName);

                 /*   Intent i = new Intent(Login.this,MainActivity.class);
                    startActivity(i);*/
                }else {
                    //The Password is wrong
                    buildAlert.setTitle("Wrong Credentials");
                    buildAlert.setMessage("The passwords do not match. If you are not a member then sign in ");
                    buildAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    buildAlert.show();
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
				HashMap<String,String> maps = new HashMap<>();
				maps.put(SinchHolders.phpUserName,theUserName);
				maps.put(SinchHolders.phpUserPassword,theUserPassword);
				return maps;
			}
		};
		RequestQueue requestQ = Volley.newRequestQueue(getApplicationContext());
		requestQ.add(sr);
	}
    private void getTheDataAndWriteInSharedPrefs(final String theUserName){
        final String URL = "http://www.mazelon.com/iListen/ilisten_get_user_details.php";
        StringRequest str = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                try{
                    JSONObject objJSON = new JSONObject(response);
                    JSONArray jArray = objJSON.getJSONArray("result");
                    JSONObject obj = jArray.getJSONObject(0);
                    sName = obj.getString("UserName");
                    sZip = obj.getString("UserZipCode");
                    sBirth = obj.getString("UserBirthYear");
                    sGender = obj.getString("UserGender");
                    sUserProf = obj.getString("UserProfession");
                    sUserExpert = obj.getString("UserExpertise");
                    writeTheDataInSharedPrefs();
                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    spinnerLog.dismiss();
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
                HashMap<String,String> maps = new HashMap<>();
                maps.put(SinchHolders.phpUserName,theUserName);
                return maps;
            }
        };
        RequestQueue req = Volley.newRequestQueue(getApplicationContext());
        req.add(str);
    }
    private void writeTheDataInSharedPrefs(){
//        Toast.makeText(getApplicationContext(),"Inside Shared Prefs",Toast.LENGTH_SHORT).show();
        SharedPreferences sprefs = getApplication().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        sFireBaseToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences.Editor edit = sprefs.edit();
        edit.putString(SinchHolders.phpUserName,sName);
        edit.putString(SinchHolders.phpUserNickName,sName);
//        edit.putString(SinchHolders.phpUserPassword,sPass);
        edit.putString(SinchHolders.phpUserFirebaseToken,sFireBaseToken);
        edit.putString(SinchHolders.phpUserexpertise,sUserExpert);
        edit.putString(SinchHolders.phpUserProfession,sUserExpert);
        edit.putString(SinchHolders.phpUserGender,sGender);
        edit.putString(SinchHolders.phpUserZip,sZip);
        edit.putString(SinchHolders.phpUserBirthYear,sBirth);
        edit.commit();
        changeActivity();
    }
    private void changeActivity(){
//        Toast.makeText(getApplicationContext(),"Inside Activity Change",Toast.LENGTH_SHORT).show();
        spinnerLog.dismiss();
        Intent i = new Intent(Login.this,MainActivity.class);
        startActivity(i);
    }
	// Set Listeners
	private void setListeners() {
		// Set check listener over checkbox for showing and hiding password
		show_hide_password.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {
							show_hide_password.setText(R.string.hide_pwd);// change checkbox text
							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change checkbox text
							password.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod.getInstance());// hide password
						}
					}
				});
	}

}
