package com.messenger.gps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/*
 * In this series we fetch tweets on a user-chosen search term by
 * calling one of Twitter's Web services URLs.
 * The results are returned as JSON which we parse and display.
 * We use AsyncTask to carry out the fetching in the background
 * but still display the results within the UI.
 */

public class MainActivity extends Activity {

	Button showmap, getlist, write;
	private MessageAdapter messageAdapter;
	ListView lv;
	List<JSONSchema> list;
	String WHICH_CLASS_CALL = null;
	boolean LISTVIEW_SET = false;
	String searchURL = "http://192.168.3.102:3000/tasks.json"; // "http://192.168.0.104:3000/tasks.json";

	// field to update with retrieved messages
	// private TextView messageDisplay;
	// ListView messagesList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getlist = (Button) findViewById(R.id.btn_getlist);
		write = (Button) findViewById(R.id.btn_write);
		showmap = (Button) findViewById(R.id.btn_showmap);

		write.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, JSONwriter.class);
				startActivity(intent);
			}
		});

		showmap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WHICH_CLASS_CALL = "Maps";
				new GetMessages().execute(searchURL);
			}
		});

		getlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
				if (wm.isWifiEnabled()) {
					try {
						WHICH_CLASS_CALL = "MainActivity";
						new GetMessages().execute(searchURL);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					Toast.makeText(MainActivity.this, "No WiFi Connection",
							Toast.LENGTH_SHORT).show();
			}
		});

		if (LISTVIEW_SET==true) {
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					JSONSchema obj = list.get(position);
					Log.d("listview listener", obj.getName());
				}
			});
		}

	}

	/*
	 * AsyncTask class to fetch messages in background thread - receives URL
	 * search string and updates UI on calling execute - specify parameter and
	 * return types as strings - parameter is twitter URL search string - result
	 * is retrieved messages
	 */
	private class GetMessages extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(MainActivity.this, "Loading",
					"Fetching Data");
		}

		@Override
		protected String doInBackground(String... messageURL) {
			// start building result which will be json string
			StringBuilder messageFeedBuilder = new StringBuilder();
			// should only be one URL, receives array
			for (String searchURL : messageURL) {
				HttpClient messageClient = new DefaultHttpClient();
				try {
					// pass search URL string to fetch
					HttpGet messageGet = new HttpGet(searchURL);
					// execute request
					HttpResponse messageResponse = messageClient
							.execute(messageGet);
					// check status, only proceed if ok
					StatusLine searchStatus = messageResponse.getStatusLine();
					if (searchStatus.getStatusCode() == 200) {
						// get the response
						HttpEntity messageEntity = messageResponse.getEntity();
						InputStream messageContent = messageEntity.getContent();
						// process the results
						InputStreamReader messageInput = new InputStreamReader(
								messageContent);
						BufferedReader messageReader = new BufferedReader(
								messageInput);
						String lineIn;
						while ((lineIn = messageReader.readLine()) != null) {
							messageFeedBuilder.append(lineIn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// return result string
			// Log.d("testing json", messageFeedBuilder.toString());
			return messageFeedBuilder.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				// why do i have to write TypeReference<arrayList> instead of
				// JSONSchema
				ObjectMapper obj = new ObjectMapper();
				list = obj.readValue(result,
						new TypeReference<ArrayList<JSONSchema>>() {
						});

				// Log.d("list testing", list.get(5).getName().toString());
				// messageAdapter.addAll(list);
				if (WHICH_CLASS_CALL == "MainActivity") {
					messageAdapter = new MessageAdapter(
							getApplicationContext(), list);
					lv = (ListView) findViewById(R.id.json_listView);
					lv.setAdapter(messageAdapter);
					LISTVIEW_SET = true;
				}
				if (WHICH_CLASS_CALL == "Maps") {
					Bundle b = new Bundle();
					// b.putString("class", "JSONwriter");
					// b.putString("nick", nick);
					Intent intent = new Intent(MainActivity.this, Maps.class);
					intent.putExtras(b);
					startActivity(intent);
				}

				// JSONSchema js = obj.readValue(result, JSONSchema.class);
				// Log.d("TESTING JSobj", "HELLO");
				pd.dismiss();
			} catch (Exception e) {
				Log.d("TESTING JSexcept", e.toString());
				pd.dismiss();
			}
		}
	}
}
