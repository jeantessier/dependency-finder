/*
 *  Copyright (c) 2001-2003, Jean Tessier
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
		Logger.getLogger(getClass()).debug("qName = " + qName);

		for (int i=0; i<atts.getLength(); i++) {
			Logger.getLogger(getClass()).debug("    " + atts.getQName(i) + ": " + atts.getValue(i));
		}

		current_name.delete(0, current_name.length());

		if ("dependencies".equals(qName)) {
			fireBeginSession();
		} else if ("package".equals(qName)) {
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

		Logger.getLogger(getClass()).debug("    current_node_type: " + current_node_type);
		Logger.getLogger(getClass()).debug("    current_dependency_type: " + current_dependency_type);
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		Logger.getLogger(getClass()).debug("qName = " + qName);

		if ("dependencies".equals(qName)) {
			fireEndSession();
		} else if ("name".equals(qName)) {
			Logger.getLogger(getClass()).debug("    Processing <name> tag:");
			Logger.getLogger(getClass()).debug("        current_name: " + current_name);
			Logger.getLogger(getClass()).debug("        current_node_type: " + current_node_type);

			switch (current_node_type) {
				case PACKAGE:
					current_package = Factory().CreatePackage(current_name.toString());
					current_node    = current_package;
					break;
				case CLASS:
					current_class = Factory().CreateClass(current_name.toString());
					current_node  = current_class;
					fireBeginClass(current_class.toString());
					break;
				case FEATURE:
					current_feature = Factory().CreateFeature(current_name.toString());
					current_node    = current_feature;
					break;
			}
		} else if ("outbound".equals(qName)) {
			Logger.getLogger(getClass()).debug("    Processing <outbound> tag:");
			Logger.getLogger(getClass()).debug("        current_name: " + current_name);
			Logger.getLogger(getClass()).debug("        current_dependency_type: " + current_dependency_type);

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
			Logger.getLogger(getClass()).debug("    Processing <inbound> tag:");
			Logger.getLogger(getClass()).debug("        current_name: " + current_name);
			Logger.getLogger(getClass()).debug("        current_dependency_type: " + current_dependency_type);

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
		Logger.getLogger(getClass()).debug("characters: \"" + new String(ch, start, length) + "\"");
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

	protected void fireBeginSession() {
		DependencyEvent event = new DependencyEvent(this);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).BeginSession(event);
		}
	}
	
	protected void fireBeginClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).BeginClass(event);
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

	protected void fireEndClass(String classname) {
		DependencyEvent event = new DependencyEvent(this, classname);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).EndClass(event);
		}
	}

	protected void fireEndSession() {
		DependencyEvent event = new DependencyEvent(this);

		HashSet listeners;
		synchronized(dependency_listeners) {
			listeners = (HashSet) dependency_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((DependencyListener) i.next()).EndSession(event);
		}
	}
}
