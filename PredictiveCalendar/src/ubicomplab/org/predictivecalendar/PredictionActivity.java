package ubicomplab.org.predictivecalendar;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class PredictionActivity extends FragmentActivity {

	long timeBetEvents;

	String distance = "";
	String duration = "";

	static long difference;

	Map<String, Integer> map = new HashMap<String, Integer>();

	Marker nextEventLocation;
	Marker stMarker;
	Marker destMarker;
	GoogleMap mMap;

	CalendarEvents nextEvent = new CalendarEvents();
	Events eventObj = new Events();

	// List of cards
	ArrayList<Card> cards = new ArrayList<Card>();

	// card flag
	boolean cardStatus = false;

	CardArrayAdapter mCardArrayAdapter;

	RadioButton driving;
	RadioButton cycling;
	RadioButton walking;
	RadioGroup rgModes;
	int mMode = 0;
	final int MODE_DRIVING = 0;
	final int MODE_BICYCLING = 1;
	final int MODE_WALKING = 2;

	CurrLocation myLocation = new CurrLocation(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cards);
		Globals g = Globals.getInstance();
		LinkedList<CalendarEvents> calendarList = g.getData();
		check(calendarList);

		// tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);

		map.put("hours", 3600000);
		map.put("mins", 60000);
		map.put("min", 60000);
		map.put("hour", 3600000);
	}

	protected void check(LinkedList<CalendarEvents> events) {

		for (CalendarEvents event : events) {
			createCard(event);
			Globals g = Globals.getInstance();
			Log.d("indexisthis", Integer.toString(g.getIndex(event)));
		}
		displayCards();
	}

	protected void createCard(CalendarEvents event) {

		final CalendarEvents eventNow;
		eventNow = event;

		// Create a Card
		Card card = new Card(this);

		// Create a CardHeader
		CardHeader header = new CardHeader(this);

		// Add Header to card
		header.setTitle("Event: " + event.getName());

		card.setTitle("Location: " + event.getLocation() + "\n"
				+ "Event Start Date and Time : "
				+ eventObj.getDate(event.getStartTime(), "dd/MM/yyyy hh:mm")
				+ "\n" + "Event End Date and Time : "
				+ eventObj.getDate(event.getEndTime(), "dd/MM/yyyy hh:mm"));

		CardThumbnail thumb = new CardThumbnail(this);
		thumb.setDrawableResource(R.drawable.ic_map);

		card.addCardThumbnail(thumb);

		card.addCardHeader(header);
		card.setSwipeable(true);
		// Set a clickListener on ContentArea

		card.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW,
				new Card.OnCardClickListener() {

					@Override
					public void onClick(Card card, View view) {

						Intent intent = new Intent(
								"ubicomplab.org.predictivecalendar.ROUTE_ACTIVITY");
						intent.putExtra("event", eventNow);
						startActivity(intent);
					}
				});
		Log.d("getstatus!!", Boolean.toString(event.getStatus()));
		// if (!event.getStatus()) {

		cards.add(card);
		// }
	}

	protected void displayCards() {
		// Problem with the below statement !! Have to figure out !!

		mCardArrayAdapter = new CardArrayAdapter(this, cards);
		Log.d("reaching", "hurray");
		CardListView listView = (CardListView) this.findViewById(R.id.myList);
		if (listView != null) {
			listView.setAdapter(mCardArrayAdapter);
		}
		mCardArrayAdapter.setEnableUndo(true);
	}

}
