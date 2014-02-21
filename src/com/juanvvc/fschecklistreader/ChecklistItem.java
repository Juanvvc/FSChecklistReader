package com.juanvvc.fschecklistreader;

/** Models information about a line in the checklist */
class ChecklistItem {
	/** Name of the item */
	private String name;
	/** Value of the item */
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return this.name + ": " + this.value;
	}
}
