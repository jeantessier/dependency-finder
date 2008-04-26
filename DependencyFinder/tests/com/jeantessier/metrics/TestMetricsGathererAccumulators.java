/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

package com.jeantessier.metrics;

import java.io.*;
import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererAccumulators extends MockObjectTestCase {
    public static final String TEST_CLASS = "ModifiedPackage.ModifiedClass";
    public static final String TEST_FILENAME = "tests" + File.separator + "JarJarDiff" + File.separator + "old" + File.separator + "ModifiedPackage" + File.separator + "ModifiedClass.class";

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);
    }

    public void testVisitField_info_public() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            allowing (mockField).isPublic();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PUBLIC_ATTRIBUTES, fieldName);
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_private() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            allowing (mockField).isPrivate();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PRIVATE_ATTRIBUTES, fieldName);
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_protected() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            allowing (mockField).isProtected();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PROTECTED_ATTRIBUTES, fieldName);
            ignoring (mockField).isPackage();
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_package() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            allowing (mockField).isPackage();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_static() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            allowing (mockField).isStatic();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.STATIC_ATTRIBUTES, fieldName);
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_final() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            ignoring (mockField).isStatic();
            allowing (mockField).isFinal();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.FINAL_ATTRIBUTES, fieldName);
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_volatile() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            allowing (mockField).isVolatile();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.VOLATILE_ATTRIBUTES, fieldName);
            ignoring (mockField).isTransient();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_transient() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            allowing (mockField).isTransient();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.TRANSIENT_ATTRIBUTES, fieldName);
            ignoring (mockField).isSynthetic();
            ignoring (mockField).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_synthetic() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);
        final Attribute_info mockSyntheticAttribute = mock(Synthetic_attribute.class);
        final Collection<? extends Attribute_info> attributes = Collections.singleton(mockSyntheticAttribute);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
        }});

        final MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlag();
            ignoring (mockField).isPublic();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, fieldName);
            ignoring (mockField).isStatic();
            ignoring (mockField).isFinal();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isTransient();
            allowing (mockField).isSynthetic();
            will(returnValue(true));
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.SYNTHETIC_ATTRIBUTES)), with(a(String.class)));
            one (mockField).getAttributes();
            will(returnValue(attributes));
            one (mockSyntheticAttribute).accept(sut);
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.CLASS_SLOC)), with(a(Number.class)));
            ignoring (mockField).getDescriptor();
        }});

        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitSynthetic_attribute_attribute() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.SYNTHETIC_ATTRIBUTES;
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Synthetic_attribute mockSyntheticAttribute = mock(Synthetic_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockSyntheticAttribute).getOwner();
            will(returnValue(mockField));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(measurementName, fieldName);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitSynthetic_attribute(mockSyntheticAttribute);
    }

    public void testVisitLocalVariable() throws Exception {
        final BasicMeasurements measurementName = BasicMeasurements.LOCAL_VARIABLES;
        final String localVariableName = "localVariableName";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockLocalVariable).getName();
            will(returnValue(localVariableName));
            one (mockMetrics).addToMeasurement(measurementName, localVariableName);
            one (mockLocalVariable).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentMethod(mockMetrics);
        sut.visitLocalVariable(mockLocalVariable);
    }
}
