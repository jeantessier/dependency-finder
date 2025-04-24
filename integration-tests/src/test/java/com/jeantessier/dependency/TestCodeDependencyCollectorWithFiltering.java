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

import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCodeDependencyCollectorWithFiltering {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();

    private final NodeFactory factory = new NodeFactory();

    @BeforeEach
    void setUp() {
        var filterCriteria = new RegularExpressionSelectionCriteria("//");
        filterCriteria.setGlobalExcludes(List.of("/^java.util/", "/^java.lang/"));

        var loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(new CodeDependencyCollector(factory, filterCriteria)));
        loader.load(Collections.singleton(TEST_FILENAME));
    }

    @Test
    void testPackages() {
        assertNodes(
                Map.of(
                        "", true,
                        "java.io", false
                ),
                factory.getPackages()
        );
    }

    @Test
    void testClasses() {
        assertNodes(
                Map.of(
                        "test", true,
                        "java.io.PrintStream", false
                ),
                factory.getClasses()
        );
    }

    @Test
    void testFeatures() {
        assertNodes(
                Map.of(
                        "test.main(java.lang.String[]): void", true,
                        "test.test()", true,
                        "java.io.PrintStream.println(java.lang.Object): void", false
                ),
                factory.getFeatures()
        );
    }

    private void assertNodes(Map<String, Boolean> expectations, Map<String, ? extends Node> nodes) {
        assertEquals(expectations.size(), nodes.size(), "nb nodes");

        expectations.forEach((name, expectedConfirmed) -> {
            var node = nodes.get(name);
            assertNotNull(node, name + " missing from " + nodes.keySet());
            assertEquals(expectedConfirmed, node.isConfirmed(), name + " concrete");
        });
    }
}
