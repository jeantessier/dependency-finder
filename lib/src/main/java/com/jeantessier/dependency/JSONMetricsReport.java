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

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class JSONMetricsReport extends MetricsReport {
    public JSONMetricsReport(PrintWriter out) {
        super(out);
    }

    public void process(MetricsGatherer metrics) {
        print("{");

        printProgrammingElementStats(metrics);
        print(",");
        printDependencyStats(metrics);

        print("}");
        println();
    }

    private void printProgrammingElementStats(MetricsGatherer metrics) {
        printProgrammingElementStats("packages", metrics.getPackages());

        print(",");

        printProgrammingElementStats("classes", metrics.getClasses());

        print(",");

        printProgrammingElementStats("features", metrics.getFeatures());
    }

    private void printProgrammingElementStats(String label, Collection<? extends Node> nodes) {
        var nbElements = nodes.size();
        var nbConfirmedElements = countConfirmedNodes(nodes);
        var ratio = nbConfirmedElements / (double) nbElements;

        print("\"" + label + "\":{");
        print("\"count\":" + nbElements);
        print(",");
        print("\"confirmed\":" + nbConfirmedElements);
        print(",");
        print("\"ratio\":" + ratio);
        if (isListingElements()) {
            print(",");
            print("\"elements\": [");
            print(renderNodes(nodes));
            print("]");
        }
        print("}");
    }

    private String renderNodes(Collection<? extends Node> nodes) {
        return nodes.stream()
                .map(node -> "{\"name\":\"" + node.getName() + "\",\"simpleName\":\"" + node.getSimpleName() + "\",\"confirmed\":" + node.isConfirmed() + "}")
                .collect(joining(","));
    }

    private void printDependencyStats(MetricsGatherer metrics) {
        print("\"outbounds\": {");
        print("\"packages\":" + metrics.getNbOutboundPackages() + ",");
        print("\"packageRatio\":" + (metrics.getNbOutboundPackages() / (double) metrics.getPackages().size()) + ",");
        print("\"classes\":" + metrics.getNbOutboundClasses() + ",");
        print("\"classRatio\":" + (metrics.getNbOutboundClasses() / (double) metrics.getClasses().size()) + ",");
        print("\"features\":" + metrics.getNbOutboundFeatures() + ",");
        print("\"featureRation\":" + (metrics.getNbOutboundFeatures() / (double) metrics.getFeatures().size()) + ",");
        print("\"total\":" + metrics.getNbOutbound());
        print("}");

        print(",");

        print("\"inbounds\": {");
        print("\"packages\":" + metrics.getNbInboundPackages() + ",");
        print("\"packageRatio\":" + (metrics.getNbInboundPackages() / (double) metrics.getPackages().size()) + ",");
        print("\"classes\":" + metrics.getNbInboundClasses() + ",");
        print("\"classRatio\":" + (metrics.getNbInboundClasses() / (double) metrics.getClasses().size()) + ",");
        print("\"features\":" + metrics.getNbInboundFeatures() + ",");
        print("\"featureRatio\":" + (metrics.getNbInboundFeatures() / (double) metrics.getFeatures().size()) + ",");
        print("\"total\":" + metrics.getNbInbound());
        print("}");
    }
}
