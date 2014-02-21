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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/** An adapter for a checklist.
 * 
 * This adapter shows a list of available checklists
 * @author juanvi
 *
 */
class ChecklistAdapter extends BaseAdapter {
	
	/** Use these colors to alternate in the list */
	private static final int colors[] = {0xeef6cef5, 0xeeced8f6};
	/** The list of checklists to show */
	ArrayList<Checklist> checklists = null;
	/** A reference to the parent activity */
	ChecklistsActivity checklistsActivity;
	
	ChecklistAdapter(ChecklistsActivity checklistsActivity) {
		this.checklistsActivity = checklistsActivity;
	}
	
	public void setChecklists(ArrayList<Checklist> cl) {
		this.checklists = cl;
	}
	
	public ArrayList<Checklist> getChecklists() {
		return this.checklists;
	}

	@Override
	public int getCount() {
		if ( this.checklists != null ) {
			return this.checklists.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return this.checklists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.checklistsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_checklist, null);
		}
		
		if ( checklists == null ) {
			MyLog.w(this, "The checklists are not set");
			return convertView;
			
		}
		
		convertView.setBackgroundColor(colors[position%2]);
		
		TextView tv = (TextView) convertView.findViewById(R.id.name);
		tv.setText(this.checklists.get(position).getTitle());
		
		return convertView;
	}
	
}