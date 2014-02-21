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

import android.app.Activity;
import android.graphics.Paint;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

/** A fragment to show a list of checklists, or a list of items in a checklist.
 * 
 *  Remember to set the adapter to the suitable ListAdapter: ChecklistAdapter or ItemsAdapter */
public class ChecklistFragment extends ListFragment {
	OnChecklistClickListener listener;
	/** The current mode of the checklist */
	private int mode = MODE_CHECKLISTS;
	
	/** Mode: show checklists */
	public static final int MODE_CHECKLISTS = 0;
	/** Mode: show elements in a checklist */
	public static final int MODE_ITEMS = 1;
	/** Mode: show both. Not used in this class but other classes may use this mode internally
	 * if they are using different fragments at the same time */
	public static final int MODE_BOTH = 2;
	
	@Override
	/** Constructor
	 * 
	 * @param activity. The parent activity. If it implements OnChecklistClickListener,
	 * it is set as the listener of this fragment
	 */
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if ( activity instanceof OnChecklistClickListener ) {
			this.listener = (OnChecklistClickListener) activity;
		}
	}

	/** Implement this class to get the event "a user clicked on a checklist" */
	public interface OnChecklistClickListener {
		public void onChecklistClick(int id);
	}
	
	/** Sets the mode of the fragment */
	public void setMode(int m) {
		this.mode = m;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// if the fragment is showing checklists, pass the event
		if ( mode == MODE_CHECKLISTS ) {
			if ( this.listener != null ) {
				this.listener.onChecklistClick(position);
			}
		}
		
		// the the fragment is showing items, strike the item
		if ( mode == MODE_ITEMS ) {
			TextView tv = (TextView) v.findViewById(R.id.name);
			tv.setPaintFlags(tv.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
			tv = (TextView) v.findViewById(R.id.value);
			tv.setPaintFlags(tv.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
}
