package com.juanvvc.fschecklistreader;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class ChecklistAdapter extends BaseAdapter {
	ChecklistsActivity checklistsActivity;
	ChecklistAdapter(ChecklistsActivity checklistsActivity) {
		this.checklistsActivity = checklistsActivity;
	}

	ArrayList<Checklist> checklists = null;
	
	public void setChecklists(ArrayList<Checklist> cl) {
		this.checklists = cl;
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
		
		TextView tv = (TextView) convertView.findViewById(R.id.name);
		tv.setText(this.checklists.get(position).getTitle());
		
		return convertView;
	}
	
}