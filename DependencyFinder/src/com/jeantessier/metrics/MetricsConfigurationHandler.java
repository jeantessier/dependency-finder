/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.metrics;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class MetricsConfigurationHandler extends DefaultHandler {
	private static final int PROJECT = 0;
	private static final int GROUP   = 1;
	private static final int CLASS   = 2;
	private static final int METHOD  = 3;
	
	private MetricsConfiguration  configuration;
	private int                   section;
	private MeasurementDescriptor descriptor;
	private String                name;
	private String                pattern;
	
	private StringBuffer current_name = new StringBuffer();

	public MetricsConfigurationHandler() {
		this(new MetricsConfiguration());
	}

	public MetricsConfigurationHandler(MetricsConfiguration configuration) {
		this.configuration = configuration;
	}

	public MetricsConfiguration MetricsConfiguration() {
		return configuration;
	}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		Logger.getLogger(getClass()).debug("startElement qName = " + qName);

		for (int i=0; i<atts.getLength(); i++) {
			Logger.getLogger(getClass()).debug("    " + atts.getQName(i) + ": " + atts.getValue(i));
		}

		current_name.delete(0, current_name.length());

		if (qName.equals("project-measurements")) {
			section = PROJECT;
		} else if (qName.equals("group-measurements")) {
			section = GROUP;
		} else if (qName.equals("class-measurements")) {
			section = CLASS;
		} else if (qName.equals("method-measurements")) {
			section = METHOD;
		} else if (qName.equals("measurement")) {
			descriptor = new MeasurementDescriptor();

			if (atts.getValue("visible") != null) {
				descriptor.Visible("true".equalsIgnoreCase(atts.getValue("visible")) ||
								   "yes".equalsIgnoreCase(atts.getValue("visible")) ||
								   "on".equalsIgnoreCase(atts.getValue("visible")));
			}
			
			switch (section) {
				case PROJECT:
					configuration.AddProjectMeasurement(descriptor);
					break;
				case GROUP:
					configuration.AddGroupMeasurement(descriptor);
					break;
				case CLASS:
					configuration.AddClassMeasurement(descriptor);
					break;
				case METHOD:
					configuration.AddMethodMeasurement(descriptor);
					break;
			}
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("short-name")) {
			descriptor.ShortName(current_name.toString().trim());
		} else if (qName.equals("long-name")) {
			descriptor.LongName(current_name.toString().trim());
		} else if (qName.equals("class")) {
			try {
				descriptor.Class(current_name.toString().trim());
			} catch (ClassNotFoundException ex) {
				throw new SAXException("Class not found: " + current_name.toString().trim());
			}
		} else if (qName.equals("init")) {
			descriptor.InitText(current_name.toString().trim());
		} else if (qName.equals("lower-threshold")) {
			descriptor.LowerThreshold(current_name.toString().trim());
		} else if (qName.equals("upper-threshold")) {
			descriptor.UpperThreshold(current_name.toString().trim());
		} else if (qName.equals("name")) {
			name = current_name.toString().trim();
		} else if (qName.equals("pattern")) {
			pattern = current_name.toString().trim();
		} else if (qName.equals("group-definition")) {
			configuration.AddGroupDefinition(name, pattern);
		}
		
		Logger.getLogger(getClass()).debug("endElement qName = " + qName + " (\"" + current_name.toString().trim() + "\")");
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		current_name.append(ch, start, length);
		Logger.getLogger(getClass()).debug("characters: \"" + new String(ch, start, length) + "\"");
	}
}
