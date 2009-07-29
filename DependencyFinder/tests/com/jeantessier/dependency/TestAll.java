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

import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.runners.Suite.*;

@RunWith(Suite.class)
@SuiteClasses({
        TestNodeFactory.class,
        TestDeletingVisitor.class,
        TestNode.class,
        TestPackageNode.class,
        TestClassNode.class,
        TestFeatureNode.class,
        TestNullSelectionCriteria.class,
        TestComprehensiveSelectionCriteria.class,
        TestAndCompositeSelectionCriteria.class,
        TestOrCompositeSelectionCriteria.class,
        TestRegularExpressionSelectionCriteria.class,
        TestCollectionSelectionCriteria.class,
        TestSelectiveTraversalStrategy.class,
        TestVisitorDecorator.class,
        TestBasicTraversal.class,
        TestSelectiveVisitor.class,
        TestLinkMinimizer.class,
        TestLinkMinimizerSystematic.class,
        TestLinkMaximizer.class,
        TestLinkMaximizerSystematic.class,
        TestFeatureResolver.class,
        TestTextPrinter.class,
        TestHTMLPrinter.class,
        TestHTMLPrinterEscapeMetaCharacters.class,
        TestXMLPrinter.class,
        TestNodeNamePrinter.class,
        TestNodeHandler.class,
        TestNodeLoader.class,
        TestCodeDependencyCollector.class,
        TestCodeDependencyCollectorWithFiltering.class,
        TestCodeDependencyCollectorUsingMocks.class,
        TestDependencyExtractor.class,
        TestGraphCopier.class,
        TestGraphCopierWithFiltering.class,
        TestGraphCopierWithConfirmed.class,
        TestGraphSummarizer.class,
        TestGraphSummarizerWithScoping.class,
        TestGraphSummarizerWithFiltering.class,
        TestGraphSummarizerWithConfirmed.class,
        TestCycle.class,
        TestCycleComparator.class,
        TestCycleDetector.class,
        TestTextCyclePrinter.class,
        TestHTMLCyclePrinter.class,
        TestXMLCyclePrinter.class,
        TestTransitiveClosure.class,
        TestTransitiveClosureWithTestClass.class,
        TestTransitiveClosureSlice.class,
        TestTransitiveClosureNonMaximized.class,
        TestClosureStartSelector.class,
        TestClosureOutboundSelector.class,
        TestClosureInboundSelector.class,
        TestClosureStopSelector.class,
        TestTransitiveClosureEngine.class,
        TestMetricsGatherer.class,
        TestLCOM4Gatherer.class
})
public class TestAll {
}
