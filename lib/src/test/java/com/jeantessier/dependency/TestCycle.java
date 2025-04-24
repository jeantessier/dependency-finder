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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCycle {
    private final NodeFactory factory = new NodeFactory();

    private final Node a = factory.createPackage("a");
    private final Node b = factory.createPackage("b");
    private final Node c = factory.createPackage("c");
    private final Node d = factory.createPackage("d");
    private final Node e = factory.createPackage("e");

    @Test
    void testConstructEmptyCycle() {
        assertThrows(Exception.class, () -> new Cycle(List.of()));
    }

    @Test
    void testConstructLength1Cycle() {
        Cycle cycle = new Cycle(List.of(a));

        assertEquals(1, cycle.getLength(), "length");
        assertEquals(a, cycle.getPath().iterator().next(), "a");
    }

    @Test
    void testEquals_Identical() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(a, b));

        assertEquals(cycle1, cycle2);
        assertEquals(cycle2, cycle1);
    }

    @Test
    void testEquals_Reversed() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(b, a));

        assertEquals(cycle1, cycle2);
        assertEquals(cycle2, cycle1);
    }

    @Test
    void testEquals_SameLength() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(c, d));

        assertNotEquals(cycle1, cycle2);
        assertNotEquals(cycle2, cycle1);
    }

    @Test
    void testEquals_DifferentLength() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(c, d, e));

        assertNotEquals(cycle1, cycle2);
        assertNotEquals(cycle2, cycle1);
    }

    @Test
    void testEquals_LengthTrumpsContent() {
        Cycle cycle1 = new Cycle(List.of(a, b, c));
        Cycle cycle2 = new Cycle(List.of(d, e));

        assertNotEquals(cycle1, cycle2);
        assertNotEquals(cycle2, cycle1);
    }

    @Test
    void testCompareTo_Identical() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(a, b));

        assertEquals(0, cycle1.compareTo(cycle2));
        assertEquals(0, cycle2.compareTo(cycle1));
    }

    @Test
    void testCompareTo_Reversed() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(b, a));

        assertEquals(0, cycle1.compareTo(cycle2));
        assertEquals(0, cycle2.compareTo(cycle1));
    }

    @Test
    void testCompareTo_SameLength() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(c, d));

        assertTrue(cycle1.compareTo(cycle2) < 0);
        assertTrue(cycle2.compareTo(cycle1) > 0);
    }

    @Test
    void testCompareTo_DifferentLength() {
        Cycle cycle1 = new Cycle(List.of(a, b));
        Cycle cycle2 = new Cycle(List.of(c, d, e));

        assertTrue(cycle1.compareTo(cycle2) < 0);
        assertTrue(cycle2.compareTo(cycle1) > 0);
    }

    @Test
    void testCompareTo_LengthTrumpsContent() {
        Cycle cycle1 = new Cycle(List.of(a, b, c));
        Cycle cycle2 = new Cycle(List.of(d, e));

        assertTrue(cycle1.compareTo(cycle2) > 0);
        assertTrue(cycle2.compareTo(cycle1) < 0);
    }
}
