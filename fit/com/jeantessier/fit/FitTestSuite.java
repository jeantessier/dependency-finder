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

package com.jeantessier.fit;

import java.io.*;

import org.apache.oro.io.*;

import junit.framework.*;

public class FitTestSuite extends TestSuite {
    protected static final String SOURCE_PATHNAME = "fit" + File.separator + "tests";
    protected static final String OUTPUT_PATHNAME = "reports" + File.separator + "fit";

    private static final File SOURCE_DIR = new File(SOURCE_PATHNAME);
    private static final File OUTPUT_DIR = new File(OUTPUT_PATHNAME);

    public FitTestSuite(String path) {
        super(path);

        File inDir = new File(SOURCE_DIR, path);
        File outDir = new File(OUTPUT_DIR, path);
        outDir.mkdirs();

        for (String filename : inDir.list(new Perl5FilenameFilter(".*\\.html"))) {
            addTest(new FitTest(filename, inDir, outDir));
        }
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite(SOURCE_PATHNAME);

        suite.addTest(new FitTestSuite("classreader"));
        suite.addTest(new FitTestSuite("classreader" + File.separator + "impl"));
        suite.addTest(new FitTestSuite("dependency"));
        suite.addTest(new FitTestSuite("dependency" + File.separator + "closure"));
        suite.addTest(new FitTestSuite("dependency" + File.separator + "cycles"));
        suite.addTest(new FitTestSuite("dependencyfinder" + File.separator + "cli"));
        suite.addTest(new FitTestSuite("metrics"));

        return suite;
    }
}
