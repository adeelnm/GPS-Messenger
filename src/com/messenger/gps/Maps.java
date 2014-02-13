package com.messenger.gps;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends FragmentActivity {
	private GoogleMap map;
	double latitude;
	double longitude;
	String nick, message, nclass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		Bundle getData = getIntent().getExtras();
		nclass = getData.getString("class");
		nick = getData.getString("nick");
		message = getData.getString("message");
		latitude = getData.getDouble("latitude");
		longitude = getData.getDouble("longitude");

		Log.d("nick", nick);
		Log.d("message", message);
		Log.d("class" , nclass);
		Log.d("latitude", String.valueOf(latitude));
		Log.d("longitude", String.valueOf(longitude));
		
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
		//if (nclass == "JSONwriter") {
			map.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude)).title(nick)
					.snippet(message));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_sethybrid:
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.menu_setsatelite:
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		case R.id.menu_setterrain:
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		case R.id.menu_setnormal:
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.menu_getcurrentlocation:
			map.setMyLocationEnabled(true);
			break;
		}
		return true;
	}
}
