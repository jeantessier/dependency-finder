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

package com.jeantessier.dependencyfinder.ant;

import org.apache.tools.ant.BuildException;
import org.jmock.Expectations;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import com.jeantessier.MockObjectTestCase;
import com.jeantessier.classreader.AccessibilitySymbolGathererStrategy;
import com.jeantessier.classreader.Classfile;
import com.jeantessier.classreader.DefaultSymbolGathererStrategy;
import com.jeantessier.classreader.Field_info;
import com.jeantessier.classreader.FilteringSymbolGathererStrategy;
import com.jeantessier.classreader.FinalMethodOrClassSymbolGathererStrategy;
import com.jeantessier.classreader.InnerClass;
import com.jeantessier.classreader.LocalVariable;
import com.jeantessier.classreader.Method_info;
import com.jeantessier.classreader.NonPrivateFieldSymbolGathererStrategy;
import com.jeantessier.classreader.SymbolGathererStrategy;
import com.jeantessier.classreader.SymbolGathererStrategyDecorator;

import static org.junit.jupiter.api.Assertions.*;

public class TestListSymbols extends MockObjectTestCase {
    private final Classfile mockClassfile = mock(Classfile.class);
    private final Field_info mockField = mock(Field_info.class);
    private final Method_info mockMethod = mock(Method_info.class);
    private final LocalVariable mockLocalVariable = mock(LocalVariable.class);
    private final InnerClass mockInnerClass = mock(InnerClass.class);

    private final ListSymbols sut = new ListSymbols();

    @Test
    void testAllMandatoryParameters() {
        sut.createPath();
        sut.setDestprefix(new File("foobar"));

        sut.validateParameters();
    }

    @Test
    void testPathNotSet() {
        try {
            sut.validateParameters();
            fail("executed without path being set");
        } catch (BuildException ex) {
            assertEquals("path must be set!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testMissingDestprefix() {
        sut.createPath();

        try {
            sut.validateParameters();
            fail("executed without destprefix being set");
        } catch (BuildException ex) {
            assertEquals("destprefix must be set!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testCreateStrategy_default() {
        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals(FilteringSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        List<String> includes = strategy.getIncludes();
        assertEquals(1, includes.size(), "filter includes");
        assertEquals("//", includes.get(0), "filter includes");
        List<String> excludes = strategy.getExcludes();
        assertEquals(0, excludes.size(), "filter excludes");

        SymbolGathererStrategy delegateStrategy = strategy.getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, delegateStrategy.getClass(), "delegate strategy class");
        assertTrue(delegateStrategy.isMatching(mockClassfile), "classes");
        assertTrue(delegateStrategy.isMatching(mockField), "fields");
        assertTrue(delegateStrategy.isMatching(mockMethod), "methods");
        assertTrue(delegateStrategy.isMatching(mockLocalVariable), "local variables");
        assertTrue(delegateStrategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_classnames() {
        sut.setClasses(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertTrue(strategy.isMatching(mockClassfile), "classes");
        assertFalse(strategy.isMatching(mockField), "fields");
        assertFalse(strategy.isMatching(mockMethod), "methods");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variables");
        assertFalse(strategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_fieldnames() {
        sut.setFields(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "classes");
        assertTrue(strategy.isMatching(mockField), "fields");
        assertFalse(strategy.isMatching(mockMethod), "methods");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variables");
        assertFalse(strategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_methodnames() {
        sut.setMethods(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "classes");
        assertFalse(strategy.isMatching(mockField), "fields");
        assertTrue(strategy.isMatching(mockMethod), "methods");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variables");
        assertFalse(strategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_localnames() {
        sut.setLocalvariables(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "classes");
        assertFalse(strategy.isMatching(mockField), "fields");
        assertFalse(strategy.isMatching(mockMethod), "methods");
        assertTrue(strategy.isMatching(mockLocalVariable), "local variables");
        assertFalse(strategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_innerclassnames() {
        sut.setInnerclasses(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(DefaultSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "classes");
        assertFalse(strategy.isMatching(mockField), "fields");
        assertFalse(strategy.isMatching(mockMethod), "methods");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variables");
        assertTrue(strategy.isMatching(mockInnerClass), "inner classes");
    }

    @Test
    void testCreateStrategy_publicaccessibility() {
        sut.setPublicaccessibility(true);

        checking(new Expectations() {{
            oneOf (mockClassfile).isPublic();
                will(returnValue(true));
            oneOf (mockClassfile).getClassName();
                will(returnValue("Foo"));
            oneOf (mockClassfile).isPublic();
                will(returnValue(false));
            oneOf (mockField).isPublic();
                will(returnValue(true));
            oneOf (mockField).getFullSignature();
                will(returnValue("int foo"));
            oneOf (mockField).isPublic();
                will(returnValue(false));
            oneOf (mockMethod).isPublic();
                will(returnValue(true));
            oneOf (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            oneOf (mockMethod).isPublic();
                will(returnValue(false));
            oneOf (mockInnerClass).isPublic();
                will(returnValue(true));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            oneOf (mockInnerClass).isPublic();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals(AccessibilitySymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertTrue(strategy.isMatching(mockClassfile), "public class");
        assertFalse(strategy.isMatching(mockClassfile), "non-public class");
        assertTrue(strategy.isMatching(mockField), "public field");
        assertFalse(strategy.isMatching(mockField), "non-public field");
        assertTrue(strategy.isMatching(mockMethod), "public method");
        assertFalse(strategy.isMatching(mockMethod), "non-public method");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variable");
        assertTrue(strategy.isMatching(mockInnerClass), "public inner class");
        assertFalse(strategy.isMatching(mockInnerClass), "non-public inner class");
    }

    @Test
    void testCreateStrategy_protectedaccessibility() {
        sut.setProtectedaccessibility(true);

        checking(new Expectations() {{
            oneOf (mockField).isProtected();
                will(returnValue(true));
            oneOf (mockField).getFullSignature();
                will(returnValue("int foo"));
            oneOf (mockField).isProtected();
                will(returnValue(false));
            oneOf (mockMethod).isProtected();
                will(returnValue(true));
            oneOf (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            oneOf (mockMethod).isProtected();
                will(returnValue(false));
            oneOf (mockInnerClass).isProtected();
                will(returnValue(true));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            oneOf (mockInnerClass).isProtected();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals(AccessibilitySymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "class");
        assertTrue(strategy.isMatching(mockField), "protected field");
        assertFalse(strategy.isMatching(mockField), "non-protected field");
        assertTrue(strategy.isMatching(mockMethod), "protected method");
        assertFalse(strategy.isMatching(mockMethod), "non-protected method");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variable");
        assertTrue(strategy.isMatching(mockInnerClass), "protected inner class");
        assertFalse(strategy.isMatching(mockInnerClass), "non-protected inner class");
    }

    @Test
    void testCreateStrategy_privateaccessibility() {
        sut.setPrivateaccessibility(true);

        checking(new Expectations() {{
            oneOf (mockField).isPrivate();
                will(returnValue(true));
            oneOf (mockField).getFullSignature();
                will(returnValue("int foo"));
            oneOf (mockField).isPrivate();
                will(returnValue(false));
            oneOf (mockMethod).isPrivate();
                will(returnValue(true));
            oneOf (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            oneOf (mockMethod).isPrivate();
                will(returnValue(false));
            oneOf (mockInnerClass).isPrivate();
                will(returnValue(true));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            oneOf (mockInnerClass).isPrivate();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals(AccessibilitySymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertFalse(strategy.isMatching(mockClassfile), "class");
        assertTrue(strategy.isMatching(mockField), "private field");
        assertFalse(strategy.isMatching(mockField), "non-private field");
        assertTrue(strategy.isMatching(mockMethod), "private method");
        assertFalse(strategy.isMatching(mockMethod), "non-private method");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variable");
        assertTrue(strategy.isMatching(mockInnerClass), "private inner class");
        assertFalse(strategy.isMatching(mockInnerClass), "non-private inner class");
    }

    @Test
    void testCreateStrategy_packageaccessibility() {
        sut.setPackageaccessibility(true);

        checking(new Expectations() {{
            oneOf (mockClassfile).isPackage();
                will(returnValue(true));
            oneOf (mockClassfile).getClassName();
                will(returnValue("Foo"));
            oneOf (mockClassfile).isPackage();
                will(returnValue(false));
            oneOf (mockField).isPackage();
                will(returnValue(true));
            oneOf (mockField).getFullSignature();
                will(returnValue("int foo"));
            oneOf (mockField).isPackage();
                will(returnValue(false));
            oneOf (mockMethod).isPackage();
                will(returnValue(true));
            oneOf (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            oneOf (mockMethod).isPackage();
                will(returnValue(false));
            oneOf (mockInnerClass).isPackage();
                will(returnValue(true));
            oneOf (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            oneOf (mockInnerClass).isPackage();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals(AccessibilitySymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        assertTrue(strategy.isMatching(mockClassfile), "package class");
        assertFalse(strategy.isMatching(mockClassfile), "non-package class");
        assertTrue(strategy.isMatching(mockField), "package field");
        assertFalse(strategy.isMatching(mockField), "non-package field");
        assertTrue(strategy.isMatching(mockMethod), "package method");
        assertFalse(strategy.isMatching(mockMethod), "non-package method");
        assertFalse(strategy.isMatching(mockLocalVariable), "local variable");
        assertTrue(strategy.isMatching(mockInnerClass), "package inner class");
        assertFalse(strategy.isMatching(mockInnerClass), "non-package inner class");
    }

    @Test
    void testCreateStrategy_nonprivatefieldnames() {
        sut.setNonprivatefields(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(NonPrivateFieldSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
    }

    @Test
    void testCreateStrategy_finalmethodorclassnames() {
        sut.setFinalmethodsorclasses(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals(FinalMethodOrClassSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
    }

    @Test
    void testCreateStrategy_singleincludes() {
        sut.setIncludes("/some/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals(FilteringSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        List<String> includes = strategy.getIncludes();
        assertEquals(1, includes.size(), "filter includes");
        assertEquals("/some/", includes.get(0), "filter includes");
    }

    @Test
    void testCreateStrategy_multipleincludes() {
        sut.setIncludes("/some/, /other/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals(FilteringSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        List<String> includes = strategy.getIncludes();
        assertEquals(2, includes.size(), "filter includes");
        assertEquals("/some/", includes.get(0), "filter includes");
        assertEquals("/other/", includes.get(1), "filter includes");
    }

    @Test
    void testCreateStrategy_singleexcludes() {
        sut.setExcludes("/some/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals(FilteringSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        List<String> excludes = strategy.getExcludes();
        assertEquals(1, excludes.size(), "filter excludes");
        assertEquals("/some/", excludes.get(0), "filter excludes");
    }

    @Test
    void testCreateStrategy_multipleexcludes() {
        sut.setExcludes("/some/, /other/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals(FilteringSymbolGathererStrategy.class, strategy.getClass(), "strategy class");
        List<String> excludes = strategy.getExcludes();
        assertEquals(2, excludes.size(), "filter excludes");
        assertEquals("/some/", excludes.get(0), "filter excludes");
        assertEquals("/other/", excludes.get(1), "filter excludes");
    }
}
