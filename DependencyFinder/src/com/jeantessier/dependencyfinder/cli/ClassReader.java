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

public class ClassReader {
	public static final String DEFAULT_LOGFILE   = "System.out";
	public static final String DEFAULT_TRACEFILE = "System.out";

	private static final Layout DEFAULT_LOG_LAYOUT = new PatternLayout("[%d{yyyy/MM/dd HH:mm:ss.SSS}] %c %m%n");

	public static void Error(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		Error(clu);
	}

	public static void Error(CommandLineUsage clu) {
		System.err.println(clu);
		System.err.println();
		System.err.println("If no files are specified, it processes the current directory.");
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine(new AtLeastParameterStrategy(1));
		command_line.AddToggleSwitch("raw");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",   DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("ClassReader");
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
			Logger logger = Logger.getLogger("com.jeantessier.classreader");
			logger.setLevel(Level.INFO);
			
			if ("System.out".equals(command_line.OptionalSwitch("verbose"))) {
				logger.addAppender(new ConsoleAppender(DEFAULT_LOG_LAYOUT));
			} else {
				logger.addAppender(new WriterAppender(DEFAULT_LOG_LAYOUT, new FileWriter(command_line.OptionalSwitch("verbose"))));
			}
		}

		if (command_line.IsPresent("trace")) {
			Logger logger = Logger.getLogger("com.jeantessier.classreader");
			logger.setLevel(Level.DEBUG);
			
			if ("System.out".equals(command_line.OptionalSwitch("trace"))) {
				logger.addAppender(new ConsoleAppender(DEFAULT_LOG_LAYOUT));
			} else {
				logger.addAppender(new WriterAppender(DEFAULT_LOG_LAYOUT, new FileWriter(command_line.OptionalSwitch("trace"))));
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

		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		Iterator i = parameters.iterator();
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
		
				Printer printer;
				if (command_line.ToggleSwitch("raw")) {
					printer = new UglyPrinter();
				} else {
					printer = new PrettyPrinter();
				}
				classfile.Accept(printer);
		
				out.println(printer);
			}
		}

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(ClassReader.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.close();
	}
}
