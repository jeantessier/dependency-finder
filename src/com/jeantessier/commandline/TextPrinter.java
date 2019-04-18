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

public class TextPrinter extends Printer {
    public TextPrinter(String command) {
        append(command).eol();
    }

    protected Set<String> getSwitchNames(CommandLine commandLine) {
        return commandLine.getPresentSwitches();
    }

    public void visitToggleSwitch(ToggleSwitch cls) {
        indent().append("-").append(cls.getName()).eol();
    }

    public void visitSingleValueSwitch(SingleValueSwitch cls) {
        indent().append("-").append(cls.getName()).append(" ").append(cls.getValue()).eol();
    }

    public void visitOptionalValueSwitch(OptionalValueSwitch cls) {
        indent().append("-").append(cls.getName());
        if (!"".equals(cls.getValue())) {
            append(" ").append(cls.getValue());
        }
        eol();
    }

    public void visitMultipleValuesSwitch(MultipleValuesSwitch cls) {
        for (String value : (List<String>) cls.getValue()) {
            indent().append("-").append(cls.getName()).append(" ").append(value).eol();
        }
    }

    public void visitAliasSwitch(AliasSwitch cls) {
        // Do nothing
    }

    public void visitNullParameterStrategy(NullParameterStrategy strategy) {
        visitParameterStrategy(strategy);
    }

    public void visitCollectingParameterStrategy(CollectingParameterStrategy strategy) {
        visitParameterStrategy(strategy);
    }

    public void visitAtLeastParameterStrategy(AtLeastParameterStrategy strategy) {
        visitParameterStrategy(strategy);
    }

    public void visitExactlyParameterStrategy(ExactlyParameterStrategy strategy) {
        visitParameterStrategy(strategy);
    }

    public void visitAtMostParameterStrategy(AtMostParameterStrategy strategy) {
        visitParameterStrategy(strategy);
    }

    private void visitParameterStrategy(ParameterStrategy strategy) {
        for (String value : strategy.getParameters()) {
            indent().append(value).eol();
        }
    }
}
