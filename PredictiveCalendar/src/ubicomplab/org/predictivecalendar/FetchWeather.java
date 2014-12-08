package ubicomplab.org.predictivecalendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class FetchWeather {

	private static final String OPEN_WEATHER_MAP_API = "http://api.worldweatheronline.com/premium/v1/weather.ashx?key=a2e0a2fed7bacea08d0d44e31b8350866ca74736&";

	public static JSONObject getJSON(Context context, String pen, String date) {
		try {
			StringBuffer buff = new StringBuffer(OPEN_WEATHER_MAP_API);
			buff.append("q=" + pen + "&");
			if(date != null)
				buff.append("date=" + date + "&");
			buff.append("format=json");
			URL url = new URL(buff.toString());
			Log.d("weatherurl", buff.toString());
			Log.d("weatherurl---", url.toString());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			if (connection != null) {
				Log.d("weatherurl---+++connection not null",
						connection.toString());
			}
			// connection.addRequestProperty("x-api-key",
			// context.getString(R.string.bd4eb9b20f32ffbc81e3f09bf22c3041));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			StringBuffer json = new StringBuffer(1024);
			String tmp = "";
			while ((tmp = reader.readLine()) != null)
				json.append(tmp).append("\n");
			reader.close();

			JSONObject data = new JSONObject(json.toString());

			// // This value will be 404 if the request was not
			// // successful
			// if (data.getInt("cod") != 200) {
			// return null;
			// }

			return data;
		} catch (Exception e) {
			return null;
		}
	}
}