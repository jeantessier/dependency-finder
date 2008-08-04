/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import java.util.*;

import org.jmock.integration.junit3.*;
import org.jmock.*;
import org.apache.oro.text.*;

public class TestFilteringSymbolGathererStrategy extends MockObjectTestCase {
    private static final String SOME_PACKAGE_NAME = "some.package";
    private static final String SOME_CLASS_NAME = SOME_PACKAGE_NAME + ".SomeClass";
    private static final String SOME_FIELD_SIGNATURE = SOME_CLASS_NAME + ".someField";
    private static final String SOME_METHOD_SIGNATURE = SOME_CLASS_NAME + ".someMethod()";
    private static final String SOME_LOCAL_VARIABLE_NAME = "someLocalVariable";
    private static final String ANOTHER_PACKAGE_NAME = "another.package";
    private static final String ANOTHER_CLASS_NAME = ANOTHER_PACKAGE_NAME + ".AnotherClass";
    private static final String ANOTHER_FIELD_SIGNATURE = ANOTHER_CLASS_NAME + ".anotherField";
    private static final String ANOTHER_METHOD_SIGNATURE = ANOTHER_CLASS_NAME + ".anotherMethod()";
    private static final String ANOTHER_LOCAL_VARIABLE_NAME = "anotherLocalVariable";

    private Classfile mockClassfile;
    private Field_info mockField;
    private Method_info mockMethod;
    private LocalVariable mockLocalVariable;
    private SymbolGathererStrategy mockStrategy;

    protected void setUp() throws Exception {
        super.setUp();

        mockClassfile = mock(Classfile.class);
        mockField = mock(Field_info.class);
        mockMethod = mock(Method_info.class);
        mockLocalVariable = mock(LocalVariable.class);
        mockStrategy = mock(SymbolGathererStrategy.class);
    }

    /*
     * Classes
     */

    public void testIsMatching_class_matchingincludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
            one (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        sut.isMatching(mockClassfile);
    }

    public void testIsMatching_class_notmatchingincludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockClassfile));
    }

    public void testIsMatching_class_noincludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.<String>emptyList(), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockClassfile));
    }

    public void testIsMatching_class_badincludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), Collections.<String>emptyList());
        try {
            sut.isMatching(mockClassfile);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    public void testIsMatching_class_matchingexcludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
            will(returnValue(SOME_CLASS_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        assertFalse(sut.isMatching(mockClassfile));
    }

    public void testIsMatching_class_notmatchingexcludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
            will(returnValue(ANOTHER_CLASS_NAME));
            one (mockStrategy).isMatching(mockClassfile);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        sut.isMatching(mockClassfile);
    }

    public void testIsMatching_class_badexcludes() {
        checking(new Expectations() {{
            one (mockClassfile).getClassName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some"));
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

    public void testIsMatching_field_matchingincludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
            one (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        sut.isMatching(mockField);
    }

    public void testIsMatching_field_notmatchingincludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockField));
    }

    public void testIsMatching_field_noincludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.<String>emptyList(), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockField));
    }

    public void testIsMatching_field_badincludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), Collections.<String>emptyList());
        try {
            sut.isMatching(mockField);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    public void testIsMatching_field_matchingexcludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
            will(returnValue(SOME_FIELD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        assertFalse(sut.isMatching(mockField));
    }

    public void testIsMatching_field_notmatchingexcludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
            will(returnValue(ANOTHER_FIELD_SIGNATURE));
            one (mockStrategy).isMatching(mockField);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        sut.isMatching(mockField);
    }

    public void testIsMatching_field_badexcludes() {
        checking(new Expectations() {{
            one (mockField).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some"));
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

    public void testIsMatching_method_matchingincludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
            one (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        sut.isMatching(mockMethod);
    }

    public void testIsMatching_method_notmatchingincludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockMethod));
    }

    public void testIsMatching_method_noincludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.<String>emptyList(), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockMethod));
    }

    public void testIsMatching_method_badincludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), Collections.<String>emptyList());
        try {
            sut.isMatching(mockMethod);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    public void testIsMatching_method_matchingexcludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
            will(returnValue(SOME_METHOD_SIGNATURE));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        assertFalse(sut.isMatching(mockMethod));
    }

    public void testIsMatching_method_notmatchingexcludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
            will(returnValue(ANOTHER_METHOD_SIGNATURE));
            one (mockStrategy).isMatching(mockMethod);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        sut.isMatching(mockMethod);
    }

    public void testIsMatching_method_badexcludes() {
        checking(new Expectations() {{
            one (mockMethod).getFullSignature();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some"));
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

    public void testIsMatching_local_matchingincludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
            one (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        sut.isMatching(mockLocalVariable);
    }

    public void testIsMatching_local_notmatchingincludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some/"), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    public void testIsMatching_local_noincludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.<String>emptyList(), Collections.<String>emptyList());
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    public void testIsMatching_local_badincludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("/some"), Collections.<String>emptyList());
        try {
            sut.isMatching(mockLocalVariable);
            fail("did not throw expection for bad excludes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }

    public void testIsMatching_local_matchingexcludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
            will(returnValue(SOME_LOCAL_VARIABLE_NAME));
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        assertFalse(sut.isMatching(mockLocalVariable));
    }

    public void testIsMatching_local_notmatchingexcludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
            will(returnValue(ANOTHER_LOCAL_VARIABLE_NAME));
            one (mockStrategy).isMatching(mockLocalVariable);
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some/"));
        sut.isMatching(mockLocalVariable);
    }

    public void testIsMatching_local_badexcludes() {
        checking(new Expectations() {{
            one (mockLocalVariable).getName();
        }});

        FilteringSymbolGathererStrategy sut = new FilteringSymbolGathererStrategy(mockStrategy, Collections.singletonList("//"), Collections.singletonList("/some"));
        try {
            sut.isMatching(mockLocalVariable);
            fail("did not throw expection for bad includes regular expression");
        } catch (MalformedCachePatternException ex) {
            // expected
        }
    }
}
