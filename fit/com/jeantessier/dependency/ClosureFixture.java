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

import fitlibrary.*;

public class ClosureFixture extends DoFixture {
    public ClosureFixture() {
        setSystemUnderTest(new NodeFactory());
    }

    public void sourceDependsOn(Node source, Node target) {
        source.addDependency(target);
    }

    public void computeClosureStartingAtWithMaximumInboundDepth(String startIncludes, int maximumInboundDepth) {
        computeClosureStartingAtWithMaximumInboundDepthWithMaximumOutboundDepth(startIncludes, maximumInboundDepth, 0);
    }

    public void computeClosureStartingAtWithMaximumOutboundDepth(String startIncludes, int maximumOutboundDepth) {
        computeClosureStartingAtWithMaximumInboundDepthWithMaximumOutboundDepth(startIncludes, 0, maximumOutboundDepth);
    }

    public void computeClosureStartingAtStopingAt(String startIncludes, String stopIncludes) {
        RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
        startCriteria.setGlobalIncludes(startIncludes);

        RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();
        stopCriteria.setGlobalIncludes(stopIncludes);

        TransitiveClosure selector = new TransitiveClosure(startCriteria, stopCriteria);

        selector.traverseNodes(((NodeFactory) getSystemUnderTest()).getPackages().values());

        setSystemUnderTest(selector.getFactory());
    }

    private void computeClosureStartingAtWithMaximumInboundDepthWithMaximumOutboundDepth(String startIncludes, int maximumInboundDepth, int maximumOutboundDepth) {
        RegularExpressionSelectionCriteria startCriteria = new RegularExpressionSelectionCriteria();
        startCriteria.setGlobalIncludes(startIncludes);

        SelectionCriteria stopCriteria = new NullSelectionCriteria();

        TransitiveClosure selector = new TransitiveClosure(startCriteria, stopCriteria);
        selector.setMaximumInboundDepth(maximumInboundDepth);
        selector.setMaximumOutboundDepth(maximumOutboundDepth);

        selector.traverseNodes(((NodeFactory) getSystemUnderTest()).getPackages().values());

        setSystemUnderTest(selector.getFactory());
    }

    public SetFixture dependenciesFor(Node node) {
        Collection<Dependency> dependencies = new LinkedList<Dependency>();

        for (Node source : node.getInboundDependencies()) {
            dependencies.add(new Dependency(node.getName(), Dependency.INBOUND, source.getName()));
        }

        for (Node target : node.getOutboundDependencies()) {
            dependencies.add(new Dependency(node.getName(), Dependency.OUTBOUND, target.getName()));
        }

        return new SetFixture(dependencies);
    }

    public Node findNode(String s) {
        Node result = ((NodeFactory) getSystemUnderTest()).getPackages().get(s);

        if (result == null) {
            result = ((NodeFactory) getSystemUnderTest()).getClasses().get(s);
        }

        if (result == null) {
            result = ((NodeFactory) getSystemUnderTest()).getFeatures().get(s);
        }

        return result;
    }
}
