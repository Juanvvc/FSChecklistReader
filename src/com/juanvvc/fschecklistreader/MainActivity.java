package com.juanvvc.fschecklistreader;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/** Main activity that shows a list of available aircrafts */
public class MainActivity extends ListActivity {
	
	ArrayList<String> listfiles = null;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if ( listfiles == null ) {
			return;
		}
		Intent i = new Intent(this, ChecklistsActivity.class);
		i.putExtra("xml", listfiles.get(position));
		startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// get the list of files from the assets repository
			String[] realfiles = getAssets().list("");
			listfiles = new ArrayList<String>();
			for( String f: realfiles ) {
				if ( f.endsWith(".xml") ) {
					listfiles.add(f);
				}
			}
			
			// TODO: implement an external repository to get files
			
			ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listfiles);
			this.setListAdapter(a);
		} catch (IOException e) {
			MyLog.e(this, "Cannot read assets");
		}
	}
	
	

}
