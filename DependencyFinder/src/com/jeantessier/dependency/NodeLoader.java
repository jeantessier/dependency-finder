/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class NodeLoader {
	private static final String DEFAULT_READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";

	private String      reader_classname;
	private NodeHandler handler;

	public NodeLoader() {
		this(new NodeFactory(), DEFAULT_READER_CLASSNAME);
	}

	public NodeLoader(NodeFactory factory) {
		this(factory, DEFAULT_READER_CLASSNAME);
	}

	public NodeLoader(String reader_classname) {
		this(new NodeFactory(), reader_classname);
	}

	public NodeLoader(NodeFactory factory, String reader_classname) {
		this.reader_classname = reader_classname;
		this.handler          = new NodeHandler(factory);
	}

	public Map Load(String filename) throws IOException, SAXException {
		Map result;

		FileReader in = (new FileReader(filename));
		result = Load(in);
		in.close();

		return result;
	}

	public Map Load(FileReader in) throws IOException, SAXException {
		return Load(new InputSource(in));
	}

	public Map Load(InputSource in) throws IOException, SAXException {
		XMLReader reader = XMLReaderFactory.createXMLReader(reader_classname);
		reader.setDTDHandler(handler);
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		try {
			reader.setFeature("http://xml.org/sax/features/validation", true);
			// reader.parse(in);
		} catch (Exception ex) {
			Category.getInstance(getClass().getName()).warn("Problem setting validation feature on XML reader",ex);
		}
	
		reader.parse(in);
	
		return handler.Factory().Packages();
	}

	public void addDependencyListener(DependencyListener listener) {
		handler.addDependencyListener(listener);
	}

	public void removeDependencyListener(DependencyListener listener) {
		handler.removeDependencyListener(listener);
	}
}
