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

package com.jeantessier.metrics;

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.Classfile;
import com.jeantessier.classreader.ClassfileLoader;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class TestMetricsGatherer {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();

    private MetricsFactory factory;
    private Classfile classfile;

    @Before
    public void loadTestData() throws IOException, ParserConfigurationException, SAXException {
        factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString()));

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(Collections.singleton(TEST_FILENAME));

        classfile = loader.getClassfile(TEST_CLASS);
        classfile.accept(new MetricsGatherer(factory));
    }
    
    @Test
    public void testNbElements() {
        assertCollectionEquals("project names", factory.getProjectNames(), "test");
        assertCollectionEquals("group names", factory.getGroupNames(), "");
        assertCollectionEquals("class names", factory.getClassNames(), "test");
        assertCollectionEquals("method names", factory.getMethodNames(), "test.main(java.lang.String[])", "test.test()");
    }
    
    @Test
    public void testNbAllElements() {
        assertCollectionEquals("all project names", factory.getAllProjectNames(), "test");
        assertCollectionEquals("all group names", factory.getAllGroupNames(), "", "java.io", "java.lang", "java.util");
        assertCollectionEquals("all class names", factory.getAllClassNames(), "java.io.PrintStream", "java.lang.NullPointerException", "java.lang.Object", "java.lang.String", "java.lang.System", "java.util.Collections", "java.util.Collection", "java.util.Set", "test");
        assertCollectionEquals("all method names", factory.getAllMethodNames(), "java.io.PrintStream.println(java.lang.Object)", "java.lang.Object.Object()", "java.util.Collections.singleton(java.lang.Object)", "test.main(java.lang.String[])", "test.test()");
    }

    @Test
    public void test_test_test() {
        Metrics metrics = factory.createMethodMetrics("test.test()", "void");

        assertMeasurementEquals(metrics, BasicMeasurements.SLOC, 1);
        assertMeasurementEquals(metrics, BasicMeasurements.PARAMETERS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.LOCAL_VARIABLES, 1);

        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, "java.lang.Object.Object(): void");
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES);
    }
    
    @Test
    public void test_test_main() {
        Metrics metrics = factory.createMethodMetrics("test.main(java.lang.String[])", "void");

        assertMeasurementEquals(metrics, BasicMeasurements.SLOC, 5);
        assertMeasurementEquals(metrics, BasicMeasurements.PARAMETERS, 1);
        assertMeasurementEquals(metrics, BasicMeasurements.LOCAL_VARIABLES, 3);

        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_INTRA_CLASS_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_INTRA_PACKAGE_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_EXTRA_PACKAGE_METHOD_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_CLASS_FEATURE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_PACKAGE_FEATURE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_PACKAGE_CLASS_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_FEATURE_DEPENDENCIES, "java.util.Collections.singleton(java.lang.Object): java.util.Set", "java.lang.Object.Object(): void", "java.io.PrintStream.println(java.lang.Object): void");
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_CLASS_DEPENDENCIES, "java.io.PrintStream", "java.lang.NullPointerException", "java.lang.Object", "java.lang.String", "java.lang.System", "java.util.Collection", "java.util.Set");
    }

    @Test
    public void test_test() {
        Metrics metrics = factory.createClassMetrics("test");

        assertMeasurementEquals(metrics, BasicMeasurements.MAJOR_VERSION, classfile.getMajorVersion());
        assertMeasurementEquals(metrics, BasicMeasurements.MINOR_VERSION, classfile.getMinorVersion());

        assertMeasurementEquals(metrics, BasicMeasurements.SLOC, 7);

        assertMeasurementEquals(metrics, BasicMeasurements.METHODS, 2);
        assertMeasurementEquals(metrics, BasicMeasurements.PUBLIC_METHODS, 2);
        assertMeasurementEquals(metrics, BasicMeasurements.PROTECTED_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PRIVATE_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PACKAGE_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.FINAL_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.ABSTRACT_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.DEPRECATED_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.SYNTHETIC_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.STATIC_METHODS, 1);
        assertMeasurementEquals(metrics, BasicMeasurements.SYNCHRONIZED_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.BRIDGE_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.VARARGS_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.NATIVE_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.TRIVIAL_METHODS, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.STRICT_METHODS, 0);
        assertMeasurementEquals(metrics, "PuMR", 1.0);
        assertMeasurementEquals(metrics, "ProMR", 0.0);
        assertMeasurementEquals(metrics, "PriMR", 0.0);
        assertMeasurementEquals(metrics, "PaMR", 0.0);
        assertMeasurementEquals(metrics, "FMR", 0.0);
        assertMeasurementEquals(metrics, "AMR", 0.0);
        assertMeasurementEquals(metrics, "DMR", 0.0);
        assertMeasurementEquals(metrics, "SynthMR", 0.0);
        assertMeasurementEquals(metrics, "SMR", 0.5);
        assertMeasurementEquals(metrics, "SynchMR", 0.0);
        assertMeasurementEquals(metrics, "BMR", 0.0);
        assertMeasurementEquals(metrics, "VMR", 0.0);
        assertMeasurementEquals(metrics, "NMR", 0.0);
        assertMeasurementEquals(metrics, "TMR", 0.0);
        assertMeasurementEquals(metrics, "StrictMR", 0.0);

        assertMeasurementEquals(metrics, BasicMeasurements.ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PUBLIC_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PROTECTED_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PRIVATE_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.PACKAGE_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.FINAL_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.DEPRECATED_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.SYNTHETIC_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.STATIC_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.TRANSIENT_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.VOLATILE_ATTRIBUTES, 0);
        assertMeasurementEquals(metrics, BasicMeasurements.ENUM_ATTRIBUTES, 0);
        assertMeasurementIsNaN(metrics, "PuAR");
        assertMeasurementIsNaN(metrics, "ProAR");
        assertMeasurementIsNaN(metrics, "PriAR");
        assertMeasurementIsNaN(metrics, "PaAR");
        assertMeasurementIsNaN(metrics, "FAR");
        assertMeasurementIsNaN(metrics, "DAR");
        assertMeasurementIsNaN(metrics, "SynthAR");
        assertMeasurementIsNaN(metrics, "SAR");
        assertMeasurementIsNaN(metrics, "TAR");
        assertMeasurementIsNaN(metrics, "VAR");
        assertMeasurementIsNaN(metrics, "EAR");

        assertMeasurementEquals(metrics, BasicMeasurements.SUBCLASSES, 0);
        assertMeasurementIsEmpty(metrics, BasicMeasurements.DEPTH_OF_INHERITANCE);

        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_INTRA_PACKAGE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.INBOUND_EXTRA_PACKAGE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_INTRA_PACKAGE_DEPENDENCIES);
        assertCollectionMeasurementEquals(metrics, BasicMeasurements.OUTBOUND_EXTRA_PACKAGE_DEPENDENCIES, "java.lang.Object");
    }

    @Test
    public void test_() {
        assertMeasurementEquals(factory.createGroupMetrics(""), BasicMeasurements.SLOC, 7);
    }

    @Test
    public void testProject() {
        assertMeasurementEquals(factory.createProjectMetrics("test"), BasicMeasurements.SLOC, 7);
    }

    private void assertCollectionEquals(String message, Collection<String> names, String ... values) {
        assertThat(message, names, hasItems(values));
        assertThat(message, names.size(), is(values.length));
    }

    private void assertCollectionMeasurementEquals(Metrics metrics, BasicMeasurements measurement, String ... values) {
        assertCollectionMeasurementEquals(metrics, measurement.getAbbreviation(), values);
    }

    private void assertCollectionMeasurementEquals(Metrics metrics, String abbreviation, String ... values) {
        Collection<String> dependencies = ((CollectionMeasurement) metrics.getMeasurement(abbreviation)).getValues();
        assertThat(abbreviation, dependencies, hasItems(values));
        assertThat(abbreviation, dependencies.size(), is(values.length));
    }

    private void assertMeasurementEquals(Metrics metrics, BasicMeasurements measurement, int expectedValue) {
        assertMeasurementEquals(metrics, measurement.getAbbreviation(), expectedValue);
    }

    private void assertMeasurementEquals(Metrics metrics, String abbreviation, int expectedValue) {
        assertThat(abbreviation, metrics.getMeasurement(abbreviation).getValue().intValue(), is(expectedValue));
    }

    private void assertMeasurementEquals(Metrics metrics, String abbreviation, double expectedValue) {
        assertThat(abbreviation, metrics.getMeasurement(abbreviation).getValue().doubleValue(), is(closeTo(expectedValue, 0.01)));
    }

    private void assertMeasurementIsNaN(Metrics metrics, String abbreviation) {
        assertTrue(abbreviation, Double.isNaN(metrics.getMeasurement(abbreviation).getValue().doubleValue()));
    }

    private void assertMeasurementIsEmpty(Metrics metrics, BasicMeasurements measurement) {
        assertMeasurementIsEmpty(metrics, measurement.getAbbreviation());
    }

    private void assertMeasurementIsEmpty(Metrics metrics, String abbreviation) {
        assertTrue(abbreviation + " should have been ignored", metrics.getMeasurement(abbreviation).isEmpty());
    }
}
