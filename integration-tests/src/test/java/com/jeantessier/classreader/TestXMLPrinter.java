/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import org.apache.oro.text.perl.Perl5Util;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TestXMLPrinter extends MockObjectTestCase {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final String TEST_DIRECTORY = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "../etc";

    private static final String ANNOTATION_TYPE = "foobar";
    private static final String ELEMENT_NAME = "foo";

    private ClassfileLoader loader;
    private StringWriter buffer;
    private Visitor printer;
    private XMLReader reader;
    private ErrorHandler errorHandler;

    private Perl5Util perl;

    protected void setUp() throws Exception {
        super.setUp();

        loader = new AggregatingClassfileLoader();

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

        boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);

        errorHandler = mock(ErrorHandler.class);
        reader.setErrorHandler(errorHandler);

        perl = new Perl5Util();
    }
    
    public void testDefaultDTDPrefix() throws Exception {
        checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }
    
    public void testSpecificDTDPrefix() throws Exception {
        checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testDefaultEncoding() throws Exception {
        checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testSpecificEncoding() throws Exception {
        checking(new Expectations() {{
            oneOf (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existent document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testSingleClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        loader.getClassfile(TEST_CLASS).accept(printer);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile[this-class='" + TEST_CLASS + "']", 1);
    }

    public void testZeroClassfile() throws Exception {
        printer.visitClassfiles(Collections.emptyList());

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile[this-class='" + TEST_CLASS + "']", 0);
    }

    public void testOneClassfile() throws Exception {
        loader.load(Collections.singleton(TEST_FILENAME));

        printer.visitClassfiles(loader.getAllClassfiles());

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    public void testMultipleClassfiles() throws Exception {
        loader.load(Collections.singleton(TEST_DIRECTORY));

        printer.visitClassfiles(loader.getAllClassfiles());

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "*/classfile", loader.getAllClassfiles().size());
    }

    public void testNonPublicClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isPublic();
                will(returnValue(false));
            ignoring(mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/public", 0);
    }

    public void testPublicClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isPublic(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/public", 1);
    }

    public void testNonFinalClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isFinal(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/final", 0);
    }

    public void testFinalClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isFinal(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/final", 1);
    }

    public void testNonSuperClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isSuper(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/super", 0);
    }

    public void testSuperClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isSuper(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/super", 1);
    }

    public void testNonInterfaceClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isInterface(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-interface", 0);
    }

    public void testInterfaceClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isInterface(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-interface", 1);
    }

    public void testNonAbstractClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isAbstract(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/abstract", 0);
    }

    public void testAbstractClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isAbstract(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/abstract", 1);
    }

    public void testNonSyntheticClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isSynthetic(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/synthetic", 0);
    }

    public void testSyntheticClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isSynthetic(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/synthetic", 1);
    }

    public void testNonAnnotationClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isAnnotation(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-annotation", 0);
    }

    public void testAnnotationClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isAnnotation(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-annotation", 1);
    }

    public void testNonEnumClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isEnum(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/enum", 0);
    }

    public void testEnumClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isEnum(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/enum", 1);
    }

    public void testNonModuleClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isModule(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/module", 0);
    }

    public void testModuleClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (mockClassfile).isModule(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-module", 1);
    }

    public void testNonPublicField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPublic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/public", 0);
    }

    public void testPublicField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPublic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/public", 1);
    }

    public void testNonProtectedField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isProtected(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/protected", 0);
    }

    public void testProtectedField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isProtected(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/protected", 1);
    }

    public void testNonPrivateField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/private", 0);
    }

    public void testPrivateField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/private", 1);
    }

    public void testNonStaticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isStatic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/static", 0);
    }

    public void testStaticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isStatic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/static", 1);
    }

    public void testNonFinalField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isFinal(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/final", 0);
    }

    public void testFinalField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isFinal(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/final", 1);
    }

    public void testNonVolatileField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isVolatile(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/volatile", 0);
    }

    public void testVolatileField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isVolatile(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/volatile", 1);
    }

    public void testNonTransientField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isTransient(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/transient", 0);
    }

    public void testTransientField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isTransient(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/transient", 1);
    }

    public void testNonSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isSynthetic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 0);
    }

    public void testSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isSynthetic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 1);
    }

    public void testNonEnumField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isEnum(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/enum", 0);
    }

    public void testEnumField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            oneOf (mockField).isEnum(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/enum", 1);
    }

    public void testNonPublicMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isPublic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/public", 0);
    }

    public void testPublicMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isPublic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/public", 1);
    }

    public void testNonPrivateMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isPrivate(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/private", 0);
    }

    public void testPrivateMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isPrivate(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/private", 1);
    }

    public void testNonProtectedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isProtected(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/protected", 0);
    }

    public void testProtectedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isProtected(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/protected", 1);
    }

    public void testNonStaticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isStatic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/static", 0);
    }

    public void testStaticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isStatic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/static", 1);
    }

    public void testNonFinalMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isFinal(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/final", 0);
    }

    public void testFinalMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isFinal(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/final", 1);
    }

    public void testNonSynchronizedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isSynchronized(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synchronized", 0);
    }

    public void testSynchronizedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isSynchronized(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synchronized", 1);
    }

    public void testNonBridgeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isBridge(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/bridge", 0);
    }

    public void testBridgeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isBridge(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/bridge", 1);
    }

    public void testNonVarargsMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isVarargs(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/varargs", 0);
    }

    public void testVarargsMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isVarargs(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/varargs", 1);
    }

    public void testNonNativeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isNative(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/native", 0);
    }

    public void testNativeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isNative(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/native", 1);
    }

    public void testNonAbstractMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isAbstract(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/abstract", 0);
    }

    public void testAbstractMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isAbstract(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/abstract", 1);
    }

    public void testNonStrictMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isStrict(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/strict", 0);
    }

    public void testStrictMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isStrict(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/strict", 1);
    }

    public void testNonSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isSynthetic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 0);
    }

    public void testSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            oneOf (mockMethod).isSynthetic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 1);
    }

    public void testNonPublicInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isPublic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/public", 0);
    }

    public void testPublicInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isPublic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/public", 1);
    }

    public void testNonPrivateInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isPrivate(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/private", 0);
    }

    public void testPrivateInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isPrivate(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/private", 1);
    }

    public void testNonProtectedInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isProtected(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/protected", 0);
    }

    public void testProtectedInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isProtected(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/protected", 1);
    }

    public void testNonStaticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isStatic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/static", 0);
    }

    public void testStaticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isStatic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/static", 1);
    }

    public void testNonFinalInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isFinal(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/final", 0);
    }

    public void testFinalInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isFinal(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/final", 1);
    }

    public void testNonInterfaceInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isInterface(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-interface", 0);
    }

    public void testInterfaceInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isInterface(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-interface", 1);
    }

    public void testNonAbstractInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isAbstract(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/abstract", 0);
    }

    public void testAbstractInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isAbstract(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/abstract", 1);
    }

    public void testNonSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isSynthetic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 0);
    }

    public void testSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isSynthetic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 1);
    }

    public void testNonAnnotationInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isAnnotation(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-annotation", 0);
    }

    public void testAnnotationInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isAnnotation(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-annotation", 1);
    }

    public void testNonEnumInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isEnum(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/enum", 0);
    }

    public void testEnumInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            oneOf (mockInnerClass).isEnum(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/enum", 1);
    }

    public void testVisitEnclosingMethod_noMethod() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final int classIndex = 123;
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 0;

        checking(new Expectations() {{
            oneOf (mockEnclosingMethod).getClassIndex();
                will(returnValue(classIndex));
            oneOf (mockEnclosingMethod).getRawClassInfo();
                will(returnValue(mockClassInfo));
            oneOf (mockClassInfo).accept(printer);
            oneOf (mockEnclosingMethod).getMethodIndex();
                will(returnValue(methodIndex));
        }});

        printer.visitEnclosingMethod_attribute(mockEnclosingMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "enclosing-method-attribute/class", 1);
        assertXPathCount(xmlDocument, "enclosing-method-attribute/method", 1);
    }

    public void testVisitEnclosingMethod_regularMethod() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final int classIndex = 123;
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 456;
        final NameAndType_info mockNameAndType = mock(NameAndType_info.class);

        checking(new Expectations() {{
            oneOf (mockEnclosingMethod).getClassIndex();
                will(returnValue(classIndex));
            oneOf (mockEnclosingMethod).getRawClassInfo();
                will(returnValue(mockClassInfo));
            oneOf (mockClassInfo).accept(printer);
            oneOf (mockEnclosingMethod).getMethodIndex();
                will(returnValue(methodIndex));
            oneOf (mockEnclosingMethod).getRawMethod();
                will(returnValue(mockNameAndType));
            exactly(2).of (mockNameAndType).getName();
                will(returnValue("testMethod"));
            exactly(2).of (mockNameAndType).getType();
                will(returnValue("()V"));
        }});

        printer.visitEnclosingMethod_attribute(mockEnclosingMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "enclosing-method-attribute/class", 1);
        assertXPathCount(xmlDocument, "enclosing-method-attribute/method", 1);
        assertXPathText(xmlDocument, "enclosing-method-attribute/method", "void testMethod()");
    }

    public void testVisitEnclosingMethod_constructor() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final int classIndex = 123;
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 456;
        final NameAndType_info mockNameAndType = mock(NameAndType_info.class);

        checking(new Expectations() {{
            oneOf (mockEnclosingMethod).getClassIndex();
                will(returnValue(classIndex));
            oneOf (mockEnclosingMethod).getRawClassInfo();
                will(returnValue(mockClassInfo));
            oneOf (mockClassInfo).accept(printer);
            oneOf (mockEnclosingMethod).getClassInfo();
                will(returnValue("com.jeantessier.test.TestClass"));
            oneOf (mockEnclosingMethod).getMethodIndex();
                will(returnValue(methodIndex));
            oneOf (mockEnclosingMethod).getRawMethod();
                will(returnValue(mockNameAndType));
            oneOf (mockNameAndType).getName();
                will(returnValue("<init>"));
            oneOf (mockNameAndType).getType();
                will(returnValue("()V"));
        }});

        printer.visitEnclosingMethod_attribute(mockEnclosingMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "enclosing-method-attribute/class", 1);
        assertXPathCount(xmlDocument, "enclosing-method-attribute/method", 1);
        assertXPathText(xmlDocument, "enclosing-method-attribute/method", "TestClass()");
    }

    public void testVisitLocalVariable() throws Exception {
        final LocalVariable localVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            oneOf (localVariable).getStartPC();
            oneOf (localVariable).getLength();
            oneOf (localVariable).getRawName();
            oneOf (localVariable).getDescriptor();
                will(returnValue("I"));
            oneOf (localVariable).getIndex();
        }});

        printer.visitLocalVariable(localVariable);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "local-variable/@pc", 1);
        assertXPathCount(xmlDocument, "local-variable/@length", 1);
        assertXPathCount(xmlDocument, "local-variable/name", 1);
        assertXPathText(xmlDocument, "local-variable/type", "int");
        assertXPathCount(xmlDocument, "local-variable/@index", 1);
    }

    public void testVisitLocalVariableType() throws Exception {
        final LocalVariableType localVariableType = mock(LocalVariableType.class);

        checking(new Expectations() {{
            oneOf (localVariableType).getStartPC();
            oneOf (localVariableType).getLength();
            oneOf (localVariableType).getRawName();
            oneOf (localVariableType).getRawSignature();
            oneOf (localVariableType).getIndex();
        }});

        printer.visitLocalVariableType(localVariableType);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "local-variable-type/@pc", 1);
        assertXPathCount(xmlDocument, "local-variable-type/@length", 1);
        assertXPathCount(xmlDocument, "local-variable-type/name", 1);
        assertXPathCount(xmlDocument, "local-variable-type/signature", 1);
        assertXPathCount(xmlDocument, "local-variable-type/@index", 1);
    }

    public void testVisitBootstrapMethod_noArguments() throws Exception {
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);
        final int bootstrapMethodRef = 123;
        final MethodHandle_info methodHandle = mock(MethodHandle_info.class);

        checking(new Expectations() {{
            oneOf (bootstrapMethod).getBootstrapMethodRef();
                will(returnValue(bootstrapMethodRef));
            oneOf (bootstrapMethod).getBootstrapMethod();
                will(returnValue(methodHandle));
            oneOf (methodHandle).accept(printer);
            oneOf (bootstrapMethod).getArgumentIndices();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitBootstrapMethod(bootstrapMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "bootstrap-method", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref/@index", 1);
        assertXPathText(xmlDocument, "bootstrap-method/bootstrap-method-ref/@index", String.valueOf(bootstrapMethodRef));
        assertXPathCount(xmlDocument, "bootstrap-method/arguments", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument", 0);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument/@index", 0);
    }

    public void testVisitBootstrapMethod_oneArgument() throws Exception {
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);
        final int bootstrapMethodRef = 123;
        final MethodHandle_info methodHandle = mock(MethodHandle_info.class);
        final int argumentIndex = 456;
        final ConstantPoolEntry argument = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (bootstrapMethod).getBootstrapMethodRef();
                will(returnValue(bootstrapMethodRef));
            oneOf (bootstrapMethod).getBootstrapMethod();
                will(returnValue(methodHandle));
            oneOf (methodHandle).accept(printer);
            oneOf (bootstrapMethod).getArgumentIndices();
                will(returnValue(Collections.singleton(argumentIndex)));
            oneOf (bootstrapMethod).getArgument(argumentIndex);
                will(returnValue(argument));
            oneOf (argument).accept(printer);
        }});

        printer.visitBootstrapMethod(bootstrapMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "bootstrap-method", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref/@index", 1);
        assertXPathText(xmlDocument, "bootstrap-method/bootstrap-method-ref/@index", String.valueOf(bootstrapMethodRef));
        assertXPathCount(xmlDocument, "bootstrap-method/arguments", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument/@index", 1);
        assertXPathText(xmlDocument, "bootstrap-method/arguments/argument/@index", String.valueOf(argumentIndex));
    }

    public void testVisitBootstrapMethod_multipleArguments() throws Exception {
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);
        final int bootstrapMethodRef = 123;
        final MethodHandle_info methodHandle = mock(MethodHandle_info.class);
        final int firstArgumentIndex = 456;
        final ConstantPoolEntry firstArgument = mock(ConstantPoolEntry.class, "first argument");
        final int secondArgumentIndex = 789;
        final ConstantPoolEntry secondArgument = mock(ConstantPoolEntry.class, "second argument");

        checking(new Expectations() {{
            oneOf (bootstrapMethod).getBootstrapMethodRef();
                will(returnValue(bootstrapMethodRef));
            oneOf (bootstrapMethod).getBootstrapMethod();
                will(returnValue(methodHandle));
            oneOf (methodHandle).accept(printer);
            oneOf (bootstrapMethod).getArgumentIndices();
                will(returnValue(List.of(firstArgumentIndex, secondArgumentIndex)));
            oneOf (bootstrapMethod).getArgument(firstArgumentIndex);
                will(returnValue(firstArgument));
            oneOf (firstArgument).accept(printer);
            oneOf (bootstrapMethod).getArgument(secondArgumentIndex);
                will(returnValue(secondArgument));
            oneOf (secondArgument).accept(printer);
        }});

        printer.visitBootstrapMethod(bootstrapMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "bootstrap-method", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/bootstrap-method-ref/@index", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments", 1);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument", 2);
        assertXPathCount(xmlDocument, "bootstrap-method/arguments/argument/@index", 2);
    }

    public void testVisitRuntimeVisibleAnnotations_attribute_WithoutAnnotations() throws Exception {
        final RuntimeVisibleAnnotations_attribute runtimeVisibleAnnotations = mock(RuntimeVisibleAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeVisibleAnnotations).getAnnotations();
        }});

        printer.visitRuntimeVisibleAnnotations_attribute(runtimeVisibleAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-annotations-attribute/annotations", 1);
    }

    public void testVisitRuntimeVisibleAnnotations_attribute_WithAnAnnotation() throws Exception {
        final RuntimeVisibleAnnotations_attribute runtimeVisibleAnnotations = mock(RuntimeVisibleAnnotations_attribute.class);
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeVisibleAnnotations).getAnnotations();
                will(returnValue(Collections.singleton(annotation)));
            oneOf (annotation).accept(printer);
        }});

        printer.visitRuntimeVisibleAnnotations_attribute(runtimeVisibleAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-annotations-attribute/annotations", 1);
    }

    public void testVisitRuntimeInvisibleAnnotations_attribute_WithoutAnnotations() throws Exception {
        final RuntimeInvisibleAnnotations_attribute runtimeInvisibleAnnotations = mock(RuntimeInvisibleAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeInvisibleAnnotations).getAnnotations();
        }});

        printer.visitRuntimeInvisibleAnnotations_attribute(runtimeInvisibleAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-annotations-attribute/annotations", 1);
    }

    public void testVisitRuntimeInvisibleAnnotations_attribute_WithAnAnnotation() throws Exception {
        final RuntimeInvisibleAnnotations_attribute runtimeInvisibleAnnotations = mock(RuntimeInvisibleAnnotations_attribute.class);
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeInvisibleAnnotations).getAnnotations();
                will(returnValue(Collections.singleton(annotation)));
            oneOf (annotation).accept(printer);
        }});

        printer.visitRuntimeInvisibleAnnotations_attribute(runtimeInvisibleAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-annotations-attribute/annotations", 1);
    }

    public void testVisitRuntimeVisibleParameterAnnotations_attribute_WithoutParameterAnnotations() throws Exception {
        final RuntimeVisibleParameterAnnotations_attribute runtimeVisibleParameterAnnotations = mock(RuntimeVisibleParameterAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeVisibleParameterAnnotations).getParameterAnnotations();
        }});

        printer.visitRuntimeVisibleParameterAnnotations_attribute(runtimeVisibleParameterAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-parameter-annotations-attribute/parameter-annotations", 1);
    }

    public void testVisitRuntimeVisibleParameterAnnotations_attribute_WithAParameterAnnotation() throws Exception {
        final RuntimeVisibleParameterAnnotations_attribute runtimeVisibleParameterAnnotations = mock(RuntimeVisibleParameterAnnotations_attribute.class);
        final ParameterAnnotation parameterAnnotation = mock(ParameterAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeVisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(parameterAnnotation)));
            oneOf (parameterAnnotation).accept(printer);
        }});

        printer.visitRuntimeVisibleParameterAnnotations_attribute(runtimeVisibleParameterAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-parameter-annotations-attribute/parameter-annotations", 1);
    }

    public void testVisitRuntimeInvisibleParameterAnnotations_attribute_WithoutParameterAnnotations() throws Exception {
        final RuntimeInvisibleParameterAnnotations_attribute runtimeInvisibleParameterAnnotations = mock(RuntimeInvisibleParameterAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeInvisibleParameterAnnotations).getParameterAnnotations();
        }});

        printer.visitRuntimeInvisibleParameterAnnotations_attribute(runtimeInvisibleParameterAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-parameter-annotations-attribute/parameter-annotations", 1);
    }

    public void testVisitRuntimeInvisibleParameterAnnotations_attribute_WithAParameterAnnotation() throws Exception {
        final RuntimeInvisibleParameterAnnotations_attribute runtimeInvisibleParameterAnnotations = mock(RuntimeInvisibleParameterAnnotations_attribute.class);
        final ParameterAnnotation parameterAnnotation = mock(ParameterAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeInvisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(parameterAnnotation)));
            oneOf (parameterAnnotation).accept(printer);
        }});

        printer.visitRuntimeInvisibleParameterAnnotations_attribute(runtimeInvisibleParameterAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-parameter-annotations-attribute/parameter-annotations", 1);
    }

    public void testVisitRuntimeVisibleTypeAnnotations_attribute_WithoutAnnotations() throws Exception {
        final RuntimeVisibleTypeAnnotations_attribute attribute = mock(RuntimeVisibleTypeAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
        }});

        printer.visitRuntimeVisibleTypeAnnotations_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-type-annotations-attribute", 1);
        assertXPathCount(xmlDocument, "runtime-visible-type-annotations-attribute/type-annotations", 1);
    }

    public void testVisitRuntimeVisibleTypeAnnotations_attribute_WithAnAnnotation() throws Exception {
        final RuntimeVisibleTypeAnnotations_attribute attribute = mock(RuntimeVisibleTypeAnnotations_attribute.class);
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
                will(returnValue(Collections.singletonList(typeAnnotation)));
            oneOf (typeAnnotation).accept(printer);
        }});

        printer.visitRuntimeVisibleTypeAnnotations_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-visible-type-annotations-attribute", 1);
        assertXPathCount(xmlDocument, "runtime-visible-type-annotations-attribute/type-annotations", 1);
    }

    public void testVisitRuntimeInvisibleTypeAnnotations_attribute_WithoutParameterAnnotations() throws Exception {
        final RuntimeInvisibleTypeAnnotations_attribute attribute = mock(RuntimeInvisibleTypeAnnotations_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
        }});

        printer.visitRuntimeInvisibleTypeAnnotations_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-type-annotations-attribute", 1);
        assertXPathCount(xmlDocument, "runtime-invisible-type-annotations-attribute/type-annotations", 1);
    }

    public void testVisitRuntimeInvisibleTypeAnnotations_attribute_WithAParameterAnnotation() throws Exception {
        final RuntimeInvisibleTypeAnnotations_attribute attribute = mock(RuntimeInvisibleTypeAnnotations_attribute.class);
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getTypeAnnotations();
                will(returnValue(Collections.singletonList(typeAnnotation)));
            oneOf (typeAnnotation).accept(printer);
        }});

        printer.visitRuntimeInvisibleTypeAnnotations_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-type-annotations-attribute", 1);
        assertXPathCount(xmlDocument, "runtime-invisible-type-annotations-attribute/type-annotations", 1);
    }

    public void testVisitAnnotationDefault_attribute() throws Exception {
        final AnnotationDefault_attribute annotationDefault = mock(AnnotationDefault_attribute.class);
        final ElementValue elementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (annotationDefault).getElemementValue();
                will(returnValue(elementValue));
            oneOf (elementValue).accept(printer);
        }});

        printer.visitAnnotationDefault_attribute(annotationDefault);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation-default-attribute", 1);
    }

    public void testVisitStackMapTable_attribute_noStackMapFrames() throws Exception {
        final StackMapTable_attribute stackMapTable = mock(StackMapTable_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (stackMapTable).getEntries();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitStackMapTable_attribute(stackMapTable);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "stack-map-table-attribute", 1);
    }

    public void testVisitStackMapTable_attribute_oneStackMapFrame() throws Exception {
        final StackMapTable_attribute stackMapTable = mock(StackMapTable_attribute.class);
        final StackMapFrame stackMapFrame = mock(StackMapFrame.class);

        checking(new Expectations() {{
            atLeast(1).of (stackMapTable).getEntries();
                will(returnValue(Collections.singleton(stackMapFrame)));
            oneOf (stackMapFrame).accept(printer);
        }});

        printer.visitStackMapTable_attribute(stackMapTable);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "stack-map-table-attribute", 1);
    }

    public void testVisitBootstrapMethods_attribute_noBootstrapMethods() throws Exception {
        final BootstrapMethods_attribute bootstrapMethods = mock(BootstrapMethods_attribute.class);

        checking(new Expectations() {{
            atLeast (1).of (bootstrapMethods).getBootstrapMethods();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitBootstrapMethods_attribute(bootstrapMethods);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "bootstrap-methods-attribute", 1);
    }

    public void testVisitBootstrapMethods_attribute_oneBootstrapMethod() throws Exception {
        final BootstrapMethods_attribute bootstrapMethods = mock(BootstrapMethods_attribute.class);
        final BootstrapMethod bootstrapMethod = mock(BootstrapMethod.class);

        checking(new Expectations() {{
            atLeast (1).of (bootstrapMethods).getBootstrapMethods();
                will(returnValue(Collections.singleton(bootstrapMethod)));
            oneOf (bootstrapMethod).accept(printer);
        }});

        printer.visitBootstrapMethods_attribute(bootstrapMethods);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "bootstrap-methods-attribute", 1);
    }

    public void testVisitMethodParameters_attribute_noMethodParameters() throws Exception {
        final MethodParameters_attribute attribute = mock(MethodParameters_attribute.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getMethodParameters();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitMethodParameters_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-parameters-attribute", 1);
    }

    public void testVisitMethodParameters_attribute_oneMethodParameter() throws Exception {
        final MethodParameters_attribute attribute = mock(MethodParameters_attribute.class);
        final MethodParameter methodParameter = mock(MethodParameter.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getMethodParameters();
                will(returnValue(Collections.singleton(methodParameter)));
            oneOf (methodParameter).accept(printer);
        }});

        printer.visitMethodParameters_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-parameters-attribute", 1);
    }

    public void testVisitMethodParameter() throws Exception {
        final MethodParameter methodParameter = mock(MethodParameter.class);
        final int accessFlags = 123;
        final String expectedAccessFlags = "00000000 01111011"; // 123 in binary
        final UTF8_info mockUtf8_info = mock(UTF8_info.class);

        checking(new Expectations() {{
            oneOf (methodParameter).getRawName();
                will(returnValue(mockUtf8_info));
            oneOf (mockUtf8_info).accept(printer);
            oneOf (methodParameter).getAccessFlags();
                will(returnValue(accessFlags));
            oneOf (methodParameter).isFinal();
                will(returnValue(true));
            oneOf (methodParameter).isSynthetic();
                will(returnValue(true));
            oneOf (methodParameter).isMandated();
                will(returnValue(true));
        }});

        printer.visitMethodParameter(methodParameter);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-parameter", 1);
        assertXPathCount(xmlDocument, "method-parameter/@access-flags", 1);
        assertXPathText(xmlDocument, "method-parameter/@access-flags", expectedAccessFlags);
        assertXPathCount(xmlDocument, "method-parameter/name", 1);
        assertXPathCount(xmlDocument, "method-parameter/final", 1);
        assertXPathCount(xmlDocument, "method-parameter/synthetic", 1);
        assertXPathCount(xmlDocument, "method-parameter/mandated", 1);
    }

    public void testVisitModule_attributeWithVersion() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final String expectedModuleFlags = "00000001 11001000"; // 456 in binary
        final int moduleVersionIndex = 789;
        final UTF8_info moduleVersion = mock(UTF8_info.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(true));
            oneOf (attribute).isSynthetic();
                will(returnValue(true));
            oneOf (attribute).isMandated();
                will(returnValue(true));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(true));
            oneOf (attribute).getModuleVersionIndex();
                will(returnValue(moduleVersionIndex));
            oneOf (attribute).getRawModuleVersion();
                will(returnValue(moduleVersion));
            oneOf (moduleVersion).accept(printer);
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/@module-flags", 1);
        assertXPathText(xmlDocument, "module-attribute/@module-flags", expectedModuleFlags);
        assertXPathCount(xmlDocument, "module-attribute/name", 1);
        assertXPathCount(xmlDocument, "module-attribute/name/@index", 1);
        assertXPathText(xmlDocument, "module-attribute/name/@index", String.valueOf(moduleNameIndex));
        assertXPathCount(xmlDocument, "module-attribute/open", 1);
        assertXPathCount(xmlDocument, "module-attribute/synthetic", 1);
        assertXPathCount(xmlDocument, "module-attribute/mandated", 1);
        assertXPathCount(xmlDocument, "module-attribute/version", 1);
        assertXPathCount(xmlDocument, "module-attribute/version/@index", 1);
        assertXPathText(xmlDocument, "module-attribute/version/@index", String.valueOf(moduleVersionIndex));
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModule_attributeWithoutVersion() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(true));
            oneOf (attribute).isSynthetic();
                will(returnValue(true));
            oneOf (attribute).isMandated();
                will(returnValue(true));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/version", 0);
    }

    public void testVisitModule_attributeWithRequires() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final ModuleRequires mockRequires = mock(ModuleRequires.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(false));
            oneOf (attribute).isSynthetic();
                will(returnValue(false));
            oneOf (attribute).isMandated();
                will(returnValue(false));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.singleton(mockRequires)));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockRequires).accept(printer);
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModuleRequiresWithVersion() throws Exception {
        final ModuleRequires moduleRequires = mock(ModuleRequires.class);
        final int requiresIndex = 123;
        final Module_info requires = mock(Module_info.class);
        final int requiresFlags = 456;
        final String expectedRequiresFlags = "00000001 11001000"; // 456 in binary
        final int requiresVersionIndex = 789;
        final UTF8_info requiresVersion = mock(UTF8_info.class);

        checking(new Expectations() {{
            oneOf (moduleRequires).getRequiresIndex();
                will(returnValue(requiresIndex));
            oneOf (moduleRequires).getRawRequires();
                will(returnValue(requires));
            oneOf (requires).accept(printer);
            oneOf (moduleRequires).getRequiresFlags();
                will(returnValue(requiresFlags));
            oneOf (moduleRequires).getRequiresVersionIndex();
                will(returnValue(requiresVersionIndex));
            oneOf (moduleRequires).hasRequiresVersion();
                will(returnValue(true));
            oneOf (moduleRequires).getRawRequiresVersion();
                will(returnValue(requiresVersion));
            oneOf (requiresVersion).accept(printer);
            oneOf (moduleRequires).isTransitive();
                will(returnValue(true));
            oneOf (moduleRequires).isStaticPhase();
                will(returnValue(true));
            oneOf (moduleRequires).isSynthetic();
                will(returnValue(true));
            oneOf (moduleRequires).isMandated();
                will(returnValue(true));
        }});

        printer.visitModuleRequires(moduleRequires);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-requires", 1);
        assertXPathCount(xmlDocument, "module-requires/@requires-flags", 1);
        assertXPathText(xmlDocument, "module-requires/@requires-flags", expectedRequiresFlags);
        assertXPathCount(xmlDocument, "module-requires/module", 1);
        assertXPathCount(xmlDocument, "module-requires/module/@index", 1);
        assertXPathText(xmlDocument, "module-requires/module/@index", String.valueOf(requiresIndex));
        assertXPathCount(xmlDocument, "module-requires/version", 1);
        assertXPathCount(xmlDocument, "module-requires/version/@index", 1);
        assertXPathText(xmlDocument, "module-requires/version/@index", String.valueOf(requiresVersionIndex));
        assertXPathCount(xmlDocument, "module-requires/transitive", 1);
        assertXPathCount(xmlDocument, "module-requires/static-phase", 1);
        assertXPathCount(xmlDocument, "module-requires/synthetic", 1);
        assertXPathCount(xmlDocument, "module-requires/mandated", 1);
    }

    public void testVisitModuleRequiresWithoutVersion() throws Exception {
        final ModuleRequires moduleRequires = mock(ModuleRequires.class);
        final int requiresIndex = 123;
        final Module_info requires = mock(Module_info.class);
        final int requiresFlags = 456;

        checking(new Expectations() {{
            oneOf (moduleRequires).getRequiresIndex();
                will(returnValue(requiresIndex));
            oneOf (moduleRequires).getRawRequires();
                will(returnValue(requires));
            oneOf (requires).accept(printer);
            oneOf (moduleRequires).getRequiresFlags();
                will(returnValue(requiresFlags));
            oneOf (moduleRequires).hasRequiresVersion();
                will(returnValue(false));
            oneOf (moduleRequires).isTransitive();
                will(returnValue(true));
            oneOf (moduleRequires).isStaticPhase();
                will(returnValue(true));
            oneOf (moduleRequires).isSynthetic();
                will(returnValue(true));
            oneOf (moduleRequires).isMandated();
                will(returnValue(true));
        }});

        printer.visitModuleRequires(moduleRequires);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-requires", 1);
        assertXPathCount(xmlDocument, "module-requires/version", 0);
    }

    public void testVisitModule_attributeWithExports() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final ModuleExports mockExports = mock(ModuleExports.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(false));
            oneOf (attribute).isSynthetic();
                will(returnValue(false));
            oneOf (attribute).isMandated();
                will(returnValue(false));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.singleton(mockExports)));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockExports).accept(printer);
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModuleExportsWithoutExportsTos() throws Exception {
        final ModuleExports moduleExports = mock(ModuleExports.class);
        final int exportsIndex = 123;
        final Package_info exports = mock(Package_info.class);
        final int exportsFlags = 456;
        final String expectedRequiresFlags = "00000001 11001000"; // 456 in binary

        checking(new Expectations() {{
            oneOf (moduleExports).getExportsIndex();
                will(returnValue(exportsIndex));
            oneOf (moduleExports).getRawExports();
                will(returnValue(exports));
            oneOf (exports).accept(printer);
            oneOf (moduleExports).getExportsFlags();
                will(returnValue(exportsFlags));
            oneOf (moduleExports).isSynthetic();
                will(returnValue(true));
            oneOf (moduleExports).isMandated();
                will(returnValue(true));
            atLeast (1).of (moduleExports).getExportsTos();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModuleExports(moduleExports);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-exports", 1);
        assertXPathCount(xmlDocument, "module-exports/@exports-flags", 1);
        assertXPathText(xmlDocument, "module-exports/@exports-flags", expectedRequiresFlags);
        assertXPathCount(xmlDocument, "module-exports/package", 1);
        assertXPathCount(xmlDocument, "module-exports/package/@index", 1);
        assertXPathText(xmlDocument, "module-exports/package/@index", String.valueOf(exportsIndex));
        assertXPathCount(xmlDocument, "module-exports/synthetic", 1);
        assertXPathCount(xmlDocument, "module-exports/mandated", 1);
    }

    public void testVisitModuleExportsWithExportsTos() throws Exception {
        final ModuleExports moduleExports = mock(ModuleExports.class);
        final int exportsIndex = 123;
        final Package_info exports = mock(Package_info.class);
        final int exportsFlags = 456;
        final ModuleExportsTo mockExportsTo = mock(ModuleExportsTo.class);

        checking(new Expectations() {{
            oneOf (moduleExports).getExportsIndex();
                will(returnValue(exportsIndex));
            oneOf (moduleExports).getRawExports();
                will(returnValue(exports));
            oneOf (exports).accept(printer);
            oneOf (moduleExports).getExportsFlags();
                will(returnValue(exportsFlags));
            oneOf (moduleExports).isSynthetic();
                will(returnValue(false));
            oneOf (moduleExports).isMandated();
                will(returnValue(false));
            atLeast (1).of (moduleExports).getExportsTos();
                will(returnValue(Collections.singleton(mockExportsTo)));
            oneOf (mockExportsTo).accept(printer);
        }});

        printer.visitModuleExports(moduleExports);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-exports", 1);
    }

    public void testVisitModuleExportsTo() throws Exception {
        final ModuleExportsTo moduleExportsTo = mock(ModuleExportsTo.class);
        final int exportsToIndex = 123;
        final Module_info exportsTo = mock(Module_info.class);

        checking(new Expectations() {{
            oneOf (moduleExportsTo).getExportsToIndex();
                will(returnValue(exportsToIndex));
            oneOf (moduleExportsTo).getRawExportsTo();
                will(returnValue(exportsTo));
            oneOf (exportsTo).accept(printer);
        }});

        printer.visitModuleExportsTo(moduleExportsTo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-exports-to", 1);
        assertXPathCount(xmlDocument, "module-exports-to/module", 1);
        assertXPathCount(xmlDocument, "module-exports-to/module/@index", 1);
        assertXPathText(xmlDocument, "module-exports-to/module/@index", String.valueOf(exportsToIndex));
    }

    public void testVisitModule_attributeWithOpens() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final ModuleOpens mockOpens = mock(ModuleOpens.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(false));
            oneOf (attribute).isSynthetic();
                will(returnValue(false));
            oneOf (attribute).isMandated();
                will(returnValue(false));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.singleton(mockOpens)));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockOpens).accept(printer);
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModuleOpensWithoutOpensTos() throws Exception {
        final ModuleOpens moduleOpens = mock(ModuleOpens.class);
        final int opensIndex = 123;
        final Package_info opens = mock(Package_info.class);
        final int opensFlags = 456;
        final String expectedRequiresFlags = "00000001 11001000"; // 456 in binary

        checking(new Expectations() {{
            oneOf (moduleOpens).getOpensIndex();
                will(returnValue(opensIndex));
            oneOf (moduleOpens).getRawOpens();
                will(returnValue(opens));
            oneOf (opens).accept(printer);
            oneOf (moduleOpens).getOpensFlags();
                will(returnValue(opensFlags));
            oneOf (moduleOpens).isSynthetic();
                will(returnValue(true));
            oneOf (moduleOpens).isMandated();
                will(returnValue(true));
            atLeast (1).of (moduleOpens).getOpensTos();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModuleOpens(moduleOpens);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-opens", 1);
        assertXPathCount(xmlDocument, "module-opens/@opens-flags", 1);
        assertXPathText(xmlDocument, "module-opens/@opens-flags", expectedRequiresFlags);
        assertXPathCount(xmlDocument, "module-opens/package", 1);
        assertXPathCount(xmlDocument, "module-opens/package/@index", 1);
        assertXPathText(xmlDocument, "module-opens/package/@index", String.valueOf(opensIndex));
        assertXPathCount(xmlDocument, "module-opens/synthetic", 1);
        assertXPathCount(xmlDocument, "module-opens/mandated", 1);
    }

    public void testVisitModuleOpensWithOpensTos() throws Exception {
        final ModuleOpens moduleOpens = mock(ModuleOpens.class);
        final int opensIndex = 123;
        final Package_info opens = mock(Package_info.class);
        final int opensFlags = 456;
        final ModuleOpensTo mockOpensTo = mock(ModuleOpensTo.class);

        checking(new Expectations() {{
            oneOf (moduleOpens).getOpensIndex();
                will(returnValue(opensIndex));
            oneOf (moduleOpens).getRawOpens();
                will(returnValue(opens));
            oneOf (opens).accept(printer);
            oneOf (moduleOpens).getOpensFlags();
                will(returnValue(opensFlags));
            oneOf (moduleOpens).isSynthetic();
                will(returnValue(false));
            oneOf (moduleOpens).isMandated();
                will(returnValue(false));
            atLeast (1).of (moduleOpens).getOpensTos();
                will(returnValue(Collections.singleton(mockOpensTo)));
            oneOf (mockOpensTo).accept(printer);
        }});

        printer.visitModuleOpens(moduleOpens);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-opens", 1);
    }

    public void testVisitModuleOpensTo() throws Exception {
        final ModuleOpensTo moduleOpensTo = mock(ModuleOpensTo.class);
        final int opensToIndex = 123;
        final Module_info opensTo = mock(Module_info.class);

        checking(new Expectations() {{
            oneOf (moduleOpensTo).getOpensToIndex();
                will(returnValue(opensToIndex));
            oneOf (moduleOpensTo).getRawOpensTo();
                will(returnValue(opensTo));
            oneOf (opensTo).accept(printer);
        }});

        printer.visitModuleOpensTo(moduleOpensTo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-opens-to", 1);
        assertXPathCount(xmlDocument, "module-opens-to/module", 1);
        assertXPathCount(xmlDocument, "module-opens-to/module/@index", 1);
        assertXPathText(xmlDocument, "module-opens-to/module/@index", String.valueOf(opensToIndex));
    }

    public void testVisitModule_attributeWithUses() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final ModuleUses mockUses = mock(ModuleUses.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(false));
            oneOf (attribute).isSynthetic();
                will(returnValue(false));
            oneOf (attribute).isMandated();
                will(returnValue(false));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.singleton(mockUses)));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.emptyList()));
            oneOf (mockUses).accept(printer);
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModuleUses() throws Exception {
        final ModuleUses moduleUses = mock(ModuleUses.class);
        final int usesIndex = 123;
        final Class_info uses = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (moduleUses).getUsesIndex();
                will(returnValue(usesIndex));
            oneOf (moduleUses).getRawUses();
                will(returnValue(uses));
            oneOf (uses).accept(printer);
        }});

        printer.visitModuleUses(moduleUses);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-uses", 1);
        assertXPathCount(xmlDocument, "module-uses/class", 1);
        assertXPathCount(xmlDocument, "module-uses/class/@index", 1);
        assertXPathText(xmlDocument, "module-uses/class/@index", String.valueOf(usesIndex));
    }

    public void testVisitModule_attributeWithProvides() throws Exception {
        final Module_attribute attribute = mock(Module_attribute.class);
        final int moduleNameIndex = 123;
        final Module_info mockModule = mock(Module_info.class);
        final int moduleFlags = 456;
        final ModuleProvides mockProvides = mock(ModuleProvides.class);

        checking(new Expectations() {{
            oneOf (attribute).getModuleNameIndex();
                will(returnValue(moduleNameIndex));
            oneOf (attribute).getRawModuleName();
                will(returnValue(mockModule));
            oneOf (mockModule).accept(printer);
            oneOf (attribute).getModuleFlags();
                will(returnValue(moduleFlags));
            oneOf (attribute).isOpen();
                will(returnValue(false));
            oneOf (attribute).isSynthetic();
                will(returnValue(false));
            oneOf (attribute).isMandated();
                will(returnValue(false));
            oneOf (attribute).hasModuleVersion();
                will(returnValue(false));
            atLeast (1).of (attribute).getRequires();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getExports();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getOpens();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getUses();
                will(returnValue(Collections.emptyList()));
            atLeast (1).of (attribute).getProvides();
                will(returnValue(Collections.singleton(mockProvides)));
            oneOf (mockProvides).accept(printer);
        }});

        printer.visitModule_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-attribute", 1);
        assertXPathCount(xmlDocument, "module-attribute/module-requires", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-exports", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-opens", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-uses", 0);
        assertXPathCount(xmlDocument, "module-attribute/module-provides", 0);
    }

    public void testVisitModuleProvidesWithoutProvidesWiths() throws Exception {
        final ModuleProvides moduleProvides = mock(ModuleProvides.class);
        final int providesIndex = 123;
        final Class_info provides = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (moduleProvides).getProvidesIndex();
                will(returnValue(providesIndex));
            oneOf (moduleProvides).getRawProvides();
                will(returnValue(provides));
            oneOf (provides).accept(printer);
            atLeast (1).of (moduleProvides).getProvidesWiths();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModuleProvides(moduleProvides);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-provides", 1);
        assertXPathCount(xmlDocument, "module-provides/class", 1);
        assertXPathCount(xmlDocument, "module-provides/class/@index", 1);
        assertXPathText(xmlDocument, "module-provides/class/@index", String.valueOf(providesIndex));
    }

    public void testVisitModuleProvidesWithProvidesWiths() throws Exception {
        final ModuleProvides moduleProvides = mock(ModuleProvides.class);
        final int providesIndex = 123;
        final Class_info provides = mock(Class_info.class);
        final ModuleProvidesWith mockProvidesWith = mock(ModuleProvidesWith.class);

        checking(new Expectations() {{
            oneOf (moduleProvides).getProvidesIndex();
                will(returnValue(providesIndex));
            oneOf (moduleProvides).getRawProvides();
                will(returnValue(provides));
            oneOf (provides).accept(printer);
            atLeast (1).of (moduleProvides).getProvidesWiths();
                will(returnValue(Collections.singleton(mockProvidesWith)));
            oneOf (mockProvidesWith).accept(printer);
        }});

        printer.visitModuleProvides(moduleProvides);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-provides", 1);
    }

    public void testVisitModuleProvidesWith() throws Exception {
        final ModuleProvidesWith moduleProvidesWith = mock(ModuleProvidesWith.class);
        final int providesWithIndex = 123;
        final Class_info providesWith = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (moduleProvidesWith).getProvidesWithIndex();
                will(returnValue(providesWithIndex));
            oneOf (moduleProvidesWith).getRawProvidesWith();
                will(returnValue(providesWith));
            oneOf (providesWith).accept(printer);
        }});

        printer.visitModuleProvidesWith(moduleProvidesWith);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-provides-with", 1);
        assertXPathCount(xmlDocument, "module-provides-with/class", 1);
        assertXPathCount(xmlDocument, "module-provides-with/class/@index", 1);
        assertXPathText(xmlDocument, "module-provides-with/class/@index", String.valueOf(providesWithIndex));
    }

    public void testVisitModulePackages_attribute() throws Exception {
        final ModulePackages_attribute attribute = mock(ModulePackages_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getPackages();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitModulePackages_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-packages-attribute", 1);
    }

    public void testVisitModulePackages_attributeWithModulePackage() throws Exception {
        final ModulePackages_attribute attribute = mock(ModulePackages_attribute.class);
        final ModulePackage mockPackage = mock(ModulePackage.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getPackages();
                will(returnValue(Collections.singleton(mockPackage)));
            oneOf (mockPackage).accept(printer);
        }});

        printer.visitModulePackages_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-packages-attribute", 1);
    }

    public void testVisitModulePackage() throws Exception {
        final ModulePackage modulePackage = mock(ModulePackage.class);
        final int packageIndex = 123;
        final Package_info mockPackage = mock(Package_info.class);

        checking(new Expectations() {{
            oneOf (modulePackage).getPackageIndex();
                will(returnValue(packageIndex));
            oneOf (modulePackage).getRawPackage();
                will(returnValue(mockPackage));
            oneOf (mockPackage).accept(printer);
        }});

        printer.visitModulePackage(modulePackage);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "package", 1);
        assertXPathCount(xmlDocument, "package/@index", 1);
        assertXPathText(xmlDocument, "package/@index", String.valueOf(packageIndex));
    }

    public void testVisitModuleMainClass_attribute() throws Exception {
        final ModuleMainClass_attribute attribute = mock(ModuleMainClass_attribute.class);
        final int mainClassIndex = 123;
        final Class_info mockClass = mock(Class_info.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getMainClassIndex();
                will(returnValue(mainClassIndex));
            atLeast(1).of (attribute).getRawMainClass();
                will(returnValue(mockClass));
            oneOf (mockClass).accept(printer);
        }});

        printer.visitModuleMainClass_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "module-main-class-attribute", 1);
        assertXPathCount(xmlDocument, "module-main-class-attribute/@index", 1);
        assertXPathText(xmlDocument, "module-main-class-attribute/@index", String.valueOf(mainClassIndex));
    }

    public void testVisitNestHost_attribute() throws Exception {
        final NestHost_attribute attribute = mock(NestHost_attribute.class);
        final int hostClassIndex = 123;
        final Class_info mockClass = mock(Class_info.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getHostClassIndex();
                will(returnValue(hostClassIndex));
            atLeast(1).of (attribute).getRawHostClass();
                will(returnValue(mockClass));
            oneOf (mockClass).accept(printer);
        }});

        printer.visitNestHost_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "nest-host-attribute", 1);
        assertXPathCount(xmlDocument, "nest-host-attribute/@index", 1);
        assertXPathText(xmlDocument, "nest-host-attribute/@index", String.valueOf(hostClassIndex));
    }

    public void testVisitNestMembers_attribute() throws Exception {
        final NestMembers_attribute attribute = mock(NestMembers_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getMembers();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitNestMembers_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "nest-members-attribute", 1);
    }

    public void testVisitNestMembers_attributeWithNestMember() throws Exception {
        final NestMembers_attribute attribute = mock(NestMembers_attribute.class);
        final NestMember mockMember = mock(NestMember.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getMembers();
                will(returnValue(Collections.singleton(mockMember)));
            oneOf (mockMember).accept(printer);
        }});

        printer.visitNestMembers_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "nest-members-attribute", 1);
    }

    public void testVisitNestMember() throws Exception {
        final NestMember nestMember = mock(NestMember.class);
        final int memberClassIndex = 123;
        final Class_info mockMemberClass = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (nestMember).getMemberClassIndex();
                will(returnValue(memberClassIndex));
            oneOf (nestMember).getRawMemberClass();
                will(returnValue(mockMemberClass));
            oneOf (mockMemberClass).accept(printer);
        }});

        printer.visitNestMember(nestMember);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "class", 1);
        assertXPathCount(xmlDocument, "class/@index", 1);
        assertXPathText(xmlDocument, "class/@index", String.valueOf(memberClassIndex));
    }

    public void testVisitRecord_attribute() throws Exception {
        final Record_attribute attribute = mock(Record_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getRecordComponents();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitRecord_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "record-attribute", 1);
    }

    public void testVisitRecord_attribute_attributeWithRecordComponent() throws Exception {
        final Record_attribute attribute = mock(Record_attribute.class);
        final RecordComponent_info mockRecordComponent = mock(RecordComponent_info.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getRecordComponents();
                will(returnValue(Collections.singleton(mockRecordComponent)));
            oneOf (mockRecordComponent).accept(printer);
        }});

        printer.visitRecord_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "record-attribute", 1);
    }

    public void testVisitRecordComponent_info() throws Exception {
        final RecordComponent_info recordComponent = mock(RecordComponent_info.class);
        final int nameIndex = 123;
        final UTF8_info name = mock(UTF8_info.class, "name");
        final int descriptorIndex = 456;
        final String type = "abc";

        checking(new Expectations() {{
            oneOf (recordComponent).getNameIndex();
                will(returnValue(nameIndex));
            oneOf (recordComponent).getRawName();
                will(returnValue(name));
            oneOf (name).accept(printer);
            oneOf (recordComponent).getDescriptorIndex();
                will(returnValue(descriptorIndex));
            oneOf (recordComponent).getType();
                will(returnValue(type));
            atLeast(1).of (recordComponent).getAttributes();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitRecordComponent_info(recordComponent);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "record-component", 1);
        assertXPathCount(xmlDocument, "record-component/name", 1);
        assertXPathCount(xmlDocument, "record-component/name/@index", 1);
        assertXPathText(xmlDocument, "record-component/name/@index", String.valueOf(nameIndex));
        assertXPathCount(xmlDocument, "record-component/type", 1);
        assertXPathCount(xmlDocument, "record-component/type/@index", 1);
        assertXPathText(xmlDocument, "record-component/type/@index", String.valueOf(descriptorIndex));
        assertXPathCount(xmlDocument, "record-component/attributes", 1);
    }

    public void testVisitRecordComponent_infoWithAttributes() throws Exception {
        final RecordComponent_info recordComponent = mock(RecordComponent_info.class);
        final int nameIndex = 123;
        final UTF8_info name = mock(UTF8_info.class, "name");
        final int descriptorIndex = 456;
        final String type = "abc";
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            oneOf (recordComponent).getNameIndex();
                will(returnValue(nameIndex));
            oneOf (recordComponent).getRawName();
                will(returnValue(name));
            oneOf (name).accept(printer);
            oneOf (recordComponent).getDescriptorIndex();
                will(returnValue(descriptorIndex));
            oneOf (recordComponent).getType();
                will(returnValue(type));
            atLeast(1).of (recordComponent).getAttributes();
                will(returnValue(Collections.singleton(mockAttribute)));
            oneOf (mockAttribute).accept(printer);
        }});

        printer.visitRecordComponent_info(recordComponent);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "record-component", 1);
        assertXPathCount(xmlDocument, "record-component/name", 1);
        assertXPathCount(xmlDocument, "record-component/name/@index", 1);
        assertXPathText(xmlDocument, "record-component/name/@index", String.valueOf(nameIndex));
        assertXPathCount(xmlDocument, "record-component/type", 1);
        assertXPathCount(xmlDocument, "record-component/type/@index", 1);
        assertXPathText(xmlDocument, "record-component/type/@index", String.valueOf(descriptorIndex));
        assertXPathCount(xmlDocument, "record-component/attributes", 1);
    }

    public void testVisitPermittedSubclasses_attribute() throws Exception {
        final PermittedSubclasses_attribute attribute = mock(PermittedSubclasses_attribute.class);

        checking(new Expectations() {{
            atLeast(1).of (attribute).getSubclasses();
            will(returnValue(Collections.emptyList()));
        }});

        printer.visitPermittedSubclasses_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "permitted-subclasses-attribute", 1);
    }

    public void testVisitPermittedSubclasses_attributeWithNestMember() throws Exception {
        final PermittedSubclasses_attribute attribute = mock(PermittedSubclasses_attribute.class);
        final PermittedSubclass mockSubclass = mock(PermittedSubclass.class);

        checking(new Expectations() {{
            atLeast (1).of (attribute).getSubclasses();
            will(returnValue(Collections.singleton(mockSubclass)));
            oneOf (mockSubclass).accept(printer);
        }});

        printer.visitPermittedSubclasses_attribute(attribute);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "permitted-subclasses-attribute", 1);
    }

    public void testVisitPermittedSubclass() throws Exception {
        final PermittedSubclass permittedSubclass = mock(PermittedSubclass.class);
        final int subclassIndex = 123;
        final Class_info mockSubclass = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (permittedSubclass).getSubclassIndex();
            will(returnValue(subclassIndex));
            oneOf (permittedSubclass).getRawSubclass();
            will(returnValue(mockSubclass));
            oneOf (mockSubclass).accept(printer);
        }});

        printer.visitPermittedSubclass(permittedSubclass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "class", 1);
        assertXPathCount(xmlDocument, "class/@index", 1);
        assertXPathText(xmlDocument, "class/@index", String.valueOf(subclassIndex));
    }

    public void testVisitAnnotation_WithoutElementValuePairs() throws Exception {
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (annotation).getType();
                will(returnValue(ANNOTATION_TYPE));
            atLeast(1).of (annotation).getElementValuePairs();
        }});

        printer.visitAnnotation(annotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation", 1);
        assertXPathText(xmlDocument, "annotation/type", ANNOTATION_TYPE);
        assertXPathCount(xmlDocument, "annotation/element-value-pairs", 1);
    }

    public void testVisitAnnotation_WithAnElementValuePair() throws Exception {
        final Annotation annotation = mock(Annotation.class);
        final ElementValuePair elementValuePair = mock(ElementValuePair.class);

        checking(new Expectations() {{
            atLeast(1).of (annotation).getType();
                will(returnValue(ANNOTATION_TYPE));
            atLeast(1).of (annotation).getElementValuePairs();
                will(returnValue(Collections.singleton(elementValuePair)));
            oneOf (elementValuePair).accept(printer);
        }});

        printer.visitAnnotation(annotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation", 1);
        assertXPathText(xmlDocument, "annotation/type", ANNOTATION_TYPE);
        assertXPathCount(xmlDocument, "annotation/element-value-pairs", 1);
    }

    public void testVisitParameterAnnotation_WithoutAnnotations() throws Exception {
        final ParameterAnnotation parameterAnnotation = mock(ParameterAnnotation.class);

        checking(new Expectations() {{
            atLeast(1).of (parameterAnnotation).getAnnotations();
        }});

        printer.visitParameterAnnotation(parameterAnnotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "parameter-annotation", 1);
        assertXPathCount(xmlDocument, "parameter-annotation/annotations", 1);
    }

    public void testVisitParameterAnnotation_WithAnAnnotation() throws Exception {
        final ParameterAnnotation parameterAnnotation = mock(ParameterAnnotation.class);
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (parameterAnnotation).getAnnotations();
                will(returnValue(Collections.singleton(annotation)));
            oneOf (annotation).accept(printer);
        }});

        printer.visitParameterAnnotation(parameterAnnotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "parameter-annotation", 1);
        assertXPathCount(xmlDocument, "parameter-annotation/annotations", 1);
    }

    public void testVisitTypeAnnotation_WithoutAnnotations() throws Exception {
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);
        final Target_info mockTarget = mock(Target_info.class);
        final TypePath mockTypePath = mock(TypePath.class);

        checking(new Expectations() {{
            oneOf (typeAnnotation).getTarget();
                will(returnValue(mockTarget));
            oneOf (mockTarget).accept(printer);
            oneOf (typeAnnotation).getTargetPath();
                will(returnValue(mockTypePath));
            oneOf (mockTypePath).accept(printer);
            atLeast(1).of (typeAnnotation).getElementValuePairs();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitTypeAnnotation(typeAnnotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-annotation", 1);
        assertXPathCount(xmlDocument, "type-annotation/target-path", 1);
        assertXPathCount(xmlDocument, "type-annotation/element-value-pairs", 1);
    }

    public void testVisitTypeAnnotation_WithAnElementValuePair() throws Exception {
        final TypeAnnotation typeAnnotation = mock(TypeAnnotation.class);
        final Target_info mockTarget = mock(Target_info.class);
        final TypePath mockTypePath = mock(TypePath.class);
        final ElementValuePair mockElementValuePair = mock(ElementValuePair.class);

        checking(new Expectations() {{
            oneOf (typeAnnotation).getTarget();
                will(returnValue(mockTarget));
            oneOf (mockTarget).accept(printer);
            oneOf (typeAnnotation).getTargetPath();
                will(returnValue(mockTypePath));
            oneOf (mockTypePath).accept(printer);
            atLeast(1).of (typeAnnotation).getElementValuePairs();
                will(returnValue(Collections.singleton(mockElementValuePair)));
            oneOf (mockElementValuePair).accept(printer);
        }});

        printer.visitTypeAnnotation(typeAnnotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-annotation", 1);
        assertXPathCount(xmlDocument, "type-annotation/target-path", 1);
        assertXPathCount(xmlDocument, "type-annotation/element-value-pairs", 1);
    }

    public void testVisitTypeParameterTarget() throws Exception {
        final TypeParameterTarget target = mock(TypeParameterTarget.class);
        final String hexTargetType = "0xABCD";
        final int typeParameterIndex = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getTypeParameterIndex();
                will(returnValue(typeParameterIndex));
        }});

        printer.visitTypeParameterTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-parameter-target", 1);
        assertXPathCount(xmlDocument, "type-parameter-target/@target-type", 1);
        assertXPathText(xmlDocument, "type-parameter-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "type-parameter-target/type-parameter-index", 1);
        assertXPathText(xmlDocument, "type-parameter-target/type-parameter-index", String.valueOf(typeParameterIndex));
    }

    public void testVisitSupertypeTarget() throws Exception {
        final SupertypeTarget target = mock(SupertypeTarget.class);
        final String hexTargetType = "0xABCD";
        final int supertypeIndex = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getSupertypeIndex();
                will(returnValue(supertypeIndex));
        }});

        printer.visitSupertypeTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "supertype-target", 1);
        assertXPathCount(xmlDocument, "supertype-target/@target-type", 1);
        assertXPathText(xmlDocument, "supertype-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "supertype-target/supertype-index", 1);
        assertXPathText(xmlDocument, "supertype-target/supertype-index", String.valueOf(supertypeIndex));
    }

    public void testVisitTypeParameterBoundTarget() throws Exception {
        final TypeParameterBoundTarget target = mock(TypeParameterBoundTarget.class);
        final String hexTargetType = "0xABCD";
        final int typeParameterIndex = 123;
        final int boundIndex = 456;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getTypeParameterIndex();
                will(returnValue(typeParameterIndex));
            oneOf (target).getBoundIndex();
                will(returnValue(boundIndex));
        }});

        printer.visitTypeParameterBoundTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-parameter-bound-target", 1);
        assertXPathCount(xmlDocument, "type-parameter-bound-target/@target-type", 1);
        assertXPathText(xmlDocument, "type-parameter-bound-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "type-parameter-bound-target/type-parameter-index", 1);
        assertXPathText(xmlDocument, "type-parameter-bound-target/type-parameter-index", String.valueOf(typeParameterIndex));
        assertXPathCount(xmlDocument, "type-parameter-bound-target/bound-index", 1);
        assertXPathText(xmlDocument, "type-parameter-bound-target/bound-index", String.valueOf(boundIndex));
    }

    public void testVisitEmptyTarget() throws Exception {
        final EmptyTarget target = mock(EmptyTarget.class);
        final String hexTargetType = "0xABCD";

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
        }});

        printer.visitEmptyTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "empty-target", 1);
        assertXPathCount(xmlDocument, "empty-target/@target-type", 1);
        assertXPathText(xmlDocument, "empty-target/@target-type", hexTargetType);
    }

    public void testVisitFormalParameterTarget() throws Exception {
        final FormalParameterTarget target = mock(FormalParameterTarget.class);
        final String hexTargetType = "0xABCD";
        final int formalParameterIndex = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getFormalParameterIndex();
                will(returnValue(formalParameterIndex));
        }});

        printer.visitFormalParameterTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "formal-parameter-target", 1);
        assertXPathCount(xmlDocument, "formal-parameter-target/@target-type", 1);
        assertXPathText(xmlDocument, "formal-parameter-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "formal-parameter-target/formal-parameter-index", 1);
        assertXPathText(xmlDocument, "formal-parameter-target/formal-parameter-index", String.valueOf(formalParameterIndex));
    }

    public void testVisitThrowsTarget() throws Exception {
        final ThrowsTarget target = mock(ThrowsTarget.class);
        final String hexTargetType = "0xABCD";
        final int throwsTypeIndex = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getThrowsTypeIndex();
                will(returnValue(throwsTypeIndex));
        }});

        printer.visitThrowsTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "throws-target", 1);
        assertXPathCount(xmlDocument, "throws-target/@target-type", 1);
        assertXPathText(xmlDocument, "throws-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "throws-target/throws-type-index", 1);
        assertXPathText(xmlDocument, "throws-target/throws-type-index", String.valueOf(throwsTypeIndex));
    }

    public void testVisitLocalvarTarget_noEntries() throws Exception {
        final LocalvarTarget target = mock(LocalvarTarget.class);
        final String hexTargetType = "0xABCD";

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            atLeast(1).of (target).getTable();
                will(returnValue(Collections.emptyList()));
        }});

        printer.visitLocalvarTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "localvar-target", 1);
        assertXPathCount(xmlDocument, "localvar-target/@target-type", 1);
        assertXPathText(xmlDocument, "localvar-target/@target-type", hexTargetType);
    }

    public void testVisitLocalvarTarget_oneEntry() throws Exception {
        final LocalvarTarget target = mock(LocalvarTarget.class);
        final String hexTargetType = "0xABCD";
        final LocalvarTableEntry mockLocalvarTableEntry = mock(LocalvarTableEntry.class);

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            atLeast(1).of (target).getTable();
                will(returnValue(Collections.singleton(mockLocalvarTableEntry)));
            oneOf (mockLocalvarTableEntry).accept(printer);
        }});

        printer.visitLocalvarTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "localvar-target", 1);
        assertXPathCount(xmlDocument, "localvar-target/@target-type", 1);
        assertXPathText(xmlDocument, "localvar-target/@target-type", hexTargetType);
    }

    public void testVisitCatchTarget() throws Exception {
        final CatchTarget target = mock(CatchTarget.class);
        final String hexTargetType = "0xABCD";
        final int exceptionTableIndex = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getExceptionTableIndex();
                will(returnValue(exceptionTableIndex));
        }});

        printer.visitCatchTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "catch-target", 1);
        assertXPathCount(xmlDocument, "catch-target/@target-type", 1);
        assertXPathText(xmlDocument, "catch-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "catch-target/exception-table-index", 1);
        assertXPathText(xmlDocument, "catch-target/exception-table-index", String.valueOf(exceptionTableIndex));
    }

    public void testVisitOffsetTarget() throws Exception {
        final OffsetTarget target = mock(OffsetTarget.class);
        final String hexTargetType = "0xABCD";
        final int offset = 123;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getOffset();
                will(returnValue(offset));
        }});

        printer.visitOffsetTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "offset-target", 1);
        assertXPathCount(xmlDocument, "offset-target/@target-type", 1);
        assertXPathText(xmlDocument, "offset-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "offset-target/offset", 1);
        assertXPathText(xmlDocument, "offset-target/offset", String.valueOf(offset));
    }

    public void testVisitTypeArgumentTarget() throws Exception {
        final TypeArgumentTarget target = mock(TypeArgumentTarget.class);
        final String hexTargetType = "0xABCD";
        final int offset = 123;
        final int typeArgumentIndex = 456;

        checking(new Expectations() {{
            oneOf (target).getHexTargetType();
                will(returnValue(hexTargetType));
            oneOf (target).getOffset();
                will(returnValue(offset));
            oneOf (target).getTypeArgumentIndex();
                will(returnValue(typeArgumentIndex));
        }});

        printer.visitTypeArgumentTarget(target);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-argument-target", 1);
        assertXPathCount(xmlDocument, "type-argument-target/@target-type", 1);
        assertXPathText(xmlDocument, "type-argument-target/@target-type", hexTargetType);
        assertXPathCount(xmlDocument, "type-argument-target/offset", 1);
        assertXPathText(xmlDocument, "type-argument-target/offset", String.valueOf(offset));
        assertXPathCount(xmlDocument, "type-argument-target/type-argument-index", 1);
        assertXPathText(xmlDocument, "type-argument-target/type-argument-index", String.valueOf(typeArgumentIndex));
    }

    public void testVisitTypePathEntry() throws Exception {
        final TypePathEntry entry = mock(TypePathEntry.class);
        final TypePathKind typePathKind = TypePathKind.DEEPER_IN_NESTED_TYPE;
        final int typeArgumentIndex = 456;

        checking(new Expectations() {{
            oneOf (entry).getTypePathKind();
                will(returnValue(typePathKind));
            oneOf (entry).getTypeArgumentIndex();
                will(returnValue(typeArgumentIndex));
        }});

        printer.visitTypePathEntry(entry);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "type-path", 1);
        assertXPathCount(xmlDocument, "type-path/type-path-kind", 1);
        assertXPathText(xmlDocument, "type-path/type-path-kind", String.valueOf(typePathKind.getTypePathKind()));
        assertXPathCount(xmlDocument, "type-path/type-argument-index", 1);
        assertXPathText(xmlDocument, "type-path/type-argument-index", String.valueOf(typeArgumentIndex));
    }

    public void testVisitElementValuePair() throws Exception {
        final ElementValuePair elementValuePair = mock(ElementValuePair.class);
        final ElementValue elementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            oneOf (elementValuePair).getElementName();
                will(returnValue(ELEMENT_NAME));
            oneOf (elementValuePair).getElementValue();
                will(returnValue(elementValue));
            oneOf (elementValue).accept(printer);
        }});

        printer.visitElementValuePair(elementValuePair);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "element-value-pair", 1);
        assertXPathText(xmlDocument, "element-value-pair/element-name", ELEMENT_NAME);
    }

    public void testVisitByteConstantElementValue() throws Exception {
        final ByteConstantElementValue constantElementValue = mock(ByteConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.BYTE.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitByteConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "byte-element-value", 1);
        assertXPathText(xmlDocument, "byte-element-value/@tag", String.valueOf(ElementValueType.BYTE.getTag()));
    }

    public void testVisitCharConstantElementValue() throws Exception {
        final CharConstantElementValue constantElementValue = mock(CharConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.CHAR.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitCharConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "char-element-value", 1);
        assertXPathText(xmlDocument, "char-element-value/@tag", String.valueOf(ElementValueType.CHAR.getTag()));
    }

    public void testVisitDoubleConstantElementValue() throws Exception {
        final DoubleConstantElementValue constantElementValue = mock(DoubleConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.DOUBLE.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitDoubleConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "double-element-value", 1);
        assertXPathText(xmlDocument, "double-element-value/@tag", String.valueOf(ElementValueType.DOUBLE.getTag()));
    }

    public void testVisitFloatConstantElementValue() throws Exception {
        final FloatConstantElementValue constantElementValue = mock(FloatConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.FLOAT.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitFloatConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "float-element-value", 1);
        assertXPathText(xmlDocument, "float-element-value/@tag", String.valueOf(ElementValueType.FLOAT.getTag()));
    }

    public void testVisitIntegerConstantElementValue() throws Exception {
        final IntegerConstantElementValue constantElementValue = mock(IntegerConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.INTEGER.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitIntegerConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "integer-element-value", 1);
        assertXPathText(xmlDocument, "integer-element-value/@tag", String.valueOf(ElementValueType.INTEGER.getTag()));
    }

    public void testVisitLongConstantElementValue() throws Exception {
        final LongConstantElementValue constantElementValue = mock(LongConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.LONG.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitLongConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "long-element-value", 1);
        assertXPathText(xmlDocument, "long-element-value/@tag", String.valueOf(ElementValueType.LONG.getTag()));
    }

    public void testVisitShortConstantElementValue() throws Exception {
        final ShortConstantElementValue constantElementValue = mock(ShortConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.SHORT.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitShortConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "short-element-value", 1);
        assertXPathText(xmlDocument, "short-element-value/@tag", String.valueOf(ElementValueType.SHORT.getTag()));
    }

    public void testVisitBooleanConstantElementValue() throws Exception {
        final BooleanConstantElementValue constantElementValue = mock(BooleanConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.BOOLEAN.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitBooleanConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "boolean-element-value", 1);
        assertXPathText(xmlDocument, "boolean-element-value/@tag", String.valueOf(ElementValueType.BOOLEAN.getTag()));
    }

    public void testVisitStringConstantElementValue() throws Exception {
        final StringConstantElementValue constantElementValue = mock(StringConstantElementValue.class);
        final ConstantPoolEntry constantPoolEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            oneOf (constantElementValue).getTag();
                will(returnValue(ElementValueType.STRING.getTag()));
            oneOf (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            oneOf (constantPoolEntry).accept(printer);
        }});

        printer.visitStringConstantElementValue(constantElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "string-element-value", 1);
        assertXPathText(xmlDocument, "string-element-value/@tag", String.valueOf(ElementValueType.STRING.getTag()));
    }

    public void testVisitEnumElementValue() throws Exception {
        final EnumElementValue enumElementValue = mock(EnumElementValue.class);
        final String constName = "BAR";

        checking(new Expectations() {{
            oneOf (enumElementValue).getTag();
                will(returnValue(ElementValueType.ENUM.getTag()));
            oneOf (enumElementValue).getTypeName();
                will(returnValue(TEST_CLASS));
            oneOf (enumElementValue).getConstName();
                will(returnValue(constName));
        }});

        printer.visitEnumElementValue(enumElementValue);

        String xmlDocument = buffer.toString();
        assertXPathText(xmlDocument, "enum-element-value", TEST_CLASS + "." + constName);
        assertXPathText(xmlDocument, "enum-element-value/@tag", String.valueOf(ElementValueType.ENUM.getTag()));
    }

    public void testVisitClassElementValue() throws Exception {
        final ClassElementValue classElementValue = mock(ClassElementValue.class);

        checking(new Expectations() {{
            oneOf (classElementValue).getTag();
                will(returnValue(ElementValueType.CLASS.getTag()));
            oneOf (classElementValue).getClassInfo();
                will(returnValue(TEST_CLASS));
        }});

        printer.visitClassElementValue(classElementValue);

        String xmlDocument = buffer.toString();
        assertXPathText(xmlDocument, "class-element-value", TEST_CLASS);
        assertXPathText(xmlDocument, "class-element-value/@tag", String.valueOf(ElementValueType.CLASS.getTag()));
    }

    public void testVisitAnnotationElementValue() throws Exception {
        final AnnotationElementValue annotationElementValue = mock(AnnotationElementValue.class);
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            oneOf (annotationElementValue).getTag();
                will(returnValue(ElementValueType.ANNOTATION.getTag()));
            oneOf (annotationElementValue).getAnnotation();
                will(returnValue(annotation));
            oneOf (annotation).accept(printer);
        }});

        printer.visitAnnotationElementValue(annotationElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation-element-value", 1);
        assertXPathText(xmlDocument, "annotation-element-value/@tag", String.valueOf(ElementValueType.ANNOTATION.getTag()));
    }

    public void testVisitArrayElementValue() throws Exception {
        final ArrayElementValue arrayElementValue = mock(ArrayElementValue.class);
        final ElementValue elementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            oneOf (arrayElementValue).getTag();
                will(returnValue(ElementValueType.ARRAY.getTag()));
            atLeast(1).of (arrayElementValue).getValues();
                will(returnValue(Collections.singleton(elementValue)));
            oneOf (elementValue).accept(printer);
        }});

        printer.visitArrayElementValue(arrayElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "array-element-value", 1);
        assertXPathText(xmlDocument, "array-element-value/@tag", String.valueOf(ElementValueType.ARRAY.getTag()));
    }

    public void testVisitLocalvarTableEntry() throws Exception {
        final LocalvarTableEntry entry = mock(LocalvarTableEntry.class);
        final int startPc = 123;
        final int length = 456;
        final int index = 789;

        checking(new Expectations() {{
            oneOf (entry).getStartPc();
            will(returnValue(startPc));
            oneOf (entry).getLength();
            will(returnValue(length));
            oneOf (entry).getIndex();
            will(returnValue(index));
        }});

        printer.visitLocalvarTableEntry(entry);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "localvar", 1);
        assertXPathCount(xmlDocument, "localvar/@start-pc", 1);
        assertXPathText(xmlDocument, "localvar/@start-pc", String.valueOf(startPc));
        assertXPathCount(xmlDocument, "localvar/@length", 1);
        assertXPathText(xmlDocument, "localvar/@length", String.valueOf(length));
        assertXPathCount(xmlDocument, "localvar/@index", 1);
        assertXPathText(xmlDocument, "localvar/@index", String.valueOf(index));
    }

    public void testVisitSameFrame() throws Exception {
        final SameFrame mockStackMapFrame = mock(SameFrame.class);
        final int frameType = 123;

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
        }});

        printer.visitSameFrame(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "same-frame", 1);
        assertXPathText(xmlDocument, "same-frame/@frame-type", String.valueOf(frameType));
    }

    public void testVisitSameLocals1StackItemFrame() throws Exception {
        final SameLocals1StackItemFrame mockStackMapFrame = mock(SameLocals1StackItemFrame.class);
        final int frameType = 123;
        final VerificationTypeInfo mockStack = mock(VerificationTypeInfo.class);

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getStack();
                will(returnValue(mockStack));
            oneOf (mockStack).accept(printer);
        }});

        printer.visitSameLocals1StackItemFrame(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "same-locals-1-stack-item-frame", 1);
        assertXPathText(xmlDocument, "same-locals-1-stack-item-frame/@frame-type", String.valueOf(frameType));
        assertXPathCount(xmlDocument, "same-locals-1-stack-item-frame/stack", 1);
    }

    public void testVisitSameLocals1StackItemFrameExtended() throws Exception {
        final SameLocals1StackItemFrameExtended mockStackMapFrame = mock(SameLocals1StackItemFrameExtended.class);
        final int frameType = 123;
        final int offsetDelta = 456;
        final VerificationTypeInfo mockStack = mock(VerificationTypeInfo.class);

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getOffsetDelta();
                will(returnValue(offsetDelta));
            oneOf (mockStackMapFrame).getStack();
                will(returnValue(mockStack));
            oneOf (mockStack).accept(printer);
        }});

        printer.visitSameLocals1StackItemFrameExtended(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "same-locals-1-stack-item-frame-extended", 1);
        assertXPathText(xmlDocument, "same-locals-1-stack-item-frame-extended/@frame-type", String.valueOf(frameType));
        assertXPathText(xmlDocument, "same-locals-1-stack-item-frame-extended/@offset-delta", String.valueOf(offsetDelta));
        assertXPathCount(xmlDocument, "same-locals-1-stack-item-frame-extended/stack", 1);
    }

    public void testVisitChopFrame() throws Exception {
        final ChopFrame mockStackMapFrame = mock(ChopFrame.class);
        final int frameType = 123;
        final int offsetDelta = 456;

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getOffsetDelta();
                will(returnValue(offsetDelta));
        }});

        printer.visitChopFrame(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "chop-frame", 1);
        assertXPathText(xmlDocument, "chop-frame/@frame-type", String.valueOf(frameType));
        assertXPathText(xmlDocument, "chop-frame/@offset-delta", String.valueOf(offsetDelta));
    }

    public void testVisitSameFrameExtended() throws Exception {
        final SameFrameExtended mockStackMapFrame = mock(SameFrameExtended.class);
        final int frameType = 123;
        final int offsetDelta = 456;

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getOffsetDelta();
                will(returnValue(offsetDelta));
        }});

        printer.visitSameFrameExtended(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "same-frame-extended", 1);
        assertXPathText(xmlDocument, "same-frame-extended/@frame-type", String.valueOf(frameType));
        assertXPathText(xmlDocument, "same-frame-extended/@offset-delta", String.valueOf(offsetDelta));
    }

    public void testVisitAppendFrame() throws Exception {
        final AppendFrame mockStackMapFrame = mock(AppendFrame.class);
        final int frameType = 123;
        final int offsetDelta = 456;
        final VerificationTypeInfo mockLocal1 = mock(VerificationTypeInfo.class, "first local");
        final VerificationTypeInfo mockLocal2 = mock(VerificationTypeInfo.class, "second local");

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getOffsetDelta();
                will(returnValue(offsetDelta));
            oneOf (mockStackMapFrame).getLocals();
                will(returnValue(List.of(mockLocal1, mockLocal2)));
            oneOf (mockLocal1).accept(printer);
            oneOf (mockLocal2).accept(printer);
        }});

        printer.visitAppendFrame(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "append-frame", 1);
        assertXPathText(xmlDocument, "append-frame/@frame-type", String.valueOf(frameType));
        assertXPathText(xmlDocument, "append-frame/@offset-delta", String.valueOf(offsetDelta));
        assertXPathCount(xmlDocument, "append-frame/locals", 1);
    }

    public void testVisitFullFrame() throws Exception {
        final FullFrame mockStackMapFrame = mock(FullFrame.class);
        final int frameType = 123;
        final int offsetDelta = 456;
        final VerificationTypeInfo mockLocal1 = mock(VerificationTypeInfo.class, "first local");
        final VerificationTypeInfo mockLocal2 = mock(VerificationTypeInfo.class, "second local");
        final VerificationTypeInfo mockStack1 = mock(VerificationTypeInfo.class, "first stack");
        final VerificationTypeInfo mockStack2 = mock(VerificationTypeInfo.class, "second stack");

        checking(new Expectations() {{
            oneOf (mockStackMapFrame).getFrameType();
                will(returnValue(frameType));
            oneOf (mockStackMapFrame).getOffsetDelta();
                will(returnValue(offsetDelta));
            oneOf (mockStackMapFrame).getLocals();
                will(returnValue(List.of(mockLocal1, mockLocal2)));
            oneOf (mockStackMapFrame).getStack();
                will(returnValue(List.of(mockStack1, mockStack2)));
            oneOf (mockLocal1).accept(printer);
            oneOf (mockLocal2).accept(printer);
            oneOf (mockStack1).accept(printer);
            oneOf (mockStack2).accept(printer);
        }});

        printer.visitFullFrame(mockStackMapFrame);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "full-frame", 1);
        assertXPathText(xmlDocument, "full-frame/@frame-type", String.valueOf(frameType));
        assertXPathText(xmlDocument, "full-frame/@offset-delta", String.valueOf(offsetDelta));
        assertXPathCount(xmlDocument, "full-frame/locals", 1);
        assertXPathCount(xmlDocument, "full-frame/stack", 1);
    }

    public void testVisitTopVariableInfo() throws Exception {
        final TopVariableInfo mockTopVariableInfo = mock(TopVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockTopVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitTopVariableInfo(mockTopVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "top-variable-info", 1);
        assertXPathText(xmlDocument, "top-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitIntegerVariableInfo() throws Exception {
        final IntegerVariableInfo mockIntegerVariableInfo = mock(IntegerVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockIntegerVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitIntegerVariableInfo(mockIntegerVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "integer-variable-info", 1);
        assertXPathText(xmlDocument, "integer-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitFloatVariableInfo() throws Exception {
        final FloatVariableInfo mockFloatVariableInfo = mock(FloatVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockFloatVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitFloatVariableInfo(mockFloatVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "float-variable-info", 1);
        assertXPathText(xmlDocument, "float-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitLongVariableInfo() throws Exception {
        final LongVariableInfo mockLongVariableInfo = mock(LongVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockLongVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitLongVariableInfo(mockLongVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "long-variable-info", 1);
        assertXPathText(xmlDocument, "long-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitDoubleVariableInfo() throws Exception {
        final DoubleVariableInfo mockDoubleVariableInfo = mock(DoubleVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockDoubleVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitDoubleVariableInfo(mockDoubleVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "double-variable-info", 1);
        assertXPathText(xmlDocument, "double-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitNullVariableInfo() throws Exception {
        final NullVariableInfo mockNullVariableInfo = mock(NullVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockNullVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitNullVariableInfo(mockNullVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "null-variable-info", 1);
        assertXPathText(xmlDocument, "null-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitUninitializedThisVariableInfo() throws Exception {
        final UninitializedThisVariableInfo mockUninitializedThisVariableInfo = mock(UninitializedThisVariableInfo.class);
        final int tag = 123;

        checking(new Expectations() {{
            oneOf (mockUninitializedThisVariableInfo).getTag();
                will(returnValue(tag));
        }});

        printer.visitUninitializedThisVariableInfo(mockUninitializedThisVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "uninitialized-this-variable-info", 1);
        assertXPathText(xmlDocument, "uninitialized-this-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitObjectVariableInfo() throws Exception {
        final ObjectVariableInfo mockObjectVariableInfo = mock(ObjectVariableInfo.class);
        final int tag = 123;
        final Class_info mockClassInfo = mock(Class_info.class);

        checking(new Expectations() {{
            oneOf (mockObjectVariableInfo).getTag();
                will(returnValue(tag));
            oneOf (mockObjectVariableInfo).getClassInfo();
                will(returnValue(mockClassInfo));
            oneOf (mockClassInfo).accept(printer);
        }});

        printer.visitObjectVariableInfo(mockObjectVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "object-variable-info", 1);
        assertXPathText(xmlDocument, "object-variable-info/@tag", String.valueOf(tag));
    }

    public void testVisitUninitializedVariableInfo() throws Exception {
        final UninitializedVariableInfo mockUninitializedVariableInfo = mock(UninitializedVariableInfo.class);
        final int tag = 123;
        final int offset = 456;

        checking(new Expectations() {{
            oneOf (mockUninitializedVariableInfo).getTag();
                will(returnValue(tag));
            oneOf (mockUninitializedVariableInfo).getOffset();
                will(returnValue(offset));
        }});

        printer.visitUninitializedVariableInfo(mockUninitializedVariableInfo);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "uninitialized-variable-info", 1);
        assertXPathText(xmlDocument, "uninitialized-variable-info/@tag", String.valueOf(tag));
        assertXPathText(xmlDocument, "uninitialized-variable-info/@offset", String.valueOf(offset));
    }

    private void assertXPathCount(String xmlDocument, String xPathExpression, int expectedCount) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        NodeList nodeList = (NodeList) xPath.evaluate(xPathExpression, in, XPathConstants.NODESET);
        int actualCount = nodeList.getLength();
        assertEquals("XPath \"" + xPathExpression + "\" in \n" + xmlDocument, expectedCount, actualCount);
    }

    private void assertXPathText(String xmlDocument, String xPathExpression, String expectedText) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        String actualText = (String) xPath.evaluate(xPathExpression, in, XPathConstants.STRING);
        assertEquals("XPath \"" + xPathExpression + "\" in \n" + xmlDocument, expectedText, actualText);
    }
}
