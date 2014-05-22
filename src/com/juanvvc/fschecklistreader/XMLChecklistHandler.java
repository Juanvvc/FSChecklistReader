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
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** This class parses a XML Checklist using SAX2.
 * 
 *  The format of these XML files is described here: http://wiki.flightgear.org/Aircraft_Checklists
 *  
 *   condition, page and marker elements are ignored. If the item has an attribute doable="false"
 *   is is marked as not doable */
class XMLChecklistHandler extends DefaultHandler {
	/** Logger to be used by this class */
	Logger logger = Logger.getLogger("XMLChecklistHandler");
	/** List of checklists inside the file */
	ArrayList<Checklist> checklists = null;
	/** A reference to the current checklist being parsed */
	Checklist current = null;
	/** A reference to the current item being parsed */
	ChecklistItem current_item = null;
	/** The current string buffer for data */
	StringBuffer sb = null;
	
	private boolean ignore_elements = false;

	/**
	 * New content for a XML element. Add the content to the current
	 * StringBuffer
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sb.append(new String(ch, start, length));
	}

	/**
	 * Ends an element. Adds the information to the checklist or the
	 * checklistitem
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		// end of the condition, check again elements
		if (qName.equals("condition") || qName.equals("binding") || qName.equals("marker")) {
			ignore_elements = false;
			return;
		}
		
		if (ignore_elements) {
			return;
		}
		
		// titles are elements of checklists
		if (qName.equals("title")) {
			if (current != null) {
				current.setTitle(sb.toString());
			} else {
				logger.warning("title element without a checklist");
			}
			// names are elements of checklistitems (set only once)
		} else if (qName.equals("name") ) {
			if (current_item != null && current_item.getName() == null ) {
				current_item.setName(sb.toString());
			} else {
				logger.warning("name element without a checklist item");
			}
			// value are elements of checklistitems (set only once)
		} else if (qName.equals("value") ) {
			if (current_item != null && current_item.getValue() == null ) {
				current_item.setValue(sb.toString());
			} else {
				logger.warning("value element without a checklist item");
			}
			// if a checklkist ends, add the checklist to the array of
			// checklists
		} else if (qName.equals("checklist")) {
			if (current != null) {
				this.checklists.add(current);
				current = null;
			} else {
				logger.warning("ending checklist without a checklist");
			}
			// if a checklistitem ends, add the item to the checklist
		} else if (qName.equals("item")) {
			if (current_item != null) {
				if (current != null) {
					current.getItems().add(current_item);
					current_item = null;
				} else {
					logger.warning("item without a checklist");
				}
			} else {
				logger.warning("ending item without an item");
			}
		}
	}

	/** Starts the parsing */
	@Override
	public void startDocument() throws SAXException {
		checklists = new ArrayList<Checklist>();
		// we will use this stringbuffer to store the content of the currenly
		// managed element
		sb = new StringBuffer();
		ignore_elements = false;
	}

	/** Starts an element */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// if ignoreElements is ON, just ignore the elements
		if (ignore_elements) {
			return;
		}
		
		// if a checklist starts, create the checklist
		if (qName.equals("checklist")) {
			current = new Checklist();
			ignore_elements = false;
		// if a checklistitem starts, create the checklistitem
		} else if (qName.equals("item")) {
			current_item = new ChecklistItem();
			
			String v = attributes.getValue("doable");
			if ( v != null && v.toLowerCase().equals("false")) {
				current_item.setDoable(false);
			} else {
				current_item.setDoable(true);
			}

			ignore_elements = false;
		} else if (qName.equals("condition") || qName.equals("binding") || qName.equals("marker")) {
			ignore_elements = true; // inside these elements, ignore values, names...
		} // any other element doesn't need any special management

		// remove the current string buffer
		// this means we won't be manage content if it is defined like this:
		// <d>content1 <e>content2</e> content3</d>
		// content1 will be lost. It is ok in these XML files (they do not
		// define
		// data like this), but other XML files may require a StringBuffer per
		// element.
		sb.delete(0, sb.length());
	}

	/** Returns the checklists in the file, when the parsing ends */
	public ArrayList<Checklist> getChecklists() {
		return checklists;
	}

}
