package org.theeuropeanlibrary.telplus.tpta;

import java.util.List;

/**
 * 
 * This class is used for generating an XML document
 * representing a set of named entities.
 * 
 * @author Michel Koppelaar
 *
 * Created on: 21 jul 2008
 * 
 * $Id: TPTAResponse.java 2704 2008-07-30 15:27:35Z michel $
 */
public class TPTAResponse {

	// Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Members ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private String version;
	private String text;
	private String language;
	private List<NamedEntity> entities;
	
	// Static ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public TPTAResponse(String version, String text, String language, 
											List<NamedEntity> entities) {
		this.version = version;
		this.text = text;
		this.language = language;
		this.entities = entities;
	}
	// X Implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Y Overrides ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Public ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public String toString(String echo) {
		String response = "";

        if ( echo == null ) {
            response = "<TPTAResponse version=\"" + this.version + "\">\n<entities>";
        } else {
            response = "<TPTAResponse version=\"" + this.version + 
                                        "\">\n<text language=\"" + this.language + 
                                        "\">" + this.text + "</text>\n<entities>";
        }

		for (NamedEntity entity : this.entities) {
			response += "\n";
			if (entity instanceof PersonEntity) {
				response += "<person>" + entity.getValue() + "</person>";
			} else if (entity instanceof OrganisationEntity) {
				response += "<organisation>" + entity.getValue() + "</organisation>";
			} else if (entity instanceof LocationEntity) {
				response += "<location>" + entity.getValue() + "</location>";
			} else if (entity instanceof OtherEntity) {
				response += "<other>" + entity.getValue() + "</other>";
			} else {
				response += "<unknown>" + entity.getValue() + "</unknown>";
			}
			response += "\n";
		}
		response += "</entities>\n</TPTAResponse>";
		return response;
	}
	
	// Protected ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Private ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
