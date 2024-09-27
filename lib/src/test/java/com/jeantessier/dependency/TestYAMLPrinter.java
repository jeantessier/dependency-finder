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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestYAMLPrinter extends TestPrinterBase {
    private final YAMLPrinter visitor = new YAMLPrinter(new PrintWriter(out));
    
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("inferred inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, true, false, true, "dependency.TestYAMLPrinter.package_to_package.inferred.show_inbounds_true.yml"),
                arguments("confirmed inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, true, false, true, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_inbounds_true.yml"),
                arguments("inferred inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, false, true, "dependency.TestYAMLPrinter.package_to_package.inferred.show_inbounds_false.yml"),
                arguments("confirmed inbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, false, true, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_inbounds_false.yml"),
                arguments("inferred outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, true, true, "dependency.TestYAMLPrinter.package_to_package.inferred.show_outbounds_true.yml"),
                arguments("confirmed outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, true, true, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_outbounds_true.yml"),
                arguments("inferred outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, false, false, true, "dependency.TestYAMLPrinter.package_to_package.inferred.show_outbounds_false.yml"),
                arguments("confirmed outbound package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, false, false, true, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_outbounds_false.yml"),

                arguments("inferred package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, true, true, true, "dependency.TestYAMLPrinter.package_to_package.inferred.show_empty_nodes_true.yml"),
                arguments("confirmed package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, true, true, true, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_empty_nodes_true.yml"),
                arguments("inferred package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, false, true, true, false, "dependency.TestYAMLPrinter.package_to_package.inferred.show_empty_nodes_false.yml"),
                arguments("confirmed package-to-package", DependencyGraph.PACKAGE_TO_PACKAGE, true, true, true, false, "dependency.TestYAMLPrinter.package_to_package.confirmed.show_empty_nodes_false.yml"),

                arguments("inferred inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, true, false, true, "dependency.TestYAMLPrinter.class_to_class.inferred.show_inbounds_true.yml"),
                arguments("confirmed inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, true, false, true, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_inbounds_true.yml"),
                arguments("inferred inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, false, true, "dependency.TestYAMLPrinter.class_to_class.inferred.show_inbounds_false.yml"),
                arguments("confirmed inbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, false, true, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_inbounds_false.yml"),
                arguments("inferred outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, true, true, "dependency.TestYAMLPrinter.class_to_class.inferred.show_outbounds_true.yml"),
                arguments("confirmed outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, true, true, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_outbounds_true.yml"),
                arguments("inferred outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, false, false, false, true, "dependency.TestYAMLPrinter.class_to_class.inferred.show_outbounds_false.yml"),
                arguments("confirmed outbound class-to-class", DependencyGraph.CLASS_TO_CLASS, true, false, false, true, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_outbounds_false.yml"),

                arguments("inferred class-to-class", DependencyGraph.CLASS_TO_CLASS, false, true, true, true, "dependency.TestYAMLPrinter.class_to_class.inferred.show_empty_nodes_true.yml"),
                arguments("confirmed class-to-class", DependencyGraph.CLASS_TO_CLASS, true, true, true, true, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_empty_nodes_true.yml"),
                arguments("inferred class-to-class", DependencyGraph.CLASS_TO_CLASS, false, true, true, false, "dependency.TestYAMLPrinter.class_to_class.inferred.show_empty_nodes_false.yml"),
                arguments("confirmed class-to-class", DependencyGraph.CLASS_TO_CLASS, true, true, true, false, "dependency.TestYAMLPrinter.class_to_class.confirmed.show_empty_nodes_false.yml"),

                arguments("inferred inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, false, true, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_inbounds_true.yml"),
                arguments("confirmed inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, false, true, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_inbounds_true.yml"),
                arguments("inferred inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, false, true, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_inbounds_false.yml"),
                arguments("confirmed inbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, false, true, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_inbounds_false.yml"),
                arguments("inferred outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, true, true, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_outbounds_true.yml"),
                arguments("confirmed outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, true, true, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_outbounds_true.yml"),
                arguments("inferred outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, false, false, true, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_outbounds_false.yml"),
                arguments("confirmed outbound feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, false, false, true, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_outbounds_false.yml"),

                arguments("inferred feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, true, true, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_empty_nodes_true.yml"),
                arguments("confirmed feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, true, true, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_empty_nodes_true.yml"),
                arguments("inferred feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, false, true, true, false, "dependency.TestYAMLPrinter.feature_to_feature.inferred.show_empty_nodes_false.yml"),
                arguments("confirmed feature-to-feature", DependencyGraph.FEATURE_TO_FEATURE, true, true, true, false, "dependency.TestYAMLPrinter.feature_to_feature.confirmed.show_empty_nodes_false.yml"),

                arguments("inferred mixed", DependencyGraph.MIXED, false, true, true, true, "dependency.TestYAMLPrinter.mixed.inferred.show_empty_nodes_true.yml"),
                arguments("confirmed mixed", DependencyGraph.MIXED, true, true, true, true, "dependency.TestYAMLPrinter.mixed.confirmed.show_empty_nodes_true.yml"),
                arguments("inferred mixed", DependencyGraph.MIXED, false, true, true, false, "dependency.TestYAMLPrinter.mixed.inferred.show_empty_nodes_false.yml"),
                arguments("confirmed mixed", DependencyGraph.MIXED, true, true, true, false, "dependency.TestYAMLPrinter.mixed.confirmed.show_empty_nodes_false.yml"),

                arguments("inferred all", DependencyGraph.ALL, false, true, true, true, "dependency.TestYAMLPrinter.all.inferred.show_empty_nodes_true.yml"),
                arguments("confirmed all", DependencyGraph.ALL, true, true, true, true, "dependency.TestYAMLPrinter.all.confirmed.show_empty_nodes_true.yml"),
                arguments("inferred all", DependencyGraph.ALL, false, true, true, false, "dependency.TestYAMLPrinter.all.inferred.show_empty_nodes_false.yml"),
                arguments("confirmed all", DependencyGraph.ALL, true, true, true, false, "dependency.TestYAMLPrinter.all.confirmed.show_empty_nodes_false.yml")
        );
    }

    @DisplayName("dependencies as YAML")
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
        assertEquals(expectedReport, actualReport, expectedOutput);
    }
}
