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

package com.jeantessier.dependencyfinder.cli;

import org.apache.logging.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.diff.*;

public class ClassClassDiff extends DiffCommand {
    public void doProcessing() throws Exception {
        // Collecting data, first classfiles from JARs,
        // then package/class trees using NodeFactory.

        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(getVerboseListener());
        oldJar.load(getCommandLine().getMultipleSwitch("old"));

        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(getVerboseListener());
        newJar.load(getCommandLine().getMultipleSwitch("new"));

        // Starting to compare, first at class level,
        // then descending to feature level.

        LogManager.getLogger(getClass()).info("Comparing ...");
        getVerboseListener().print("Comparing ...");

        String name = getCommandLine().getSingleSwitch("name");
        String oldLabel = getCommandLine().getSingleSwitch("old-label");
        String newLabel = getCommandLine().getSingleSwitch("new-label");

        Classfile oldClass = oldJar.getAllClassfiles().iterator().next();
        Classfile newClass = newJar.getAllClassfiles().iterator().next();

        Differences differences = getDifferencesFactory().createClassDifferences(name, oldClass, newClass);

        LogManager.getLogger(getClass()).info("Printing results ...");
        getVerboseListener().print("Printing results ...");

        Report report = new Report(getCommandLine().getSingleSwitch("indent-text"), getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        report.setName(name);
        report.setOldVersion(oldLabel);
        report.setNewVersion(newLabel);

        differences.accept(report);
        getOut().print(report.render());
    }

    public static void main(String[] args) throws Exception {
        new ClassClassDiff().run(args);
    }
}
