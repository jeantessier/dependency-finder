/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

import java.util.*;

import org.apache.log4j.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class NodeHandler extends DefaultHandler {
    private static final int PACKAGE = 1;
    private static final int CLASS   = 2;
    private static final int FEATURE = 3;

    private NodeFactory  factory;

    private int          currentNodeType;
    private int          currentDependencyType;
    private Attributes   currentDependencyAttributes;
    private Node         currentNode;
    private Attributes   currentPackageAttributes;
    private Attributes   currentClassAttributes;
    private Attributes   currentFeatureAttributes;
    private StringBuffer currentName = new StringBuffer();

    private HashSet<DependencyListener> dependencyListeners = new HashSet<DependencyListener>();

    public NodeHandler() {
        this(new NodeFactory());
    }

    public NodeHandler(NodeFactory factory) {
        this.factory = factory;
    }

    public NodeFactory getFactory() {
        return factory;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        Logger.getLogger(getClass()).debug("qName = " + qName);

        for (int i=0; i<atts.getLength(); i++) {
            Logger.getLogger(getClass()).debug("    " + atts.getQName(i) + ": " + atts.getValue(i));
        }

        currentName.delete(0, currentName.length());

        if ("dependencies".equals(qName)) {
            fireBeginSession();
        } else if ("package".equals(qName)) {
            currentNodeType = PACKAGE;
            currentPackageAttributes = new AttributesImpl(atts);
        } else if ("class".equals(qName)) {
            currentNodeType = CLASS;
            currentClassAttributes = new AttributesImpl(atts);
        } else if ("feature".equals(qName)) {
            currentNodeType = FEATURE;
            currentFeatureAttributes = new AttributesImpl(atts);
        } else if ("inbound".equals(qName) || "outbound".equals(qName)) {
            if ("package".equals(atts.getValue("type"))) {
                currentDependencyType = PACKAGE;
            } else if ("class".equals(atts.getValue("type"))) {
                currentDependencyType = CLASS;
            } else if ("feature".equals(atts.getValue("type"))) {
                currentDependencyType = FEATURE;
            }
            currentDependencyAttributes = new AttributesImpl(atts);
        }

        Logger.getLogger(getClass()).debug("    current node type: " + currentNodeType);
        Logger.getLogger(getClass()).debug("    current dependency type: " + currentDependencyType);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        Logger.getLogger(getClass()).debug("qName = " + qName);

        if ("dependencies".equals(qName)) {
            fireEndSession();
        } else if ("name".equals(qName)) {
            Logger.getLogger(getClass()).debug("    Processing <name> tag:");
            Logger.getLogger(getClass()).debug("        current name: " + currentName);
            Logger.getLogger(getClass()).debug("        current node type: " + currentNodeType);

            switch (currentNodeType) {
                case PACKAGE:
                    currentNode = getFactory().createPackage(currentName.toString(), isConfirmed(currentPackageAttributes));
                    break;
                case CLASS:
                    currentNode = getFactory().createClass(currentName.toString(), isConfirmed(currentClassAttributes));
                    fireBeginClass(currentNode.getName());
                    break;
                case FEATURE:
                    currentNode = getFactory().createFeature(currentName.toString(), isConfirmed(currentFeatureAttributes));
                    break;
            }
        } else if ("outbound".equals(qName)) {
            Logger.getLogger(getClass()).debug("    Processing <outbound> tag:");
            Logger.getLogger(getClass()).debug("        current_name: " + currentName);
            Logger.getLogger(getClass()).debug("        current_dependency_type: " + currentDependencyType);

            Node other = null;
            switch (currentDependencyType) {
                case PACKAGE:
                    other = getFactory().createPackage(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
                case CLASS:
                    other = getFactory().createClass(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
                case FEATURE:
                    other = getFactory().createFeature(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
            }
            currentNode.addDependency(other);
            fireDependency(currentNode, other);
        } else if ("inbound".equals(qName)) {
            Logger.getLogger(getClass()).debug("    Processing <inbound> tag:");
            Logger.getLogger(getClass()).debug("        current_name: " + currentName);
            Logger.getLogger(getClass()).debug("        current_dependency_type: " + currentDependencyType);

            Node other = null;
            switch (currentDependencyType) {
                case PACKAGE:
                    other = getFactory().createPackage(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
                case CLASS:
                    other = getFactory().createClass(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
                case FEATURE:
                    other = getFactory().createFeature(currentName.toString(), isConfirmed(currentDependencyAttributes));
                    break;
            }
            other.addDependency(currentNode);
            fireDependency(other, currentNode);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        currentName.append(ch, start, length);
        Logger.getLogger(getClass()).debug("characters: \"" + new String(ch, start, length) + "\"");
    }

    public void addDependencyListener(DependencyListener listener) {
        synchronized(dependencyListeners) {
            dependencyListeners.add(listener);
        }
    }

    public void removeDependencyListener(DependencyListener listener) {
        synchronized(dependencyListeners) {
            dependencyListeners.remove(listener);
        }
    }

    protected void fireBeginSession() {
        DependencyEvent event = new DependencyEvent(this);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.beginSession(event);
        }
    }
    
    protected void fireBeginClass(String classname) {
        DependencyEvent event = new DependencyEvent(this, classname);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.beginClass(event);
        }
    }

    protected void fireDependency(Node dependent, Node dependable) {
        DependencyEvent event = new DependencyEvent(this, dependent, dependable);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.dependency(event);
        }
    }

    protected void fireEndClass(String classname) {
        DependencyEvent event = new DependencyEvent(this, classname);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.endClass(event);
        }
    }

    protected void fireEndSession() {
        DependencyEvent event = new DependencyEvent(this);

        HashSet<DependencyListener> listeners;
        synchronized(dependencyListeners) {
            listeners = (HashSet<DependencyListener>) dependencyListeners.clone();
        }

        for (DependencyListener listener : listeners) {
            listener.endSession(event);
        }
    }

    private boolean isConfirmed(Attributes atts) {
        return atts.getValue("confirmed") == null || "yes".equalsIgnoreCase(atts.getValue("confirmed"));
    }
}
