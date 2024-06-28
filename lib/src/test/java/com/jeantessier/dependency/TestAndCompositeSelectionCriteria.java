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

package com.jeantessier.dependency;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestAndCompositeSelectionCriteria extends CompositeSelectionCriteriaTestBase {
    @Parameters(name="AndCompositeSelectionCriteria with {0} subcriteria should return {2}")
    public static Object[][] data() {
        return new Object[][] {
                {"empty", new Boolean[] {}, true},
                {"single false", new Boolean[] {false}, false},
                {"single true", new Boolean[] {true}, true},
                {"multiple false false", new Boolean[] {false, false}, false},
                {"multiple false true", new Boolean[] {false, true}, false},
                {"multiple true false", new Boolean[] {true, false}, false},
                {"multiple true true", new Boolean[] {true, true}, true},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public Boolean[] subcriteria;

    @Parameter(2)
    public boolean expectedValue;

    private SelectionCriteria sut;

    @Before
    public void setUp() {
        sut = new AndCompositeSelectionCriteria(build(subcriteria));
    }

    @Test
    public void testIsMatchingPackages() {
        assertEquals("a", expectedValue, sut.isMatchingPackages());
    }

    @Test
    public void testIsMatchingClasses() {
        assertEquals("a", expectedValue, sut.isMatchingClasses());
    }

    @Test
    public void testIsMatchingFeatures() {
        assertEquals("a", expectedValue, sut.isMatchingFeatures());
    }

    @Test
    public void testMatchesWithPackageNode() {
        assertEquals("a", expectedValue, sut.matches(context.mock(PackageNode.class)));
    }

    @Test
    public void testMatchesWithClassNode() {
        assertEquals("a.A", expectedValue, sut.matches(context.mock(ClassNode.class)));
    }

    @Test
    public void testMatchesWithFeatureNode() {
        assertEquals("a.A.a", expectedValue, sut.matches(context.mock(FeatureNode.class)));
    }

    @Test
    public void testMatchesPackageName() {
        assertEquals("a", expectedValue, sut.matchesPackageName("a"));
    }

    @Test
    public void testMatchesClassName() {
        assertEquals("a", expectedValue, sut.matchesClassName("a.A"));
    }

    @Test
    public void testMatchesFeatureName() {
        assertEquals("a", expectedValue, sut.matchesFeatureName("a.A.a"));
    }
}
