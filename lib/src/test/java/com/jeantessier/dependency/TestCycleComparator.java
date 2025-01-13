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

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCycleComparator {
    @Test
    void testCompareBasedOnLength() {
        var node = new PackageNode("node", true);

        var shortPath = List.<Node>of(node, node);
        Cycle shortCycle = new Cycle(shortPath);

        var longPath = List.<Node>of(node, node, node);
        Cycle longCycle = new Cycle(longPath);

        var comparator = new CycleComparator();

        assertTrue(comparator.compare(shortCycle, longCycle) < 0, "2 < 3");
        assertTrue(comparator.compare(shortCycle, shortCycle) == 0, "2 == 2");
        assertTrue(comparator.compare(longCycle, shortCycle) > 0, "3 > 2");
    }

    @Test
    void testCompareBasedOnPath() {
        var node1 = new PackageNode("node1", true);
        var node2 = new PackageNode("node2", true);
        var node3 = new PackageNode("node3", true);

        var path1 = List.<Node>of(node1, node2);
        Cycle cycle1 = new Cycle(path1);

        var path2 = List.<Node>of(node2, node3);
        Cycle cycle2 = new Cycle(path2);

        var comparator = new CycleComparator();

        assertTrue(comparator.compare(cycle1, cycle2) < 0, "1 < 2");
        assertTrue(comparator.compare(cycle1, cycle1) == 0, "1 == 1");
        assertTrue(comparator.compare(cycle2, cycle1) > 0, "2 > 1");
    }
}
