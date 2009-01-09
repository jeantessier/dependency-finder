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

package com.jeantessier.dependencyfinder.gui;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class RefreshDependencyGraphAction extends AbstractAction implements Runnable {
    private DependencyFinder model = null;

    public RefreshDependencyGraphAction(DependencyFinder model) {
        this.model = model;

        putValue(Action.LONG_DESCRIPTION, "Re-extract the current dependency graph");
        putValue(Action.NAME, "Refresh");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/refresh.gif")));
    }

    public void actionPerformed(ActionEvent e) {
        new Thread(this).start();
    }

    public void run() {
        Date start = new Date();

        model.clearDependencyResult();
        model.clearClosureResult();
        model.clearMetricsResult();

        model.getStatusLine().showInfo("Scanning ...");
        ClassfileScanner scanner = new ClassfileScanner();
        scanner.load(model.getInputFiles());

        model.getProgressBar().setMaximum(scanner.getNbFiles());

        model.getMonitor().setClosedSession(true);

        ClassfileLoader loader = new TransientClassfileLoader(model.getClassfileLoaderDispatcher());
        loader.addLoadListener(new VerboseListener(model.getStatusLine(), model.getProgressBar()));
        loader.addLoadListener(model.getMonitor());
        loader.load(model.getInputFiles());

        if (model.getMaximize()) {
            model.getStatusLine().showInfo("Maximizing ...");
            new LinkMaximizer().traverseNodes(model.getPackages());
        } else if (model.getMinimize()) {
            model.getStatusLine().showInfo("Minimizing ...");
            new LinkMinimizer().traverseNodes(model.getPackages());
        }

        Date stop = new Date();

        model.getStatusLine().showInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
    }
}
