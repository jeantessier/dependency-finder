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

package com.jeantessier.dependencyfinder.ant;

import org.jmock.integration.junit3.*;

import com.jeantessier.classreader.*;

public class TestListSymbols extends MockObjectTestCase {
    private Classfile mockClassfile;
    private Field_info mockField;
    private Method_info mockMethod;
    private LocalVariable mockLocalVariable;

    private ListSymbols sut;

    protected void setUp() throws Exception {
        super.setUp();

        mockClassfile = mock(Classfile.class);
        mockField = mock(Field_info.class);
        mockMethod = mock(Method_info.class);
        mockLocalVariable = mock(LocalVariable.class);

        sut = new ListSymbols();
    }
    
    public void testDefaultStrategy() {
        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertTrue("class names", strategy.isMatching(mockClassfile));
        assertTrue("field names", strategy.isMatching(mockField));
        assertTrue("method names", strategy.isMatching(mockMethod));
        assertTrue("local variable names", strategy.isMatching(mockLocalVariable));
    }

    public void testStrategy_classnames() {
        sut.setClassnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertTrue("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
    }

    public void testStrategy_fieldnames() {
        sut.setFieldnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertTrue("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
    }

    public void testStrategy_methodnames() {
        sut.setMethodnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertTrue("method names", strategy.isMatching(mockMethod));
        assertFalse("local variable names", strategy.isMatching(mockLocalVariable));
    }

    public void testStrategy_localnames() {
        sut.setLocalnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", DefaultSymbolGathererStrategy.class, strategy.getClass());
        assertFalse("class names", strategy.isMatching(mockClassfile));
        assertFalse("field names", strategy.isMatching(mockField));
        assertFalse("method names", strategy.isMatching(mockMethod));
        assertTrue("local variable names", strategy.isMatching(mockLocalVariable));
    }

    public void testStrategy_nonprivatefieldnames() {
        sut.setNonprivatefieldnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", NonPrivateFieldSymbolGathererStrategy.class, strategy.getClass());
    }

    public void testStrategy_finalmethodorclassnames() {
        sut.setFinalmethodorclassnames(true);

        SymbolGathererStrategy strategy = sut.getStrategy();
        assertEquals("strategy class", FinalMethodOrClassSymbolGathererStrategy.class, strategy.getClass());
    }
}
