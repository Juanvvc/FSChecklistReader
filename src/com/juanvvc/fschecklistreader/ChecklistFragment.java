package com.juanvvc.fschecklistreader;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ChecklistFragment extends ListFragment {
	OnChecklistClickListener listener;
	private int mode = MODE_CHECKLISTS;
	public static final int MODE_CHECKLISTS = 0;
	public static final int MODE_ITEMS = 1;
	public static final int MODE_BOTH = 2;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.listener = (OnChecklistClickListener) activity;
	}

	public interface OnChecklistClickListener {
		public void onChecklistClick(int id);
	}
	
	public void setMode(int m) {
		this.mode = m;
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if ( mode == MODE_CHECKLISTS ) {
			if ( this.listener != null ) {
				this.listener.onChecklistClick(position);
			}
		}
		
		if ( mode == MODE_ITEMS ) {
			TextView tv = (TextView) v.findViewById(R.id.name);
			tv.setPaintFlags(tv.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
			tv = (TextView) v.findViewById(R.id.value);
			tv.setPaintFlags(tv.getPaintFlags() ^ Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
}
