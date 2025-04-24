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

package com.jeantessier.dependency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestOrCompositeSelectionCriteria extends CompositeSelectionCriteriaTestBase {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("empty", new Boolean[] {}, true),
                arguments("single false", new Boolean[] {false}, false),
                arguments("single true", new Boolean[] {true}, true),
                arguments("multiple false false", new Boolean[] {false, false}, false),
                arguments("multiple false true", new Boolean[] {false, true}, true),
                arguments("multiple true false", new Boolean[] {true, false}, true),
                arguments("multiple true true", new Boolean[] {true, true}, true)
        );
    }

    @DisplayName("isMatchingPackages")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testIsMatchingPackages(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.isMatchingPackages(), "a");
    }

    @DisplayName("isMatchingClasses")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testIsMatchingClasses(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.isMatchingClasses(), "a.A");
    }

    @DisplayName("isMatchingFeatures")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testIsMatchingFeatures(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.isMatchingFeatures(), "a.A.a");
    }

    @DisplayName("matches with PackageNode")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesWithPackageNode(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matches(context.mock(PackageNode.class)), "a");
    }

    @DisplayName("matches with ClassNode")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesWithClassNode(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matches(context.mock(ClassNode.class)), "a.A");
    }

    @DisplayName("matches with FeatureNode")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesWithFeatureNode(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matches(context.mock(FeatureNode.class)), "a.A.a");
    }

    @DisplayName("matches with package name")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesPackageName(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matchesPackageName("a"), "a");
    }

    @DisplayName("matches with class name")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesClassName(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matchesClassName("a.A"), "a.A");
    }

    @DisplayName("matches with feature name")
    @ParameterizedTest(name="with {0} subcriteria should return {2}")
    @MethodSource("dataProvider")
    void testMatchesFeatureName(String variation, Boolean[] subcriteria, boolean expectedValue) {
        var sut = new OrCompositeSelectionCriteria(build(subcriteria));
        assertEquals(expectedValue, sut.matchesFeatureName("a.A.a"), "a.A.a");
    }
}
