package ubicomplab.org.predictivecalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class SearchActivity extends Activity {

	AutoCompleteTextView place1;
	AutoCompleteTextView place2;
	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;
	HashMap<String, String> placeData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Intent intent = getIntent();
		// Getting a reference to the AutoCompleteTextView
		place1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		place1.setThreshold(1);

		place2 = (AutoCompleteTextView) findViewById(R.id.AutoCompleteTextView2);
		place2.setThreshold(1);
		
		Button search = (Button)findViewById(R.id.button1);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String url = "http://maps.google.com/maps?f=d&daddr="
						+ "40.433114" + "," + "-79.924537" + "&dirflg=d";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(url));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);
				
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(SearchActivity.this,SearchInMap.class);
//				intent.putExtra("Place1", place1.getText().toString());
//				intent.putExtra("Place2", place2.getText().toString());
//				startActivity(intent);
				
				
				
			}
		});
		
		// Adding textchange listener
		place1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Creating a DownloadTask to download Google Places matching
				// "s"
				placesDownloadTask = new DownloadTask();

				// Getting url to the Google Places Autocomplete api
				String url = getAutoCompleteUrl(s.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		// Setting an item click listener for the AutoCompleteTextView dropdown
		// list
		place1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long id) {

				placeData = (HashMap<String, String>) arg0.getItemAtPosition(index);
				Log.d("WowPlace", placeData.toString());
				place1.setText(placeData.get("description"));
				//places.setText((CharSequence) arg0.getItemAtPosition(index));
			}

		});
		
		place2.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
				// Creating a DownloadTask to download Google Places matching
				// "s"
				placesDownloadTask = new DownloadTask();

				// Getting url to the Google Places Autocomplete api
				String url = getAutoCompleteUrl(s.toString());

				// Start downloading Google Places
				// This causes to execute doInBackground() of DownloadTask class
				placesDownloadTask.execute(url);
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		place2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long id) {

				placeData = (HashMap<String, String>) arg0.getItemAtPosition(index);
				Log.d("WowPlace", placeData.toString());
				place2.setText(placeData.get("description"));
				//places.setText((CharSequence) arg0.getItemAtPosition(index));
			}

		});
	}

	private String getAutoCompleteUrl(String place) {

		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyAM49m2NOQUXYCooixkGpnvvxgJSrssXvA";

		// place to be be searched
		String input = "input=" + place;

		// place type to be searched
		String types = "types=geocode";

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = input + "&" + types + "&" + sensor + "&" + key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
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

		private int downloadType = 0;

		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask for parsing Google Places
			placesParserTask = new ParserTask();

			// Start parsing google places json data
			// This causes to execute doInBackground() of ParserTask class
			placesParserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<HashMap<String, String>> list = null;

			try {
				jObject = new JSONObject(jsonData[0]);

				PlaceJSONParser placeJsonParser = new PlaceJSONParser();
				// Getting the parsed data as a List construct
				list = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			place1.setAdapter(adapter);

			place2.setAdapter(adapter);
		}
	}

}
