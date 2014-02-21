package com.juanvvc.fschecklistreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class ItemsAdapter extends ChecklistAdapter {
	ItemsAdapter(ChecklistsActivity checklistsActivity) {
		super(checklistsActivity);
		xmlposition = -1;
	}
	
	private int xmlposition;
	
	public void setPosition(int pos) {
		xmlposition = pos;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
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
		
		ChecklistItem item = (ChecklistItem) this.getItem(position);
		
		if ( item != null) {
			TextView tv = (TextView) convertView.findViewById(R.id.name);
			tv.setText(item.getName());
			tv = (TextView) convertView.findViewById(R.id.value);
			tv.setText(item.getValue());
		} else {
			MyLog.w(this, "Trying to show a null item");
		}
		
		return convertView;
	}
}