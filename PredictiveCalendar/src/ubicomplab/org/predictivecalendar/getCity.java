package ubicomplab.org.predictivecalendar;

import java.io.IOException;
import java.util.Locale;
import java.util.List;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class getCity {

	Context mContext;

	public getCity(Context context) {
		this.mContext = context;
	}

	protected String getMyCity(LatLng latlng) throws IOException {
		if(latlng != null)
		{
		Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
		List<Address> addresses = gcd.getFromLocation(latlng.latitude,
				latlng.longitude, 1);
		if (addresses.size() > 0)
			return addresses.get(0).getLocality();
		return null;
		}
		return null;
	}
	
	protected String getMyCityFromAdd(String location) throws IOException {
		
		Geocoder gc = new Geocoder(mContext);
		List<Address> list;
		Address add;
		double lat;
		double lng;
		list = gc.getFromLocationName(location, 1);
		add = list.get(0);
		lat = add.getLatitude();
		lng = add.getLongitude();
		LatLng latlng = new LatLng(lat, lng);
		Log.d("llll", mContext.toString());
		Log.d("llll", latlng.toString());
		Log.d("llll", location);
		String city = getMyCity(latlng);
		return city;
	}
}

