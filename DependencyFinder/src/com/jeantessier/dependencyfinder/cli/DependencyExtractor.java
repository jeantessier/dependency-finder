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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

public class DependencyExtractor {
    public static final String DEFAULT_LOGFILE   = "System.out";
    public static final String DEFAULT_TRACEFILE = "System.out";

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

    public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine();
		command_line.AddToggleSwitch("serialize");
		command_line.AddToggleSwitch("plain");
		command_line.AddToggleSwitch("xml");
		command_line.AddToggleSwitch("maximize");
		command_line.AddToggleSwitch("minimize");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",     DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("DependencyExtractor");
		command_line.Accept(usage);

		try {
			command_line.Parse(args);
		} catch (IllegalArgumentException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		} catch (CommandLineException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		}

		if (command_line.ToggleSwitch("help")) {
			Error(usage);
			System.exit(1);
		}

		if (command_line.IsPresent("verbose")) {
			if ("System.out".equals(command_line.OptionalSwitch("verbose"))) {

			} else {

			}
		}

		if (command_line.IsPresent("trace")) {
			if ("System.out".equals(command_line.OptionalSwitch("trace"))) {

			} else {

			}
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		List parameters = command_line.Parameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		NodeFactory factory = new NodeFactory();
		Iterator    i       = parameters.iterator();
		while (i.hasNext()) {
			String entry = (String) i.next();

			ClassfileLoader loader;
			if (entry.endsWith(".jar")) {
				loader = new JarClassfileLoader(new String[] {entry});
			} else if (entry.endsWith(".zip")) {
				loader = new ZipClassfileLoader(new String[] {entry});
			} else {
				loader = new DirectoryClassfileLoader(new String[] {entry});
			}

			loader.Start();

			Iterator j = loader.Classfiles().iterator();
			while (j.hasNext()) {
				Classfile classfile = (Classfile) j.next();
		
				if (true) {
					// This version scans the bytecode to capture dependencies
					// at features scope.
					Category.getInstance(DependencyExtractor.class.getName()).info("Getting dependencies ...");
					classfile.Accept(new CodeDependencyCollector(factory));
				} else {
					// This version scans the constant pool to capture dependencies
					// at class scope.

					Node this_class = factory.CreateClass(classfile.Class());

					Collector collector;
					Iterator  k;

					Category.getInstance(DependencyExtractor.class.getName()).info("Getting class dependencies ...");
					collector = new ClassDependencyCollector();
					classfile.Accept(collector);
					k = collector.Collection().iterator();
					while (k.hasNext()) {
						Node dependency = factory.CreateClass((String) k.next());
						Category.getInstance(DependencyExtractor.class.getName()).info("\t" + dependency.Name());
						this_class.AddDependency(dependency);
					}
		    
					Category.getInstance(DependencyExtractor.class.getName()).info("Getting feature dependencies ...");
					collector = new FeatureDependencyCollector();
					classfile.Accept(collector);
					k = collector.Collection().iterator();
					while (k.hasNext()) {
						Node dependency = factory.CreateFeature((String) k.next());
						Category.getInstance(DependencyExtractor.class.getName()).info("\t" + dependency.Name());
						this_class.AddDependency(dependency);
					}
				}
			}
		}
	    
		if (command_line.IsPresent("minimize")) {
			LinkMinimizer minimizer = new LinkMinimizer();
			minimizer.TraverseNodes(factory.Packages().values());
		} else if (command_line.IsPresent("maximize")) {
			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.TraverseNodes(factory.Packages().values());
		}

		if (command_line.ToggleSwitch("serialize")) {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(command_line.SingleSwitch("out")));
			out.writeObject(new ArrayList(factory.Packages().values()));
			out.close();
		} else {
			com.jeantessier.dependency.Printer printer;
			if (command_line.ToggleSwitch("xml")) {
				printer = new com.jeantessier.dependency.XMLPrinter();
			} else if (command_line.ToggleSwitch("plain")) {
				printer = new com.jeantessier.dependency.TextPrinter();
			} else {
				printer = new com.jeantessier.dependency.PrettyPrinter();
			}
	    
			printer.TraverseNodes(factory.Packages().values());

			if (command_line.IsPresent("out")) {
				PrintWriter out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
				out.print(printer);
				out.close();
			} else {
				System.out.print(printer);
			}
		}

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(DependencyExtractor.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}
    }
}
