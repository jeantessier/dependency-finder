/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class DependencyExtractor {
	public static final String DEFAULT_LOGFILE = "System.out";

	public static void Error(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		Error(clu);
	}

	public static void Error(CommandLineUsage clu) {
		System.err.println(clu);
		System.err.println();
		System.err.println("If no files are specified, it processes the current directory.");
		System.err.println();
		System.err.println("If file is a directory, it is recusively scanned for files");
		System.err.println("ending in \".class\".");
		System.err.println();
		System.err.println("Defaults is text output to the console.");
		System.err.println();
	}

	public static void Version() throws IOException {
		Version version = new Version();
		
		System.err.print(version.ImplementationTitle());
		System.err.print(" ");
		System.err.print(version.ImplementationVersion());
		System.err.print(" (c) ");
		System.err.print(version.CopyrightDate());
		System.err.print(" ");
		System.err.print(version.CopyrightHolder());
		System.err.println();
		
		System.err.print(version.ImplementationURL());
		System.err.println();
		
		System.err.print("Compiled on ");
		System.err.print(version.ImplementationDate());
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine();
		command_line.addToggleSwitch("xml");
		command_line.addToggleSwitch("maximize");
		command_line.addToggleSwitch("minimize");
		command_line.addSingleValueSwitch("encoding",    com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING);
		command_line.addSingleValueSwitch("dtd-prefix",  com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX);
		command_line.addSingleValueSwitch("indent-text");
		command_line.addToggleSwitch("time");
		command_line.addSingleValueSwitch("out");
		command_line.addToggleSwitch("help");
		command_line.addOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
		command_line.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("DependencyExtractor");
		command_line.accept(usage);

		try {
			command_line.parse(args);
		} catch (IllegalArgumentException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		} catch (CommandLineException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		}

		if (command_line.getToggleSwitch("help")) {
			Error(usage);
		}
		
		if (command_line.getToggleSwitch("version")) {
			Version();
		}

		if (command_line.getToggleSwitch("help") || command_line.getToggleSwitch("version")) {
			System.exit(1);
		}

		VerboseListener verbose_listener = new VerboseListener();
		if (command_line.isPresent("verbose")) {
			if ("System.out".equals(command_line.getOptionalSwitch("verbose"))) {
				verbose_listener.Writer(System.out);
			} else {
				verbose_listener.Writer(new FileWriter(command_line.getOptionalSwitch("verbose")));
			}
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		List parameters = command_line.getParameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		NodeFactory factory = new NodeFactory();
		CodeDependencyCollector collector = new CodeDependencyCollector(factory);
		
		ClassfileLoader loader = new TransientClassfileLoader();
		loader.addLoadListener(collector);
		loader.addLoadListener(verbose_listener);
		loader.load(parameters);

		if (command_line.isPresent("minimize")) {
			LinkMinimizer minimizer = new LinkMinimizer();
			minimizer.traverseNodes(factory.Packages().values());
		} else if (command_line.isPresent("maximize")) {
			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.traverseNodes(factory.Packages().values());
		}

		verbose_listener.Print("Printing the graph ...");

		PrintWriter out;
		if (command_line.isPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(System.out);
		}
			
		com.jeantessier.dependency.Printer printer;
		if (command_line.getToggleSwitch("xml")) {
			printer = new com.jeantessier.dependency.XMLPrinter(out, command_line.getSingleSwitch("encoding"), command_line.getSingleSwitch("dtd-prefix"));
		} else {
			printer = new com.jeantessier.dependency.TextPrinter(out);
		}
			
		if (command_line.isPresent("indent-text")) {
			printer.IndentText(command_line.getSingleSwitch("indent-text"));
		}

		printer.traverseNodes(factory.Packages().values());

		out.close();

		Date end = new Date();

		if (command_line.getToggleSwitch("time")) {
			System.err.println(DependencyExtractor.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verbose_listener.Close();
	}
}
