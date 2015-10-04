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

package com.jeantessier.dependencyfinder.cli;

import com.jeantessier.dependency.Printer;
import com.jeantessier.dependency.TextPrinter;
import com.jeantessier.dependency.*;

public class DependencyClosure extends DependencyGraphCommand {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(XMLPrinter.DEFAULT_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX, XMLPrinter.DEFAULT_INDENT_TEXT);

        populateCommandLineSwitchesForStartCondition();
        populateCommandLineSwitchesForStopCondition();

        getCommandLine().addOptionalValueSwitch("maximum-inbound-depth");
        getCommandLine().addOptionalValueSwitch("maximum-outbound-depth");

        getCommandLine().addToggleSwitch("xml");
    }

    protected void doProcessing() throws Exception {
        TransitiveClosure selector = new TransitiveClosure(getStartCriteria(), getStopCriteria());

        try {
            if (getCommandLine().isPresent("maximum-inbound-depth")) {
                selector.setMaximumInboundDepth(Long.parseLong(getCommandLine().getSingleSwitch("maximum-inbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        try {
            if (getCommandLine().isPresent("maximum-outbound-depth")) {
                selector.setMaximumOutboundDepth(Long.parseLong(getCommandLine().getSingleSwitch("maximum-outbound-depth")));
            }
        } catch (NumberFormatException ex) {
            selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        selector.traverseNodes(loadGraph().getPackages().values());

        getVerboseListener().print("Printing the graph ...");

        Printer printer;
        if (getCommandLine().isPresent("xml")) {
            printer = new XMLPrinter(getOut(), getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        } else {
            printer = new TextPrinter(getOut());
        }

        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        printer.traverseNodes(selector.getFactory().getPackages().values());
    }

    public static void main(String[] args) throws Exception {
        new DependencyClosure().run(args);
    }
}
