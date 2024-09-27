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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

public class TestTextPrinter {
    private static final Path REPORTS_DIR = Paths.get("build/resources/test");

    private final NodeFactory factory = new NodeFactory();
    private final StringWriter out = new StringWriter();
    private final TextPrinter visitor = new TextPrinter(new PrintWriter(out));

    private enum DependencyGraph {
        PACKAGE_TO_PACKAGE {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createPackage("outbound", confirmed).addDependency(factory.createPackage("inbound", confirmed));
                factory.createPackage("empty", confirmed);
            }
        },
        CLASS_TO_CLASS {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createClass("empty.Empty", confirmed);
            }
        },
        FEATURE_TO_FEATURE {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createFeature("empty.Empty.empty()", confirmed);
            }
        },
        MIXED {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createClass("empty.Empty", confirmed);
            }
        },
        ALL {
            void create(NodeFactory factory, boolean confirmed) {
                factory.createPackage("outbound", confirmed).addDependency(factory.createPackage("inbound", confirmed));
                factory.createClass("outbound.Outbound", confirmed).addDependency(factory.createClass("inbound.Inbound", confirmed));
                factory.createFeature("outbound.Outbound.outbound()", confirmed).addDependency(factory.createFeature("inbound.Inbound.inbound()", confirmed));
                factory.createPackage("empty", confirmed);
            }
        };

        abstract void create(NodeFactory factory, boolean confirmed);
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("inferred inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, true, false, true, "dependency.TestTextPrinter.package_to_package.inferred.show_inbounds_true.txt"),
                arguments("confirmed inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, true, false, true, "dependency.TestTextPrinter.package_to_package.confirmed.show_inbounds_true.txt"),
                arguments("inferred inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, false, true, "dependency.TestTextPrinter.package_to_package.inferred.show_inbounds_false.txt"),
                arguments("confirmed inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, false, true, "dependency.TestTextPrinter.package_to_package.confirmed.show_inbounds_false.txt"),
                arguments("inferred outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, true, true, "dependency.TestTextPrinter.package_to_package.inferred.show_outbounds_true.txt"),
                arguments("confirmed outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, true, true, "dependency.TestTextPrinter.package_to_package.confirmed.show_outbounds_true.txt"),
                arguments("inferred outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, false, true, "dependency.TestTextPrinter.package_to_package.inferred.show_outbounds_false.txt"),
                arguments("confirmed outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, false, true, "dependency.TestTextPrinter.package_to_package.confirmed.show_outbounds_false.txt"),

                arguments("inferred inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, true, false, true, "dependency.TestTextPrinter.class_to_class.inferred.show_inbounds_true.txt"),
                arguments("confirmed inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, true, false, true, "dependency.TestTextPrinter.class_to_class.confirmed.show_inbounds_true.txt"),
                arguments("inferred inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, false, true, "dependency.TestTextPrinter.class_to_class.inferred.show_inbounds_false.txt"),
                arguments("confirmed inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, false, true, "dependency.TestTextPrinter.class_to_class.confirmed.show_inbounds_false.txt"),
                arguments("inferred outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, true, true, "dependency.TestTextPrinter.class_to_class.inferred.show_outbounds_true.txt"),
                arguments("confirmed outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, true, true, "dependency.TestTextPrinter.class_to_class.confirmed.show_outbounds_true.txt"),
                arguments("inferred outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, false, true, "dependency.TestTextPrinter.class_to_class.inferred.show_outbounds_false.txt"),
                arguments("confirmed outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, false, true, "dependency.TestTextPrinter.class_to_class.confirmed.show_outbounds_false.txt"),

                arguments("inferred inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, false, true, "dependency.TestTextPrinter.feature_to_feature.inferred.show_inbounds_true.txt"),
                arguments("confirmed inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, false, true, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_inbounds_true.txt"),
                arguments("inferred inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, false, true, "dependency.TestTextPrinter.feature_to_feature.inferred.show_inbounds_false.txt"),
                arguments("confirmed inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, false, true, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_inbounds_false.txt"),
                arguments("inferred outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, true, true, "dependency.TestTextPrinter.feature_to_feature.inferred.show_outbounds_true.txt"),
                arguments("confirmed outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, true, true, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_outbounds_true.txt"),
                arguments("inferred outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, false, true, "dependency.TestTextPrinter.feature_to_feature.inferred.show_outbounds_false.txt"),
                arguments("confirmed outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, false, true, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_outbounds_false.txt"),

                arguments("inferred feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, true, true, "dependency.TestTextPrinter.feature_to_feature.inferred.show_empty_nodes_true.txt"),
                arguments("confirmed feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, true, true, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_empty_nodes_true.txt"),
                arguments("inferred feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, true, false, "dependency.TestTextPrinter.feature_to_feature.inferred.show_empty_nodes_false.txt"),
                arguments("confirmed feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, true, false, "dependency.TestTextPrinter.feature_to_feature.confirmed.show_empty_nodes_false.txt"),

                arguments("inferred mixed", DependencyGraph.MIXED, false, true, true, true, "dependency.TestTextPrinter.mixed.inferred.show_empty_nodes_true.txt"),
                arguments("confirmed mixed", DependencyGraph.MIXED, true, true, true, true, "dependency.TestTextPrinter.mixed.confirmed.show_empty_nodes_true.txt"),
                arguments("inferred mixed", DependencyGraph.MIXED, false, true, true, false, "dependency.TestTextPrinter.mixed.inferred.show_empty_nodes_false.txt"),
                arguments("confirmed mixed", DependencyGraph.MIXED, true, true, true, false, "dependency.TestTextPrinter.mixed.confirmed.show_empty_nodes_false.txt"),

                arguments("inferred all", DependencyGraph.ALL, false, true, true, true, "dependency.TestTextPrinter.all.inferred.show_empty_nodes_true.txt"),
                arguments("confirmed all", DependencyGraph.ALL, true, true, true, true, "dependency.TestTextPrinter.all.confirmed.show_empty_nodes_true.txt"),
                arguments("inferred all", DependencyGraph.ALL, false, true, true, false, "dependency.TestTextPrinter.all.inferred.show_empty_nodes_false.txt"),
                arguments("confirmed all", DependencyGraph.ALL, true, true, true, false, "dependency.TestTextPrinter.all.confirmed.show_empty_nodes_false.txt")
        );
    }

    @DisplayName("dependencies as text")
    @ParameterizedTest(name = "when the input is {0} should be ''{6}''")
    @MethodSource("dataProvider")
    void generateReportAndCompareToFile(String variation, DependencyGraph dependencyGraph, boolean confirmed, boolean showInbounds, boolean showOutbounds, boolean showEmptyNodes, String expectedOutput) throws IOException {
        // Given
        dependencyGraph.create(factory, confirmed);

        // And
        visitor.setShowInbounds(showInbounds);
        visitor.setShowOutbounds(showOutbounds);
        visitor.setShowEmptyNodes(showEmptyNodes);

        // When
        visitor.traverseNodes(factory.getPackages().values());

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput));
        var actualReport = out.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    void hideInferredMarkWhenRenderingInferredNodes() throws IOException {
        // Given a dependency graph with inferred nodes
        DependencyGraph.ALL.create(factory, false);

        // And a printer that does not distinguish inferred nodes
        visitor.setShowInferred(false);

        // When
        visitor.traverseNodes(factory.getPackages().values());

        // Then the report should be identical to one for confirmed nodes
        var expectedReport = Files.readString(REPORTS_DIR.resolve("dependency.TestTextPrinter.all.confirmed.show_empty_nodes_true.txt"));
        var actualReport = out.toString();
        assertEquals(expectedReport, actualReport);
    }
}
