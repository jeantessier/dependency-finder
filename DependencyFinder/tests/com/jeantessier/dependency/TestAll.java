/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import junit.framework.*;

public class TestAll extends TestCase {
	public TestAll(String name) {
		super(name);
	}
	
	public static Test suite() {
		TestSuite result = new TestSuite();

		result.addTestSuite(TestSelectiveTraversalStrategy.class);
		result.addTestSuite(TestLinkMinimizer.class);
		result.addTestSuite(TestLinkMinimizerSystematic.class);
		result.addTestSuite(TestLinkMaximizer.class);
		result.addTestSuite(TestLinkMaximizerSystematic.class);
		result.addTestSuite(TestPrettyPrinter.class);
		result.addTestSuite(TestDependencyExtractor.class);
		result.addTestSuite(TestGraphCopier.class);
		result.addTestSuite(TestGraphCopierWithFiltering.class);
		result.addTestSuite(TestGraphSummarizer.class);
		result.addTestSuite(TestGraphSummarizerWithFiltering.class);
		result.addTestSuite(TestTransitiveClosure.class);

		return result;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}
