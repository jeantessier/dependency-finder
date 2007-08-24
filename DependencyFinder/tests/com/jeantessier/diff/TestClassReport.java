package com.jeantessier.diff;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.w3c.dom.*;

import com.sun.org.apache.xpath.internal.*;

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

        assertAttributeValue("removed-fields/declaration", name, "'");
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

        assertAttributeValue("new-fields/declaration", name, "'");
    }

    private void addRemovedFieldDifferences(String name) {
        Field_info oldField = oldClassfile.getField(name);
        FieldDifferences fieldDifferences = new FieldDifferences(oldField.getFullName(), oldField, null);
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

        InputSource in  = new InputSource(new StringReader(xmlDocument));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

        String xPathExpression = "*/" + nodeName + "[@name='" + nameAttribute + "']/@value";
        Node node = XPathAPI.selectSingleNode(doc, xPathExpression);
        assertEquals("XPath \"" + xPathExpression + "\" in \n" + xmlDocument, valueAttribute, node.getNodeValue());
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