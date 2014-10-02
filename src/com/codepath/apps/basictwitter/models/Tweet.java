package com.codepath.apps.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.widget.Toast;

public class Tweet {
	private String body;
	private long uid;
	private String createdAt;
	private User user;
	public static long max_id = 0;
	
	public static Tweet fromJson(JSONObject jsonObject){
		Tweet tweet = new Tweet();
		//Extract values from json to populate the member variables
		try {
			
			tweet.body = jsonObject.getString("text");
			tweet.uid =  jsonObject.getLong("id");
			tweet.createdAt =  jsonObject.getString("created_at");
			tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
			if(Tweet.max_id == 0 || Tweet.max_id <  tweet.uid) {
				Tweet.max_id =  tweet.uid;
			}
			
			//Log.d("naveen3", "Body: " + tweet.body);
		}catch(JSONException e){
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public String getBody() {
		return body;
	}

	public long getUid() {
		return uid;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public User getUser() {
		return user;
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
		for(int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJson(tweetJson);
			if(tweet!=null){
				tweets.add(tweet);
			}
		}
		return tweets;
	}
 @Override
public String toString() {
	// TODO Auto-generated method stub
	return getBody() + " - " + getUser().getScreenName();
}
}
