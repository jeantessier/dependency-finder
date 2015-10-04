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

public class TestCycle extends TestCase {
    private Node a;
    private Node b;
    private Node c;
    private Node d;
    private Node e;

    protected void setUp() throws Exception {
        NodeFactory factory = new NodeFactory();

        a = factory.createPackage("a");
        b = factory.createPackage("b");
        c = factory.createPackage("c");
        d = factory.createPackage("d");
        e = factory.createPackage("e");
    }

    public void testConstructEmptyCycle() {
        try {
            new Cycle(new ArrayList<Node>());
            fail("Constructed empty cycle");
        } catch (Exception ex) {
            // expected
        }
    }

    public void testConstructLength1Cycle() {
        List<Node> path = new ArrayList<Node>();
        path.add(a);
        Cycle cycle = new Cycle(path);

        assertEquals("length", 1, cycle.getLength());
        assertEquals("a", a, cycle.getPath().iterator().next());
    }

    public void testEquals_Identical() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(a);
        path2.add(b);
        Cycle cycle2 = new Cycle(path2);

        assertTrue(cycle1.equals(cycle2));
        assertTrue(cycle2.equals(cycle1));
    }

    public void testEquals_Reversed() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(b);
        path2.add(a);
        Cycle cycle2 = new Cycle(path2);

        assertTrue(cycle1.equals(cycle2));
        assertTrue(cycle2.equals(cycle1));
    }

    public void testEquals_SameLength() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(c);
        path2.add(d);
        Cycle cycle2 = new Cycle(path2);

        assertFalse(cycle1.equals(cycle2));
        assertFalse(cycle2.equals(cycle1));
    }

    public void testEquals_DifferentLength() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(c);
        path2.add(d);
        path2.add(e);
        Cycle cycle2 = new Cycle(path2);

        assertFalse(cycle1.equals(cycle2));
        assertFalse(cycle2.equals(cycle1));
    }

    public void testEquals_LengthTrumpsContent() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        path1.add(c);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(d);
        path2.add(e);
        Cycle cycle2 = new Cycle(path2);

        assertFalse(cycle1.equals(cycle2));
        assertFalse(cycle2.equals(cycle1));
    }

    public void testCompareTo_Identical() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(a);
        path2.add(b);
        Cycle cycle2 = new Cycle(path2);

        assertEquals(0, cycle1.compareTo(cycle2));
        assertEquals(0, cycle2.compareTo(cycle1));
    }

    public void testCompareTo_Reversed() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(b);
        path2.add(a);
        Cycle cycle2 = new Cycle(path2);

        assertEquals(0, cycle1.compareTo(cycle2));
        assertEquals(0, cycle2.compareTo(cycle1));
    }

    public void testCompareTo_SameLength() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(c);
        path2.add(d);
        Cycle cycle2 = new Cycle(path2);

        assertTrue(cycle1.compareTo(cycle2) < 0);
        assertTrue(cycle2.compareTo(cycle1) > 0);
    }

    public void testCompareTo_DifferentLength() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(c);
        path2.add(d);
        path2.add(e);
        Cycle cycle2 = new Cycle(path2);

        assertTrue(cycle1.compareTo(cycle2) < 0);
        assertTrue(cycle2.compareTo(cycle1) > 0);
    }

    public void testCompareTo_LengthTrumpsContent() {
        List<Node> path1 = new ArrayList<Node>();
        path1.add(a);
        path1.add(b);
        path1.add(c);
        Cycle cycle1 = new Cycle(path1);

        List<Node> path2 = new ArrayList<Node>();
        path2.add(d);
        path2.add(e);
        Cycle cycle2 = new Cycle(path2);

        assertTrue(cycle1.compareTo(cycle2) > 0);
        assertTrue(cycle2.compareTo(cycle1) < 0);
    }
}
