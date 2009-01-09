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
import java.io.*;

import fitlibrary.*;
import fit.*;

public class CycleDetectorFixture extends NodeFactoryFixture {
    public void detectCycles() {
        doDetectCycles(new CycleDetector());
    }

    public void detectCyclesScopeIncludes(String scopeIncludes) {
        doDetectCycles(new CycleDetector(new RegularExpressionSelectionCriteria(scopeIncludes)));
    }

    public void detectCyclesScopeIncludesList(String scopeIncludesList) {
        Collection<String> includes = new ArrayList<String>();
        includes.add(scopeIncludesList);
        Collection<String> excludes = new ArrayList<String>();

        doDetectCycles(new CycleDetector(new CollectionSelectionCriteria(includes, excludes)));
    }

    public void detectClassToClassCyclesScopeIncludes(String scopeIncludes) {
        doReduceGraphToClasses();
        doDetectCycles(new CycleDetector(new RegularExpressionSelectionCriteria(scopeIncludes)));
    }

    private void doDetectCycles(Visitor visitor) {
        visitor.traverseNodes(((NodeFactory) getSystemUnderTest()).getPackages().values());
        setSystemUnderTest(visitor);
    }

    private void doReduceGraphToClasses() {
        RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria("//");
        scopeCriteria.setMatchingPackages(false);
        scopeCriteria.setMatchingClasses(true);
        scopeCriteria.setMatchingFeatures(false);
        RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria("//");
        filterCriteria.setMatchingPackages(false);
        filterCriteria.setMatchingClasses(true);
        filterCriteria.setMatchingFeatures(false);
        GraphSummarizer summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
        summarizer.traverseNodes(((NodeFactory) getSystemUnderTest()).getPackages().values());
        setSystemUnderTest(summarizer.getScopeFactory());
    }

    public Fixture pathForCycle(int pos) {
        return new ArrayFixture(getCycle(pos).getPath());
    }

    public Fixture textForCycle(int pos) throws IOException {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);
        CyclePrinter printer = new TextCyclePrinter(out);
        printer.visitCycle(getCycle(pos));
        out.close();

        List<Line> lines = new ArrayList<Line>();

        BufferedReader in = new BufferedReader(new StringReader(buffer.toString()));
        String line;
        while ((line = in.readLine()) != null) {
            lines.add(new Line(line.trim()));
        }
        in.close();

        return new ArrayFixture(lines);
    }

    private Cycle getCycle(int pos) {
        CycleDetector detector = (CycleDetector) getSystemUnderTest();
        ArrayList<Cycle> cycles = new ArrayList<Cycle>(detector.getCycles());
        return cycles.get(pos);
    }

    public static class Line {
        public String line;
        public Line(String line) {
            this.line = line;
        }
    }
}
