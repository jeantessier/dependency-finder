/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

import javax.swing.*;

public class SaveFileAction extends AbstractAction implements Runnable {
	private DependencyFinder model;
	private String           dtd_prefix;

	private String indent_text;
	private File   file;

	public SaveFileAction(DependencyFinder model, String dtd_prefix) {
		this.model = model;

		putValue(Action.LONG_DESCRIPTION, "Save current graph to XML file");
		putValue(Action.NAME, "Save");
		putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/save.gif")));
	}

	public String IndentText() {
		return indent_text;
	}

	public void IndentText(String indent_text) {
		this.indent_text = indent_text;
	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(model.InputFile());
		chooser.addChoosableFileFilter(new XMLFileFilter());
		int returnValue = chooser.showSaveDialog(model);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = chooser.getSelectedFile();
			new Thread(this).start();
		}
	}
	
	public void run() {
		com.jeantessier.dependency.Printer printer = new com.jeantessier.dependency.XMLPrinter(dtd_prefix);
		if (indent_text != null) {
			printer.IndentText(indent_text);
		}
		
		printer.TraverseNodes(model.Packages());

		try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			out.println(printer);
			out.close();
		} catch (IOException ex) {
			model.StatusLine().ShowError("Cannot save: " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}
}
