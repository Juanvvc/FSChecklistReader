package com.juanvvc.fschecklistreader;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {
	
	String[] files = null;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String file = files[position];
		Intent i = new Intent(this, ChecklistsActivity.class);
		i.putExtra("xml", file);
		startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			files = getAssets().list("");
			ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);
			this.setListAdapter(a);
		} catch (IOException e) {
			MyLog.e(this, "Cannot read assets");
		}
	}
	
	

}
