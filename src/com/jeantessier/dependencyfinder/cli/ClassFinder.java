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

import java.util.*;
import java.io.*;

import com.jeantessier.classreader.*;

public class ClassFinder extends DirectoryExplorerCommand {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addMultipleValuesSwitch("includes", DEFAULT_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("excludes");
        getCommandLine().addToggleSwitch("compact");
        getCommandLine().addSingleValueSwitch("indent-text", "    ");
    }

    public void doProcessing() throws Exception {
        ClassMatcher matcher = new ClassMatcher(getCommandLine().getMultipleSwitch("includes"), getCommandLine().getMultipleSwitch("excludes"));

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.addLoadListener(getVerboseListener());
        loader.load(getCommandLine().getParameters());

        for (Map.Entry<String, List<String>> entry : matcher.getResults().entrySet()) {
            String className = entry.getKey();
            List<String> groups = entry.getValue();

            if (getCommandLine().getToggleSwitch("compact")) {
                printCompact(className, groups);
            } else {
                printMultiline(className, groups);
            }
        }
    }

    private void printCompact(String className, List<String> groups) throws IOException {
        getOut().print(className);
        getOut().print(": ");

        Iterator i = groups.iterator();
        while (i.hasNext()) {
            getOut().print(i.next());
            if (i.hasNext()) {
                getOut().print(", ");
            }
        }

        getOut().println();
    }

    private void printMultiline(String className, List<String> groups) throws IOException {
        getOut().println(className);

        for (String group : groups) {
            getOut().println(getCommandLine().getSingleSwitch("indent-text") + group);
        }

        getOut().println();
    }

    public static void main(String[] args) throws Exception {
        new ClassFinder().run(args);
    }
}
