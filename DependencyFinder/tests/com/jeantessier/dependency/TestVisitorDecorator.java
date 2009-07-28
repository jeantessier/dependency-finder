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

package com.jeantessier.dependency;

import java.util.*;

import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.junit.*;
import org.junit.runner.*;

@RunWith(JMock.class)
public class TestVisitorDecorator {
    private Mockery context;

    private Visitor mockVisitor;

    private PackageNode packageNode;
    private ClassNode classNode;
    private FeatureNode featureNode;

    private VisitorDecorator sut;

    @Before
    public void setUp() {
        context = new Mockery();

        mockVisitor = context.mock(Visitor.class);

        packageNode = new PackageNode("foo", true);
        classNode = new ClassNode(packageNode, "foo.Foo", true);
        featureNode = new FeatureNode(classNode, "foo.Foo.foo", true);

        sut = new VisitorDecorator();
        sut.setDelegate(mockVisitor);
    }

    @Test
    public void testTraverseNodes() {
        final Collection<? extends Node> nodes = new ArrayList<Node>();

        context.checking(new Expectations() {{
            one (mockVisitor).traverseNodes(nodes);
        }});

        sut.traverseNodes(nodes);
    }

    @Test
    public void testVisitPackageNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitPackageNode(packageNode);
        }});

        sut.visitPackageNode(packageNode);
    }

    @Test
    public void testVisitInboundPackageNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitInboundPackageNode(packageNode);
        }});

        sut.visitInboundPackageNode(packageNode);
    }

    @Test
    public void testVisitOutboundPackageNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitOutboundPackageNode(packageNode);
        }});

        sut.visitOutboundPackageNode(packageNode);
    }

    @Test
    public void testVisitClassNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitClassNode(classNode);
        }});

        sut.visitClassNode(classNode);
    }

    @Test
    public void testVisitInboundClassNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitInboundClassNode(classNode);
        }});

        sut.visitInboundClassNode(classNode);
    }

    @Test
    public void testVisitOutboundClassNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitOutboundClassNode(classNode);
        }});

        sut.visitOutboundClassNode(classNode);
    }

    @Test
    public void testVisitFeatureNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitFeatureNode(featureNode);
        }});

        sut.visitFeatureNode(featureNode);
    }

    @Test
    public void testVisitInboundFeatureNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitInboundFeatureNode(featureNode);
        }});

        sut.visitInboundFeatureNode(featureNode);
    }

    @Test
    public void testVisitOutboundFeatureNodes() {
        context.checking(new Expectations() {{
            one (mockVisitor).visitOutboundFeatureNode(featureNode);
        }});

        sut.visitOutboundFeatureNode(featureNode);
    }
}
