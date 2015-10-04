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
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class OpenFileAction extends AbstractAction implements Runnable, DependencyListener {
    private DependencyFinder model;
    private File file;

    public OpenFileAction(DependencyFinder model) {
        this.model = model;

        putValue(Action.LONG_DESCRIPTION, "Load dependency graph from XML file");
        putValue(Action.NAME, "Open");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/openfile.gif")));
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(new XMLFileFilter());
        int returnValue = chooser.showOpenDialog(model);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            new Thread(this).start();
        }
    }

    public void run() {
        model.setNewDependencyGraph();
        model.addInputFile(file);

        try {
            Date start = new Date();

            String filename = file.getCanonicalPath();

            NodeLoader loader = new NodeLoader(model.getNodeFactory());
            loader.addDependencyListener(this);

            model.getProgressBar().setIndeterminate(true);

            model.getStatusLine().showInfo("Loading " + filename + " ...");
            ProgressMonitorInputStream in = new ProgressMonitorInputStream(model, "Reading " + filename, new FileInputStream(filename));
            in.getProgressMonitor().setMillisToDecideToPopup(0);
            loader.load(in);
            model.setTitle("Dependency Finder - " + filename);

            if (model.getMaximize()) {
                model.getStatusLine().showInfo("Maximizing ...");
                new LinkMaximizer().traverseNodes(model.getPackages());
            } else if (model.getMinimize()) {
                model.getStatusLine().showInfo("Minimizing ...");
                new LinkMinimizer().traverseNodes(model.getPackages());
            }

            Date stop = new Date();

            model.getStatusLine().showInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
        } catch (SAXException ex) {
            model.getStatusLine().showError("Cannot parse: " + ex.getClass().getName() + ": " + ex.getMessage());
        } catch (ParserConfigurationException ex) {
            model.getStatusLine().showError("Cannot parse: " + ex.getClass().getName() + ": " + ex.getMessage());
        } catch (IOException ex) {
            model.getStatusLine().showError("Cannot load: " + ex.getClass().getName() + ": " + ex.getMessage());
        } finally {
            model.getProgressBar().setIndeterminate(false);

            if (model.getPackages() == null) {
                model.setTitle("Dependency Finder");
            }
        }
    }

    public void beginSession(DependencyEvent event) {
        // Do nothing
    }

    public void beginClass(DependencyEvent event) {
        model.getStatusLine().showInfo("Loading dependencies for " + event.getClassName() + " ...");
    }

    public void dependency(DependencyEvent event) {
        // Do nothing
    }

    public void endClass(DependencyEvent event) {
        // Do nothing
    }

    public void endSession(DependencyEvent event) {
        // Do nothing
    }
}
