package ubicomplab.org.predictivecalendar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class weather {

	Handler handler;
	Context context;
	getCity myCity;
	CurrLocation myLocation;
	LatLng myLatLng;
	String myLoc;
	boolean status;

	public weather(Context context) {

		this.context = context;
		handler = new Handler();
	}

	protected boolean giveWeather(String place1, String date)
			throws InterruptedException {

		myLocation = new CurrLocation(context);
		myCity = new getCity(context);
		myLatLng = myLocation.getMyLocation();
		try {
			myLoc = myCity.getMyCity(myLatLng);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (place1 != null)
			updateWeatherData(place1, date);
		else
			updateWeatherData(myLoc, date);
		return status;

	}

	private void updateWeatherData(final String city, final String date)
			throws InterruptedException {

		Runnable r = new Runnable() {
			public void run() {
				final JSONObject json = FetchWeather.getJSON(context, city,
						date);
				if (json == null) {
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(
									context,
									context.getString(R.string.place_not_found),
									Toast.LENGTH_LONG).show();
						}
					});
				} else {
					handler.post(new Runnable() {
						public void run() {
							renderWeather(json);
							Log.d("onlineweather", json.toString());
						}
					});
				}
			}
		};

		Thread t = new Thread(r);
		t.start();
		t.join();

	}

	private void renderWeather(JSONObject json) {

		try {

			JSONObject details = json.getJSONObject("data");
			JSONObject weather = details.getJSONArray("weather").getJSONObject(
					0);
			Log.d("dispweatherseperate!", details.toString());
			Log.d("dispweatherseperate-----", weather.toString());
			JSONObject dataType = weather.getJSONArray("hourly").getJSONObject(
					1);
			Log.d("dispweatherseperate+++", dataType.toString());
			Log.d("dispweatherseperate+++weathercode",
					Integer.toString(dataType.getInt("weatherCode")));
			status = checkCondition(dataType.getInt("weatherCode"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkCondition(int weatherCode) throws IOException {
		Log.d("disp inside checkCondition", Integer.toString(weatherCode));
		String str = "";
		String[] data = null;
		boolean weatherStatus = true;
		InputStream is = context.getResources().openRawResource(
				R.drawable.wwoconditioncodes);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		if (is != null) {
			while ((str = reader.readLine()) != null) {
				data = str.split("\\*");
 				int code = Integer.parseInt(data[0]);
				Log.d("disp code from file!", Integer.toString(code));
				if (weatherCode == code) {
					weatherStatus = Boolean.parseBoolean(data[2]);
					Log.d("dispfiledata", data[2]);
					break;
				}

				else {

				}

			}
		}
		is.close();
		Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show();

		return weatherStatus;
	}

}
