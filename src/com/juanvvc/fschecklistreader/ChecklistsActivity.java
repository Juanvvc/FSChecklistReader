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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/** The activity that shows the available checklists. It is coded using fragments.
 * 
 *  On small device will show only a fragment. On big devices it will show two fragments.
 *  In all cases, the fragments to show are instances of ChecklistFragment, using different modes.
 *  
 *  Important note: I'm suporting also Android 2.2, so I'm using the support libraries. */
public class ChecklistsActivity extends FragmentActivity implements ChecklistFragment.OnChecklistClickListener, OnClickListener {
	
	/** The name of the xml file to load, WITHOUT the path */
	private String filename = null;
	/** The checklist to load inside the xml file: 0, 1, 2... Set to -1 to not load any specific list */
	private int fileposition = -1;
	/** The mode of the activity:
	 * 
	 * ChecklistFragment.MODE_CHECKLISTS: shows the available checklists
	 * ChecklistFragment.MODE_ITEMS: shows the items in the fileposition checklist
	 * ChecklistFragment.MODE_BOTH: this activity is showing two fragments
	 */
	private int mode = 0;
	
	/** A task to read the files in the background */
	private AsyncTask<String, Object, ArrayList<Checklist>> readFile = null;
	
	// TODO: make this configurable
	/** If true, items that are not doable are shown differently */
	private static final boolean SHOW_DOABLES = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// default values
		filename = "c172";
		fileposition = -1;
		
		// get the filename and fileposition from the intent
		if (this.getIntent().getExtras() != null) {
			if ( this.getIntent().getExtras().containsKey("file")) {
				filename = this.getIntent().getExtras().getString("file");
			}
			if ( this.getIntent().getExtras().containsKey("fileposition")) {
				fileposition = this.getIntent().getExtras().getInt("fileposition");
			}
		}
		
		if ( fileposition == -1) {
			// notice: in large devices, this will load TWO fragments. MODE_BOTH will be set
			// in small devices, this only loads checklist MODE_CHECKLIST will be set
			setContentView(R.layout.activity_checklists);
		} else {
			// if the intent had some fileposition, load this layout
			// this is only for small devices!! MODE_ITEMS
			setContentView(R.layout.activity_items);
		}
		
		// configure the execution mode, according to the last notes.
		// we will distinguish if we are using one mode or the other checking the fragments
		ChecklistFragment checklists = (ChecklistFragment) this.getSupportFragmentManager().findFragmentById(R.id.checklists);
		ChecklistFragment items = (ChecklistFragment) this.getSupportFragmentManager().findFragmentById(R.id.items);
		if ( checklists != null ) {
			if ( items != null ) {
				mode = ChecklistFragment.MODE_BOTH;
				items.setMode(ChecklistFragment.MODE_ITEMS);
				checklists.setMode(ChecklistFragment.MODE_CHECKLISTS);
			} else {
				mode = ChecklistFragment.MODE_CHECKLISTS;
				checklists.setMode(ChecklistFragment.MODE_CHECKLISTS);
			}
		} else {
			mode = ChecklistFragment.MODE_ITEMS;
			items.setMode(ChecklistFragment.MODE_ITEMS);
		}
		
		// configure the buttons
		Button b = (Button) this.findViewById(R.id.next);
		if ( b != null ) {
			b.setOnClickListener(this);
		}
	}

	@Override
	/** onStart() loads the XML in the background.
	 * 
	 * TODO: on small devices, the XML is loaded each time the user changed the activity.
	 * This is not very efficient...
	 */
	protected void onStart() {
		super.onStart();
		// if there is another readXMl running, stop it.
		if ( readFile != null) {
			readFile.cancel(true);
		}
		
		// get the reader
		if (this.filename.toLowerCase(Locale.US).endsWith(".chk")) {
			readFile = new ReadCHK();
		} else {
			readFile = new ReadXML();
		}
		readFile.execute(this.filename);
	}
	
	/** A task to read an XML file. Check XMLChecklistHandler for the format  */
	private class ReadXML extends AsyncTask <String, Object, ArrayList<Checklist>> {
		@Override
		/** @param arg0 The name of the XML file to load (without path) */
		protected ArrayList<Checklist> doInBackground(String... arg0) {
			InputStream in = null;

			// parses the file
			try {
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				XMLChecklistHandler h = new XMLChecklistHandler();
				in = name2file(arg0[0]);
				parser.parse(in, h);
				return h.getChecklists();
			} catch (ParserConfigurationException e) {
				MyLog.e(this, "Cannot create XML parser: " + e);
			} catch (SAXException e) {
				MyLog.e(this, "Error while parsing XML: " + e);
			} catch (IOException e) {
				MyLog.e(this, "Error reading XML: " + e);
			} finally {
				if ( in != null ) {
					try {
						in.close();
					} catch(IOException e) {
						MyLog.w(this, "Error while closing file: " + e);
					}
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Checklist> result) {
			
			if ( result == null ) {
				return;
			}
			
			ItemsAdapter items;
			ChecklistAdapter checklists;
			ChecklistFragment f;
			
			// once the data is loaded, change the adapters of the available fragments
			
			switch(mode) {
			case ChecklistFragment.MODE_ITEMS: // show only items
				items = new ItemsAdapter(ChecklistsActivity.this, SHOW_DOABLES);
				items.setChecklists(result);
				items.setPosition(fileposition);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
				f.setListAdapter(items);
				break;
			case ChecklistFragment.MODE_CHECKLISTS: // show only available checklists
				checklists = new ChecklistAdapter(ChecklistsActivity.this);
				checklists.setChecklists(result);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.checklists);
				f.setListAdapter(checklists);
				break;
			case ChecklistFragment.MODE_BOTH: // show both
				items = new ItemsAdapter(ChecklistsActivity.this, SHOW_DOABLES);
				items.setChecklists(result);
				items.setPosition(fileposition);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
				f.setListAdapter(items);

				checklists = new ChecklistAdapter(ChecklistsActivity.this);
				checklists.setChecklists(result);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.checklists);
				f.setListAdapter(checklists);
			}
			
			// set the title of the list (name of the aircraft)
			TextView tv = (TextView) findViewById(R.id.title);
			if ( fileposition == -1 ) {
				tv.setText(filename);
			} else {
				if ( result != null && result.size() >= fileposition ) {
					tv.setText(result.get(fileposition).getTitle());
				} else {
					tv.setText("Error");
				}
			}
		}
	}
	
	/** Reads CHK files, an internal format for checklists.
	 * 
	 * Example of this filetype:
	 * Checlists separated by newlines, the first line is the title of the checlists, the other
	 * are pairs name | value. Spaces are ignored and removed.
	 * If the name starts with a *, it is not doable
	 * 
First checklist
BATTERY                 | ON
MASTER                  | ON
MAGNETOS                | START
 
Second checklist
RPM                     | 1000
*OIL TEMP.              | Check >40º C

Third checklist
Speed                   | 100IAS
Flaps                   | FULL

	 * This class extends ReadXML to use the same onPostExecute()
	 * @author juanvi
	 *
	 */
	private class ReadCHK extends ReadXML {
		protected ArrayList<Checklist> doInBackground(String... arg0) {
			InputStream in = null;

			// parses the file
			try {
				in = name2file(arg0[0]);
				Scanner scanner = new Scanner(in);
				
				Checklist cl = null;
				ArrayList<Checklist> al = new ArrayList<Checklist>();
				boolean new_checklist = true;
				
				while(scanner.hasNext()) {
					String line = scanner.nextLine().trim();
					if ( line.length() == 0) {
						// empty lines mark a new checklist
						new_checklist = true;
						if ( cl != null ){
							al.add(cl);
							cl = null;
						}
					} else {
						if ( new_checklist ) {
							cl = new Checklist();
							cl.setTitle(line);
							new_checklist = false;
						} else {
							if ( cl == null ) {
								throw new Exception("Item without a checklists!");
							}
							String[] v = line.split("\\|", 2);
							ChecklistItem i = new ChecklistItem();
							if ( v[0].startsWith("*")) {
								i.setDoable(false);
								i.setName(v[0].substring(1).trim());
							} else {
								i.setDoable(true);
								i.setName(v[0].trim());
							}
							i.setValue(v[1]);
							
							cl.getItems().add(i);
						}
					}
				}
				
				// add the last checklist, is it is still in the memory
				if ( cl != null ) {
					al.add(cl);
				}
				
				return al;
				
			} catch (IOException e) {
				MyLog.e(this, "Error reading XML: " + e);
			} catch ( Exception e ) {
				MyLog.e(this, "Error parsing DEM: " + MyLog.stackToString(e));
			} finally {
				if ( in != null ) {
					try {
						in.close();
					} catch(IOException e) {
						MyLog.w(this, "Error while closing file: " + e);
					}
				}
			}
			
			return null;
		}
	}
	
	/** Giving the name of a file to load, search in the external directory and assets
	 * 
	 *  @param name The name of the file to load, without path */
	private InputStream name2file(String name) throws IOException{
		// first, look for the name in the external storage. These files have priority
		File extStore = Environment.getExternalStorageDirectory();
		File extDir = new File(extStore, MainActivity.EXTERNAL_DIR);
		if (extDir.exists() && extDir.isDirectory() ) {
			File file = new File(extDir, name);
			if (file.exists() && file.canRead()) {
				return new FileInputStream(file);
			}
		}
		
		// No luck. look for the name in the assets
		// notice: if the name does not exists, this must throw a IOException
		return getAssets().open(name);
	}

	@Override
	/** The user clicked on a checklist: load its items */
	public void onChecklistClick(int id) {
		ChecklistFragment f;
		
		switch(mode) {
		case ChecklistFragment.MODE_CHECKLISTS: // in mode checklist, open another activity to show items
			Intent i = new Intent(this, ChecklistsActivity.class);
			// only change if it is under limits
			f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.checklists);
			ChecklistAdapter ca = (ChecklistAdapter) f.getListAdapter();
			// only change the position if it is a inside the limits
			if ( ca.getChecklists() != null && id >=0 && ca.getChecklists().size() > id ) {
				i.putExtra("file", filename);
				i.putExtra("fileposition", id);
				this.startActivity(i);
			}
			break;
		case ChecklistFragment.MODE_ITEMS: // in mode items, it is the same than mode both
		case ChecklistFragment.MODE_BOTH: // in mode both, change the adapter of the items fragment
			// change the items
			f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
			ItemsAdapter ia = (ItemsAdapter) f.getListAdapter();
			// only change the position if it is a inside the limits
			if ( ia.getChecklists() != null && id >=0 && ia.getChecklists().size() > id ) {
				ia.setPosition(id);
				// change the second title (name of the checklist)
				TextView tv = (TextView) this.findViewById(R.id.title2);
				if ( tv == null ) {
					// this happens in mode items
					tv = (TextView) this.findViewById(R.id.title);
				}
				tv.setText(ia.getChecklists().get(id).getTitle());
				
				fileposition = id;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.next ) {
			// load the next checklist. Notice onChecklistClick() checks for correct values!
			onChecklistClick(fileposition + 1);
		}
	}
}
