/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class OpenFileAction extends AbstractAction implements Runnable, DependencyListener {
	private DependencyFinder model;
	private File             file;
	
	public OpenFileAction(DependencyFinder model) {
		this.model = model;

		putValue(Action.LONG_DESCRIPTION, "Open file");
		putValue(Action.NAME, "Open");
		putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/openfile.gif")));
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(model.InputFile());
		chooser.addChoosableFileFilter(new XMLFileFilter());
		int returnValue = chooser.showOpenDialog(model);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			new Thread(this).start();
		}
	}

	public void run() {
	    model.InputFile(file);
		model.NewDependencyGraph();
		
		try {
			Date start = new Date();

			String filename = model.InputFile().getCanonicalPath();

			NodeLoader loader = new NodeLoader();
			loader.addDependencyListener(this);
			
			model.StatusLine().ShowInfo("Loading " + filename + " ...");
			model.Packages(loader.Load(filename).values());
			model.setTitle("Dependency Finder - " + filename);
	    
			model.StatusLine().ShowInfo("Maximizing ...");
			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.TraverseNodes(model.Packages());

			Date stop = new Date();

			model.StatusLine().ShowInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
		} catch (SAXException ex) {
			model.StatusLine().ShowError("Cannot parse: " + ex.getClass().getName() + ": " + ex.getMessage());
		} catch (IOException ex) {
			model.StatusLine().ShowError("Cannot load: " + ex.getClass().getName() + ": " + ex.getMessage());
		} finally {
			if (model.Packages() == null) {
				model.setTitle("Dependency Finder");
			}
		}
	}

	public void StartClass(DependencyEvent event) {
		model.StatusLine().ShowInfo("Loading dependencies for " + event.Classname() + " ...");
	}
	
	public void StopClass(DependencyEvent event) {
		// Do nothing
	}
	
	public void Dependency(DependencyEvent event) {
		// Do nothing
	}
}
