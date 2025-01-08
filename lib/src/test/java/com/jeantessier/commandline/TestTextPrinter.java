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

package com.jeantessier.commandline;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestTextPrinter {
    private final Random random = new Random();
    private final String commandName = "command " + random.nextInt(1_000);
    private final CommandLine commandLine = new CommandLine(false);

    private final TextPrinter printer = new TextPrinter(commandName);

    @Test
    void testNoArgs() {
        // Given
        commandLine.addToggleSwitch("switch1");
        commandLine.addToggleSwitch("switch2");

        // and
        commandLine.parse(new String[0]);

        // and
        var expectedLines = Stream.of(
                commandName
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testPartialSwitches() {
        // Given
        commandLine.addToggleSwitch("switch1");
        commandLine.addToggleSwitch("switch2");

        // and
        commandLine.parse(new String[] {"-switch1"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch1"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testToggleSwitch() {
        // Given
        commandLine.addToggleSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testSingleValueSwitch() {
        // Given
        commandLine.addSingleValueSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch", "value"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch value"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testOptionalValueSwitchWithNoValue() {
        // Given
        commandLine.addOptionalValueSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testOptionalValueSwitchWithOneValue() {
        // Given
        commandLine.addOptionalValueSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch", "value"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch value"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testMultipleValuesSwitchWithOneValue() {
        // Given
        commandLine.addMultipleValuesSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch", "value"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch value"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testMultipleValuesSwitchWithMultipleValues() {
        // Given
        commandLine.addMultipleValuesSwitch("switch");

        // and
        commandLine.parse(new String[] {"-switch", "value1", "-switch", "value2"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch value1",
                "    -switch value2"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testUnknownSwitch() {
        // Given
        commandLine.parse(new String[] {"-switch"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testOneParameter() {
        // Given
        commandLine.parse(new String[] {"param"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    param"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testMultipleParameter() {
        // Given
        commandLine.parse(new String[] {"param1", "param2"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    param1",
                "    param2"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }

    @Test
    void testSingleValueSwitchAndParameter() {
        // Given
        commandLine.parse(new String[] {"-switch", "value", "param"});

        // and
        var expectedLines = Stream.of(
                commandName,
                "    -switch value",
                "    param"
        );

        // When
        commandLine.accept(printer);

        // Then
        assertLinesMatch(expectedLines, printer.toString().lines());
    }
}
