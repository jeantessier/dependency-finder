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
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

public class TestFeatureResolver extends MockObjectTestCase {
    private FeatureResolver sut;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        sut = new FeatureResolver();
    }

    public void testTraverseNodes() {
        final Node mockNode1 = mock(Node.class, "node1");
        final Node mockNode2 = mock(Node.class, "node2");

        final Collection<Node> nodes = new LinkedList<Node>();
        nodes.add(mockNode1);
        nodes.add(mockNode2);

        checking(new Expectations() {{
            one (mockNode1).accept(sut);
            one (mockNode2).accept(sut);
        }});

        sut.traverseNodes(nodes);
    }

    public void testVisitPackageNode() {
        final PackageNode mockPackageNode = mock(PackageNode.class);
        final ClassNode mockClassNode1 = mock(ClassNode.class, "class1");
        final ClassNode mockClassNode2 = mock(ClassNode.class, "class2");

        final Collection<ClassNode> classes = new LinkedList<ClassNode>();
        classes.add(mockClassNode1);
        classes.add(mockClassNode2);

        checking(new Expectations() {{
            one (mockPackageNode).getClasses();
                will(returnValue(classes));
            one (mockClassNode1).accept(sut);
            one (mockClassNode2).accept(sut);
        }});

        sut.visitPackageNode(mockPackageNode);
    }

    public void testVisitInboundPackageNode() {
        final PackageNode mockPackageNode = mock(PackageNode.class);
        sut.visitInboundPackageNode(mockPackageNode);
    }

    public void testVisitOutboundPackageNode() {
        final PackageNode mockPackageNode = mock(PackageNode.class);
        sut.visitOutboundPackageNode(mockPackageNode);
    }

    public void testVisitClassNode() {
        final ClassNode mockClassNode = mock(ClassNode.class);
        final FeatureNode mockFeatureNode1 = mock(FeatureNode.class, "feature1");
        final FeatureNode mockFeatureNode2 = mock(FeatureNode.class, "feature2");

        final Collection<FeatureNode> features = new LinkedList<FeatureNode>();
        features.add(mockFeatureNode1);
        features.add(mockFeatureNode2);

        checking(new Expectations() {{
            one (mockClassNode).getFeatures();
                will(returnValue(features));
            one (mockFeatureNode1).accept(sut);
            one (mockFeatureNode2).accept(sut);
        }});

        sut.visitClassNode(mockClassNode);
    }

    public void testVisitInboundClassNode() {
        final ClassNode mockClassNode = mock(ClassNode.class);
        sut.visitInboundClassNode(mockClassNode);
    }

    public void testVisitOutboundClassNode() {
        final ClassNode mockClassNode = mock(ClassNode.class);
        sut.visitOutboundClassNode(mockClassNode);
    }

    public void testVisitFeatureNode() {
        final String TARGET_SIMPLE_NAME = "target()";
        final String CHILD_NAME = "Child";
        final String CHILD_TARGET_NAME = CHILD_NAME + "." + TARGET_SIMPLE_NAME;
        final String PARENT_NAME = "Parent";
        final String PARENT_TARGET_NAME = PARENT_NAME + "." + TARGET_SIMPLE_NAME;

        final FeatureNode mockCallerSource = mock(FeatureNode.class, "Caller.source()");
        final FeatureNode mockParentTarget = mock(FeatureNode.class, PARENT_TARGET_NAME);
        final ClassNode mockChild = mock(ClassNode.class, CHILD_NAME);
        final FeatureNode mockChildTarget = mock(FeatureNode.class, CHILD_TARGET_NAME);

        checking(new Expectations() {{
            atLeast(1).of (mockChildTarget).getSimpleName();
                will(returnValue(TARGET_SIMPLE_NAME));
            atLeast(1).of (mockChildTarget).getClassNode();
                will(returnValue(mockChild));
            one (mockChild).getInheritedFeatures(TARGET_SIMPLE_NAME);
                will(returnValue(Collections.singleton(mockParentTarget)));
            one (mockChildTarget).getInboundDependencies();
                will(returnValue(Collections.singleton(mockCallerSource)));
            one (mockCallerSource).addDependency(mockParentTarget);
        }});

        sut.visitFeatureNode(mockChildTarget);
    }

    public void testVisitInboundFeatureNode() {
        final FeatureNode mockFeatureNode = mock(FeatureNode.class);
        sut.visitInboundFeatureNode(mockFeatureNode);
    }

    public void testVisitOutboundFeatureNode() {
        final FeatureNode mockFeatureNode = mock(FeatureNode.class);
        sut.visitOutboundFeatureNode(mockFeatureNode);
    }
}
