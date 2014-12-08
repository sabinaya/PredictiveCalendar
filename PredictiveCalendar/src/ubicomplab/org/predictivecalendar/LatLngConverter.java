package ubicomplab.org.predictivecalendar;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class LatLngConverter {
	
	Context context;
	List<Address> list;
	Address add;
	double lat;
	double lng;
	public LatLngConverter(Context context)
	{
		this.context = context;
	}
	
	protected LatLng convertToLatlng(String address) throws IOException
	{
		
		Geocoder gc = new Geocoder(context);
		list = gc.getFromLocationName(address, 1);
		add = list.get(0);
		lat = add.getLatitude();
		lng = add.getLongitude();
		LatLng latlng = new LatLng(lat, lng);
		Log.d("llll", context.toString());
		Log.d("llll", latlng.toString());
		Log.d("llll", address);
		return latlng;
		
	}

}
