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

import java.util.*;

import junit.framework.*;

public class TestOrCompositeSelectionCriteria extends TestCase {
    private PackageNode a;
    private ClassNode   a_A;
    private FeatureNode a_A_a;

    private Collection                   subcriteria;
    private OrCompositeSelectionCriteria criteria;
    
    protected void setUp() throws Exception {
        NodeFactory factory = new NodeFactory();

        a     = factory.createPackage("a");
        a_A   = factory.createClass("a.A");
        a_A_a = factory.createFeature("a.A.a");

        subcriteria = new ArrayList();
        criteria    = new OrCompositeSelectionCriteria(subcriteria);
    }
    
    public void testMatchWithEmptyList() {
        assertEquals("a",     true, criteria.matches(a));
        assertEquals("a.A",   true, criteria.matches(a_A));
        assertEquals("a.A.a", true, criteria.matches(a_A_a));
    }
    
    public void testMatchWithOneFalseSubcriteria() {
        MockSelectionCriteria mock = new MockSelectionCriteria();
        mock.setValue(false);
        subcriteria.add(mock);

        assertEquals("a",     mock.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock.getValue(), criteria.matches(a_A_a));
    }
    
    public void testMatchWithOneTrueSubcriteria() {
        MockSelectionCriteria mock = new MockSelectionCriteria();
        mock.setValue(true);
        subcriteria.add(mock);

        assertEquals("a",     mock.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock.getValue(), criteria.matches(a_A_a));
    }
    
    public void testMatchWithTwoSubcriteriaFalseFalse() {
        MockSelectionCriteria mock1 = new MockSelectionCriteria();
        mock1.setValue(false);
        subcriteria.add(mock1);

        MockSelectionCriteria mock2 = new MockSelectionCriteria();
        mock2.setValue(false);
        subcriteria.add(mock2);

        assertEquals("a",     mock1.getValue() || mock2.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock1.getValue() || mock2.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock1.getValue() || mock2.getValue(), criteria.matches(a_A_a));
    }
    
    public void testMatchWithTwoSubcriteriaFalseTrue() {
        MockSelectionCriteria mock1 = new MockSelectionCriteria();
        mock1.setValue(false);
        subcriteria.add(mock1);

        MockSelectionCriteria mock2 = new MockSelectionCriteria();
        mock2.setValue(true);
        subcriteria.add(mock2);

        assertEquals("a",     mock1.getValue() || mock2.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock1.getValue() || mock2.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock1.getValue() || mock2.getValue(), criteria.matches(a_A_a));
    }
    
    public void testMatchWithTwoSubcriteriaTrueFalse() {
        MockSelectionCriteria mock1 = new MockSelectionCriteria();
        mock1.setValue(true);
        subcriteria.add(mock1);

        MockSelectionCriteria mock2 = new MockSelectionCriteria();
        mock2.setValue(false);
        subcriteria.add(mock2);

        assertEquals("a",     mock1.getValue() || mock2.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock1.getValue() || mock2.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock1.getValue() || mock2.getValue(), criteria.matches(a_A_a));
    }
    
    public void testMatchWithTwoSubcriteriaTrueTrue() {
        MockSelectionCriteria mock1 = new MockSelectionCriteria();
        mock1.setValue(true);
        subcriteria.add(mock1);

        MockSelectionCriteria mock2 = new MockSelectionCriteria();
        mock2.setValue(true);
        subcriteria.add(mock2);

        assertEquals("a",     mock1.getValue() || mock2.getValue(), criteria.matches(a));
        assertEquals("a.A",   mock1.getValue() || mock2.getValue(), criteria.matches(a_A));
        assertEquals("a.A.a", mock1.getValue() || mock2.getValue(), criteria.matches(a_A_a));
    }
}
