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

public class TestCycleComparator extends TestCase {
    public void testCompareBasedOnLength() {
        Node node = new PackageNode("node", true);

        List<Node> shortPath = new ArrayList<Node>(2);
        shortPath.add(node);
        shortPath.add(node);

        List<Node> longPath = new ArrayList<Node>(3);
        longPath.add(node);
        longPath.add(node);
        longPath.add(node);

        Cycle shortCycle = new Cycle(shortPath);
        Cycle longCycle = new Cycle(longPath);

        Comparator<Cycle> comparator = new CycleComparator();

        assertTrue("2 < 3", comparator.compare(shortCycle, longCycle) < 0);
        assertTrue("2 == 2", comparator.compare(shortCycle, shortCycle) == 0);
        assertTrue("3 > 2", comparator.compare(longCycle, shortCycle) > 0);
    }

    public void testCompareBasedOnPath() {
        Node node1 = new PackageNode("node1", true);
        Node node2 = new PackageNode("node2", true);
        Node node3 = new PackageNode("node3", true);

        List<Node> path1 = new ArrayList<Node>(2);
        path1.add(node1);
        path1.add(node2);

        List<Node> path2 = new ArrayList<Node>(3);
        path2.add(node2);
        path2.add(node3);

        Cycle cycle1 = new Cycle(path1);
        Cycle cycle2 = new Cycle(path2);

        Comparator<Cycle> comparator = new CycleComparator();

        assertTrue("1 < 2", comparator.compare(cycle1, cycle2) < 0);
        assertTrue("1 == 1", comparator.compare(cycle1, cycle1) == 0);
        assertTrue("2 > 1", comparator.compare(cycle2, cycle1) > 0);
    }
}
