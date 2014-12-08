package ubicomplab.org.predictivecalendar;

import java.io.IOException;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class WeatherActivity extends Fragment {

	Handler handler;
	TextView weatherData;
	getCity myCity;
	CurrLocation myLocation;
	LatLng myLatLng;
	String myLoc;

	public WeatherActivity() {

		handler = new Handler();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.activity_weather, container,
				false);
		myLocation = new CurrLocation(getActivity());
		myCity = new getCity(getActivity());
		myLatLng = myLocation.getMyLocation();
		try {
			myLoc = myCity.getMyCity(myLatLng);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateWeatherData(myLoc);
		weatherData = (TextView) rootView.findViewById(R.id.textView1);
		// Inflate the layout for this fragment
		return rootView;
	}

	private void updateWeatherData(final String city) {
		new Thread() {
			public void run() {
				final JSONObject json = FetchWeather.getJSON(getActivity(),
						city, null);
				if (json == null) {
					handler.post(new Runnable() {
						public void run() {
							Toast.makeText(
									getActivity(),
									getActivity().getString(
											R.string.place_not_found),
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
		}.start();
	}

	private void renderWeather(JSONObject json) {

		try {

			JSONObject details = json.getJSONObject("data");
			JSONObject weather = details.getJSONArray("weather").getJSONObject(0);
			Log.d("dispweather!", details.toString());
			Log.d("dispweather-----", weather.toString());
			JSONObject dataType = weather.getJSONArray("hourly").getJSONObject(1);
			Log.d("dispweather+++", dataType.toString());
			JSONObject desc = dataType.getJSONArray("weatherDesc").getJSONObject(0);
			Log.d("dispweather====", desc.toString());
			weatherData.setText("weather Desc: "
					+ desc.getString("value") + "temp(c)" + dataType.getInt("tempC") + dataType.getInt("tempF"));

		} catch (Exception e) {
			Log.e("SimpleWeather",
					"One or more fields not found in the JSON data");
		}
	}

}
