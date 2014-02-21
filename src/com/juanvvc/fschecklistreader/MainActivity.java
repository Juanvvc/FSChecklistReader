/*  FSChecklistReader is an Android application to show checklists for flight simulators
    Copyright (C) 2014, Juan Vera <juanvvc@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.juanvvc.fschecklistreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/* ////////////////////////////////////////
check list: before uploading a new version to Google Play:

1.- Create the normal version:
- List the changes and date in res/raw/changelog.txt. Change the version there
- Check the version number in the manifest: it should be higher than the one in Google Play
- Commit the version to git and set a new flag:
    git commit -a -m blahblahblah
    git tag v2.0
    git push --tags github
    git push --tags linsertel
- Set DEBUG_VERSION to false in this file
- Export the project to FSChecklistReader.apk
- Signature: ~/.android/myjuanvvc.keystore
- Send to Google Play. Remember listing the last changes and updating the description, if necessary

2.- Create the donate version:
- Set DONATE_VERSION to true in this file
- Change the icon in the manifest to ic_launcherplus
- RMB on each package Refactor->Rename to com.juanvvc.fschecklistreaderdonate
- RMB on the project name->Android tools->rename application package to com.juanvvc.fschecklistreaderdonate
- Run the project to check if it is working!
- Export the project to FSChecklistReader.apk
- Signature: ~/.android/myjuanvvc.keystore
- Send to Google Play. Remember listing the last changes and updating the description, if necessary

3.- Back to development version:
- Undo all changes. Easy way:
    Exit Eclipse
    git --hard reset
- Open Eclipse again, ok to the warning message
- Start a new iteration by updating the version number in the manifest: one higher
    
///////////////////////////////////////// */

/** Main activity that shows a list of available aircrafts */
public class MainActivity extends ListActivity {
	/** Is this a development version? */
	public static final boolean DEBUG_VERSION = true;
	/** Is this a donate version? Currently not used */
	public static final boolean DONATE_VERSION = false;
	
	// TODO: make this configurable
	/** Path in the extermal storage for external checklists */
	public static final String EXTERNAL_DIR = "checklists";
	
	/** The list of files */
	private ArrayList<String> listfiles = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MyLog.setDebug(DEBUG_VERSION);

		listfiles = new ArrayList<String>();
		
		// Read files from external storage
		File extStore = Environment.getExternalStorageDirectory();
		File extDir = new File(extStore, EXTERNAL_DIR);
		if (extDir.exists() && extDir.isDirectory() ) {
			for ( String f: extDir.list() ) {
				if ( f.toLowerCase().endsWith(".xml") || f.toLowerCase().endsWith(".chk")) {
					// avoid repeated files
					if ( !listfiles.contains(f) ) {
						listfiles.add(f);
					}
				}
			}
		}
		
		try {
			// get the list of files from the assets repository
			String[] realfiles = getAssets().list("");
			for( String f: realfiles ) {
				if ( f.toLowerCase().endsWith(".xml") || f.toLowerCase().endsWith(".chk") ) {
					// only add .xml and .chk files
					// avoid repeating files
					if ( !listfiles.contains(f) ) {
						listfiles.add(f);
					}
				}
			}
		} catch (IOException e) {
			MyLog.e(this, "Cannot read assets");
		}
			

		
		ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listfiles);
		this.setListAdapter(a);
		
		// if it is the first run of this version, show the changelog.txt
		ChangeLog cl = new ChangeLog(this);
		if (cl.firstRun() || MyLog.isDebug()) {
			cl.getLogDialog().show();
		}

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if ( listfiles == null ) {
			return;
		}
		Intent i = new Intent(this, ChecklistsActivity.class);
		i.putExtra("file", listfiles.get(position));
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if ( item.getItemId() == R.id.action_about ) {
			ChangeLog cl = new ChangeLog(this);
			cl.getFullLogDialog().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
