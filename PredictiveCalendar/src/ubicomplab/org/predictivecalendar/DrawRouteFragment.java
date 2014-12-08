package ubicomplab.org.predictivecalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawRouteFragment {

	Context context;
	// Traveling mode
	String mode = "mode=driving";
	PolylineOptions localLineOptions = new PolylineOptions();
	String distance = "";
	String duration = "";
	long durat;
	long difference;
	int activityType = 0;
	weather wea;
	String check = "Weather";
	ProblemType weather;

	public DrawRouteFragment(Context context) {
		this.context = context;
	}

	LatLngConverter converter = new LatLngConverter(context);

	protected void drawRoute(CalendarEvents start, CalendarEvents NextLocation,
			LatLng latlng, String mode) throws IOException {

		activityType = 1; // when activity which displays the deck of cards
							// calls

		Events eventObj = new Events();

		this.mode = mode;

		LatLng nextLocation;

		if (NextLocation != null) {
			nextLocation = convertToLatlng(NextLocation.getLocation());

		}

		difference = eventObj.diffBtwEvents(start, NextLocation);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(start);

		return;
	}

	protected void drawRoute(CalendarEvents start, CalendarEvents NextLocation,
			String mode) throws IOException {

		Log.d("nikki!", "inside drawRoute");
		activityType = 2; // When the activity which is Checking the status
							// calls

		Events eventObj = new Events();

		this.mode = mode;

		LatLng nextLocation;

		if (NextLocation != null) {
			nextLocation = convertToLatlng(NextLocation.getLocation());

		}

		difference = eventObj.diffBtwEvents(start, NextLocation);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(start);

		return;
	}

	protected LatLng convertToLatlng(String address) throws IOException {
		List<Address> list;
		Address add;
		double lat;
		double lng;
		Log.d("context inside Geocoder", context.toString());
		Geocoder gc = new Geocoder(context);
		list = gc.getFromLocationName(address, 1);
		try {
			if (list != null && list.size() > 0) {
				add = list.get(0);
				lat = add.getLatitude();
				lng = add.getLongitude();
				LatLng latlng = new LatLng(lat, lng);
				return latlng;
			}
		} catch (Exception e) {
			Toast tst = Toast.makeText(context,
					"Please check your gps settings", Toast.LENGTH_LONG);
			tst.show();
		}
		return null;

	}

	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		if (activityType == 2)
			Log.d("nikki!", "inside getDirectionsUrl");
		if (origin != null || dest != null) {
			Log.d("hey!!", "inside getDirectionsUrl");
			// Origin of route
			String str_origin = "origin=" + origin.latitude + ","
					+ origin.longitude;

			// Destination of route
			String str_dest = "destination=" + dest.latitude + ","
					+ dest.longitude;

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			String parameters = str_origin + "&" + str_dest + "&" + sensor
					+ "&" + mode;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/directions/"
					+ output + "?" + parameters;

			return url;
		}
		return null;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {

		if (activityType == 2)
			Log.d("nikki!", "inside downloadUrl");
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
	private class DownloadTask extends AsyncTask<CalendarEvents, Void, String> {

		LatLng origin;
		LatLng latlng;
		CurrLocation myLocation = new CurrLocation(context);
		CalendarEvents currEvent;
		CalendarEvents nextEvent;

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(CalendarEvents... event) {

			// android.os.Debug.waitForDebugger();

			if (activityType == 2)
				Log.d("nikki!", "inside doinbackground");

			currEvent = event[0];
			Events eventObj = new Events();
			eventObj.parseConsecutiveEvents(currEvent);
			nextEvent = eventObj.getSecondEvent();

			try {
				origin = convertToLatlng(event[0].getLocation());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (activityType == 1) {
				latlng = myLocation.getMyLocation();
			} else {
				try {
					latlng = convertToLatlng(nextEvent.getLocation());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			// Getting URL to the Google Directions API

			if (origin != null && latlng != null) {
				String url = getDirectionsUrl(origin, latlng);

				// For storing data from web service
				String data = "";

				try {
					// Fetching the data from web service
					data = downloadUrl(url);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
				return data;
			}
			return null;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();
			if (result != null) {
				if (activityType == 2)
					Log.d("nikki!", "result is not null after postexecute");
				currEvent.setResult(result);

				// Invokes the thread for parsing the JSON data
				parserTask.execute(currEvent);
			}

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask
			extends
			AsyncTask<CalendarEvents, Integer, List<List<HashMap<String, String>>>> {
		CalendarEvents currEvent;
		CalendarEvents nextEvent;

		Events eventObj = new Events();

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				CalendarEvents... event) {

			if (activityType == 2)
				Log.d("nikki!", "inside Parsertask doinbck");

			android.os.Debug.waitForDebugger();

			currEvent = event[0];
			eventObj.parseConsecutiveEvents(currEvent);
			nextEvent = eventObj.getSecondEvent();
			String jsonData;
			jsonData = event[0].getResult();
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData);
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

			final PolylineOptions lineOptions = new PolylineOptions();
			getCity eventCity = new getCity(context);
			ArrayList<LatLng> points = null;
			String date;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);
					Log.d("result", point.toString());
					if (j == 0) { // Get distance from the list
						distance = (String) point.get("distance");
						continue;
					} else if (j == 1) { // Get duration from the list
						duration = (String) point.get("duration");
						continue;
					}
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);

				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5);
				setLocalLineOptions(lineOptions);
				if (result.size() < 1) {
					Toast.makeText(context, "No Points", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (activityType == 2) {
					boolean weatherStatus = true;
					durat = calculateDuration(distance, duration);
					difference = eventObj.diffBtwEvents(currEvent, nextEvent);

					if (difference >= durat) {
						nextEvent.setStatus(true);

					} else {
						nextEvent.setStatus(false);
						ShowNotification show = new ShowNotification();
						show.showNotification(context, nextEvent);
					}

					wea = new weather(context);

					date = eventObj.getDate(currEvent.getStartTime(),
							"yyyy-MM-dd");

					char y = date.charAt(3);
					int yi = (int) (y - '0');

					for (int j = 0; j < 3; j++) {
						yi = yi - 1;
						StringBuilder change = new StringBuilder(date);
						change.setCharAt(3, (char) (yi + '0'));
						date = change.toString();
						try {
							try {
								weatherStatus = wea.giveWeather(eventCity
										.getMyCityFromAdd(currEvent
												.getLocation()), date);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (!weatherStatus) {
						currEvent.setProblemType(weather.Weather);
					}
					if (activityType == 2)
						Log.d("nikki!", "all right");
				}
			}
			return;
		}

	}

	protected long calculateDuration(String distance, String duration) {

		long dura = 0;
		Log.d("hey!!", "inside calculateDuration");
		String[] splittedDuration;
		splittedDuration = duration.split(" ");
		for (int index = 0; index < splittedDuration.length; index++) {
			if (index % 2 == 0)
				dura += unitConversion(splittedDuration[index],
						splittedDuration[index + 1]);
		}
		return dura;

	}

	private long unitConversion(String num, String unit) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("hours", 3600000);
		map.put("mins", 60000);
		map.put("min", 60000);
		map.put("hour", 3600000);
		if (num.length() != 0 && unit.length() != 0) {
			int number = Integer.parseInt(num);
			int value = map.get(unit);
			return (number * value);
		} else {
			return 0;
		}

	}

	protected void setLocalLineOptions(PolylineOptions lineOptions) {
		localLineOptions = lineOptions;
	}

	protected PolylineOptions getLineOptions() {
		Log.d("correct!", localLineOptions.toString());
		return localLineOptions;
	}

	protected long getDuration() {
		Log.d("return Duration!! yahoo", Long.toString(durat));
		return durat;
	}

}
