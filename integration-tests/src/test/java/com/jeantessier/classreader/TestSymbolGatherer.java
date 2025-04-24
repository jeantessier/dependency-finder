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

package com.jeantessier.classreader;


import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestSymbolGatherer {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    public static final String INNER_TEST_CLASS = "test$InnerClass";
    public static final String INNER_TEST_FILENAME = CLASSES_DIR.resolve(INNER_TEST_CLASS + ".class").toString();

    private final DefaultSymbolGathererStrategy strategy = new DefaultSymbolGathererStrategy();
    private final SymbolGatherer gatherer = new SymbolGatherer(strategy);
    private final ClassfileLoader loader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() {
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
    }

    @Test
    void testEmpty() {
        var expectedSymbols = Stream.<String>empty();

        loader.load(Collections.emptyList());

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }
    
    @Test
    void testOnOneClass() {
        var expectedSymbols = Stream.of(
                "test.test()",
                "test.test(): this",
                "test.main(java.lang.String[]): void",
                "test.main(java.lang.String[]): void: c",
                "test.main(java.lang.String[]): void: ex",
                "test.main(java.lang.String[]): void: args",
                "test"
        );

        loader.load(Collections.singleton(TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testOnOneInnerClass() {
        var expectedSymbols = Stream.of(
                "test$InnerClass.innerField",
                "test$InnerClass.test$InnerClass()",
                "test$InnerClass.test$InnerClass(): this",
                "test$InnerClass"
        );

        loader.load(Collections.singleton(INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testClassNamesOnly() {
        strategy.setMatchingClasses(true);
        strategy.setMatchingFields(false);
        strategy.setMatchingMethods(false);
        strategy.setMatchingLocalVariables(false);
        strategy.setMatchingInnerClasses(false);

        var expectedSymbols = Stream.of(
                "test"
        );

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testFieldNamesOnly() {
        strategy.setMatchingClasses(false);
        strategy.setMatchingFields(true);
        strategy.setMatchingMethods(false);
        strategy.setMatchingLocalVariables(false);
        strategy.setMatchingInnerClasses(false);

        var expectedSymbols = Stream.of(
                "test$InnerClass.innerField"
        );

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testMethodNamesOnly() {
        strategy.setMatchingClasses(false);
        strategy.setMatchingFields(false);
        strategy.setMatchingMethods(true);
        strategy.setMatchingLocalVariables(false);
        strategy.setMatchingInnerClasses(false);

        var expectedSymbols = Stream.of(
                "test.test()",
                "test.main(java.lang.String[]): void",
                "test$InnerClass.test$InnerClass()"
        );

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testLocalNamesOnly() {
        strategy.setMatchingClasses(false);
        strategy.setMatchingFields(false);
        strategy.setMatchingMethods(false);
        strategy.setMatchingLocalVariables(true);
        strategy.setMatchingInnerClasses(false);

        var expectedSymbols = Stream.of(
                "test.test(): this",
                "test.main(java.lang.String[]): void: c",
                "test.main(java.lang.String[]): void: ex",
                "test.main(java.lang.String[]): void: args",
                "test$InnerClass.test$InnerClass(): this"
        );

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }

    @Test
    void testInnerClassNamesOnly() {
        strategy.setMatchingClasses(false);
        strategy.setMatchingFields(false);
        strategy.setMatchingMethods(false);
        strategy.setMatchingLocalVariables(false);
        strategy.setMatchingInnerClasses(true);

        var expectedSymbols = Stream.of(
                "test$InnerClass"
        );

        loader.load(Arrays.asList(TEST_FILENAME, INNER_TEST_FILENAME));

        assertLinesMatch(expectedSymbols, gatherer.stream().map(Object::toString), "Gathered symbols");
    }
}
