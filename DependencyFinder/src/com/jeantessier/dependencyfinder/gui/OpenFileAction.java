/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
