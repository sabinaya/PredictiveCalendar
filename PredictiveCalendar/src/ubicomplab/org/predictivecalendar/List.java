package ubicomplab.org.predictivecalendar;

import java.util.*;

import android.util.Log;

public class List {

	// create a linked list
	LinkedList<CalendarEvents> ll = new LinkedList<CalendarEvents>();
	int index = 0;
	//CalendarEvents nextEvent = new CalendarEvents();

	protected int insertAt(CalendarEvents data, int index) {
		// add event to the linked list
		if(ll.isEmpty())
		{
			ll.add(data);
			return index;
		}
		ll.add(index, data);
		return index;
	}
	protected int getHead() {
		return ll.lastIndexOf(ll);
	}

	protected void printList(int head)
	{
		Log.d("this is head!",Integer.toString(head));
		Log.d("hurray", ll.element().getName());
	}
}