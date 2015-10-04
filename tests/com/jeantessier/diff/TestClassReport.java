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

package com.jeantessier.diff;

import java.io.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestClassReport extends TestDifferencesFactoryBase implements ErrorHandler {
    private static final String MODIFIED_CLASS_NAME = "ModifiedPackage.ModifiedClass";

    private Classfile oldClassfile;
    private Classfile newClassfile;

    private ClassDifferences classDifferences;

    private ClassReport classReport;

    private XMLReader reader;

    protected void setUp() throws Exception {
        super.setUp();

        oldClassfile = getOldJar().getClassfile(MODIFIED_CLASS_NAME);
        newClassfile = getNewJar().getClassfile(MODIFIED_CLASS_NAME);

        classDifferences = new ClassDifferences(MODIFIED_CLASS_NAME, oldClassfile, newClassfile);

        classReport = new ClassReport();

        boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
        reader.setErrorHandler(this);
    }

    public void testXmlEscapingRemovedLessThanFieldValue() throws Exception {
        String name = "REMOVED_LESS_THAN_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "<");
    }

    public void testXmlEscapingRemovedAmpersandFieldValue() throws Exception {
        String name = "REMOVED_AMPERSAND_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "&");
    }

    public void testXmlEscapingRemovedGreaterThanFieldValue() throws Exception {
        String name = "REMOVED_GREATER_THAN_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, ">");
    }

    public void testXmlEscapingRemovedQuoteFieldValue() throws Exception {
        String name = "REMOVED_QUOTE_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "\"");
    }

    public void testXmlEscapingRemovedApostropheFieldValue() throws Exception {
        String name = "REMOVED_APOSTROPHE_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "'");
    }

    public void testXmlEscapingRemovedNonAsciiFieldValue() throws Exception {
        String name = "REMOVED_NON_ASCII_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "\u00A5");
    }

    public void testXmlEscapingModifiedLessThanFieldValue() throws Exception {
        String name = "MODIFIED_LESS_THAN_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "<");
    }

    public void testXmlEscapingModifiedAmpersandFieldValue() throws Exception {
        String name = "MODIFIED_AMPERSAND_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "&");
    }

    public void testXmlEscapingModifiedGreaterThanFieldValue() throws Exception {
        String name = "MODIFIED_GREATER_THAN_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, ">");
    }

    public void testXmlEscapingModifiedQuoteFieldValue() throws Exception {
        String name = "MODIFIED_QUOTE_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "\"");
    }

    public void testXmlEscapingModifiedApostropheFieldValue() throws Exception {
        String name = "MODIFIED_APOSTROPHE_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "'");
    }

    public void testXmlEscapingModifiedNonAsciiFieldValue() throws Exception {
        String name = "MODIFIED_NON_ASCII_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "\u00A5");
    }

    public void testXmlEscapingNewLessThanFieldValue() throws Exception {
        String name = "NEW_LESS_THAN_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "<");
    }

    public void testXmlEscapingNewAmpersandFieldValue() throws Exception {
        String name = "NEW_AMPERSAND_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "&");
    }

    public void testXmlEscapingNewGreaterThanFieldValue() throws Exception {
        String name = "NEW_GREATER_THAN_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, ">");
    }

    public void testXmlEscapingNewQuoteFieldValue() throws Exception {
        String name = "NEW_QUOTE_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "\"");
    }

    public void testXmlEscapingNewApostropheFieldValue() throws Exception {
        String name = "NEW_APOSTROPHE_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "'");
    }

    public void testXmlEscapingNewNonAsciiFieldValue() throws Exception {
        String name = "NEW_NON_ASCII_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "\u00A5");
    }

    private void addRemovedFieldDifferences(String name) {
        Field_info oldField = oldClassfile.getField(name);
        FieldDifferences fieldDifferences = new FieldDifferences(oldField.getFullName(), oldField, null);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void addModifiedFieldDifferences(String name) {
        Field_info oldField = oldClassfile.getField(name);
        Field_info newField = newClassfile.getField(name);
        FieldDifferences fieldDifferences = new FieldDifferences(oldField.getFullName(), oldField, newField);
        fieldDifferences.setConstantValueDifference(true);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void addNewFieldDifferences(String name) {
        Field_info newField = newClassfile.getField(name);
        FieldDifferences fieldDifferences = new FieldDifferences(newField.getFullName(), null, newField);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void assertAttributeValue(String nodeName, String nameAttribute, String valueAttribute) throws Exception {
        String xmlDocument = classReport.render();

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        String xPathExpression = "*/" + nodeName + "[@name='" + nameAttribute + "']/@value";
        String result = xPath.evaluate(xPathExpression, in);
        assertEquals("XPath \"" + xPathExpression + "\" in \n" + xmlDocument, valueAttribute, result);
    }

    public void error(SAXParseException ex) {
        // Ignore
    }

    public void fatalError(SAXParseException ex) {
        // Ignore
    }

    public void warning(SAXParseException ex) {
        // Ignore
    }
}