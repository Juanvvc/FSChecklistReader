package com.juanvvc.fschecklistreader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class ChecklistsActivity extends FragmentActivity implements ChecklistFragment.OnChecklistClickListener {
	
	private String xmlurl = null;
	private int xmlposition = -1;
	private int mode = 0;
	
	// TODO: make this configurable
	private static final boolean SHOW_DOABLES = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		xmlurl = "c172.xml";
		xmlposition = -1;
		
		// get the xmlurl and position from the intent
		if (this.getIntent().getExtras() != null) {
			if ( this.getIntent().getExtras().containsKey("xml")) {
				xmlurl = this.getIntent().getExtras().getString("xml");
			}
			if ( this.getIntent().getExtras().containsKey("xmlposition")) {
				xmlposition = this.getIntent().getExtras().getInt("xmlposition");
			}
		}
		
		if ( xmlposition == -1) {
			setContentView(R.layout.activity_checklists);
		} else {
			setContentView(R.layout.activity_items);
		}
		
		// configure the execution mode
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
	}

	ReadXML readXML = null;
	@Override
	protected void onStart() {
		super.onStart();
		if ( readXML != null) {
			readXML.cancel(true);
		}
		readXML = new ReadXML();
		readXML.execute(this.xmlurl);
	}
	
	private class ReadXML extends AsyncTask <String, Object, XMLChecklistHandler> {
		@Override
		protected XMLChecklistHandler doInBackground(String... arg0) {
			// reads a xml file in the background
			String file = arg0[0];
			
			InputStream in = null;
			
			try {
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				XMLChecklistHandler h = new XMLChecklistHandler();
				MyLog.i(this, "Loading XML file: " + file);
				in = getAssets().open(file);
				parser.parse(in, h);
				return h;
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
		protected void onPostExecute(XMLChecklistHandler result) {
			
			if ( result == null ) {
				return;
			}
			
			ItemsAdapter items;
			ChecklistAdapter checklists;
			ChecklistFragment f;
			
			switch(mode) {
			case ChecklistFragment.MODE_ITEMS:
				items = new ItemsAdapter(ChecklistsActivity.this, SHOW_DOABLES);
				items.setChecklists(result.getChecklists());
				items.setPosition(xmlposition);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
				f.setListAdapter(items);
				break;
			case ChecklistFragment.MODE_CHECKLISTS:
				checklists = new ChecklistAdapter(ChecklistsActivity.this);
				checklists.setChecklists(result.getChecklists());
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.checklists);
				f.setListAdapter(checklists);
				break;
			case ChecklistFragment.MODE_BOTH:
				items = new ItemsAdapter(ChecklistsActivity.this, SHOW_DOABLES);
				items.setChecklists(result.getChecklists());
				items.setPosition(xmlposition);
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
				f.setListAdapter(items);

				checklists = new ChecklistAdapter(ChecklistsActivity.this);
				checklists.setChecklists(result.getChecklists());
				f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.checklists);
				f.setListAdapter(checklists);
			}
			
			// set the title of the list
			TextView tv = (TextView) findViewById(R.id.title);
			if ( xmlposition == -1 ) {
				tv.setText(xmlurl);
			} else {
				if ( result.getChecklists() != null && result.getChecklists().size() >= xmlposition ) {
					tv.setText(result.getChecklists().get(xmlposition).getTitle());
				} else {
					tv.setText("Error");
				}
			}
		}
	}

	@Override
	public void onChecklistClick(int id) {
		switch(mode) {
		case ChecklistFragment.MODE_CHECKLISTS:
			Intent i = new Intent(this, ChecklistsActivity.class);
			i.putExtra("xml", xmlurl);
			i.putExtra("xmlposition", id);
			this.startActivity(i);
			break;
		case ChecklistFragment.MODE_BOTH:
			ChecklistFragment f = (ChecklistFragment) getSupportFragmentManager().findFragmentById(R.id.items);
			((ItemsAdapter) f.getListAdapter()).setPosition(id);
		}
	}
}
