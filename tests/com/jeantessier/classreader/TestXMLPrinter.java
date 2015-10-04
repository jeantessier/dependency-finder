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
import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;

public class TestXMLPrinter extends MockObjectTestCase {
    private static final String TEST_CLASS = "test";
    private static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    private static final String TEST_DIRECTORY = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

    private static final String SPECIFIC_ENCODING = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

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
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }
    
    public void testSpecificDTDPrefix() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testDefaultEncoding() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer));

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXParseException ex) {
            // Expected
        }
    }

    public void testSpecificEncoding() throws Exception {
        checking(new Expectations() {{
            one (errorHandler).fatalError(with(any(SAXParseException.class)));
        }});

        buffer = new StringWriter();
        printer = new XMLPrinter(new PrintWriter(buffer), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = buffer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
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
        printer.visitClassfiles(Collections.<Classfile>emptyList());

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
            one(mockClassfile).isPublic();
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
            one (mockClassfile).isPublic(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/public", 1);
    }

    public void testNonFinalClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isFinal(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/final", 0);
    }

    public void testFinalClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isFinal(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/final", 1);
    }

    public void testNonSuperClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isSuper(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/super", 0);
    }

    public void testSuperClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isSuper(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/super", 1);
    }

    public void testNonInterfaceClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isInterface(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-interface", 0);
    }

    public void testInterfaceClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isInterface(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-interface", 1);
    }

    public void testNonAbstractClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isAbstract(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/abstract", 0);
    }

    public void testAbstractClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isAbstract(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/abstract", 1);
    }

    public void testNonSyntheticClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isSynthetic(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/synthetic", 0);
    }

    public void testSyntheticClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isSynthetic(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/synthetic", 1);
    }

    public void testNonAnnotationClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isAnnotation(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-annotation", 0);
    }

    public void testAnnotationClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isAnnotation(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/is-annotation", 1);
    }

    public void testNonEnumClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isEnum(); will(returnValue(false));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/enum", 0);
    }

    public void testEnumClassfile() throws Exception {
        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
            one (mockClassfile).isEnum(); will(returnValue(true));
            ignoring (mockClassfile);
        }});

        printer.visitClassfile(mockClassfile);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "classfile/enum", 1);
    }

    public void testNonPublicField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isPublic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/public", 0);
    }

    public void testPublicField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isPublic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/public", 1);
    }

    public void testNonProtectedField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isProtected(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/protected", 0);
    }

    public void testProtectedField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isProtected(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/protected", 1);
    }

    public void testNonPrivateField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isPrivate(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/private", 0);
    }

    public void testPrivateField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isPrivate(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/private", 1);
    }

    public void testNonStaticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isStatic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/static", 0);
    }

    public void testStaticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isStatic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/static", 1);
    }

    public void testNonFinalField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isFinal(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/final", 0);
    }

    public void testFinalField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isFinal(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/final", 1);
    }

    public void testNonVolatileField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isVolatile(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/volatile", 0);
    }

    public void testVolatileField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isVolatile(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/volatile", 1);
    }

    public void testNonTransientField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isTransient(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/transient", 0);
    }

    public void testTransientField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isTransient(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/transient", 1);
    }

    public void testNonSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isSynthetic(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 0);
    }

    public void testSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isSynthetic(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 1);
    }

    public void testNonEnumField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isEnum(); will(returnValue(false));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/enum", 0);
    }

    public void testEnumField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            one (mockField).isEnum(); will(returnValue(true));
            ignoring (mockField);
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/enum", 1);
    }

    public void testNonPublicMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isPublic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/public", 0);
    }

    public void testPublicMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isPublic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/public", 1);
    }

    public void testNonPrivateMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isPrivate(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/private", 0);
    }

    public void testPrivateMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isPrivate(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/private", 1);
    }

    public void testNonProtectedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isProtected(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/protected", 0);
    }

    public void testProtectedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isProtected(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/protected", 1);
    }

    public void testNonStaticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isStatic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/static", 0);
    }

    public void testStaticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isStatic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/static", 1);
    }

    public void testNonFinalMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isFinal(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/final", 0);
    }

    public void testFinalMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isFinal(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/final", 1);
    }

    public void testNonSynchronizedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isSynchronized(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synchronized", 0);
    }

    public void testSynchronizedMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isSynchronized(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synchronized", 1);
    }

    public void testNonBridgeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isBridge(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/bridge", 0);
    }

    public void testBridgeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isBridge(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/bridge", 1);
    }

    public void testNonVarargsMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isVarargs(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/varargs", 0);
    }

    public void testVarargsMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isVarargs(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/varargs", 1);
    }

    public void testNonNativeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isNative(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/native", 0);
    }

    public void testNativeMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isNative(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/native", 1);
    }

    public void testNonAbstractMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isAbstract(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/abstract", 0);
    }

    public void testAbstractMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isAbstract(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/abstract", 1);
    }

    public void testNonStrictMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isStrict(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/strict", 0);
    }

    public void testStrictMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isStrict(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/strict", 1);
    }

    public void testNonSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isSynthetic(); will(returnValue(false));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 0);
    }

    public void testSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockMethod).isSynthetic(); will(returnValue(true));
            ignoring (mockMethod);
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 1);
    }

    public void testNonPublicInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isPublic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/public", 0);
    }

    public void testPublicInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isPublic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/public", 1);
    }

    public void testNonPrivateInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isPrivate(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/private", 0);
    }

    public void testPrivateInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isPrivate(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/private", 1);
    }

    public void testNonProtectedInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isProtected(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/protected", 0);
    }

    public void testProtectedInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isProtected(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/protected", 1);
    }

    public void testNonStaticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isStatic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/static", 0);
    }

    public void testStaticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isStatic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/static", 1);
    }

    public void testNonFinalInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isFinal(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/final", 0);
    }

    public void testFinalInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isFinal(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/final", 1);
    }

    public void testNonInterfaceInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isInterface(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-interface", 0);
    }

    public void testInterfaceInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isInterface(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-interface", 1);
    }

    public void testNonAbstractInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isAbstract(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/abstract", 0);
    }

    public void testAbstractInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isAbstract(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/abstract", 1);
    }

    public void testNonSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isSynthetic(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 0);
    }

    public void testSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isSynthetic(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 1);
    }

    public void testNonAnnotationInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isAnnotation(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-annotation", 0);
    }

    public void testAnnotationInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isAnnotation(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/is-annotation", 1);
    }

    public void testNonEnumInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isEnum(); will(returnValue(false));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/enum", 0);
    }

    public void testEnumInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            one (mockInnerClass).isEnum(); will(returnValue(true));
            ignoring (mockInnerClass);
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/enum", 1);
    }

    public void testVisitEnclosingMethod_noMethod() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 0;

        checking(new Expectations() {{
            one(mockEnclosingMethod).getRawClassInfo();
            will(returnValue(mockClassInfo));
            one(mockClassInfo).accept(printer);
            one(mockEnclosingMethod).getMethodIndex();
            will(returnValue(methodIndex));
        }});

        printer.visitEnclosingMethod_attribute(mockEnclosingMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "enclosing-method-attribute/class", 1);
        assertXPathCount(xmlDocument, "enclosing-method-attribute/method", 1);
    }

    public void testVisitEnclosingMethod_regularMethod() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 123;
        final NameAndType_info mockNameAndType = mock(NameAndType_info.class);

        checking(new Expectations() {{
            one (mockEnclosingMethod).getRawClassInfo(); will(returnValue(mockClassInfo));
            one (mockClassInfo).accept(printer);
            one (mockEnclosingMethod).getMethodIndex(); will(returnValue(methodIndex));
            one (mockEnclosingMethod).getRawMethod(); will(returnValue(mockNameAndType));
            exactly(2).of (mockNameAndType).getName(); will(returnValue("testMethod"));
            exactly(2).of (mockNameAndType).getType(); will(returnValue("()V"));
        }});

        printer.visitEnclosingMethod_attribute(mockEnclosingMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "enclosing-method-attribute/class", 1);
        assertXPathCount(xmlDocument, "enclosing-method-attribute/method", 1);
        assertXPathText(xmlDocument, "enclosing-method-attribute/method", "void testMethod()");
    }

    public void testVisitEnclosingMethod_constructor() throws Exception {
        final EnclosingMethod_attribute mockEnclosingMethod = mock(EnclosingMethod_attribute.class);
        final Class_info mockClassInfo = mock(Class_info.class);
        final int methodIndex = 123;
        final NameAndType_info mockNameAndType = mock(NameAndType_info.class);

        checking(new Expectations() {{
            one (mockEnclosingMethod).getRawClassInfo(); will(returnValue(mockClassInfo));
            one (mockClassInfo).accept(printer);
            one (mockEnclosingMethod).getClassInfo(); will(returnValue("com.jeantessier.test.TestClass"));
            one (mockEnclosingMethod).getMethodIndex(); will(returnValue(methodIndex));
            one (mockEnclosingMethod).getRawMethod(); will(returnValue(mockNameAndType));
            one (mockNameAndType).getName(); will(returnValue("<init>"));
            one (mockNameAndType).getType(); will(returnValue("()V"));
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
            one (localVariable).getStartPC();
            one (localVariable).getLength();
            one (localVariable).getRawName();
            one (localVariable).getDescriptor();
                will(returnValue("I"));
            one (localVariable).getIndex();
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
            one (localVariableType).getStartPC();
            one (localVariableType).getLength();
            one (localVariableType).getRawName();
            one (localVariableType).getRawSignature();
            one (localVariableType).getIndex();
        }});

        printer.visitLocalVariableType(localVariableType);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "local-variable-type/@pc", 1);
        assertXPathCount(xmlDocument, "local-variable-type/@length", 1);
        assertXPathCount(xmlDocument, "local-variable-type/name", 1);
        assertXPathCount(xmlDocument, "local-variable-type/signature", 1);
        assertXPathCount(xmlDocument, "local-variable-type/@index", 1);
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
            one (annotation).accept(printer);
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
            one (annotation).accept(printer);
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
        final Parameter parameter = mock(Parameter.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeVisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(parameter)));
            one (parameter).accept(printer);
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
        final Parameter parameter = mock(Parameter.class);

        checking(new Expectations() {{
            atLeast(1).of (runtimeInvisibleParameterAnnotations).getParameterAnnotations();
                will(returnValue(Collections.singletonList(parameter)));
            one (parameter).accept(printer);
        }});

        printer.visitRuntimeInvisibleParameterAnnotations_attribute(runtimeInvisibleParameterAnnotations);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "runtime-invisible-parameter-annotations-attribute/parameter-annotations", 1);
    }

    public void testVisitAnnotationDefault_attribute() throws Exception {
        final AnnotationDefault_attribute annotationDefault = mock(AnnotationDefault_attribute.class);
        final ElementValue elementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            atLeast(1).of (annotationDefault).getElemementValue();
                will(returnValue(elementValue));
            one (elementValue).accept(printer);
        }});

        printer.visitAnnotationDefault_attribute(annotationDefault);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation-default-attribute", 1);
    }

    public void testVisitParameter_WithoutAnnotations() throws Exception {
        final Parameter parameter = mock(Parameter.class);

        checking(new Expectations() {{
            atLeast(1).of (parameter).getAnnotations();
        }});

        printer.visitParameter(parameter);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "parameter", 1);
        assertXPathCount(xmlDocument, "parameter/annotations", 1);
    }

    public void testVisitParameter_WithAnAnnotation() throws Exception {
        final Parameter parameter = mock(Parameter.class);
        final Annotation annotation = mock(Annotation.class);

        checking(new Expectations() {{
            atLeast(1).of (parameter).getAnnotations();
                will(returnValue(Collections.singleton(annotation)));
            one (annotation).accept(printer);
        }});

        printer.visitParameter(parameter);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "parameter", 1);
        assertXPathCount(xmlDocument, "parameter/annotations", 1);
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
            one (elementValuePair).accept(printer);
        }});

        printer.visitAnnotation(annotation);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "annotation", 1);
        assertXPathText(xmlDocument, "annotation/type", ANNOTATION_TYPE);
        assertXPathCount(xmlDocument, "annotation/element-value-pairs", 1);
    }

    public void testVisitElementValuePair() throws Exception {
        final ElementValuePair elementValuePair = mock(ElementValuePair.class);
        final ElementValue elementValue = mock(ElementValue.class);

        checking(new Expectations() {{
            one (elementValuePair).getElementName();
                will(returnValue(ELEMENT_NAME));
            one (elementValuePair).getElementValue();
                will(returnValue(elementValue));
            one (elementValue).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.BYTE.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.CHAR.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.DOUBLE.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.FLOAT.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.INTEGER.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.LONG.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.SHORT.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.BOOLEAN.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (constantElementValue).getTag();
                will(returnValue(ElementValueType.STRING.getTag()));
            one (constantElementValue).getRawConstValue();
                will(returnValue(constantPoolEntry));
            one (constantPoolEntry).accept(printer);
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
            one (enumElementValue).getTag();
                will(returnValue(ElementValueType.ENUM.getTag()));
            one (enumElementValue).getTypeName();
                will(returnValue(TEST_CLASS));
            one (enumElementValue).getConstName();
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
            one (classElementValue).getTag();
                will(returnValue(ElementValueType.CLASS.getTag()));
            one (classElementValue).getClassInfo();
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
            one (annotationElementValue).getTag();
                will(returnValue(ElementValueType.ANNOTATION.getTag()));
            one (annotationElementValue).getAnnotation();
                will(returnValue(annotation));
            one (annotation).accept(printer);
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
            one (arrayElementValue).getTag();
                will(returnValue(ElementValueType.ARRAY.getTag()));
            atLeast(1).of (arrayElementValue).getValues();
                will(returnValue(Collections.singleton(elementValue)));
            one (elementValue).accept(printer);
        }});

        printer.visitArrayElementValue(arrayElementValue);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "array-element-value", 1);
        assertXPathText(xmlDocument, "array-element-value/@tag", String.valueOf(ElementValueType.ARRAY.getTag()));
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
