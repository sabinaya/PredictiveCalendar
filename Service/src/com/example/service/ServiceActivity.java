package com.example.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ServiceActivity extends ActionBarActivity {

	private ResponseReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);

		IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new ResponseReceiver();
		registerReceiver(receiver, filter);

		Button but1 = (Button) findViewById(R.id.button1);
		but1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText input = (EditText) findViewById(R.id.txt_input);
				String strInputMsg = input.getText().toString();

				Intent msgIntent = new Intent(getApplicationContext(),
						SimpleIntentService.class);
				msgIntent.putExtra(SimpleIntentService.PARAM_IN_MSG,
						strInputMsg);
				getApplicationContext().startService(msgIntent);

			}
		});

		Button but2 = (Button) findViewById(R.id.button2);
		but2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String url = "http://maps.google.com/maps?f=d&daddr="
						+ "40.433114" + "," + "-79.924537" + "&dirflg=d";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(url));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				startActivity(intent);
			}
		});

	}

	public class ResponseReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP = "com.mamlambo.intent.action.MESSAGE_PROCESSED";

		@Override
		public void onReceive(Context context, Intent intent) {
			TextView result = (TextView) findViewById(R.id.txt_result);
			String text = intent
					.getStringExtra(SimpleIntentService.PARAM_OUT_MSG);
			result.setText(text);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
}
