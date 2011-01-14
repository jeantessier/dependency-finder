/*
 *  Copyright (c) 2001-2011, Jean Tessier
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

import com.google.common.collect.Lists;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class TestMetricsGatherer {
    private Mockery context;

    private MetricsGatherer metricsGatherer;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();

        metricsGatherer = new MetricsGatherer();
    }

    @Test
    public void testVisitRuntimeVisibleAnnotations_attribute() {
        final RuntimeVisibleAnnotations_attribute mockRuntimeVisibleAnnotations_attribute = context.mock(RuntimeVisibleAnnotations_attribute.class);

        final Annotation mockAnnotation1 = context.mock(Annotation.class, "annotation 1");
        final Annotation mockAnnotation2 = context.mock(Annotation.class, "annotation 2");
        final Collection<? extends Annotation> annotations = Lists.newArrayList(mockAnnotation1, mockAnnotation2);

        context.checking(new Expectations() {{
            atLeast(1).of (mockRuntimeVisibleAnnotations_attribute).getAnnotations();
                will(returnValue(annotations));
            one (mockAnnotation1).accept(metricsGatherer);
            one (mockAnnotation2).accept(metricsGatherer);
        }});

        metricsGatherer.visitRuntimeVisibleAnnotations_attribute(mockRuntimeVisibleAnnotations_attribute);

        assertThat("RuntimeVisibleAnnotations attribute count", metricsGatherer.getAttributeCounts().get("RuntimeVisibleAnnotations"), is(1L));
    }

    @Test
    public void testVisitRuntimeInvisibleAnnotations_attribute() {
        final RuntimeInvisibleAnnotations_attribute mockRuntimeInvisibleAnnotations_attribute = context.mock(RuntimeInvisibleAnnotations_attribute.class);

        final Annotation mockAnnotation1 = context.mock(Annotation.class, "annotation 1");
        final Annotation mockAnnotation2 = context.mock(Annotation.class, "annotation 2");
        final Collection<? extends Annotation> annotations = Lists.newArrayList(mockAnnotation1, mockAnnotation2);

        context.checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleAnnotations_attribute).getAnnotations();
                will(returnValue(annotations));
            one (mockAnnotation1).accept(metricsGatherer);
            one (mockAnnotation2).accept(metricsGatherer);
        }});

        metricsGatherer.visitRuntimeInvisibleAnnotations_attribute(mockRuntimeInvisibleAnnotations_attribute);

        assertThat("RuntimeInvisibleAnnotations attribute count", metricsGatherer.getAttributeCounts().get("RuntimeInvisibleAnnotations"), is(1L));
    }

    @Test
    public void testVisitRuntimeVisibleParameterAnnotations_attribute() {
        final RuntimeVisibleParameterAnnotations_attribute mockRuntimeVisibleParameterAnnotations_attribute = context.mock(RuntimeVisibleParameterAnnotations_attribute.class);

        final Parameter mockParameter1 = context.mock(Parameter.class, "parameter 1");
        final Parameter mockParameter2 = context.mock(Parameter.class, "parameter 2");
        final Collection<? extends Parameter> parameters = Lists.newArrayList(mockParameter1, mockParameter2);

        context.checking(new Expectations() {{
            atLeast(1).of (mockRuntimeVisibleParameterAnnotations_attribute).getParameterAnnotations();
                will(returnValue(parameters));
            one (mockParameter1).accept(metricsGatherer);
            one (mockParameter2).accept(metricsGatherer);
        }});

        metricsGatherer.visitRuntimeVisibleParameterAnnotations_attribute(mockRuntimeVisibleParameterAnnotations_attribute);

        assertThat("RuntimeVisibleParameterAnnotations attribute count", metricsGatherer.getAttributeCounts().get("RuntimeVisibleParameterAnnotations"), is(1L));
    }

    @Test
    public void testVisitRuntimeInvisibleParameterAnnotations_attribute() {
        final RuntimeInvisibleParameterAnnotations_attribute mockRuntimeInvisibleParameterAnnotations_attribute = context.mock(RuntimeInvisibleParameterAnnotations_attribute.class);

        final Parameter mockParameter1 = context.mock(Parameter.class, "parameter 1");
        final Parameter mockParameter2 = context.mock(Parameter.class, "parameter 2");
        final Collection<? extends Parameter> parameters = Lists.newArrayList(mockParameter1, mockParameter2);

        context.checking(new Expectations() {{
            atLeast(1).of (mockRuntimeInvisibleParameterAnnotations_attribute).getParameterAnnotations();
                will(returnValue(parameters));
            one (mockParameter1).accept(metricsGatherer);
            one (mockParameter2).accept(metricsGatherer);
        }});

        metricsGatherer.visitRuntimeInvisibleParameterAnnotations_attribute(mockRuntimeInvisibleParameterAnnotations_attribute);

        assertThat("RuntimeInvisibleParameterAnnotations attribute count", metricsGatherer.getAttributeCounts().get("RuntimeInvisibleParameterAnnotations"), is(1L));
    }

    @Test
    public void testVisitAnnotationDefault_attribute() {
        final AnnotationDefault_attribute mockAnnotationDefault_attribute = context.mock(AnnotationDefault_attribute.class);

        final ElementValue mockElementValue = context.mock(ElementValue.class);

        context.checking(new Expectations() {{
            atLeast(1).of (mockAnnotationDefault_attribute).getElemementValue();
                will(returnValue(mockElementValue));
            one (mockElementValue).accept(metricsGatherer);
        }});

        metricsGatherer.visitAnnotationDefault_attribute(mockAnnotationDefault_attribute);

        assertThat("AnnotationDefault attribute count", metricsGatherer.getAttributeCounts().get("AnnotationDefault"), is(1L));
    }
}
