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

	private static final Layout DEFAULT_LOG_LAYOUT = new PatternLayout("[%d{yyyy/MM/dd HH:mm:ss.SSS}] %c %m%n");

	public static void Log(Logger logger, String filename) throws IOException {
		Log(logger, filename, Level.DEBUG);
	}
	
	public static void Log(Logger logger, String filename, Level level) throws IOException {
		logger.setLevel(level);
			
		if ("System.out".equals(filename)) {
			logger.addAppender(new ConsoleAppender(DEFAULT_LOG_LAYOUT));
		} else {
			logger.addAppender(new WriterAppender(DEFAULT_LOG_LAYOUT, new FileWriter(filename)));
		}
	}

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
		command_line.AddSingleValueSwitch("dtd-prefix",  com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX);
		command_line.AddSingleValueSwitch("indent-text");
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
			Log(Logger.getLogger("com.jeantessier.dependencyfinder.cli"), command_line.OptionalSwitch("verbose"));
			Log(Logger.getLogger("com.jeantessier.dependency"), command_line.OptionalSwitch("verbose"));
		}

		if (command_line.IsPresent("trace")) {
			Log(Logger.getLogger("com.jeantessier.dependencyfinder.cli"), command_line.OptionalSwitch("verbose"));
			Log(Logger.getLogger("com.jeantessier.classreader"), command_line.OptionalSwitch("trace"));
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
		CodeDependencyCollector collector = new CodeDependencyCollector(factory);
		
		ClassfileLoader loader = new TransientClassfileLoader();
		loader.addLoadListener(collector);
		
		Iterator i = parameters.iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();

			if (filename.endsWith(".jar")) {
				JarClassfileLoader jar_loader = new JarClassfileLoader(loader);
				jar_loader.Load(filename);
			} else if (filename.endsWith(".zip")) {
				ZipClassfileLoader zip_loader = new ZipClassfileLoader(loader);
				zip_loader.Load(filename);
			} else {
				DirectoryClassfileLoader directory_loader = new DirectoryClassfileLoader(loader);
				directory_loader.Load(new DirectoryExplorer(filename));
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
				printer = new com.jeantessier.dependency.XMLPrinter(command_line.SingleSwitch("dtd-prefix"));
			} else if (command_line.ToggleSwitch("plain")) {
				printer = new com.jeantessier.dependency.TextPrinter();
			} else {
				printer = new com.jeantessier.dependency.PrettyPrinter();
			}
			
			if (command_line.IsPresent("indent-text")) {
				printer.IndentText(command_line.SingleSwitch("indent-text"));
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
