package com.codepath.apps.basictwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeActivity extends Activity {

	private ImageView ivMyProfilePic;
	private TextView tvScreenName;
	private EditText etMessage;
	
	private String screen_name;
	private String profile_image_url;
	private String text_message;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_compose);
		setupViews();
		
		screen_name = getIntent().getStringExtra("screen_name");
		profile_image_url = getIntent().getStringExtra(
				"profile_image_url");
		
		ivMyProfilePic.setImageResource(0);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(profile_image_url,ivMyProfilePic);
		tvScreenName.setText("@" + screen_name);
		
	}
	public void onTweet(View v) {
		text_message = etMessage.getText().toString();
		Intent data = new Intent();
		data.putExtra("message", text_message);
		setResult(RESULT_OK, data);
		finish();
	}
	public void onCancel(View v) {
		finish();
	}

	private void setupViews() {	
		ivMyProfilePic = (ImageView) findViewById(R.id.ivMyProfilePic);
		tvScreenName = (TextView )findViewById(R.id.tvUsername);
		etMessage = (EditText)findViewById(R.id.etMessage);

	}
}
