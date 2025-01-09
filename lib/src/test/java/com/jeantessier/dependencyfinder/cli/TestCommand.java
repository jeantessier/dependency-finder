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

package com.jeantessier.dependencyfinder.cli;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestCommand {
    private File outFile;
    private String[] args;

    @BeforeEach
    void setUp() throws Exception {
        var random = new Random();
        var tempName = "temp" + random.nextInt(1_000);

        outFile = File.createTempFile(tempName, "");
        outFile.deleteOnExit();
        assertTrue(outFile.delete(), "Temp file " + outFile + " could not be deleted");

        args = new String[] {
                "-out",
                outFile.getAbsolutePath(),
        };
    }

    @Test
    void testOutNotCreatedIfNotAccessed() throws Exception {
        Command sut = new Command() {
            protected void doProcessing() throws Exception {
                // Do Nothing
            }

            protected void showSpecificUsage(PrintStream out) {
                // Do Nothing
            }
        };

        assertFalse(outFile.exists(), "Output file " + outFile + " exists before test");
        sut.run(args);
        assertFalse(outFile.exists(), "Output file " + outFile + " exists after test");
    }

    @Test
    void testOutCreatedAfterAccess() throws Exception {
        Command sut = new Command() {
            protected void doProcessing() throws Exception {
                getOut();
            }

            protected void showSpecificUsage(PrintStream out) {
                // Do Nothing
            }
        };

        assertFalse(outFile.exists(), "Output file " + outFile + " exists before test");
        sut.run(args);
        assertTrue(outFile.exists(), "Output file " + outFile + " does not exist after test");
    }
}
