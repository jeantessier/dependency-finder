/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class NodeLoader {
    private static final String  DEFAULT_READER_CLASS_NAME = "org.apache.xerces.parsers.SAXParser";
    private static final boolean DEFAULT_VALIDATE          = false;

    private NodeHandler handler;
    private String      readerClassName;
    private boolean     validate;

    public NodeLoader() {
        this(new NodeFactory(), DEFAULT_READER_CLASS_NAME, DEFAULT_VALIDATE);
    }

    public NodeLoader(NodeFactory factory) {
        this(factory, DEFAULT_READER_CLASS_NAME, DEFAULT_VALIDATE);
    }

    public NodeLoader(String readerClassName) {
        this(new NodeFactory(), readerClassName, DEFAULT_VALIDATE);
    }

    public NodeLoader(boolean validate) {
        this(new NodeFactory(), DEFAULT_READER_CLASS_NAME, validate);
    }

    public NodeLoader(NodeFactory factory, String readerClassName) {
        this(factory, readerClassName, DEFAULT_VALIDATE);
    }

    public NodeLoader(NodeFactory factory, boolean validate) {
        this(factory, DEFAULT_READER_CLASS_NAME, validate);
    }

    public NodeLoader(String readerClassName, boolean validate) {
        this(new NodeFactory(), readerClassName, validate);
    }
    
    public NodeLoader(NodeFactory factory, String readerClassName, boolean validate) {
        this.handler         = new NodeHandler(factory);
        this.readerClassName = readerClassName;
        this.validate        = validate;
    }

    public NodeFactory load(String filename) throws IOException, SAXException {
        NodeFactory result = null;

        FileReader in = null;
        try {
            in = new FileReader(filename);
            result = load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result;
    }

    public NodeFactory load(InputStream in) throws IOException, SAXException {
        return load(new InputSource(in));
    }

    public NodeFactory load(Reader in) throws IOException, SAXException {
        return load(new InputSource(in));
    }

    public NodeFactory load(InputSource in) throws IOException, SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader(readerClassName);
        reader.setDTDHandler(handler);
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);

        try {
            if (validate) {
                Logger.getLogger(getClass()).warn("XML validation turned on");
                reader.setFeature("http://xml.org/sax/features/validation", true);
                reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
            } else {
                Logger.getLogger(getClass()).info("XML validation turned off");
                reader.setFeature("http://xml.org/sax/features/validation", false);
                reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass()).warn("Problem setting validation feature on XML reader",ex);
        }
    
        reader.parse(in);
    
        return handler.getFactory();
    }

    public void addDependencyListener(DependencyListener listener) {
        handler.addDependencyListener(listener);
    }

    public void removeDependencyListener(DependencyListener listener) {
        handler.removeDependencyListener(listener);
    }
}
