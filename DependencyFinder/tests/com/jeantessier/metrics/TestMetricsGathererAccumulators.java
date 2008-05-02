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

import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

import com.jeantessier.classreader.*;

public class TestMetricsGathererAccumulators extends MockObjectTestCase {
    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);
    }

    public void testVisitField_info_public() throws Exception {
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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
            one (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, fieldName);
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

    public void testVisitMethod_info_public() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            allowing (mockMethod).isPublic();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PUBLIC_METHODS, methodSignature);
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_private() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            allowing (mockMethod).isPrivate();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PRIVATE_METHODS, methodSignature);
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_protected() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            allowing (mockMethod).isProtected();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PROTECTED_METHODS, methodSignature);
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_package() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            allowing (mockMethod).isPackage();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_static() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            allowing (mockMethod).isStatic();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.STATIC_METHODS, methodSignature);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_final() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            allowing (mockMethod).isFinal();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.FINAL_METHODS, methodSignature);
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_synchronized() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            allowing (mockMethod).isSynchronized();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.SYNCHRONIZED_METHODS, methodSignature);
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_native() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            allowing (mockMethod).isNative();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.NATIVE_METHODS, methodSignature);
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_abstract() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            allowing (mockMethod).isAbstract();
            will(returnValue(true));
            one (mockMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_METHODS, methodSignature);
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_synthetic() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            allowing (mockMethod).isSynthetic();
            will(returnValue(true));
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.SYNTHETIC_METHODS)), with(a(String.class)));
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.SLOC)), with(a(Number.class)));
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_parameters() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockFactory).createMethodMetrics(methodSignature);
            will(returnValue(mockMetrics));
            one (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
            allowing (mockMethod).getAccessFlag();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPackage();
            one (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, methodSignature);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isSynthetic();
            atLeast(1).of (mockMethod).getDescriptor();
            will(returnValue("(iLjava/lang/Object;)V"));
            ignoring (mockFactory).createClassMetrics("java.lang.Object");
            ignoring (mockMetrics).getParent();
            ignoring (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)), with(any(String.class)));
            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 2);
            ignoring (mockMethod).getAttributes();
            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testIsInnerClassOfCurrentClass_NamedInnerClass() throws Exception {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final InnerClasses_attribute mockInnerClasses = mock (InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));

            one (mockInnerClass).getInnerClasses();
            will(returnValue(mockInnerClasses));
            one (mockInnerClasses).getClassfile();
            will(returnValue(mockClassfile));
            one (mockClassfile).getClassName();
            will(returnValue("testpackage.TestClass"));
            exactly(2).of (mockInnerClass).getOuterClassInfo();
            will(returnValue("testpackage.TestClass"));
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", true, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testIsInnerClassOfCurrentClass_AnonymousInnerClass() throws Exception {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final InnerClasses_attribute mockInnerClasses = mock (InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));

            one (mockInnerClass).getInnerClasses();
            will(returnValue(mockInnerClasses));
            one (mockInnerClasses).getClassfile();
            will(returnValue(mockClassfile));
            one (mockClassfile).getClassName();
            will(returnValue("testpackage.TestClass"));
            one (mockInnerClass).getOuterClassInfo();
            will(returnValue(""));
            one (mockInnerClass).getInnerClassInfo();
            will(returnValue("testpackage.TestClass$1"));
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", true, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testVisitInnerClass_public() throws Exception {
//        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClasses_attribute mockInnerClasses = mock(InnerClasses_attribute.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final int innerClassInfoIndex = 123;
        final Metrics mockMetrics = mock(Metrics.class);


        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            allowing (mockInnerClass).getInnerClassInfoIndex();
            will(returnValue(innerClassInfoIndex));
            allowing (mockInnerClass).getInnerClasses();
            will(returnValue(mockInnerClasses));
//            allowing (mockInnerClass).getClassfile();
//            allowing (mockInnerClass).getFullSignature();
//            will(returnValue(methodSignature));
//            one (mockFactory).createMethodMetrics(methodSignature);
//            will(returnValue(mockMetrics));
//            one (mockFactory).includeMethodMetrics(mockMetrics);
//            allowing (mockMetrics).getName();
//            allowing (mockInnerClass).getAccessFlag();
//            allowing (mockInnerClass).isPublic();
//            will(returnValue(true));
//            one (mockMetrics).addToMeasurement(BasicMeasurements.PUBLIC_METHODS, methodSignature);
//            ignoring (mockInnerClass).isPrivate();
//            ignoring (mockInnerClass).isProtected();
//            ignoring (mockInnerClass).isPackage();
//            ignoring (mockInnerClass).isStatic();
//            ignoring (mockInnerClass).isFinal();
//            ignoring (mockInnerClass).isSynchronized();
//            ignoring (mockInnerClass).isNative();
//            ignoring (mockInnerClass).isAbstract();
//            ignoring (mockInnerClass).isSynthetic();
//            allowing (mockInnerClass).getDescriptor();
//            will(returnValue("()V"));
//            one (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
//            ignoring (mockInnerClass).getAttributes();
//            one (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitSynthetic_attribute_class() throws Exception {
        final String className = "testpackage.TestClass";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Synthetic_attribute mockSyntheticAttribute = mock(Synthetic_attribute.class);
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockSyntheticAttribute).getOwner();
            will(returnValue(mockClassfile));
            one (mockClassfile).getClassName();
            will(returnValue(className));
            one (mockProjectMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, className);
            one (mockGroupMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, className);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentProject(mockProjectMetrics);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.visitSynthetic_attribute(mockSyntheticAttribute);
    }

    public void testVisitSynthetic_attribute_field() throws Exception {
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
            one (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, fieldName);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitSynthetic_attribute(mockSyntheticAttribute);
    }

    public void testVisitSynthetic_attribute_method() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Synthetic_attribute mockSyntheticAttribute = mock(Synthetic_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockSyntheticAttribute).getOwner();
            will(returnValue(mockMethod));
            one (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS, methodSignature);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitSynthetic_attribute(mockSyntheticAttribute);
    }

    public void testVisitSynthetic_attribute_other() throws Exception {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Synthetic_attribute mockSyntheticAttribute = mock(Synthetic_attribute.class);
        final Visitable mockOwner = mock(Visitable.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockSyntheticAttribute).getOwner();
            will(returnValue(mockOwner));
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.visitSynthetic_attribute(mockSyntheticAttribute);
    }

    public void testVisitDeprecated_attribute_class() throws Exception {
        final String className = "testpackage.TestClass";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockClassfile));
            one (mockClassfile).getClassName();
            will(returnValue(className));
            one (mockProjectMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className);
            one (mockGroupMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, className);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentProject(mockProjectMetrics);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_field() throws Exception {
        final String fieldName = "testpackage.TestClass.testField";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockField));
            one (mockField).getFullName();
            will(returnValue(fieldName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, fieldName);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_method() throws Exception {
        final String methodSignature = "testpackage.TestClass.testMethod()";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockMethod));
            one (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            one (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, methodSignature);
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_other() throws Exception {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Visitable mockOwner = mock(Visitable.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockOwner));
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitLocalVariable() throws Exception {
        final String localVariableName = "localVariableName";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics(with(any(String.class)));
            one (mockLocalVariable).getName();
            will(returnValue(localVariableName));
            one (mockMetrics).addToMeasurement(BasicMeasurements.LOCAL_VARIABLES, localVariableName);
            one (mockLocalVariable).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer("test project", mockFactory);
        sut.setCurrentMethod(mockMetrics);
        sut.visitLocalVariable(mockLocalVariable);
    }
}
