package ubicomplab.org.predictivecalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import android.util.Log;

public class Events {

	CalendarEvents event1;
	CalendarEvents event2;
	LinkedList<CalendarEvents> calList;

	protected void parseConsecutiveEvents(CalendarEvents event) {
		CalendarEvents nextEvent = null;
		Globals g = Globals.getInstance();
		calList = g.getData();
		if (g.getIndex(event) + 1 < g.getData().size())
			nextEvent = g.getNextEvent(event);
		else
			nextEvent = null;
		this.event1 = event;
		this.event2 = nextEvent;
		return;
	}

	protected CalendarEvents getFirstEvent() {
		if (event1 == null) {
			return null;
		}
		Log.d("tang!return", event1.getName());
		return event1;
	}

	protected CalendarEvents getSecondEvent() {
		if (event2 == null) {
			return null;
		}
		Log.d("tang!return", event2.getName());
		return event2;
	}

	protected long diffBtwEvents(CalendarEvents event, CalendarEvents nextEvent) {
		long difference;
		long event1EndTime;
		long event2StartTime;
		if (nextEvent != null) {
			event1EndTime = event.getEndTime();
			event2StartTime = nextEvent.getStartTime();
			difference = event2StartTime - event1EndTime;
			return difference;
		}
		return 0;
	}

	/**
	 * Return date in specified format.
	 * 
	 * @param milliSeconds
	 *            Date in milliseconds
	 * @param dateFormat
	 *            Date format
	 * @return String representing date in specified format
	 */
	protected String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}
}
