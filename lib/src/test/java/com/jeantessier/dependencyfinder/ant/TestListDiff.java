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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;

import org.apache.tools.ant.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestListDiff {
    private File existingOldFile;
    private File existingNewFile;
    private File existingDir;
    private File nonExistingFile;

    private final ListDiff sut = new ListDiff();

    @BeforeEach
    void setUp() throws IOException {
        existingOldFile = File.createTempFile("test", "old");
        existingOldFile.deleteOnExit();
        existingNewFile = File.createTempFile("test", "new");
        existingNewFile.deleteOnExit();
        existingDir = existingOldFile.getParentFile();
        nonExistingFile = new File("DoesNotExist");
    }

    @Test
    void testAllMandatoryParameters() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
    }

    @Test
    void testOldNotSet() {
        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("old must be set!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testOldDoesNotExist() {
        sut.setOld(nonExistingFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("old does not exist!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testOldNotAFile() {
        sut.setOld(existingDir);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("old is not a file!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testOldLabel() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        String expectedOldLabel = "old label";
        sut.setOldlabel(expectedOldLabel);

        sut.validateParameters();
        assertEquals(expectedOldLabel, sut.getOldlabel());
    }

    @Test
    void testOldLabelNotSet() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
        assertEquals(sut.getOld().getPath(), sut.getOldlabel(), "default old label");
    }

    @Test
    void testNewNotSet() {
        sut.setOld(existingOldFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("new must be set!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testNewDoesNotExist() {
        sut.setOld(existingOldFile);
        sut.setNew(nonExistingFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("new does not exist!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testNewNotAFile() {
        sut.setOld(existingOldFile);
        sut.setNew(existingDir);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("new is not a file!", ex.getMessage(), "Wrong message");
        }
    }

    @Test
    void testNewLabel() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        String expectedNewLabel = "new label";
        sut.setNewlabel(expectedNewLabel);

        sut.validateParameters();
        assertEquals(expectedNewLabel, sut.getNewlabel());
    }

    @Test
    void testNewLabelNotSet() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
        assertEquals(sut.getNew().getPath(), sut.getNewlabel(), "default new label");
    }

    @Test
    void testMissingDestfile() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);

        try {
            sut.validateParameters();
            fail("executed without destfile being set");
        } catch (BuildException ex) {
            assertEquals("destfile must be set!", ex.getMessage(), "Wrong message");
        }
    }
}
