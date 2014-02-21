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
