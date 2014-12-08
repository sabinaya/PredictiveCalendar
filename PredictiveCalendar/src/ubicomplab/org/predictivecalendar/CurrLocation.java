package ubicomplab.org.predictivecalendar;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class CurrLocation {
	Context mContext;
	LatLng latlng;

	public CurrLocation(Context mContext) {
		this.mContext = mContext;
	}

	protected LatLng getMyLocation() {

		double latitude;
		double longitude;

		LocationManager locationManager;
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(true);
		String provider = locationManager.getBestProvider(criteria, true);
		provider = "network";

		Location myLocation = locationManager.getLastKnownLocation(provider);

		if (myLocation != null) {
			latitude = myLocation.getLatitude();
			longitude = myLocation.getLongitude();
			LatLng latlng = new LatLng(latitude, longitude);
			return latlng;
		}
		else
		{
			Toast.makeText(mContext, "Please turn on the GPS or wifi",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
			return null;
		}

	}

	private final LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			// myLocation = location;
			// Log.d("getting the location", myLocation.toString());
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	};
}
