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
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.oro.text.perl.*;
import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.w3c.dom.*;
import org.xml.sax.*;

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

    public void testNonSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            allowing (mockField).getAccessFlag();
            allowing (mockField).isPublic();
            allowing (mockField).isProtected();
            allowing (mockField).isPrivate();
            allowing (mockField).isStatic();
            allowing (mockField).isFinal();
            allowing (mockField).isVolatile();
            allowing (mockField).isTransient();
            one (mockField).isSynthetic(); will(returnValue(false));
            allowing (mockField).getRawName();
            allowing (mockField).getType();
            allowing (mockField).getAttributes();
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 0);
    }

    public void testSyntheticField() throws Exception {
        final Field_info mockField = mock(Field_info.class);

        checking(new Expectations() {{
            allowing (mockField).getAccessFlag();
            allowing (mockField).isPublic();
            allowing (mockField).isProtected();
            allowing (mockField).isPrivate();
            allowing (mockField).isStatic();
            allowing (mockField).isFinal();
            allowing (mockField).isVolatile();
            allowing (mockField).isTransient();
            one (mockField).isSynthetic(); will(returnValue(true));
            allowing (mockField).getRawName();
            allowing (mockField).getType();
            allowing (mockField).getAttributes();
        }});

        printer.visitField_info(mockField);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "field-info/synthetic", 1);
    }

    public void testNonSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            allowing (mockMethod).getAccessFlag();
            allowing (mockMethod).isPublic();
            allowing (mockMethod).isProtected();
            allowing (mockMethod).isPrivate();
            allowing (mockMethod).isStatic();
            allowing (mockMethod).isFinal();
            allowing (mockMethod).isSynchronized();
            allowing (mockMethod).isNative();
            allowing (mockMethod).isAbstract();
            allowing (mockMethod).isStrict();
            one (mockMethod).isSynthetic(); will(returnValue(false));
            allowing (mockMethod).getRawName();
            allowing (mockMethod).getName();
            allowing (mockMethod).getReturnType();
            allowing (mockMethod).getSignature();
            allowing (mockMethod).getAttributes();
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 0);
    }

    public void testSyntheticMethod() throws Exception {
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            allowing (mockMethod).getAccessFlag();
            allowing (mockMethod).isPublic();
            allowing (mockMethod).isProtected();
            allowing (mockMethod).isPrivate();
            allowing (mockMethod).isStatic();
            allowing (mockMethod).isFinal();
            allowing (mockMethod).isSynchronized();
            allowing (mockMethod).isNative();
            allowing (mockMethod).isAbstract();
            allowing (mockMethod).isStrict();
            one (mockMethod).isSynthetic(); will(returnValue(true));
            allowing (mockMethod).getRawName();
            allowing (mockMethod).getName();
            allowing (mockMethod).getReturnType();
            allowing (mockMethod).getSignature();
            allowing (mockMethod).getAttributes();
        }});

        printer.visitMethod_info(mockMethod);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "method-info/synthetic", 1);
    }

    public void testNonSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            allowing (mockInnerClass).getAccessFlag();
            allowing (mockInnerClass).isPublic();
            allowing (mockInnerClass).isProtected();
            allowing (mockInnerClass).isPrivate();
            allowing (mockInnerClass).isStatic();
            allowing (mockInnerClass).isFinal();
            allowing (mockInnerClass).isInterface();
            allowing (mockInnerClass).isAbstract();
            one (mockInnerClass).isSynthetic(); will(returnValue(false));
            allowing (mockInnerClass).getInnerClassInfoIndex();
            allowing (mockInnerClass).getOuterClassInfoIndex();
            allowing (mockInnerClass).getInnerNameIndex();
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 0);
    }

    public void testSyntheticInnerClass() throws Exception {
        final InnerClass mockInnerClass = mock(InnerClass.class);

        checking(new Expectations() {{
            allowing (mockInnerClass).getAccessFlag();
            allowing (mockInnerClass).isPublic();
            allowing (mockInnerClass).isProtected();
            allowing (mockInnerClass).isPrivate();
            allowing (mockInnerClass).isStatic();
            allowing (mockInnerClass).isFinal();
            allowing (mockInnerClass).isInterface();
            allowing (mockInnerClass).isAbstract();
            one (mockInnerClass).isSynthetic(); will(returnValue(true));
            allowing (mockInnerClass).getInnerClassInfoIndex();
            allowing (mockInnerClass).getOuterClassInfoIndex();
            allowing (mockInnerClass).getInnerNameIndex();
        }});

        printer.visitInnerClass(mockInnerClass);

        String xmlDocument = buffer.toString();
        assertXPathCount(xmlDocument, "inner-class/synthetic", 1);
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
        assertXPathCount(xmlDocument, "annotation-default-attribute/element-value", 1);
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
        assertXPathCount(xmlDocument, "element-value-pair/element-value", 1);
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
