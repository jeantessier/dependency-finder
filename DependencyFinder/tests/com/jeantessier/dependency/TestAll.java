/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

import junit.framework.*;

public class TestAll extends TestCase {
    public static Test suite() {
        TestSuite result = new TestSuite();

        result.addTestSuite(TestNodeFactory.class);
        result.addTestSuite(TestDeletingVisitor.class);
        result.addTestSuite(TestNode.class);
        result.addTestSuite(TestPackageNode.class);
        result.addTestSuite(TestClassNode.class);
        result.addTestSuite(TestFeatureNode.class);
        result.addTestSuite(TestNullSelectionCriteria.class);
        result.addTestSuite(TestComprehensiveSelectionCriteria.class);
        result.addTestSuite(TestAndCompositeSelectionCriteria.class);
        result.addTestSuite(TestOrCompositeSelectionCriteria.class);
        result.addTestSuite(TestRegularExpressionSelectionCriteria.class);
        result.addTestSuite(TestCollectionSelectionCriteria.class);
        result.addTestSuite(TestSelectiveTraversalStrategy.class);
        result.addTestSuite(TestLinkMinimizer.class);
        result.addTestSuite(TestLinkMinimizerSystematic.class);
        result.addTestSuite(TestLinkMaximizer.class);
        result.addTestSuite(TestLinkMaximizerSystematic.class);
        result.addTestSuite(TestTextPrinter.class);
        result.addTestSuite(TestXMLPrinter.class);
        result.addTestSuite(TestNodeHandler.class);
        result.addTestSuite(TestNodeLoader.class);
        result.addTestSuite(TestCodeDependencyCollector.class);
        result.addTestSuite(TestDependencyExtractor.class);
        result.addTestSuite(TestGraphCopier.class);
        result.addTestSuite(TestGraphCopierWithFiltering.class);
        result.addTestSuite(TestGraphCopierWithConfirmed.class);
        result.addTestSuite(TestGraphSummarizer.class);
        result.addTestSuite(TestGraphSummarizerWithScoping.class);
        result.addTestSuite(TestGraphSummarizerWithFiltering.class);
        result.addTestSuite(TestGraphSummarizerWithConfirmed.class);
        result.addTestSuite(TestTransitiveClosure.class);
        result.addTestSuite(TestTransitiveClosureWithTestClass.class);
        result.addTestSuite(TestTransitiveClosureSlice.class);
        result.addTestSuite(TestTransitiveClosureNonMaximized.class);
        result.addTestSuite(TestClosureStartSelector.class);
        result.addTestSuite(TestClosureOutboundSelector.class);
        result.addTestSuite(TestClosureInboundSelector.class);
        result.addTestSuite(TestTransitiveClosureEngine.class);
        result.addTestSuite(TestMetricsGatherer.class);

        return result;
    }
}
