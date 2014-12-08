package com.example.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.service.ServiceActivity.ResponseReceiver;

public class SimpleIntentService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";

	String resultTxt;
	public SimpleIntentService() {
		super("SimpleIntentService");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		
				try {
					String msg = intent.getStringExtra(PARAM_IN_MSG);
					// SystemClock.sleep(30000); // 30 seconds
					resultTxt = msg
							+ " "
							+ DateFormat.format("MM/dd/yy h:mmaa",
									System.currentTimeMillis());
					Log.d("heeeee",resultTxt);
							
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


		 Intent broadcastIntent = new Intent();
		 broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
		 broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		 broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
		 sendBroadcast(broadcastIntent);

	}
}