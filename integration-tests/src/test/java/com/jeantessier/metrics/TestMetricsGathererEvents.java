/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import java.nio.file.*;
import java.util.*;

import org.jmock.*;
import org.jmock.api.*;
import org.jmock.junit5.*;
import org.jmock.lib.action.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMetricsGathererEvents {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    public static final String TEST_DIRNAME  = CLASSES_DIR.resolve("testpackage").toString();
    public static final String OTHER_DIRNAME = CLASSES_DIR.resolve("otherpackage").toString();

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final ClassfileLoader loader = new AggregatingClassfileLoader();
    private final MetricsListener mockListener = context.mock(MetricsListener.class);

    private MetricsGatherer gatherer;

    @BeforeEach
    void setUp() throws Exception {
        MetricsFactory factory = new MetricsFactory("test", new MetricsConfigurationLoader(Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")).load(Paths.get("../etc/MetricsConfig.xml").toString()));
        gatherer = new MetricsGatherer(factory);
        gatherer.addMetricsListener(mockListener);
    }
    
    @Test
    void testEvents() {
        loader.load(Collections.singleton(TEST_FILENAME));

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(MetricsEvent.class)));
            exactly(1).of (mockListener).beginClass(with(any(MetricsEvent.class)));
                will(new CustomAction("make sure it was for TEST_CLASS") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(TEST_CLASS, ((MetricsEvent) invocation.getParameter(0)).getClassfile().getClassName());
                        return null;
                    }
                });
            exactly(2).of (mockListener).beginMethod(with(any(MetricsEvent.class)));
            exactly(2).of (mockListener).endMethod(with(any(MetricsEvent.class)));
            exactly(1).of (mockListener).endClass(with(any(MetricsEvent.class)));
                will(new CustomAction("make sure it was for TEST_CLASS") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(TEST_CLASS, ((MetricsEvent) invocation.getParameter(0)).getClassfile().getClassName());
                        return null;
                    }
                });
            exactly(1).of (mockListener).endSession(with(any(MetricsEvent.class)));
        }});

        gatherer.visitClassfiles(loader.getAllClassfiles());
    }
    
    @Test
    void testMultipleEvents() {
        Collection<String> dirs = new ArrayList<>();
        dirs.add(TEST_DIRNAME);
        dirs.add(OTHER_DIRNAME);
        loader.load(dirs);

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(MetricsEvent.class)));
            exactly(9).of (mockListener).beginClass(with(any(MetricsEvent.class)));
            exactly(16).of (mockListener).beginMethod(with(any(MetricsEvent.class)));
            exactly(16).of (mockListener).endMethod(with(any(MetricsEvent.class)));
            exactly(9).of (mockListener).endClass(with(any(MetricsEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(MetricsEvent.class)));
        }});

        gatherer.visitClassfiles(loader.getAllClassfiles());
    }
    
    @Test
    void testEventsWithNothing() {
        loader.load(Collections.emptySet());

        context.checking(new Expectations() {{
            oneOf (mockListener).beginSession(with(any(MetricsEvent.class)));
            oneOf (mockListener).endSession(with(any(MetricsEvent.class)));
        }});

        gatherer.visitClassfiles(loader.getAllClassfiles());
    }
}
