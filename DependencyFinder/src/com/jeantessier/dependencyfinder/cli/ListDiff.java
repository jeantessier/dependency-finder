/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.diff.*;

public class ListDiff {
	public static void showError(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		showError(clu);
	}

	public static void showError(CommandLineUsage clu) {
		System.err.println(clu);
		System.err.println();
		System.err.println("Defaults is text output to the console.");
		System.err.println();
	}

	public static void showVersion() throws IOException {
		Version version = new Version();
		
		System.err.print(version.getImplementationTitle());
		System.err.print(" ");
		System.err.print(version.getImplementationVersion());
		System.err.print(" (c) ");
		System.err.print(version.getCopyrightDate());
		System.err.print(" ");
		System.err.print(version.getCopyrightHolder());
		System.err.println();
		
		System.err.print(version.getImplementationURL());
		System.err.println();
		
		System.err.print("Compiled on ");
		System.err.print(version.getImplementationDate());
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine commandLine = new CommandLine(new NullParameterStrategy());
		commandLine.addSingleValueSwitch("name");
		commandLine.addSingleValueSwitch("old-label");
		commandLine.addSingleValueSwitch("old", true);
		commandLine.addSingleValueSwitch("new-label");
		commandLine.addSingleValueSwitch("new", true);
		commandLine.addToggleSwitch("compress");
		commandLine.addSingleValueSwitch("encoding",   ListDiffPrinter.DEFAULT_ENCODING);
		commandLine.addSingleValueSwitch("dtd-prefix", ListDiffPrinter.DEFAULT_DTD_PREFIX);
		commandLine.addSingleValueSwitch("indent-text");
		commandLine.addToggleSwitch("time");
		commandLine.addSingleValueSwitch("out");
		commandLine.addToggleSwitch("help");
		commandLine.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("ListDiff");
		commandLine.accept(usage);

		try {
			commandLine.parse(args);
		} catch (IllegalArgumentException ex) {
			showError(usage, ex.toString());
			System.exit(1);
		} catch (CommandLineException ex) {
			showError(usage, ex.toString());
			System.exit(1);
		}

		if (commandLine.getToggleSwitch("help")) {
			showError(usage);
		}
		
		if (commandLine.getToggleSwitch("version")) {
			showVersion();
		}

		if (commandLine.getToggleSwitch("help") || commandLine.getToggleSwitch("version")) {
			System.exit(1);
		}

		/*
		 *  Beginning of main processing
		 */
		
		Date start = new Date();
		
		String line;
		
		Logger.getLogger(ListDiff.class).info("Loading data ...");
		
		Collection oldAPI = new TreeSet();
		BufferedReader oldIn = new BufferedReader(new FileReader(commandLine.getSingleSwitch("old")));
		while((line = oldIn.readLine()) != null) {
			oldAPI.add(line);
		}
		
		Collection newAPI = new TreeSet();
		BufferedReader newIn = new BufferedReader(new FileReader(commandLine.getSingleSwitch("new")));
		while((line = newIn.readLine()) != null) {
			newAPI.add(line);
		}
		
		ListDiffPrinter printer = new ListDiffPrinter(commandLine.getToggleSwitch("compress"), commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
		printer.setName(commandLine.getSingleSwitch("name"));
		printer.setOldVersion(commandLine.getSingleSwitch("old-label"));
		printer.setNewVersion(commandLine.getSingleSwitch("new-label"));
		if (commandLine.isPresent("indent-text")) {
			printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
		}
		
		Iterator i;

		i = oldAPI.iterator();
		while (i.hasNext()) {
			line = (String) i.next();
			if (!newAPI.contains(line)) {
				printer.remove(line);
			}
		}

		i = newAPI.iterator();
		while (i.hasNext()) {
			line = (String) i.next();
			if (!oldAPI.contains(line)) {
				printer.add(line);
			}
		}

		Logger.getLogger(ListDiff.class).info("Printing results ...");
		
		PrintWriter out;
		if (commandLine.isPresent("out")) {
			out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}
		out.print(printer);
		out.close();

		Date end = new Date();

		if (commandLine.getToggleSwitch("time")) {
			System.err.println(ListDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}
	}
}
