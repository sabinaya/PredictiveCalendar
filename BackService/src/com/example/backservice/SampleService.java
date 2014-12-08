package com.example.backservice;

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

					String result = "hello";
					Log.d("heeeee", result);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};

		timer.schedule(doAsynchronousTask, 0, 10000); // execute in every 50000

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
