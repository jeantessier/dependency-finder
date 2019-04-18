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

package com.jeantessier.commandline;

import java.io.*;

import junit.framework.*;

public class TestCommandLineUsage extends TestCase {
    public void testToggleSwitch() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine(new NullParameterStrategy());
        commandLine.addToggleSwitch("switch");

        CommandLineUsage commandLineUsage = new CommandLineUsage(getName());
        commandLine.accept(commandLineUsage);

        BufferedReader in = new BufferedReader(new StringReader(commandLineUsage.toString()));
        int i = 1;
        assertEquals("line " + i++, "USAGE:", in.readLine());
        assertEquals("line " + i++, "    " + getName(), in.readLine());
        assertEquals("line " + i++, "        [-switch] (defaults to false)", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testAliasSwitch() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine(new NullParameterStrategy());
        commandLine.addToggleSwitch("toggle1");
        commandLine.addToggleSwitch("toggle2");
        commandLine.addAliasSwitch("alias", "toggle1", "toggle2");

        CommandLineUsage commandLineUsage = new CommandLineUsage(getName());
        commandLine.accept(commandLineUsage);

        BufferedReader in = new BufferedReader(new StringReader(commandLineUsage.toString()));
        int i = 1;
        assertEquals("line " + i++, "USAGE:", in.readLine());
        assertEquals("line " + i++, "    " + getName(), in.readLine());
        assertEquals("line " + i++, "        [-alias]", in.readLine());
        assertEquals("line " + i++, "        [-toggle1] (defaults to false)", in.readLine());
        assertEquals("line " + i++, "        [-toggle2] (defaults to false)", in.readLine());
        assertEquals("line " + i++, "", in.readLine());
        assertEquals("line " + i++, "-alias is shorthand for the combination:", in.readLine());
        assertEquals("line " + i++, "    -toggle1", in.readLine());
        assertEquals("line " + i++, "    -toggle2", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }
}
