package ubicomplab.org.predictivecalendar;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class ShowNotification {

	protected void showNotification(Context context, CalendarEvents event) {

		String appName = context.getString(R.string.app_name);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(appName).setContentText(appName)
				.setContentText(event.getName());

		Uri sound = Uri.parse("android.resource://" + context.getPackageName()
				+ "/" + R.raw.audio);
		mBuilder.setSound(sound);
		mBuilder.setAutoCancel(true);
		mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, CalendarActivity.class);
		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(CalendarActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(321, mBuilder.build());
	}

}
