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

package com.jeantessier.classreader;

import java.io.*;

import junit.framework.*;
import java.util.*;

public class TestDeprecationPrinter extends TestCase {
    private static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

    private ClassfileLoader    loader;
    private StringWriter       writer;
    private DeprecationPrinter printer;
    
    protected void setUp() throws Exception {
        loader = new AggregatingClassfileLoader();
        loader.load(NEW_CLASSPATH);

        writer  = new StringWriter();
        printer = new DeprecationPrinter(new PrintWriter(writer));
    }
    
    public void testOneNonDeprecatedClass() {
        loader.getClassfile("NewPackage.NewClass").accept(printer);

        assertEquals("No deprecation", "", writer.toString());
    }
    
    public void testOneDeprecatedClass() throws IOException {
        loader.getClassfile("ModifiedPackage.DeprecatedInterface").accept(printer);

        Collection entries = parse(writer.toString());
        
        assertTrue("Deprecated class", entries.contains("ModifiedPackage.DeprecatedInterface"));
        assertEquals("Deprecated class", 1, entries.size());
    }
    
    public void testDeprecatedMethods() throws IOException {
        loader.getClassfile("ModifiedPackage.ModifiedClass").accept(printer);

        Collection entries = parse(writer.toString());
        
        assertTrue("Deprecated field",       entries.contains("ModifiedPackage.ModifiedClass.deprecatedField"));
        assertTrue("Deprecated constructor", entries.contains("ModifiedPackage.ModifiedClass.ModifiedClass(int)"));
        assertTrue("Deprecated method",      entries.contains("ModifiedPackage.ModifiedClass.deprecatedMethod()"));
        assertEquals("Modified class", 3, entries.size());
    }
    
    public void testListenerBehavior() throws IOException {
        loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(printer));
        loader.load(NEW_CLASSPATH);

        Collection entries = parse(writer.toString());
        
        assertTrue("Deprecated class",       entries.contains("ModifiedPackage.DeprecatedClass"));
        assertTrue("Deprecated interface",   entries.contains("ModifiedPackage.DeprecatedInterface"));
        assertTrue("Deprecated field",       entries.contains("ModifiedPackage.ModifiedClass.deprecatedField"));
        assertTrue("Deprecated field",       entries.contains("ModifiedPackage.ModifiedInterface.deprecatedField"));
        assertTrue("Deprecated constructor", entries.contains("ModifiedPackage.DeprecatedClass.DeprecatedClass()"));
        assertTrue("Deprecated constructor", entries.contains("ModifiedPackage.ModifiedClass.ModifiedClass(int)"));
        assertTrue("Deprecated method",      entries.contains("ModifiedPackage.ModifiedClass.deprecatedMethod()"));
        assertTrue("Deprecated method",      entries.contains("ModifiedPackage.ModifiedInterface.deprecatedMethod()"));
        assertEquals("Classpath " + entries, 8, entries.size());
    }

    private Collection parse(String text) throws IOException {
        Collection result = new HashSet();
        
        BufferedReader in = new BufferedReader(new StringReader(text));
        String line;
        while ((line = in.readLine()) != null) {
            result.add(line);
        }
        in.close();

        return result;
    }
}
