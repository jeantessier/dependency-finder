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

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class DependencyExtractAction extends AbstractAction implements Runnable, LoadListener, DependencyListener {
	private DependencyFinder model;
	private File[]           files;

	public DependencyExtractAction(DependencyFinder model) {
		this.model = model;

		putValue(Action.LONG_DESCRIPTION, "Extract classes");
		putValue(Action.NAME, "Extract");
		putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/extract.gif")));
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(model.InputFile());
		chooser.addChoosableFileFilter(new JavaBytecodeFileFilter());
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		int returnValue = chooser.showDialog(model, "Extract");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			files = chooser.getSelectedFiles();
			new Thread(this).start();
		}
	}

	public void run() {
		Date start = new Date();
		
		for (int i=0; i<files.length; i++) {
			Extract(files[i]);
		}

		model.Packages(model.NodeFactory().Packages().values());

		if (model.Maximize()) {
			model.StatusLine().ShowInfo("Maximizing ...");
			new LinkMaximizer().TraverseNodes(model.Packages());
		} else if (model.Minimize()) {
			model.StatusLine().ShowInfo("Minimizing ...");
			new LinkMinimizer().TraverseNodes(model.Packages());
		}
		
		Date stop = new Date();
		
		model.StatusLine().ShowInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
		model.setTitle("Dependency Finder - Extractor");
	}

	private void Extract(File file) {
	    model.InputFile(file);
		String filename = model.InputFile().toString();

		if (model.NodeFactory() == null && model.Collector() == null) {
			NodeFactory factory = new NodeFactory();
			CodeDependencyCollector collector = new CodeDependencyCollector(factory);
			collector.addDependencyListener(this);
			
			model.NodeFactory(factory);
			model.Collector(collector);
		}

		try {
			ClassfileLoader loader;
			if (filename.endsWith(".jar")) {
				loader = new JarClassfileLoader(new String[] {filename});
			} else if (filename.endsWith(".zip")) {
				loader = new ZipClassfileLoader(new String[] {filename});
			} else {
				loader = new DirectoryClassfileLoader(new String[] {filename});
			}

			loader.addLoadListener(this);
			loader.Start();

			Iterator i = loader.Classfiles().iterator();
			while (i.hasNext()) {
				((Classfile) i.next()).Accept(model.Collector());
			}
		} catch (IOException ex) {
			model.StatusLine().ShowError("Cannot extract from " + filename + ": " + ex.getClass().getName() + ": " + ex.getMessage());
		}

		model.StatusLine().ShowInfo("Done with " + filename + ".");
	}

	public void LoadStart(LoadEvent event) {
		model.StatusLine().ShowInfo("Loading " + event.Filename() + " ...");
	}

	public void LoadStop(LoadEvent event) {
		// Do nothing
	}

	public void LoadElement(LoadEvent event) {
		if (event.Element() == null) {
			model.StatusLine().ShowInfo("Loading " + event.Filename() + " ...");
		} else {
			model.StatusLine().ShowInfo("Loading " + event.Filename() + " >> " + event.Element() + " ...");
		}
	}

	public void StartClass(DependencyEvent event) {
		model.StatusLine().ShowInfo("Analyzing " + event.Classname() + " ...");
	}
	
	public void StopClass(DependencyEvent event) {
		// Do nothing
	}
	
	public void Dependency(DependencyEvent event) {
		// Do nothing
	}
}
