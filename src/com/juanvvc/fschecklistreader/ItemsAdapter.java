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

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** An adapter to show a list of items in a checklist.
 * 
 * Actually, checklists are a List and this class selects a checklist from the list. This is
 * why we extend ChecklistAdapter: to access the list of checklists */
class ItemsAdapter extends ChecklistAdapter {
	/** the list will alternate between these colors */
	private final int colors[] = {0xfff5f6ce, 0xffcef6ce};
	/** if show_doables is set, an item not doable has this color */
	private final int color_notdoable = 0x88f6cef5;
	/** if set, not doable items will have a different background color */
	private boolean show_doables = true;

	/** The position of the checklist to show */
	private int xmlposition;
	
	/** Constructor.
	 * 
	 * @param checklistsActivity
	 * @param doables If set, items not doable will have a different background color
	 */
	ItemsAdapter(ChecklistsActivity checklistsActivity, boolean doables) {
		super(checklistsActivity);
		xmlposition = -1;
		show_doables = doables;
	}
	
	/** The position is the order of the checklist inside the set of checklist.
	 * You have to set the list of checklists with setChecklists()
	 * 
	 * @param pos 0 for the first checklist, 1 for the seconds... -1 to not use the list
	 */
	public void setPosition(int pos) {
		xmlposition = pos;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// if the list of checklists is not defined, or the positin is not defined, return 0
		if ( checklists == null || xmlposition == -1 || checklists.size() < xmlposition ) {
			return 0;
		}
		return this.checklists.get(xmlposition).getItems().size();
	}

	@Override
	public Object getItem(int position) {
		if ( checklists == null || xmlposition == -1 || checklists.size() < xmlposition ) {
			return null;
		}
		return this.checklists.get(xmlposition).getItems().get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.checklistsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_checklist_item, null);
		}
		
		// get the selected checklist
		ChecklistItem item = (ChecklistItem) this.getItem(position);
		
		
		// fill the information
		if ( item != null) {
			// set the texts and remove the strike (in any)
			TextView tv = (TextView) convertView.findViewById(R.id.name);
			tv.setText(item.getName());
			tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			tv = (TextView) convertView.findViewById(R.id.value);
			tv.setText(item.getValue());
			tv.setPaintFlags(tv.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			
			// set the background color
			if ( show_doables && !item.isDoable() ) {
				convertView.setBackgroundColor(color_notdoable);
			} else {
				convertView.setBackgroundColor(colors[position%2]);
			}
		} else {
			MyLog.w(this, "Trying to show a null item");
		}
		
		return convertView;
	}
}