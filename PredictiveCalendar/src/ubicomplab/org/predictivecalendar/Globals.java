package ubicomplab.org.predictivecalendar;

import java.util.LinkedList;

import android.app.Application;
import android.util.Log;

public class Globals extends Application {
	private static Globals instance;
	// Linked List
	private LinkedList<CalendarEvents> calList = new LinkedList<CalendarEvents>();

	public LinkedList<CalendarEvents> getData() {
		Log.d("inside getdata", "there");
		return this.calList;
	}

	public void setData(LinkedList<CalendarEvents> data) {
		this.calList = data;
		Log.d("inside setdata", "there");
		return;
	}

	public void printlList() {
		for (CalendarEvents eventCursor : calList) {
			Log.d("good", eventCursor.getName());
			Log.d("good:Location",
					Integer.toString(calList.indexOf(eventCursor)));
		}
		return;
	}

	public void clearList() {
		calList.clear();
		return;
	}

	public int getIndex(CalendarEvents event) {
		int index = 0;
		for (CalendarEvents eventCursor : calList) {
			if (eventCursor.getId() == (event.getId())) {
				index = calList.indexOf(eventCursor);
			}
		}
		return index;

	}

	public CalendarEvents getDataAtIndex(int index) {
		return calList.get(index);

	}

	public CalendarEvents getNextEvent(CalendarEvents event) {
		if (getIndex(event) + 1 < calList.size()) {
			int index = getIndex(event);
			return calList.get(++index);
		} else
			return null;
	}

	public static synchronized Globals getInstance() {
		if (instance == null) {
			instance = new Globals();
		}
		return instance;
	}
}
