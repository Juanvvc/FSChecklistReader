package com.juanvvc.fschecklistreader;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** This class parses a XML Checklist using SAX2 */
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
		// titles are elements of checklists
		if (qName.equals("title")) {
			if (current != null) {
				current.setTitle(sb.toString());
			} else {
				logger.warning("title element without a checklist");
			}
			// names are elements of checklistitems
		} else if (qName.equals("name")) {
			if (current_item != null) {
				current_item.setName(sb.toString());
			} else {
				logger.warning("name element without a checklist item");
			}
			// names are elements of checklistitems
		} else if (qName.equals("value")) {
			if (current_item != null) {
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
	}

	/** Starts an element */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// if a checklist starts, create the checklist
		if (qName.equals("checklist")) {
			current = new Checklist();
			// if a checklistitem starts, create the checklistitem
		} else if (qName.equals("item")) {
			current_item = new ChecklistItem();
		} // any other elements do not need a special management
			// notice many elements will be ignored: markers, for example

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
