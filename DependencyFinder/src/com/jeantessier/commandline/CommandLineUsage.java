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

import java.util.*;

import com.jeantessier.text.*;

/**
 *  Generates summary information about the command-line specification.
 */
public class CommandLineUsage extends Printer {
    private String commandName;
    private PrinterBuffer extraText = new PrinterBuffer();

    public CommandLineUsage(String commandName) {
        this.commandName = commandName;
        raiseIndent();
    }

    public String toString() {
        PrinterBuffer buffer = new PrinterBuffer();

        buffer.indent().append("USAGE:").eol();
        buffer.raiseIndent();
        buffer.indent().append(commandName).eol();
        buffer.lowerIndent();

        buffer.append(super.toString());

        if (extraText.length() > 0) {
            buffer.append(extraText);
        }

        return buffer.toString();
    }

    protected Set<String> getSwitchNames(CommandLine commandLine) {
        return commandLine.getKnownSwitches();
    }

    public void visitToggleSwitch(ToggleSwitch cls) {
        if (cls.isMandatory()) {
            indent().append("-").append(cls.getName()).append(" (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        } else {
            indent().append("[-").append(cls.getName()).append("] (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        }
    }

    public void visitSingleValueSwitch(SingleValueSwitch cls) {
        if (cls.isMandatory()) {
            indent().append("-").append(cls.getName()).append(" value (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        } else {
            indent().append("[-").append(cls.getName()).append(" value] (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        }
    }

    public void visitOptionalValueSwitch(OptionalValueSwitch cls) {
        if (cls.isMandatory()) {
            indent().append("-").append(cls.getName()).append(" [value] (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        } else {
            indent().append("[-").append(cls.getName()).append(" [value]] (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        }
    }

    public void visitMultipleValuesSwitch(MultipleValuesSwitch cls) {
        if (cls.isMandatory()) {
            indent().append("(-").append(cls.getName()).append(" value)+ (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        } else {
            indent().append("(-").append(cls.getName()).append(" value)* (defaults to ").append(cls.getDefaultValue()).append(")").eol();
        }
    }

    public void visitAliasSwitch(AliasSwitch cls) {
        indent().append("[-").append(cls.getName()).append("]").eol();

        extraText.eol();
        extraText.indent().append("-").append(cls.getName()).append(" is shorthand for the combination:").eol();

        extraText.raiseIndent();
        for (CommandLineSwitch commandLineSwitch : cls.getSwitches()) {
            extraText.indent().append("-").append(commandLineSwitch.getName()).eol();
        }
        extraText.lowerIndent();
    }

    public void visitNullParameterStrategy(NullParameterStrategy strategy) {
        // Do nothing
    }

    public void visitCollectingParameterStrategy(CollectingParameterStrategy strategy) {
        indent().append("[param ...]").eol();
    }

    public void visitAtLeastParameterStrategy(AtLeastParameterStrategy strategy) {
        for (int i=1; i<=strategy.getLimit(); i++) {
            indent().append("param").append(i).eol();
        }

        indent().append("...").eol();
    }

    public void visitExactlyParameterStrategy(ExactlyParameterStrategy strategy) {
        for (int i=1; i<=strategy.getLimit(); i++) {
            indent().append("param").append(i).eol();
        }
    }

    public void visitAtMostParameterStrategy(AtMostParameterStrategy strategy) {
        indent();

        for (int i=1; i<=strategy.getLimit(); i++) {
            append("[param").append(i);
            if (i < strategy.getLimit()) {
                append(" ");
            }
        }

        for (int i=1; i<=strategy.getLimit(); i++) {
            append("]");
        }

        eol();
    }
}
