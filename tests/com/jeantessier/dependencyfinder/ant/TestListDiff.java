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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;

import junit.framework.*;

import org.apache.tools.ant.*;

public class TestListDiff extends TestCase {
    private File existingOldFile;
    private File existingNewFile;
    private File existingDir;
    private File nonExistingFile;

    private ListDiff sut;

    protected void setUp() throws Exception {
        super.setUp();

        existingOldFile = File.createTempFile(getName(), "old");
        existingOldFile.deleteOnExit();
        existingNewFile = File.createTempFile(getName(), "new");
        existingNewFile.deleteOnExit();
        existingDir = existingOldFile.getParentFile();
        nonExistingFile = new File("DoesNotExist");

        sut = new ListDiff();
    }

    public void testAllMandatoryParameters() throws Exception {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
    }

    public void testOldNotSet() {
        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "old must be set!", ex.getMessage());
        }
    }

    public void testOldDoesNotExist() {
        sut.setOld(nonExistingFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "old does not exist!", ex.getMessage());
        }
    }

    public void testOldNotAFile() {
        sut.setOld(existingDir);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "old is not a file!", ex.getMessage());
        }
    }

    public void testOldLabel() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        String expectedOldLabel = "old label";
        sut.setOldlabel(expectedOldLabel);

        sut.validateParameters();
        assertEquals(expectedOldLabel, sut.getOldlabel());
    }

    public void testOldLabelNotSet() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
        assertEquals("default old label", sut.getOld().getPath(), sut.getOldlabel());
    }

    public void testNewNotSet() {
        sut.setOld(existingOldFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "new must be set!", ex.getMessage());
        }
    }

    public void testNewDoesNotExist() {
        sut.setOld(existingOldFile);
        sut.setNew(nonExistingFile);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "new does not exist!", ex.getMessage());
        }
    }

    public void testNewNotAFile() {
        sut.setOld(existingOldFile);
        sut.setNew(existingDir);

        try {
            sut.validateParameters();
            fail("executed without old being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "new is not a file!", ex.getMessage());
        }
    }

    public void testNewLabel() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        String expectedNewLabel = "new label";
        sut.setNewlabel(expectedNewLabel);

        sut.validateParameters();
        assertEquals(expectedNewLabel, sut.getNewlabel());
    }

    public void testNewLabelNotSet() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);
        sut.setDestfile(nonExistingFile);

        sut.validateParameters();
        assertEquals("default new label", sut.getNew().getPath(), sut.getNewlabel());
    }

    public void testMissingDestfile() {
        sut.setOld(existingOldFile);
        sut.setNew(existingNewFile);

        try {
            sut.validateParameters();
            fail("executed without destfile being set");
        } catch (BuildException ex) {
            assertEquals("Wrong message", "destfile must be set!", ex.getMessage());
        }
    }
}
