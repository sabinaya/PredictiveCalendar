package ubicomplab.org.predictivecalendar;

import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

public class CalendarActivity extends ActionBarActivity {

	// Variable that holds calendar events
	JSONArray events;

	int index;

	// Linked List
	LinkedList<CalendarEvents> calendarList = new LinkedList<CalendarEvents>();

	// to capture the consecutive events in the calendar
	boolean isFirstTime = true;

	CalendarEvents event;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.activity_cards);
		events = getCalendarData(1000);
		setLinkedList(calendarList);

		Intent i = new Intent(CalendarActivity.this, SampleService.class);
		CalendarActivity.this.startService(i);

		Intent intent = new Intent(this, PredictionActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		this.startActivity(intent);

	}

	private JSONArray getCalendarData(int numDays) {

		calendarList.clear();
		JSONArray result = new JSONArray();

		// Calendar API
		ContentResolver resolver = this.getContentResolver();
		Cursor mCursor;
		final String[] COLS = new String[] { CalendarContract.Instances.TITLE,
				CalendarContract.Instances.BEGIN,
				CalendarContract.Instances.END,
				CalendarContract.Instances.DURATION,
				CalendarContract.Instances.EVENT_LOCATION,
				CalendarContract.Instances._ID };

		// Dates
		Date start = new Date();
		Date end = new Date(start.getTime() + numDays * 86400000);

		// Generates the Calendar URI
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI
				.buildUpon();
		ContentUris.appendId(eventsUriBuilder, start.getTime());
		ContentUris.appendId(eventsUriBuilder, end.getTime());
		Uri eventsUri = eventsUriBuilder.build();

		mCursor = resolver.query(eventsUri, COLS, null, null, "begin ASC");

		if (mCursor != null) {
			while (mCursor.moveToNext()) {

				String eventName = mCursor.getString(0);
				Date startTime = new Date(mCursor.getLong(1));
				Date endTime = new Date(mCursor.getLong(2));
				String eventLocation = mCursor.getString(4);
				int id = mCursor.getInt(5);

				calendarList.add(new CalendarEvents(eventName, startTime
						.getTime(), endTime.getTime(), eventLocation, id));

				try {
					JSONObject calendarEntry = new JSONObject();
					calendarEntry.put("name", eventName);
					calendarEntry.put("start", startTime.getTime());
					calendarEntry.put("end", endTime.getTime());
					calendarEntry.put("location", eventLocation);
					result.put(calendarEntry);

				} catch (Exception ex) {
					Log.e("My App",
							"Problem occurred while getting calendar information: "
									+ ex.getMessage());
				}
			}
		}
		return result;
	}

	protected void setLinkedList(LinkedList<CalendarEvents> list) {
		// To access the global linked list
		Globals g = Globals.getInstance();
		g.setData(list);

	}

}
