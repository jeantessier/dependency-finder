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
import com.jeantessier.diff.*;

public class JarJarDiff {
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
		System.err.println("Defaults is text output to the console.");
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine(new NullParameterStrategy());
		command_line.AddMultipleValuesSwitch("old", true);
		command_line.AddSingleValueSwitch("old-label");
		command_line.AddMultipleValuesSwitch("new", true);
		command_line.AddSingleValueSwitch("new-label");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",     DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("JarJarDiff");
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
			Logger logger = Logger.getLogger("com.jeantessier.diff");
			logger.setLevel(Level.DEBUG);
			
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

		// Collecting data, first classfiles from JARs,
		// then package/class trees using NodeFactory.

		AggregatingClassfileLoader old_jar = new AggregatingClassfileLoader();
		Iterator old_sources = command_line.MultipleSwitch("old").iterator();
		while(old_sources.hasNext()) {
			String name = (String) old_sources.next();
			Logger.getLogger(JarJarDiff.class).info("Reading old JAR: " + name);
			if (name.endsWith(".jar")) {
				old_jar.AddClassfiles(new JarClassfileLoader(new String[] {name}).Classfiles());
			} else if (name.endsWith(".zip")) {
				old_jar.AddClassfiles(new ZipClassfileLoader(new String[] {name}).Classfiles());
			} else {
				old_jar.AddClassfiles(new DirectoryClassfileLoader(new String[] {name}).Classfiles());
			}
		}
		
		AggregatingClassfileLoader new_jar = new AggregatingClassfileLoader();
		Iterator new_sources = command_line.MultipleSwitch("new").iterator();
		while(new_sources.hasNext()) {
			String name = (String) new_sources.next();
			Logger.getLogger(JarJarDiff.class).info("Reading new JAR: " + name);
			if (name.endsWith(".jar")) {
				new_jar.AddClassfiles(new JarClassfileLoader(new String[] {name}).Classfiles());
			} else if (name.endsWith(".zip")) {
				new_jar.AddClassfiles(new ZipClassfileLoader(new String[] {name}).Classfiles());
			} else {
				new_jar.AddClassfiles(new DirectoryClassfileLoader(new String[] {name}).Classfiles());
			}
		}

		// Starting to compare, first at package level,
		// then descending to class level for packages
		// that are in both the old and the new codebase.
	
		Logger.getLogger(JarJarDiff.class).info("Comparing ...");

		String      old_label   = command_line.IsPresent("old-label") ? command_line.SingleSwitch("old-label") : command_line.Switch("old").toString();
		String      new_label   = command_line.IsPresent("new-label") ? command_line.SingleSwitch("new-label") : command_line.Switch("new").toString();
		JarDifferences differences = new JarDifferences(old_label, new_label);
		differences.Compare(old_jar, new_jar);

		Logger.getLogger(JarJarDiff.class).info("Printing results ...");

		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		com.jeantessier.diff.Printer printer = new Report();
		differences.Accept(printer);
		out.print(printer);

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(JarJarDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.flush();
		out.close();
	}
}
