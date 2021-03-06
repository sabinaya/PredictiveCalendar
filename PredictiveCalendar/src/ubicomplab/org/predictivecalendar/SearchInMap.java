package ubicomplab.org.predictivecalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class SearchInMap extends FragmentActivity {

	GoogleMap map;
	ArrayList<LatLng> markerOptions;
	String origin;
	String dest;
	LatLngConverter converter;
	LatLng latlng1;
	LatLng latlng2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_in_map);
		converter = new LatLngConverter(this);
		Bundle bundle = getIntent().getExtras();
		origin = bundle.getString("Place1");
		dest = bundle.getString("Place2");
		
		try {
			latlng1 = converter.convertToLatlng(origin);
			latlng2 = converter.convertToLatlng(dest);
			Log.d("not null latlng1", latlng1.toString());
			Log.d("not null latlng2", latlng2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d("not null latlng1", latlng1.toString());
		Log.d("not null latlng2", latlng2.toString());
		
		this.markerOptions = new ArrayList<LatLng>();
		SupportMapFragment fm = (SupportMapFragment) this
				.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
		this.map = fm.getMap();
		this.map.setMyLocationEnabled(true);
		this.map.moveCamera(CameraUpdateFactory.newLatLng(latlng1));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));

		
		this.markerOptions.add(latlng1);
		this.markerOptions.add(latlng2);
		MarkerOptions option1 = new MarkerOptions();
		MarkerOptions option2 = new MarkerOptions();
		option1.position(latlng1);
		option2.position(latlng2);
		SearchInMap.this.map.addMarker(option1.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		
		SearchInMap.this.map.addMarker(option2.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		
		if (SearchInMap.this.markerOptions.size() >= 2) {
			LatLng origin = SearchInMap.this.markerOptions.get(0);
			LatLng dest = SearchInMap.this.markerOptions.get(1);
			String url = SearchInMap.this
					.getDirectionsUrl(origin, dest);
			DownloadTask downloadTask = new DownloadTask();

			// Start downloading json data from Google Directions API
			downloadTask.execute(url);
		}

		this.map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				// TODO Auto-generated method stub
				if (SearchInMap.this.markerOptions.size() > 1) {
					SearchInMap.this.markerOptions.clear();
					SearchInMap.this.map.clear();
				}

				SearchInMap.this.markerOptions.add(point);
				MarkerOptions options = new MarkerOptions();
				options.position(point);

				if (SearchInMap.this.markerOptions.size() == 1) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				} else if (SearchInMap.this.markerOptions.size() == 2) {
					options.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				}
				SearchInMap.this.map.addMarker(options);

				if (SearchInMap.this.markerOptions.size() >= 2) {
					LatLng origin = SearchInMap.this.markerOptions.get(0);
					LatLng dest = SearchInMap.this.markerOptions.get(1);
					String url = SearchInMap.this
							.getDirectionsUrl(origin, dest);
					DownloadTask downloadTask = new DownloadTask();

					// Start downloading json data from Google Directions API
					downloadTask.execute(url);
				}

			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		Log.w("onPostCreat()", "success");
		super.onPostCreate(savedInstanceState);
	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}

	
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = SearchInMap.this.downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			if (result.size() < 1) {
				Toast.makeText(SearchInMap.this.getBaseContext(), "No Points",
						Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if (j == 0) { // Get distance from the list
						distance = point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = point.get("duration");
						continue;
					}
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);
			}
			// Drawing polyline in the Google Map for the i-th route
			SearchInMap.this.map.addPolyline(lineOptions);
		}
	}

	@Override
	protected void onStop() {
		Log.w("Stop", "App stopped");

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.w("Destroy", "App destoryed");

		super.onDestroy();
	}

}
