package com.messenger.gps;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JSONwriter extends Activity {

	EditText enick, emessage;
	Button submit, gpslocation;
	GPSTracker gps;
	int FLAG = 0;
	double longitude, latitude;
	String nick = "", message = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_write);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		enick = (EditText) findViewById(R.id.txt_Nick);
		emessage = (EditText) findViewById(R.id.txt_Message);
		submit = (Button) findViewById(R.id.btn_submit);
		gpslocation = (Button) findViewById(R.id.btn_getGPSLocation);

		gpslocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gps = new GPSTracker(JSONwriter.this);
				if (gps.canGetLocation()) {
					latitude = gps.getLatitude();
					longitude = gps.getLongitude();
					if (longitude != 0 || latitude != 0)
						FLAG = 1;
				} else {
					gps.showSettingsAlert();
				}
			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
				
				if (wm.isWifiEnabled()) {
					if (FLAG == 1) {
						if (enick.getText().toString().length()!=0 && emessage.getText().toString().length()!=0) {
							try {
								nick = enick.getText().toString();
								message = emessage.getText().toString();
								new SetMessages().execute();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else
							Toast.makeText(JSONwriter.this,
									"Please enter Nick and Message",
									Toast.LENGTH_LONG).show();
					} else
						Toast.makeText(JSONwriter.this,
								"Press 'Get GPS Location' to get coordinates",
								Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(JSONwriter.this, "No WiFi Connection",
							Toast.LENGTH_SHORT).show();
			}
		});
	}

	private class SetMessages extends AsyncTask<Void, Void, String> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(JSONwriter.this, "Loading",
					"Messing Sending!");
		}

		@Override
		protected String doInBackground(Void... arg0) {

			JSONObject jdetails = new JSONObject();
			JSONObject jtask = new JSONObject();
			String result = null;
			try {
				jdetails.put("name", nick);
				jdetails.put("message", message);
				jdetails.put("longitude", String.valueOf(longitude));
				jdetails.put("latitude", String.valueOf(latitude));
				jtask.put("task", jdetails);
				// Log.d("json write", jtask.toString());

				DefaultHttpClient httpclient = new DefaultHttpClient();

				// url with the post data
				HttpPost httpost = new HttpPost(
						"http://192.168.3.102:3000/tasks.json");
				// "http://192.168.0.104:3000/tasks.json");

				// passes the results to a string builder/entity
				StringEntity se = new StringEntity(jtask.toString());

				// sets the post request as the resulting string
				httpost.setEntity(se);
				// sets a request header so the page receiving the request
				// will know what to do with it
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");

				// Handles what is returned from the page
				ResponseHandler responseHandler = new BasicResponseHandler();
				result = httpclient.execute(httpost, responseHandler)
						.toString();
				// result gets
				// {"created_at":"2014-01-29T09:40:53Z","id":20,"latitude":"666.000}
				// etc

			} catch (JSONException e) {
				Log.d("Exception", "block");
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// return null;
			return result;
		}

		protected void onPostExecute(String result) {
			Log.d("hhtpexecute", result);
			pd.dismiss();

			Bundle b = new Bundle();
			b.putString("class", "JSONwriter");
			b.putString("nick", nick);
			b.putString("message", message);
			b.putDouble("longitude", longitude);
			b.putDouble("latitude", latitude);
			Intent intent = new Intent(JSONwriter.this, Maps.class);
			intent.putExtras(b);
			startActivity(intent);
		}
	}
}