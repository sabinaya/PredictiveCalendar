package ubicomplab.org.predictivecalendar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	CalendarEvents calEvent;
	Button weatherBut;
	DrawRouteFragment routeFrag;
	Button replan;
	RadioButton driving;
	RadioButton cycling;
	RadioButton walking;
	RadioGroup rgModes;
	Events eventObj = new Events();
	Marker nextEventLocation;
	Marker stMarker;
	Marker destMarker;
	GoogleMap mMap;
	int mMode = 0;
	String distance = "";
	String duration = "";
	static long difference;
	// card flag
	boolean cardStatus = false;
	final int MODE_DRIVING = 0;
	final int MODE_BICYCLING = 1;
	final int MODE_WALKING = 2;
	Map<String, Integer> map = new HashMap<String, Integer>();

	CurrLocation myLocation = new CurrLocation(this);
	LatLngConverter converter = new LatLngConverter(this);

	// Travelling Mode
	String mode = "mode=driving";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		routeFrag = new DrawRouteFragment(this);
		Intent intent = getIntent();
		calEvent = (CalendarEvents) intent.getSerializableExtra("event");

		Log.d("nikki!", "inside oncreate");
		replan = (Button) findViewById(R.id.replanBut);
		replan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");

				startActivity(intent);

			}
		});

		ImageButton getDirections = (ImageButton) findViewById(R.id.direction);
		getDirections.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Navigate to activity showing search editboxes for source
				// and destination
				Intent intent = new Intent(RouteActivity.this,
						SearchActivity.class);
				startActivity(intent);
			}
		});
		map.put("hours", 3600000);
		map.put("mins", 60000);
		map.put("min", 60000);
		map.put("hour", 3600000);
		handleIntent(getIntent());
		getMap(calEvent);

		weatherBut = (Button) findViewById(R.id.button1);
		weatherBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("hellloweather!", "yahooo !!");

				FragmentTransaction fragmentTransaction = getSupportFragmentManager()
						.beginTransaction();
				// fragmentTransaction.replace(R.id.fragment_container,
				// fragment,
				// "NewFragmentTag");
				// fragmentTransaction.addToBackStack(null);
				// fragmentTransaction.commit();
				fragmentTransaction.add(R.id.fragment_container,
						new WeatherActivity());

				fragmentTransaction.commit();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			onSearchRequested();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void getMap(CalendarEvents event) {

		final CalendarEvents eventNow;
		eventNow = event;

		// Getting reference to rb_driving
		driving = (RadioButton) findViewById(R.id.driving);

		// Getting reference to rb_bicylcing
		cycling = (RadioButton) findViewById(R.id.bicycling);

		// Getting reference to rb_walking
		walking = (RadioButton) findViewById(R.id.walking);

		// Getting Reference to rg_modes
		rgModes = (RadioGroup) findViewById(R.id.modes);

		setMap();

		PolylineOptions lineOptions = null;
		eventObj.parseConsecutiveEvents(event);
		CalendarEvents event1;
		CalendarEvents event2;
		LatLng latlng;
		event1 = eventObj.getFirstEvent();
		event2 = eventObj.getSecondEvent();
		difference = eventObj.diffBtwEvents(event1, event2);
		latlng = myLocation.getMyLocation();

		try {
			if (event2 == null) {
				FixMarkers(event1.getLocation(), latlng, null);
				routeFrag.drawRoute(event1, null, latlng, mode);
				lineOptions = routeFrag.getLineOptions();
				drawLineOptions(lineOptions);
				return;
			}
			Log.d("kkkk",
					event1.getLocation() + latlng.toString()
							+ event2.getLocation());
			FixMarkers(event1.getLocation(), latlng, event2.getLocation());

			routeFrag.drawRoute(event1, event2, latlng, mode);
			lineOptions = routeFrag.getLineOptions();
			drawLineOptions(lineOptions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d("nikki!", "inside oncheckedchangeListener");
				mMap.clear();
				PolylineOptions lineOptions = null;
				eventObj.parseConsecutiveEvents(eventNow);
				CalendarEvents event1;
				CalendarEvents event2;
				LatLng latlng;
				event1 = eventObj.getFirstEvent();
				event2 = eventObj.getSecondEvent();
				difference = eventObj.diffBtwEvents(event1, event2);
				latlng = myLocation.getMyLocation();
				try {
					if (event2 == null) {
						Log.d("nikki!", "event2==null");
						FixMarkers(event1.getLocation(), latlng, null);
						routeFrag.drawRoute(event1, null, latlng, mode);
						lineOptions = routeFrag.getLineOptions();
						drawLineOptions(lineOptions);
						return;
					}
					Log.d("kkkk", event1.getLocation() + latlng.toString()
							+ event2.getLocation());
					FixMarkers(event1.getLocation(), latlng,
							event2.getLocation());
					Log.d("nikki!", "after fixing markers");
					routeFrag.drawRoute(event1, event2, latlng, mode);
					lineOptions = routeFrag.getLineOptions();
					drawLineOptions(lineOptions);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return;
	}

	protected void setMap() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				Criteria criteria = new Criteria();
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setSpeedRequired(true);
				String provider = locationManager.getBestProvider(criteria,
						true);
				Location myLocation = locationManager
						.getLastKnownLocation(provider);
				double latitude = myLocation.getLatitude();
				double longitude = myLocation.getLongitude();
				LatLng curr_loc = new LatLng(latitude, longitude);
				mMap.moveCamera(CameraUpdateFactory.newLatLng(curr_loc));
				// mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
				// mMap.addMarker(new MarkerOptions().position(latlng).title(
				// "Current Location!"));
			}
		}
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(0, data, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
		if (arg0 == 0)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.SEARCH_URI, null, null,
					new String[] { query.getString("query") }, null);
		else if (arg0 == 1)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.DETAILS_URI, null, null,
					new String[] { query.getString("query") }, null);
		return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		showLocations(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(1, data, this);
	}

	private void showLocations(Cursor c) {
		MarkerOptions markerOptions = null;
		LatLng position = null;
		mMap.clear();
		while (c.moveToNext()) {
			markerOptions = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));
			markerOptions.position(position);
			markerOptions.title(c.getString(0));
			mMap.addMarker(markerOptions);
		}
		if (position != null) {
			CameraUpdate cameraPosition = CameraUpdateFactory
					.newLatLng(position);
			mMap.animateCamera(cameraPosition);
		}
	}

	protected void FixMarkers(String start, LatLng latlng, String NextLocation)
			throws IOException {
		LatLng origin;
		LatLng nextLocation;
		origin = converter.convertToLatlng(start);

		mMap.clear();
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		destMarker = mMap.addMarker(new MarkerOptions()
				.position(origin)
				.title("EventLocation")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		stMarker = mMap.addMarker(new MarkerOptions().position(latlng).title(
				"Current Location"));

		if (NextLocation != null) {
			nextLocation = converter.convertToLatlng(NextLocation);
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			nextEventLocation = mMap
					.addMarker(new MarkerOptions()
							.position(nextLocation)
							.title("Next Event Location")
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
		}

		if (driving.isChecked()) {
			mode = "mode=driving";
			mMode = 0;

		} else if (cycling.isChecked()) {
			mode = "mode=bicycling";
			mMode = 1;

		} else if (walking.isChecked()) {
			mode = "mode=walking";
			mMode = 2;

		}

		return;
	}

	protected void drawLineOptions(PolylineOptions lineOptions) {
		// Changing the color polyline according to the mode
		if (mMode == MODE_DRIVING)
			lineOptions.color(Color.RED);
		else if (mMode == MODE_BICYCLING)
			lineOptions.color(Color.GREEN);
		else if (mMode == MODE_WALKING)
			lineOptions.color(Color.BLUE);

		Log.d("line options", lineOptions.toString());
		// Drawing polyline in the Google Map for the i-th route
		mMap.addPolyline(lineOptions);
	}

}
