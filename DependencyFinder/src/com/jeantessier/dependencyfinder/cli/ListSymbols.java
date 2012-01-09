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

import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.DefaultSymbolGathererStrategy;
import com.jeantessier.classreader.FilteringSymbolGathererStrategy;
import com.jeantessier.classreader.FinalMethodOrClassSymbolGathererStrategy;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.NonPrivateFieldSymbolGathererStrategy;
import com.jeantessier.classreader.SymbolGatherer;
import com.jeantessier.classreader.SymbolGathererStrategy;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.commandline.CommandLineException;

import java.util.Collection;

public class ListSymbols extends DirectoryExplorerCommand {
    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addToggleSwitch("class-names");
        getCommandLine().addToggleSwitch("field-names");
        getCommandLine().addToggleSwitch("method-names");
        getCommandLine().addToggleSwitch("local-names");

        getCommandLine().addToggleSwitch("non-private-field-names");
        getCommandLine().addToggleSwitch("final-method-or-class-names");

        getCommandLine().addMultipleValuesSwitch("includes", DEFAULT_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("includes-list");
        getCommandLine().addMultipleValuesSwitch("excludes");
        getCommandLine().addMultipleValuesSwitch("excludes-list");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        if (!getCommandLine().isPresent("class-names") && !getCommandLine().isPresent("field-names") && !getCommandLine().isPresent("method-names") && !getCommandLine().isPresent("local-names")) {
            getCommandLine().getSwitch("class-names").setValue(true);
            getCommandLine().getSwitch("field-names").setValue(true);
            getCommandLine().getSwitch("method-names").setValue(true);
            getCommandLine().getSwitch("local-names").setValue(true);
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        SymbolGathererStrategy gathererStrategy;

        if (getCommandLine().getToggleSwitch("non-private-field-names")) {
            gathererStrategy = new NonPrivateFieldSymbolGathererStrategy();
        } else if (getCommandLine().getToggleSwitch("final-method-or-class-names")) {
            gathererStrategy = new FinalMethodOrClassSymbolGathererStrategy();
        } else {
            DefaultSymbolGathererStrategy defaultGathererStrategy = new DefaultSymbolGathererStrategy();
            defaultGathererStrategy.setMatchingClassNames(getCommandLine().getToggleSwitch("class-names"));
            defaultGathererStrategy.setMatchingFieldNames(getCommandLine().getToggleSwitch("field-names"));
            defaultGathererStrategy.setMatchingMethodNames(getCommandLine().getToggleSwitch("method-names"));
            defaultGathererStrategy.setMatchingLocalNames(getCommandLine().getToggleSwitch("local-names"));

            gathererStrategy = defaultGathererStrategy;
        }

        if (getCommandLine().isPresent("includes") || getCommandLine().isPresent("includes-list") || getCommandLine().isPresent("excludes") || getCommandLine().isPresent("excludes-list")) {
            gathererStrategy = new FilteringSymbolGathererStrategy(gathererStrategy, getCommandLine().getMultipleSwitch("includes"), loadCollection(getCommandLine().getMultipleSwitch("includes-list")), getCommandLine().getMultipleSwitch("excludes"), loadCollection(getCommandLine().getMultipleSwitch("excludes-list")));
        }

        SymbolGatherer gatherer = new SymbolGatherer(gathererStrategy);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
        loader.addLoadListener(getVerboseListener());
        loader.load(getCommandLine().getParameters());

        for (String symbol : gatherer.getCollection()) {
            getOut().println(symbol);
        }
    }

    public static void main(String[] args) throws Exception {
        new ListSymbols().run(args);
    }
}
