package com.juanvvc.fschecklistreader;

/** Models information about a line in the checklist */
class ChecklistItem {
	/** Name of the item */
	private String name;
	/** Value of the item */
	private String value;
	/** If false, cannot be done in the simulator */
	private boolean doable = true;

	public boolean isDoable() {
		return doable;
	}

	public void setDoable(boolean doable) {
		this.doable = doable;
	}

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
