package com.codepath.apps.basictwitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.codepath.apps.basictwitter.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity {
	private TwitterClient client;
	private ArrayList<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private Boolean check_once = false;
	//private ListView lvTweets;
	PullToRefreshListView lvTweets;
	private String composed_message;
	
	private String myScreenName;
	private String myProfilePicURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("INFO", "I am Here");
		setContentView(R.layout.activity_timeline);
		client  = TwitterApplication.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		//lvTweets = (ListView) findViewById(R.id.lvTweets);
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapater(this,tweets);

		lvTweets.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call listView.onRefreshComplete() when
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

		lvTweets.setAdapter(aTweets);
		myProfileInfo();
		populateTimeLine();
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list

				Log.d("INFO", "page = " + page + "and total " + totalItemsCount);
				// Add whatever code is needed to append new items to your
				// AdapterView
				//if(check_once) {
					customLoadMoreDataFromApi();
				//}
				// or customLoadMoreDataFromApi(totalItemsCount);
			}
		});

		
	}
	public void fetchTimelineAsync(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray json) {
                // ...the data has come back, finish populating listview...
                // Now we call onRefreshComplete to signify refresh has finished
                lvTweets.onRefreshComplete();
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        }, 10, Tweet.max_id-10000);
    }
	
	  // Append more data into the adapter
    public void customLoadMoreDataFromApi() {
      // This method probably sends out a network request and appends new data items to your adapter. 
      // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
      // Deserialize API response and then construct new objects to append to the adapter
    	/*AsyncHttpClient client = new AsyncHttpClient();
    	*/
    	
		//Tweet latest_tweet  = (Tweet) lvTweets.getItemAtPosition(0);
    	//Toast.makeText(this, "max_id = "+ Tweet.max_id , Toast.LENGTH_SHORT).show();
		//Log.d("SinceID", "SinceID = " + latest_tweet.getUid()+1);

		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				//Log.d("naveen2", json.toString());
				aTweets.addAll(Tweet.fromJSONArray(json));
				
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		}, 10, Tweet.max_id+1);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miCompose:
			composeMessage();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void composeMessage() {

		//Toast.makeText(this, "Compose ", Toast.LENGTH_SHORT).show();

		Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
		// ImageResult result = imageResults.get(position);
		i.putExtra("screen_name", myScreenName);//need to serializable or parceble.
		i.putExtra("profile_image_url", myProfilePicURL);
		//startActivity(i);
		startActivityForResult(i, 20);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 20) {

		 	composed_message = data.getExtras().getString("message");

			//Toast.makeText(this, "Composed Message "+ composed_message, Toast.LENGTH_SHORT).show();
		 	//View v = this.findViewById(android.R.id.content);
		 	//onImageSearch(v);
		 	postStatus(composed_message);
		 	populateTimeLine();
		 
	  }
	}
	
	private void postStatus(String message) {
		client.postStatus(new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject json) {
				//Log.d("naveen1", json.toString());
				aTweets.insert(Tweet.fromJson(json), 0);
				//aTweets.notifyDataSetChanged();
				//populateTimeLine();
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		}, message);
		
	}



	private void myProfileInfo() {
		client.getMyProfileInfo(new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject json) {
				//Log.d("naveen", json.toString());
				//aTweets.addAll(Tweet.fromJSONArray(json));
				try {
					myScreenName = json.getString("screen_name");
					myProfilePicURL = json.getString("profile_image_url");
					
					//Log.d("naveen", myScreenName);
					//Log.d("naveen", myProfilePicURL);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		});
		
	}
	
	public void populateTimeLine() {
		//aTweets.clear();
		//Toast.makeText(this, "max_id = "+ Tweet.max_id , Toast.LENGTH_SHORT).show();
		client.getHomeTimeline(new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray json) {
				//Log.d("naveen2", json.toString());
				aTweets.addAll(Tweet.fromJSONArray(json));
			}
			@Override
			public void onFailure(Throwable e, String s) {
				Log.d("debug", e.toString());
				Log.d("debug", s.toString());
			}
		}, 10, Tweet.max_id+1);
	}

}
