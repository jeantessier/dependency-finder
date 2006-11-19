/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

public class TestTextPrinter extends TestCase {
    public void testNoArgs() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addToggleSwitch("switch1");
        commandLine.addToggleSwitch("switch2");

        commandLine.parse(new String[0]);

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testPartialSwitches() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addToggleSwitch("switch1");
        commandLine.addToggleSwitch("switch2");

        commandLine.parse(new String[] {"-switch1"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testToggleSwitch() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addToggleSwitch("switch1");

        commandLine.parse(new String[] {"-switch1"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testSingleValueSwitch() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addSingleValueSwitch("switch1");

        commandLine.parse(new String[] {"-switch1", "value"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1 value", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testOptionalValueSwitchWithNoValue() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addOptionalValueSwitch("switch1");

        commandLine.parse(new String[] {"-switch1"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testOptionalValueSwitchWithOneValue() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addOptionalValueSwitch("switch1");

        commandLine.parse(new String[] {"-switch1", "value"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1 value", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testMultipleValuesSwitchWithOneValue() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addMultipleValuesSwitch("switch1");

        commandLine.parse(new String[] {"-switch1", "value"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1 value", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testMultipleValuesSwitchWithMultipleValues() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine();
        commandLine.addMultipleValuesSwitch("switch1");

        commandLine.parse(new String[] {"-switch1", "value1", "-switch1", "value2"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1 value1", in.readLine());
        assertEquals("line " + i++, "    -switch1 value2", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }

    public void testUnknownSwitch() throws CommandLineException, IOException {
        CommandLine commandLine = new CommandLine(false);

        commandLine.parse(new String[] {"-switch1"});

        Printer printer = new TextPrinter(getName());
        commandLine.accept(printer);

        BufferedReader in = new BufferedReader(new StringReader(printer.toString()));
        int i = 1;
        assertEquals("line " + i++, getName(), in.readLine());
        assertEquals("line " + i++, "    -switch1", in.readLine());
        assertEquals("line " + i++, null, in.readLine());
    }
}
