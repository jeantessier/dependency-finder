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

package com.jeantessier.metrics;

import com.jeantessier.classreader.*;
import org.jmock.Expectations;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.util.*;

public class TestMetricsGathererAccumulators extends MockObjectTestCase {
    private static final String PACKAGE_NAME = "test.package";
    private static final String CLASS_NAME = PACKAGE_NAME + ".TestClass";
    private static final String FIELD_NAME = CLASS_NAME + ".testField";
    private static final String METHOD_SIGNATURE = CLASS_NAME + ".testMethod()";
    private static final String METHOD_RETURN_TYPE = "void";
    private static final String METHOD_METRICS_NAME = METHOD_SIGNATURE + ": " + METHOD_RETURN_TYPE;
    private static final String INNER_CLASS_NAME = CLASS_NAME + "$InnerClass";
    private static final String ANONYMOUS_INNER_CLASS_NAME = CLASS_NAME + "$1";

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }

    public void testVisitClassfile_public() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "defined group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(List.of(mockDefinedGroupMetrics)));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            allowing (mockClassfile).isPublic();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PUBLIC_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PUBLIC_CLASSES, CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PUBLIC_CLASSES, CLASS_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPackage();
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_package() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            allowing (mockClassfile).isPackage();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_final() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "defined group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(List.of(mockDefinedGroupMetrics)));
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            allowing (mockClassfile).isFinal();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.FINAL_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.FINAL_CLASSES, CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.FINAL_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_super() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "defined group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(List.of(mockDefinedGroupMetrics)));
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            allowing (mockClassfile).isSuper();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.SUPER_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.SUPER_CLASSES, CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.SUPER_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_interface() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            allowing (mockClassfile).isInterface();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INTERFACES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INTERFACES, CLASS_NAME);
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_abstract() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
            will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
            will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
            will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
            will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
            will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            allowing (mockClassfile).isAbstract();
            will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_CLASSES, CLASS_NAME);

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_synthetic_withSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");
        final Synthetic_attribute mockSynthetic_attribute = mock(Synthetic_attribute.class);

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
                will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
                will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
                will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
                will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
                will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            allowing (mockClassfile).isSynthetic();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();
        }});

        final MetricsGatherer sut = new MetricsGatherer(mockFactory);

        checking(new Expectations() {{
            allowing (mockClassfile).getAttributes();
                will(returnValue(Collections.singleton(mockSynthetic_attribute)));
            oneOf (mockSynthetic_attribute).accept(sut);
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_synthetic_withoutSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
                will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
                will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
                will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
                will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
                will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            allowing (mockClassfile).isSynthetic();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_annotation() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
                will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
                will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
                will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
                will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
                will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            allowing (mockClassfile).isAnnotation();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ANNOTATION_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ANNOTATION_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isEnum();
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_enum() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
                will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
                will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
                will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
                will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
                will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            allowing (mockClassfile).isEnum();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ENUM_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ENUM_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isModule();
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClassfile_module() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockClassMetrics = mock(Metrics.class, "class");

        checking(new Expectations() {{
            oneOf (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassfile).getClassName();
                will(returnValue(CLASS_NAME));
            oneOf (mockFactory).createClassMetrics(CLASS_NAME);
                will(returnValue(mockClassMetrics));
            oneOf (mockFactory).includeClassMetrics(mockClassMetrics);
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockClassMetrics).getParent();
                will(returnValue(mockGroupMetrics));
            oneOf (mockGroupMetrics).getParent();
                will(returnValue(mockProjectMetrics));
            atLeast(1).of (mockGroupMetrics).getName();
                will(returnValue(PACKAGE_NAME));
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGES, PACKAGE_NAME);

            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.CLASSES, CLASS_NAME);
            ignoring (mockClassfile).getMajorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MAJOR_VERSION, 0);
            ignoring (mockClassfile).getMinorVersion();
            ignoring (mockClassMetrics).addToMeasurement(BasicMeasurements.MINOR_VERSION, 0);
            ignoring (mockClassfile).isPublic();
            ignoring (mockClassfile).isPackage();
            ignoring (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isFinal();
            ignoring (mockClassfile).isSuper();
            ignoring (mockClassfile).isInterface();
            ignoring (mockClassfile).isAbstract();

            ignoring (mockClassfile).hasSuperclass();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllInterfaces();
            ignoring (mockClassfile).getAllFields();
            ignoring (mockClassfile).getAllMethods();

            ignoring (mockClassfile).isSynthetic();
            ignoring (mockClassfile).isAnnotation();
            ignoring (mockClassfile).isEnum();
            allowing (mockClassfile).isModule();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.MODULE_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.MODULE_CLASSES, CLASS_NAME);
            ignoring (mockClassfile).isDeprecated();
            ignoring (mockClassfile).isGeneric();

            ignoring (mockClassfile).getAttributes();

            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitClassfile(mockClassfile);
    }

    public void testVisitField_info_public() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            allowing (mockField).isPublic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PUBLIC_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_protected() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            allowing (mockField).isProtected();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PROTECTED_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_private() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            allowing (mockField).isPrivate();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PRIVATE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isPackage();
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_package() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            allowing (mockField).isPackage();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_final() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            allowing (mockField).isFinal();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.FINAL_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_deprecated() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            allowing (mockField).isDeprecated();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_synthetic_withSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);
        final Synthetic_attribute mockSynthetic_attribute = mock(Synthetic_attribute.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            allowing (mockField).isSynthetic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.CLASS_SLOC)), with(any(Number.class)));
            ignoring (mockField).getDescriptor();
        }});

        final MetricsGatherer sut = new MetricsGatherer(mockFactory);

        checking(new Expectations() {{
            oneOf (mockField).getAttributes();
                will(returnValue(Collections.singleton(mockSynthetic_attribute)));
            oneOf (mockSynthetic_attribute).accept(sut);
        }});

        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_synthetic_withoutSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            allowing (mockField).isSynthetic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.CLASS_SLOC)), with(any(Number.class)));
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_static() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            allowing (mockField).isStatic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.STATIC_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_transient() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            allowing (mockField).isTransient();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.TRANSIENT_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isVolatile();
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_volatile() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            allowing (mockField).isVolatile();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.VOLATILE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isEnum();
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitField_info_enum() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockField).getFullName();
                will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ATTRIBUTES, FIELD_NAME);
            allowing (mockField).getFullSignature();
            allowing (mockMetrics).getName();
            allowing (mockField).getAccessFlags();
            ignoring (mockField).isPublic();
            ignoring (mockField).isProtected();
            ignoring (mockField).isPrivate();
            ignoring (mockField).isPackage();
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).isFinal();
            ignoring (mockField).isDeprecated();
            ignoring (mockField).isSynthetic();
            ignoring (mockField).isStatic();
            ignoring (mockField).isTransient();
            ignoring (mockField).isVolatile();
            allowing (mockField).isEnum();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ENUM_ATTRIBUTES, FIELD_NAME);
            ignoring (mockField).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.CLASS_SLOC, 1);
            ignoring (mockField).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitField_info(mockField);
    }

    public void testVisitMethod_info_public() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            allowing (mockMethod).isPublic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PUBLIC_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_protected() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            allowing (mockMethod).isProtected();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PROTECTED_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_private() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            allowing (mockMethod).isPrivate();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PRIVATE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isPackage();
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_package() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            allowing (mockMethod).isPackage();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_final() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).isFinal();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.FINAL_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_abstract() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            allowing (mockMethod).isAbstract();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 1);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_deprecated() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            allowing (mockMethod).isDeprecated();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_synthetic_withSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);
        final Synthetic_attribute mockSynthetic_attribute = mock(Synthetic_attribute.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            allowing (mockMethod).isSynthetic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.SLOC)), with(any(Number.class)));
        }});

        final MetricsGatherer sut = new MetricsGatherer(mockFactory);

        checking(new Expectations() {{
            oneOf (mockMethod).getAttributes();
                will(returnValue(Collections.singleton(mockSynthetic_attribute)));
            oneOf (mockSynthetic_attribute).accept(sut);
        }});

        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_synthetic_withoutSyntheticAttribute() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            allowing (mockMethod).isSynthetic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            never (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.SLOC)), with(any(Number.class)));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_static() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            allowing (mockMethod).isStatic();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.STATIC_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_synchronized() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            allowing (mockMethod).isSynchronized();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SYNCHRONIZED_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_bridge() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            allowing (mockMethod).isBridge();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.BRIDGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
            will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_varars() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            allowing (mockMethod).isVarargs();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.VARARGS_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_native() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            allowing (mockMethod).isNative();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.NATIVE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isStrict();
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_strict() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            allowing (mockMethod).isStrict();
                will(returnValue(true));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.STRICT_METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getDescriptor();
                will(returnValue("()V"));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 0);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testVisitMethod_info_parameters() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            allowing (mockMethod).getClassfile();
            allowing (mockMethod).getFullSignature();
                will(returnValue(METHOD_SIGNATURE));
            allowing (mockMethod).getReturnType();
                will(returnValue(METHOD_RETURN_TYPE));
            oneOf (mockFactory).createMethodMetrics(METHOD_METRICS_NAME);
                will(returnValue(mockMetrics));
            oneOf (mockFactory).includeMethodMetrics(mockMetrics);
            allowing (mockMetrics).getName();
                will(returnValue(METHOD_METRICS_NAME));
            ignoring (mockMetrics).addToMeasurement(BasicMeasurements.METHODS, METHOD_METRICS_NAME);
            allowing (mockMethod).getAccessFlags();
            ignoring (mockMethod).isPublic();
            ignoring (mockMethod).isProtected();
            ignoring (mockMethod).isPrivate();
            ignoring (mockMethod).isPackage();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PACKAGE_METHODS, METHOD_METRICS_NAME);
            ignoring (mockMethod).isFinal();
            ignoring (mockMethod).isAbstract();
            ignoring (mockMethod).isDeprecated();
            ignoring (mockMethod).isSynthetic();
            ignoring (mockMethod).isStatic();
            ignoring (mockMethod).isSynchronized();
            ignoring (mockMethod).isBridge();
            ignoring (mockMethod).isVarargs();
            ignoring (mockMethod).isNative();
            ignoring (mockMethod).isStrict();
            atLeast(1).of (mockMethod).getDescriptor();
                will(returnValue("(iLjava/lang/Object;)V"));
            ignoring (mockFactory).createClassMetrics("java.lang.Object");
            ignoring (mockMetrics).getParent();
            ignoring (mockMetrics).addToMeasurement(with(equal(BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES)), with(any(String.class)));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.PARAMETERS, 2);
            ignoring (mockMethod).getAttributes();
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.SLOC, 0);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitMethod_info(mockMethod);
    }

    public void testIsInnerClassOfCurrentClass_NamedInnerClass_OfCurrentClass() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", true, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testIsInnerClassOfCurrentClass_NamedInnerClass_OfOtherClass() {
        final String otherClassName = "otherpackage.OtherClass";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(otherClassName));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", false, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testIsInnerClassOfCurrentClass_AnonymousInnerClass_OfCurrentClass() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(false));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue(ANONYMOUS_INNER_CLASS_NAME));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", true, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testIsInnerClassOfCurrentClass_AnonymousInnerClass_OfOtherClass() {
        final String otherInnerClassName = "otherpackage.OtherClass$1";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(false));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue(otherInnerClassName));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        assertEquals("return value", false, sut.isInnerClassOfCurrentClass(mockInnerClass));
    }

    public void testVisitInnerClass_public() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "definedGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(List.of(mockDefinedGroupMetrics)));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            allowing (mockInnerClass).isPublic();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PUBLIC_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isPackage();
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_protected() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "definedGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(List.of(mockDefinedGroupMetrics)));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            allowing (mockInnerClass).isProtected();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PROTECTED_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isPackage();
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_private() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isProtected();
            allowing (mockInnerClass).isPrivate();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PRIVATE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPackage();
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_package() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPrivate();
            allowing (mockInnerClass).isPackage();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_static() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            allowing (mockInnerClass).isStatic();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.STATIC_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_final() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            allowing (mockInnerClass).isFinal();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.FINAL_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_interface() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            allowing (mockInnerClass).isInterface();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INTERFACE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_abstract() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            allowing (mockInnerClass).isAbstract();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.ABSTRACT_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_synthetic() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            allowing (mockInnerClass).isSynthetic();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.SYNTHETIC_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isAnnotation();
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_annotation() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            allowing (mockInnerClass).isAnnotation();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.ANNOTATION_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isEnum();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitInnerClass_enum() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final InnerClass mockInnerClass = mock(InnerClass.class);
        final Metrics mockProjectMetrics = mock(Metrics.class, "currentProject");
        final Metrics mockGroupMetrics = mock(Metrics.class, "currentGroup");
        final Metrics mockClassMetrics = mock(Metrics.class, "currentClass");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
                will(returnValue(mockProjectMetrics));
            oneOf (mockClassMetrics).getName();
                will(returnValue(CLASS_NAME));
            oneOf (mockInnerClass).hasOuterClassInfo();
                will(returnValue(true));
            oneOf (mockInnerClass).getOuterClassInfo();
                will(returnValue(CLASS_NAME));
            allowing (mockInnerClass).getInnerClassInfo();
                will(returnValue(INNER_CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(INNER_CLASS_NAME);
                will(returnValue(Collections.emptyList()));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isPublic();
            ignoring (mockInnerClass).isPrivate();
            ignoring (mockInnerClass).isProtected();
            ignoring (mockInnerClass).isPackage();
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.PACKAGE_INNER_CLASSES, INNER_CLASS_NAME);
            ignoring (mockInnerClass).isStatic();
            ignoring (mockInnerClass).isFinal();
            ignoring (mockInnerClass).isInterface();
            ignoring (mockInnerClass).isAbstract();
            ignoring (mockInnerClass).isSynthetic();
            ignoring (mockInnerClass).isAnnotation();
            allowing (mockInnerClass).isEnum();
                will(returnValue(true));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, INNER_CLASS_NAME);
            oneOf (mockClassMetrics).addToMeasurement(BasicMeasurements.ENUM_INNER_CLASSES, INNER_CLASS_NAME);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.setCurrentClass(mockClassMetrics);
        sut.visitInnerClass(mockInnerClass);
    }

    public void testVisitDeprecated_attribute_class() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Classfile mockClassfile = mock(Classfile.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockGroupMetrics = mock(Metrics.class, "group");
        final Metrics mockDefinedGroupMetrics = mock(Metrics.class, "defined group");
        final Metrics mockProjectMetrics = mock(Metrics.class, "project");

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockClassfile));
            oneOf (mockClassfile).getClassName();
            will(returnValue(CLASS_NAME));
            oneOf (mockFactory).getGroupMetrics(CLASS_NAME);
            will(returnValue(List.of(mockDefinedGroupMetrics)));
            oneOf (mockProjectMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, CLASS_NAME);
            oneOf (mockGroupMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, CLASS_NAME);
            oneOf (mockDefinedGroupMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_CLASSES, CLASS_NAME);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentProject(mockProjectMetrics);
        sut.setCurrentGroup(mockGroupMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_field() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Field_info mockField = mock(Field_info.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockField));
            oneOf (mockField).getFullName();
            will(returnValue(FIELD_NAME));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_ATTRIBUTES, FIELD_NAME);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_method() {
        final String methodSignature = METHOD_SIGNATURE;

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Method_info mockMethod = mock(Method_info.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockMethod));
            oneOf (mockMethod).getFullSignature();
            will(returnValue(methodSignature));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.DEPRECATED_METHODS, methodSignature);
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentClass(mockMetrics);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitDeprecated_attribute_other() {
        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final Deprecated_attribute mockDeprecatedAttribute = mock(Deprecated_attribute.class);
        final Visitable mockOwner = mock(Visitable.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockDeprecatedAttribute).getOwner();
            will(returnValue(mockOwner));
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.visitDeprecated_attribute(mockDeprecatedAttribute);
    }

    public void testVisitLocalVariable() {
        final String localVariableName = "localVariableName";

        final MetricsFactory mockFactory = mock(MetricsFactory.class);
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);
        final Metrics mockMetrics = mock(Metrics.class);

        checking(new Expectations() {{
            allowing (mockFactory).createProjectMetrics();
            oneOf (mockLocalVariable).getName();
            will(returnValue(localVariableName));
            oneOf (mockMetrics).addToMeasurement(BasicMeasurements.LOCAL_VARIABLES, localVariableName);
            oneOf (mockLocalVariable).getDescriptor();
        }});

        MetricsGatherer sut = new MetricsGatherer(mockFactory);
        sut.setCurrentMethod(mockMetrics);
        sut.visitLocalVariable(mockLocalVariable);
    }
}
