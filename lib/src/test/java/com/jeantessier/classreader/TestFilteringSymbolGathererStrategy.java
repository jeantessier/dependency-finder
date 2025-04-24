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

import org.apache.oro.text.MalformedCachePatternException;
import org.jmock.*;
import org.junit.jupiter.api.*;

import java.util.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestFilteringSymbolGathererStrategy extends MockObjectTestCase {
    private static final String SOME_PACKAGE_NAME = "some.package";
    private static final String SOME_CLASS_NAME = SOME_PACKAGE_NAME + ".SomeClass";
    private static final String SOME_FIELD_SIGNATURE = SOME_CLASS_NAME + ".someField";
    private static final String SOME_METHOD_SIGNATURE = SOME_CLASS_NAME + ".someMethod()";
    private static final String SOME_LOCAL_VARIABLE_NAME = "someLocalVariable";
    private static final String SOME_INNER_CLASS_NAME = SOME_CLASS_NAME + "$SomeInnerClass";
    private static final String ANOTHER_PACKAGE_NAME = "another.package";
    private static final String ANOTHER_CLASS_NAME = ANOTHER_PACKAGE_NAME + ".AnotherClass";
    private static final String ANOTHER_FIELD_SIGNATURE = ANOTHER_CLASS_NAME + ".anotherField";
    private static final String ANOTHER_METHOD_SIGNATURE = ANOTHER_CLASS_NAME + ".anotherMethod()";
    private static final String ANOTHER_LOCAL_VARIABLE_NAME = "anotherLocalVariable";
    private static final String ANOTHER_INNER_CLASS_NAME = ANOTHER_CLASS_NAME + "$AnotherInnerClass";

    private final Classfile mockClassfile = mock(Classfile.class);
    private final Field_info mockField = mock(Field_info.class);
    private final Method_info mockMethod = mock(Method_info.class);
    private final LocalVariable mockLocalVariable = mock(LocalVariable.class);
    private final InnerClass mockInnerClass = mock(InnerClass.class);
    private final SymbolGathererStrategy mockStrategy = mock(SymbolGathererStrategy.class);

    private final List<String> noMatch = Collections.emptyList();
    private final List<String> allMatch = Collections.singletonList("//");

    /*
     * Classes
     */

    @Test
    void testIsMatching_class_matchingincludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_notmatchingincludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_matchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_CLASS_NAME), noMatch, noMatch);
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_partialmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList("SomeClass"), noMatch, noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_notmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_CLASS_NAME), noMatch, noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_nullmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, null, noMatch, noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_noincludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_badincludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), noMatch, noMatch, noMatch);
        try {
            sut.isMatching(mockClassfile);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    @Test
    void testIsMatching_class_matchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_notmatchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_matchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_CLASS_NAME));
        assertFalse(sut.isMatching(mockClassfile));
    }

    @Test
    void testIsMatching_class_partialmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList("SomeClass"));
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_notmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_CLASS_NAME));
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_nullmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, null);
        sut.isMatching(mockClassfile);
    }

    @Test
    void testIsMatching_class_badexcludes() {
        checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some"), noMatch);
        try {
            sut.isMatching(mockClassfile);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    /*
     * Fields
     */

    @Test
    void testIsMatching_field_matchingincludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_notmatchingincludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_matchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_FIELD_SIGNATURE), noMatch, noMatch);
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_partialmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList("someField"), noMatch, noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_notmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_FIELD_SIGNATURE), noMatch, noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_nullmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, null, noMatch, noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_noincludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_badincludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), noMatch, noMatch, noMatch);
        try {
            sut.isMatching(mockField);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    @Test
    void testIsMatching_field_matchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_notmatchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_matchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_FIELD_SIGNATURE));
        assertFalse(sut.isMatching(mockField));
    }

    @Test
    void testIsMatching_field_partialmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList("someField"));
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_notmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_FIELD_SIGNATURE));
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_nullmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, null);
        sut.isMatching(mockField);
    }

    @Test
    void testIsMatching_field_badexcludes() {
        checking(new Expectations() {{
            oneOf (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some"), noMatch);
        try {
            sut.isMatching(mockField);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    /*
     * Methods
     */

    @Test
    void testIsMatching_method_matchingincludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_notmatchingincludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_matchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_METHOD_SIGNATURE), noMatch, noMatch);
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_partialmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList("someMethod()"), noMatch, noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_notmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_METHOD_SIGNATURE), noMatch, noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_nullmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, null, noMatch, noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_noincludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_badincludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), noMatch, noMatch, noMatch);
        try {
            sut.isMatching(mockMethod);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    @Test
    void testIsMatching_method_matchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_notmatchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_matchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_METHOD_SIGNATURE));
        assertFalse(sut.isMatching(mockMethod));
    }

    @Test
    void testIsMatching_method_partialmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList("someMethod()"));
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_notmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_METHOD_SIGNATURE));
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_nullmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
            oneOf (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, null);
        sut.isMatching(mockMethod);
    }

    @Test
    void testIsMatching_method_badexcludes() {
        checking(new Expectations() {{
            oneOf (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some"), noMatch);
        try {
            sut.isMatching(mockMethod);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    /*
     * Local variables
     */

    @Test
    void testIsMatching_local_matchingincludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_notmatchingincludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_matchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_LOCAL_VARIABLE_NAME), noMatch, noMatch);
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_partialmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList("someLocal"), noMatch, noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_notmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_LOCAL_VARIABLE_NAME), noMatch, noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_nullmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, null, noMatch, noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_noincludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_badincludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), noMatch, noMatch, noMatch);
        try {
            sut.isMatching(mockLocalVariable);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    @Test
    void testIsMatching_local_matchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_notmatchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_matchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_LOCAL_VARIABLE_NAME));
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    @Test
    void testIsMatching_local_partialmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList("someLocal"));
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_notmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_LOCAL_VARIABLE_NAME));
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_nullmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
            oneOf (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, null);
        sut.isMatching(mockLocalVariable);
    }

    @Test
    void testIsMatching_local_badexcludes() {
        checking(new Expectations() {{
            oneOf (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some"), noMatch);
        try {
            sut.isMatching(mockLocalVariable);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    /*
     * Inner Classes
     */

    @Test
    void testIsMatching_innerClass_matchingincludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_notmatchingincludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(ANOTHER_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_matchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_INNER_CLASS_NAME), noMatch, noMatch);
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_partialmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList("SomeInnerClass"), noMatch, noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_notmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(ANOTHER_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, Collections.singletonList(SOME_INNER_CLASS_NAME), noMatch, noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_nullmatchingincludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, null, noMatch, noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_noincludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, noMatch, noMatch, noMatch, noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_badincludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), noMatch, noMatch, noMatch);
        try {
            sut.isMatching(mockInnerClass);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    @Test
    void testIsMatching_innerClass_matchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_notmatchingexcludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(ANOTHER_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some/"), noMatch);
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_matchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_INNER_CLASS_NAME));
        assertFalse(sut.isMatching(mockInnerClass));
    }

    @Test
    void testIsMatching_innerClass_partialmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList("SomeInnerClass"));
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_notmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(ANOTHER_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, Collections.singletonList(SOME_INNER_CLASS_NAME));
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_nullmatchingexcludeslist() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
            will(returnValue(SOME_INNER_CLASS_NAME));
            oneOf (mockStrategy).isMatching(mockInnerClass);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, noMatch, null);
        sut.isMatching(mockInnerClass);
    }

    @Test
    void testIsMatching_innerClass_badexcludes() {
        checking(new Expectations() {{
            oneOf (mockInnerClass).getInnerClassInfo();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, allMatch, noMatch, Collections.singletonList("/some"), noMatch);
        try {
            sut.isMatching(mockInnerClass);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }
}
