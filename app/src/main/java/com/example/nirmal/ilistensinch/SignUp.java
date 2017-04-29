package com.example.nirmal.ilistensinch;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SignUp extends AppCompatActivity implements OnClickListener {

	 EditText fullName, emailId, mobileNumber, location,password, confirmPassword;
	TextView login;
	Button signUpButton;
	CheckBox terms_conditions;
	RadioGroup radioSexGroup;
RadioButton radioSexButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup_layout);
		initViews();
		setListeners();
	}
	// Initialize all views
	private void initViews() {
		fullName = (EditText)findViewById(R.id.fullName);
		emailId = (EditText) findViewById(R.id.userEmailId);
		mobileNumber = (EditText)findViewById(R.id.mobileNumber);
		password = (EditText)findViewById(R.id.password);
		confirmPassword = (EditText)findViewById(R.id.confirmPassword);
		signUpButton = (Button)findViewById(R.id.signUpBtn);
		login = (TextView)findViewById(R.id.already_user);
		terms_conditions = (CheckBox)findViewById(R.id.terms_conditions);
		radioSexGroup = (RadioGroup)findViewById(R.id.radioSex);
		// get selected radio button from radioGroup
		int selectedId = radioSexGroup.getCheckedRadioButtonId();

		// find the radiobutton by returned id
		radioSexButton = (RadioButton)findViewById(selectedId);
		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			login.setTextColor(csl);
			terms_conditions.setTextColor(csl);
		} catch (Exception e) {
		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:

			// Call checkValidation method
			Intent i = new Intent(getApplicationContext(), Pass.class);
			startActivity(i);
			break;

		case R.id.already_user:
			Intent intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			break;
		}

	}

}
