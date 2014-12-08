package ubicomplab.org.predictivecalendar;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SampleService extends Service {

	@Override
	public void onCreate() {

		super.onCreate();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		Timer timer = new Timer();

		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {

				try {

					Log.d("heeeee", "service");
					Events eventObj = new Events();

					// Traveling Mode
					String mode = "mode=driving";

					CalendarEvents nextEvent;
					DrawRouteFragment process = new DrawRouteFragment(
							getBaseContext());
					Globals g = Globals.getInstance();
					LinkedList<CalendarEvents> calendarList = g.getData();

					for (CalendarEvents event : calendarList) {

						nextEvent = g.getNextEvent(event);
						// g.getIndex(event) <= g.getData().size() &&
						g.printlList();
						if (nextEvent != null) {
							eventObj.parseConsecutiveEvents(event);
							nextEvent = eventObj.getSecondEvent();
							process.drawRoute(event, nextEvent, mode);

						}

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		timer.schedule(doAsynchronousTask, 0, 60000);
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
