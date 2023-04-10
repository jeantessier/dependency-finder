/*
 *  Copyright (c) 2001-2023, Jean Tessier
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
import org.apache.tools.ant.BuildException;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import java.io.File;
import java.util.List;

public class TestListSymbols extends MockObjectTestCase {
    private Classfile mockClassfile;
    private Field_info mockField;
    private Method_info mockMethod;
    private LocalVariable mockLocalVariable;
    private InnerClass mockInnerClass;

    private ListSymbols sut;

    protected void setUp() throws Exception {
        super.setUp();

        mockClassfile = mock(Classfile.class);
        mockField = mock(Field_info.class);
        mockMethod = mock(Method_info.class);
        mockLocalVariable = mock(LocalVariable.class);
        mockInnerClass = mock(InnerClass.class);

        sut = new ListSymbols();
    }

    public void testAllMandatoryParameters() {
        sut.createPath();
        sut.setDestfile(new File("foobar"));

        sut.validateParameters();
    }

    public void testPathNotSet() {
        try {
            sut.validateParameters();
            fail("executed without path being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "path must be set!", ex.getMessage());
        }
    }

    public void testMissingDestfile() {
        sut.createPath();

        try {
            sut.validateParameters();
            fail("executed without destfile being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "destfile must be set!", ex.getMessage());
        }
    }

    public void testCreateStrategy_default() {
        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals("strategy class", FilteringSymbolGathererStrategy.class, strategy.getClass());
        List<String> includes = strategy.getIncludes();
        assertEquals("filter includes", 1, includes.size());
        assertEquals("filter includes", "//", includes.get(0));
        List<String> excludes = strategy.getExcludes();
        assertEquals("filter excludes", 0, excludes.size());

        SymbolGathererStrategy delegateStrategy = strategy.getDelegate();
        assertEquals("delegate strategy class", DefaultSymbolGathererStrategy.class, delegateStrategy.getClass());
        assertTrue("class names", delegateStrategy.isMatching(mockClassfile));
        assertTrue("field names", delegateStrategy.isMatching(mockField));
        assertTrue("method names", delegateStrategy.isMatching(mockMethod));
        assertTrue("local variable names", delegateStrategy.isMatching(mockLocalVariable));
        assertTrue("inner class names", delegateStrategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_classnames() {
        sut.setClassnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertTrue("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
        assertFalse("inner class names", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_fieldnames() {
        sut.setFieldnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertTrue("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
        assertFalse("inner class names", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_methodnames() {
        sut.setMethodnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertTrue("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
        assertFalse("inner class names", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_localnames() {
        sut.setLocalnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertTrue("local variable names", strategy.isMatching(mockLocalVariable));
        assertFalse("inner class names", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_innerclassnames() {
        sut.setInnerclassnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
        assertTrue("inner class names", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_publicaccessibility() {
        sut.setPublicaccessibility(true);

        checking(new Expectations() {{
            one (mockClassfile).isPublic();
                will(returnValue(true));
            one (mockClassfile).getClassName();
                will(returnValue("Foo"));
            one (mockClassfile).isPublic();
                will(returnValue(false));
            one (mockField).isPublic();
                will(returnValue(true));
            one (mockField).getFullSignature();
                will(returnValue("int foo"));
            one (mockField).isPublic();
                will(returnValue(false));
            one (mockMethod).isPublic();
                will(returnValue(true));
            one (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            one (mockMethod).isPublic();
                will(returnValue(false));
            one (mockInnerClass).isPublic();
                will(returnValue(true));
            one (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            one (mockInnerClass).isPublic();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals("strategy class", AccessibilitySymbolGathererStrategy.class, strategy.getClass());
        assertTrue("public class", strategy.isMatching(mockClassfile));
        assertFalse("non-public class", strategy.isMatching(mockClassfile));
        assertTrue("public field", strategy.isMatching(mockField));
        assertFalse("non-public field", strategy.isMatching(mockField));
        assertTrue("public method", strategy.isMatching(mockMethod));
        assertFalse("non-public method", strategy.isMatching(mockMethod));
        assertFalse("local variable", strategy.isMatching(mockLocalVariable));
        assertTrue("public inner class", strategy.isMatching(mockInnerClass));
        assertFalse("non-public inner class", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_protectedaccessibility() {
        sut.setProtectedaccessibility(true);

        checking(new Expectations() {{
            one (mockField).isProtected();
                will(returnValue(true));
            one (mockField).getFullSignature();
                will(returnValue("int foo"));
            one (mockField).isProtected();
                will(returnValue(false));
            one (mockMethod).isProtected();
                will(returnValue(true));
            one (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            one (mockMethod).isProtected();
                will(returnValue(false));
            one (mockInnerClass).isProtected();
                will(returnValue(true));
            one (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            one (mockInnerClass).isProtected();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals("strategy class", AccessibilitySymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class", strategy.isMatching(mockClassfile));
        assertTrue("protected field", strategy.isMatching(mockField));
        assertFalse("non-protected field", strategy.isMatching(mockField));
        assertTrue("protected method", strategy.isMatching(mockMethod));
        assertFalse("non-protected method", strategy.isMatching(mockMethod));
        assertFalse("local variable", strategy.isMatching(mockLocalVariable));
        assertTrue("protected inner class", strategy.isMatching(mockInnerClass));
        assertFalse("non-protected inner class", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_privateaccessibility() {
        sut.setPrivateaccessibility(true);

        checking(new Expectations() {{
            one (mockField).isPrivate();
                will(returnValue(true));
            one (mockField).getFullSignature();
                will(returnValue("int foo"));
            one (mockField).isPrivate();
                will(returnValue(false));
            one (mockMethod).isPrivate();
                will(returnValue(true));
            one (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            one (mockMethod).isPrivate();
                will(returnValue(false));
            one (mockInnerClass).isPrivate();
                will(returnValue(true));
            one (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            one (mockInnerClass).isPrivate();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals("strategy class", AccessibilitySymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class", strategy.isMatching(mockClassfile));
        assertTrue("private field", strategy.isMatching(mockField));
        assertFalse("non-private field", strategy.isMatching(mockField));
        assertTrue("private method", strategy.isMatching(mockMethod));
        assertFalse("non-private method", strategy.isMatching(mockMethod));
        assertFalse("local variable", strategy.isMatching(mockLocalVariable));
        assertTrue("private inner class", strategy.isMatching(mockInnerClass));
        assertFalse("non-private inner class", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_packageaccessibility() {
        sut.setPackageaccessibility(true);

        checking(new Expectations() {{
            one (mockClassfile).isPackage();
                will(returnValue(true));
            one (mockClassfile).getClassName();
                will(returnValue("Foo"));
            one (mockClassfile).isPackage();
                will(returnValue(false));
            one (mockField).isPackage();
                will(returnValue(true));
            one (mockField).getFullSignature();
                will(returnValue("int foo"));
            one (mockField).isPackage();
                will(returnValue(false));
            one (mockMethod).isPackage();
                will(returnValue(true));
            one (mockMethod).getFullSignature();
                will(returnValue("void foo()"));
            one (mockMethod).isPackage();
                will(returnValue(false));
            one (mockInnerClass).isPackage();
                will(returnValue(true));
            one (mockInnerClass).getInnerClassInfo();
                will(returnValue("Foo.InnerFoo"));
            one (mockInnerClass).isPackage();
                will(returnValue(false));
        }});

        SymbolGathererStrategy strategy = sut.createStrategy();
        assertEquals("strategy class", AccessibilitySymbolGathererStrategy.class, strategy.getClass());
        assertTrue("package class", strategy.isMatching(mockClassfile));
        assertFalse("non-package class", strategy.isMatching(mockClassfile));
        assertTrue("package field", strategy.isMatching(mockField));
        assertFalse("non-package field", strategy.isMatching(mockField));
        assertTrue("package method", strategy.isMatching(mockMethod));
        assertFalse("non-package method", strategy.isMatching(mockMethod));
        assertFalse("local variable", strategy.isMatching(mockLocalVariable));
        assertTrue("package inner class", strategy.isMatching(mockInnerClass));
        assertFalse("non-package inner class", strategy.isMatching(mockInnerClass));
    }

    public void testCreateStrategy_nonprivatefieldnames() {
        sut.setNonprivatefieldnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", NonPrivateFieldSymbolGathererStrategy.class, strategy.getClass());
    }

    public void testCreateStrategy_finalmethodorclassnames() {
        sut.setFinalmethodorclassnames(true);

        SymbolGathererStrategy strategy = ((SymbolGathererStrategyDecorator) sut.createStrategy()).getDelegate();
        assertEquals("strategy class", FinalMethodOrClassSymbolGathererStrategy.class, strategy.getClass());
    }

    public void testCreateStrategy_singleincludes() {
        sut.setIncludes("/some/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals("strategy class", FilteringSymbolGathererStrategy.class, strategy.getClass());
        List<String> includes = strategy.getIncludes();
        assertEquals("filter includes", 1, includes.size());
        assertEquals("filter includes", "/some/", includes.get(0));
    }

    public void testCreateStrategy_multipleincludes() {
        sut.setIncludes("/some/, /other/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals("strategy class", FilteringSymbolGathererStrategy.class, strategy.getClass());
        List<String> includes = strategy.getIncludes();
        assertEquals("filter includes", 2, includes.size());
        assertEquals("filter includes", "/some/", includes.get(0));
        assertEquals("filter includes", "/other/", includes.get(1));
    }

    public void testCreateStrategy_singleexcludes() {
        sut.setExcludes("/some/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals("strategy class", FilteringSymbolGathererStrategy.class, strategy.getClass());
        List<String> excludes = strategy.getExcludes();
        assertEquals("filter excludes", 1, excludes.size());
        assertEquals("filter excludes", "/some/", excludes.get(0));
    }

    public void testCreateStrategy_multipleexcludes() {
        sut.setExcludes("/some/, /other/");

        FilteringSymbolGathererStrategy strategy = (FilteringSymbolGathererStrategy) sut.createStrategy();
        assertEquals("strategy class", FilteringSymbolGathererStrategy.class, strategy.getClass());
        List<String> excludes = strategy.getExcludes();
        assertEquals("filter excludes", 2, excludes.size());
        assertEquals("filter excludes", "/some/", excludes.get(0));
        assertEquals("filter excludes", "/other/", excludes.get(1));
    }
}
