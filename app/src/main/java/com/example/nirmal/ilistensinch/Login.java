package com.example.nirmal.ilistensinch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
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

import com.android.volley.toolbox.StringRequest;
import com.sinch.android.rtc.Sinch;

public class Login extends AppCompatActivity {
	private static View view;

	EditText emailid, password;
	Button loginButton;
	TextView forgotPassword, signUp;
	CheckBox show_hide_password;
	LinearLayout loginLayout;
	Animation shakeAnimation;



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
    private void checkIfDataAlreadyexists(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(SinchHolders.SharedPrefName,MODE_PRIVATE);
        try{
            String name = prefs.getString(SinchHolders.phpUserName,"1");
            String nick = prefs.getString(SinchHolders.phpUserNickName,"2");
            if((name.equals("1"))&&(nick.equals("2"))){
                Toast.makeText(getApplicationContext(),"No user data found",Toast.LENGTH_SHORT).show();
            }else {
				//Toast.makeText(getApplicationContext(),"Came here from login" + name,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Login.this,SinchLoginActivity.class);
                startActivity(i);
            }
        }catch (NullPointerException e){
            Toast.makeText(getApplicationContext(),"No user data found",Toast.LENGTH_SHORT).show();
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
				String email = emailid.getText().toString().trim();
				String pass = password.getText().toString().trim();
                if(email.isEmpty())
                    Toast.makeText(getApplicationContext(),"Enter valid details",Toast.LENGTH_SHORT).show();
                else if(pass.isEmpty())
                    Toast.makeText(getApplicationContext(),"Password is Blank",Toast.LENGTH_SHORT).show();
                else
				    Toast.makeText(getApplicationContext(),"User Details not found, Please Sign up",Toast.LENGTH_SHORT).show();

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

	// Set Listeners
	private void setListeners() {

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {

						// If it is checkec then show password else hide
						// password
						if (isChecked) {

							show_hide_password.setText(R.string.hide_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT);
							password.setTransformationMethod(HideReturnsTransformationMethod
									.getInstance());// show password
						} else {
							show_hide_password.setText(R.string.show_pwd);// change
																			// checkbox
																			// text

							password.setInputType(InputType.TYPE_CLASS_TEXT
									| InputType.TYPE_TEXT_VARIATION_PASSWORD);
							password.setTransformationMethod(PasswordTransformationMethod
									.getInstance());// hide password

						}

					}
				});
	}

}
