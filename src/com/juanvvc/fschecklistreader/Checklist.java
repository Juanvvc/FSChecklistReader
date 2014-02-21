package com.juanvvc.fschecklistreader;

import java.util.ArrayList;

/** Models information about a checklist */
class Checklist {
	/** The title of the checklist */
	private String title;
	/** The array of items (lines) in the checklist */
	private ArrayList<ChecklistItem> items = new ArrayList<ChecklistItem>();

	public ArrayList<ChecklistItem> getItems() {
		return items;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/** Converts the checklist into a string, to debug the app */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String nl = "\n";

		sb.append(this.title).append(nl);

		for (ChecklistItem i : this.items) {
			sb.append(i.toString()).append(nl);
		}
		return sb.toString();
	}
}
