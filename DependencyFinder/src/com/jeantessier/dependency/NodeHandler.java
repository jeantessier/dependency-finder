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

public class NodeHandler extends DefaultHandler {
	private static final int PACKAGE = 1;
	private static final int CLASS   = 2;
	private static final int FEATURE = 3;

	private NodeFactory factory;
    
	private int          current_node_type;
	private int          current_dependency_type;
	private Node         current_node;
	private PackageNode  current_package;
	private ClassNode    current_class;
	private FeatureNode  current_feature;
	private StringBuffer current_name = new StringBuffer();

	private HashSet      dependency_listeners = new HashSet();

	public NodeHandler() {
		this(new NodeFactory());
	}

	public NodeHandler(NodeFactory factory) {
		this.factory = factory;
	}

	public NodeFactory Factory() {
		return factory;
	}
    
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		Category.getInstance(getClass().getName()).debug("qName = " + qName);

		for (int i=0; i<atts.getLength(); i++) {
			Category.getInstance(getClass().getName()).debug("    " + atts.getQName(i) + ": " + atts.getValue(i));
		}

		current_name.delete(0, current_name.length());

		if ("package".equals(qName)) {
			current_node_type = PACKAGE;
		} else if ("class".equals(qName)) {
			current_node_type = CLASS;
		} else if ("feature".equals(qName)) {
			current_node_type = FEATURE;
		} else if ("inbound".equals(qName) || "outbound".equals(qName)) {
			if ("package".equals(atts.getValue("type"))) {
				current_dependency_type = PACKAGE;
			} else if ("class".equals(atts.getValue("type"))) {
				current_dependency_type = CLASS;
			} else if ("feature".equals(atts.getValue("type"))) {
				current_dependency_type = FEATURE;
			}
		}

		Category.getInstance(getClass().getName()).debug("    current_node_type: " + current_node_type);
		Category.getInstance(getClass().getName()).debug("    current_dependency_type: " + current_dependency_type);
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		Category.getInstance(getClass().getName()).debug("qName = " + qName);

		if ("name".equals(qName)) {
			Category.getInstance(getClass().getName()).debug("    Processing <name> tag:");
			Category.getInstance(getClass().getName()).debug("        current_name: " + current_name);
			Category.getInstance(getClass().getName()).debug("        current_node_type: " + current_node_type);

			switch (current_node_type) {
				case PACKAGE:
					current_package = Factory().CreatePackage(current_name.toString());
					current_node    = current_package;
					break;
				case CLASS:
					current_class = Factory().CreateClass(current_name.toString());
					current_node  = current_class;
					fireStartClass(current_class.toString());
					break;
				case FEATURE:
					current_feature = Factory().CreateFeature(current_name.toString());
					current_node    = current_feature;
					break;
			}
		} else if ("outbound".equals(qName)) {
			Category.getInstance(getClass().getName()).debug("    Processing <outbound> tag:");
			Category.getInstance(getClass().getName()).debug("        current_name: " + current_name);
			Category.getInstance(getClass().getName()).debug("        current_dependency_type: " + current_dependency_type);

			Node other = null;
			switch (current_dependency_type) {
				case PACKAGE:
					other = Factory().CreatePackage(current_name.toString());
					break;
				case CLASS:
					other = Factory().CreateClass(current_name.toString());
					break;
				case FEATURE:
					other = Factory().CreateFeature(current_name.toString());
					break;
			}
			current_node.AddDependency(other);
			fireDependency(current_node, other);
		} else if ("inbound".equals(qName)) {
			Category.getInstance(getClass().getName()).debug("    Processing <inbound> tag:");
			Category.getInstance(getClass().getName()).debug("        current_name: " + current_name);
			Category.getInstance(getClass().getName()).debug("        current_dependency_type: " + current_dependency_type);

			Node other = null;
			switch (current_dependency_type) {
				case PACKAGE:
					other = Factory().CreatePackage(current_name.toString());
					break;
				case CLASS:
					other = Factory().CreateClass(current_name.toString());
					break;
				case FEATURE:
					other = Factory().CreateFeature(current_name.toString());
					break;
			}
			other.AddDependency(current_node);
			fireDependency(other, current_node);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		current_name.append(ch, start, length);
		Category.getInstance(getClass().getName()).debug("characters: \"" + new String(ch, start, length) + "\"");
	}

	public void addDependencyListener(DependencyListener listener) {
		synchronized(dependency_listeners) {
			dependency_listeners.add(listener);
		}
	}

	public void removeDependencyListener(DependencyListener listener) {
		synchronized(dependency_listeners) {
			dependency_listeners.remove(listener);
		}
	}

	protected void fireStartClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).StartClass(event);
		}
	}

	protected void fireStopClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).StopClass(event);
		}
	}
	
	protected void fireDependency(Node dependent, Node dependable) {
		DependencyEvent event = new DependencyEvent(this, dependent, dependable);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).Dependency(event);
		}
	}
}
