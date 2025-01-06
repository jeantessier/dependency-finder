/*
 *  Copyright (c) 2001-2024, Jean Tessier
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
import javax.xml.xpath.*;

import org.junit.jupiter.api.*;
import org.xml.sax.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassReport extends TestDifferencesFactoryBase {
    private static final String MODIFIED_CLASS_NAME = "ModifiedPackage.ModifiedClass";

    private Classfile oldClassfile;
    private Classfile newClassfile;

    private ClassDifferences classDifferences;

    private final ClassReport classReport = new ClassReport();

    @BeforeEach
    void setUp() {
        oldClassfile = oldLoader.getClassfile(MODIFIED_CLASS_NAME);
        newClassfile = newLoader.getClassfile(MODIFIED_CLASS_NAME);

        classDifferences = new ClassDifferences(MODIFIED_CLASS_NAME, oldClassfile, newClassfile);
    }

    @Test
    void testXmlEscapingRemovedLessThanFieldValue() throws Exception {
        String name = "REMOVED_LESS_THAN_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "<");
    }

    @Test
    void testXmlEscapingRemovedAmpersandFieldValue() throws Exception {
        String name = "REMOVED_AMPERSAND_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "&");
    }

    @Test
    void testXmlEscapingRemovedGreaterThanFieldValue() throws Exception {
        String name = "REMOVED_GREATER_THAN_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, ">");
    }

    @Test
    void testXmlEscapingRemovedQuoteFieldValue() throws Exception {
        String name = "REMOVED_QUOTE_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "\"");
    }

    @Test
    void testXmlEscapingRemovedApostropheFieldValue() throws Exception {
        String name = "REMOVED_APOSTROPHE_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "'");
    }

    @Test
    void testXmlEscapingRemovedNonAsciiFieldValue() throws Exception {
        String name = "REMOVED_NON_ASCII_STRING";
        addRemovedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("removed-fields/declaration", name, "\u00A5");
    }

    @Test
    void testXmlEscapingModifiedLessThanFieldValue() throws Exception {
        String name = "MODIFIED_LESS_THAN_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "<");
    }

    @Test
    void testXmlEscapingModifiedAmpersandFieldValue() throws Exception {
        String name = "MODIFIED_AMPERSAND_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "&");
    }

    @Test
    void testXmlEscapingModifiedGreaterThanFieldValue() throws Exception {
        String name = "MODIFIED_GREATER_THAN_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, ">");
    }

    @Test
    void testXmlEscapingModifiedQuoteFieldValue() throws Exception {
        String name = "MODIFIED_QUOTE_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "\"");
    }

    @Test
    void testXmlEscapingModifiedApostropheFieldValue() throws Exception {
        String name = "MODIFIED_APOSTROPHE_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "'");
    }

    @Test
    void testXmlEscapingModifiedNonAsciiFieldValue() throws Exception {
        String name = "MODIFIED_NON_ASCII_STRING";
        addModifiedFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("modified-fields/feature/modified-declaration/new-declaration", name, "\u00A5");
    }

    @Test
    void testXmlEscapingNewLessThanFieldValue() throws Exception {
        String name = "NEW_LESS_THAN_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "<");
    }

    @Test
    void testXmlEscapingNewAmpersandFieldValue() throws Exception {
        String name = "NEW_AMPERSAND_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "&");
    }

    @Test
    void testXmlEscapingNewGreaterThanFieldValue() throws Exception {
        String name = "NEW_GREATER_THAN_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, ">");
    }

    @Test
    void testXmlEscapingNewQuoteFieldValue() throws Exception {
        String name = "NEW_QUOTE_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "\"");
    }

    @Test
    void testXmlEscapingNewApostropheFieldValue() throws Exception {
        String name = "NEW_APOSTROPHE_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "'");
    }

    @Test
    void testXmlEscapingNewNonAsciiFieldValue() throws Exception {
        String name = "NEW_NON_ASCII_STRING";
        addNewFieldDifferences(name);

        classReport.visitClassDifferences(classDifferences);

        assertAttributeValue("new-fields/declaration", name, "\u00A5");
    }

    private void addRemovedFieldDifferences(String name) {
        Field_info oldField = oldClassfile.getField(f -> f.getName().equals(name));
        FieldDifferences fieldDifferences = new FieldDifferences(oldField.getFullName(), oldField, null);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void addModifiedFieldDifferences(String name) {
        Field_info oldField = oldClassfile.getField(f -> f.getName().equals(name));
        Field_info newField = newClassfile.getField(f -> f.getName().equals(name));
        FieldDifferences fieldDifferences = new FieldDifferences(oldField.getFullName(), oldField, newField);
        fieldDifferences.setConstantValueDifference(true);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void addNewFieldDifferences(String name) {
        Field_info newField = newClassfile.getField(f -> f.getName().equals(name));
        FieldDifferences fieldDifferences = new FieldDifferences(newField.getFullName(), null, newField);
        classDifferences.getFeatureDifferences().add(fieldDifferences);
    }

    private void assertAttributeValue(String nodeName, String nameAttribute, String valueAttribute) throws Exception {
        String xmlDocument = classReport.render();

        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));

        String xPathExpression = "*/" + nodeName + "[@name='" + nameAttribute + "']/@value";
        String result = xPath.evaluate(xPathExpression, in);
        assertEquals(valueAttribute, result, "XPath \"" + xPathExpression + "\" in \n" + xmlDocument);
    }
}
