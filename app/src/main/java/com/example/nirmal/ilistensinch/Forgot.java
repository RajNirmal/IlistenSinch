package com.example.nirmal.ilistensinch;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Forgot extends AppCompatActivity implements
		OnClickListener {


	private static EditText emailId;
	private static TextView submit, back;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword_layout);
		initViews();
		setListeners();
	}

	// Initialize the views
	private void initViews() {
		emailId = (EditText)findViewById(R.id.registered_emailid);
		submit = (TextView)findViewById(R.id.forgot_button);
		back = (TextView)findViewById(R.id.backToLoginBtn);

		// Setting text selector over textviews
		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp);

			back.setTextColor(csl);
			submit.setTextColor(csl);

		} catch (Exception e) {
		}

	}

	// Set Listeners over buttons
	private void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backToLoginBtn:
			Intent intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			break;
		case R.id.forgot_button:
			Toast.makeText(this, "Get Forgot Password.",
					Toast.LENGTH_SHORT).show();
			break;

		}

	}

}