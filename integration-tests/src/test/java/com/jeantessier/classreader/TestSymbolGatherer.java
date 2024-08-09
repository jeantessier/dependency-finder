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

package com.jeantessier.classreader;

import junit.framework.TestCase;

import java.nio.file.*;
import java.util.*;

public class TestSymbolGatherer extends TestCase {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    public static final String INNER_TEST_CLASS = "test$InnerClass";
    public static final String INNER_TEST_FILENAME = CLASSES_DIR.resolve(INNER_TEST_CLASS + ".class").toString();

    private DefaultSymbolGathererStrategy strategy;
    private SymbolGatherer gatherer;
    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        strategy = new DefaultSymbolGathererStrategy();
        gatherer = new SymbolGatherer(strategy);
        loader = new AggregatingClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
    }

    public void testEmpty() {
        loader.load(Collections.emptyList());

        assertEquals("Different number of symbols", 0, gatherer.stream().count());
    }
    
    public void testOnOneClass() {
        loader.load(Collections.singleton(TEST_FILENAME));

        assertTrue("Missing test class name from " + getGatheredSymbols(), getGatheredSymbols().contains("test"));
        assertTrue("Missing test.main() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void"));
        assertTrue("Missing args parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: args"));
        assertTrue("Missing c local variable from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: c"));
        assertTrue("Missing ex local variable from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: ex"));
        assertTrue("Missing test.test() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test.test()"));
        assertTrue("Missing this parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test.test(): this"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 7, gatherer.stream().count());
    }

    public void testOnOneInnerClass() {
        loader.load(Collections.singleton(INNER_TEST_FILENAME));

        assertTrue("Missing test$InnerClass class name from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass"));
        assertTrue("Missing test$InnerClass.innerField field from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.innerField"));
        assertTrue("Missing test$InnerClass.test$InnerClass() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.test$InnerClass()"));
        assertTrue("Missing this parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.test$InnerClass(): this"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 4, gatherer.stream().count());
    }

    public void testClassNamesOnly() {
        strategy.setMatchingClassNames(true);
        strategy.setMatchingFieldNames(false);
        strategy.setMatchingMethodNames(false);
        strategy.setMatchingLocalNames(false);
        strategy.setMatchingInnerClassNames(false);

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertTrue("Missing test class name from " + getGatheredSymbols(), getGatheredSymbols().contains("test"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 1, gatherer.stream().count());
    }

    public void testFieldNamesOnly() {
        strategy.setMatchingClassNames(false);
        strategy.setMatchingFieldNames(true);
        strategy.setMatchingMethodNames(false);
        strategy.setMatchingLocalNames(false);
        strategy.setMatchingInnerClassNames(false);

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertTrue("Missing test$InnerClass.innerField field from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.innerField"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 1, gatherer.stream().count());
    }

    public void testMethodNamesOnly() {
        strategy.setMatchingClassNames(false);
        strategy.setMatchingFieldNames(false);
        strategy.setMatchingMethodNames(true);
        strategy.setMatchingLocalNames(false);
        strategy.setMatchingInnerClassNames(false);

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertTrue("Missing test$InnerClass.test$InnerClass() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.test$InnerClass()"));
        assertTrue("Missing test.main() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void"));
        assertTrue("Missing test.test() method from " + getGatheredSymbols(), getGatheredSymbols().contains("test.test()"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 3, gatherer.stream().count());
    }

    public void testLocalNamesOnly() {
        strategy.setMatchingClassNames(false);
        strategy.setMatchingFieldNames(false);
        strategy.setMatchingMethodNames(false);
        strategy.setMatchingLocalNames(true);
        strategy.setMatchingInnerClassNames(false);

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertTrue("Missing this parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass.test$InnerClass(): this"));
        assertTrue("Missing args parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: args"));
        assertTrue("Missing c local variable from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: c"));
        assertTrue("Missing ex local variable from " + getGatheredSymbols(), getGatheredSymbols().contains("test.main(java.lang.String[]): void: ex"));
        assertTrue("Missing this parameter from " + getGatheredSymbols(), getGatheredSymbols().contains("test.test(): this"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 5, gatherer.stream().count());
    }

    public void testInnerClassNamesOnly() {
        strategy.setMatchingClassNames(false);
        strategy.setMatchingFieldNames(false);
        strategy.setMatchingMethodNames(false);
        strategy.setMatchingLocalNames(false);
        strategy.setMatchingInnerClassNames(true);

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertTrue("Missing test$InnerClass class name from " + getGatheredSymbols(), getGatheredSymbols().contains("test$InnerClass"));
        assertEquals("Different number of symbols in " + getGatheredSymbols(), 1, gatherer.stream().count());
    }

    private Collection<String> getGatheredSymbols() {
        return gatherer.stream()
                .map(Object::toString)
                .toList();
    }
}
