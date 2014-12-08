package com.example.linkedlistimpl;

import java.util.LinkedList;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class List extends ActionBarActivity {

	String names[] = { "abi", "sushi", "suprita", "shakthi", "kiruthika" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		LinkedList<String> nameList = new LinkedList<String>();
		Log.d("hey", "hey");
		for (String l : names) {
			nameList.add(l);
		}
		printList(nameList);
	}
	
	protected void printList(LinkedList<String> list)
	{
		for(String ele : list)
		{
			Log.d("list",ele);
			Log.d("list index", Integer.toString(list.indexOf(ele)));
		}
	}
}
