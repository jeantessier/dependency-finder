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

public class ListDiff {
	public static final String DEFAULT_LOGFILE    = "System.out";
	public static final String DEFAULT_TRACEFILE  = "System.out";

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
		System.err.println("Defaults is text output to the console.");
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine(new NullParameterStrategy());
		command_line.AddSingleValueSwitch("name");
		command_line.AddSingleValueSwitch("old-label");
		command_line.AddSingleValueSwitch("old", true);
		command_line.AddSingleValueSwitch("new-label");
		command_line.AddSingleValueSwitch("new", true);
		command_line.AddToggleSwitch("compress");
		command_line.AddSingleValueSwitch("dtd-prefix", ListDiffPrinter.DEFAULT_DTD_PREFIX);
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",  DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",    DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("ListDiff");
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
		}

		if (command_line.IsPresent("trace")) {
			Log(Logger.getLogger("com.jeantessier.dependencyfinder.cli"), command_line.OptionalSwitch("verbose"));
			Log(Logger.getLogger("com.jeantessier.classreader"), command_line.OptionalSwitch("trace"));
		}

		/*
		 *  Beginning of main processing
		 */
		
		Date start = new Date();
		
		String line;
		
		Logger.getLogger(ListDiff.class).info("Loading data ...");
		
		Collection old_api = new TreeSet();
		BufferedReader old_in = new BufferedReader(new FileReader(command_line.SingleSwitch("old")));
		while((line = old_in.readLine()) != null) {
			old_api.add(line);
		}
		
		Collection new_api = new TreeSet();
		BufferedReader new_in = new BufferedReader(new FileReader(command_line.SingleSwitch("new")));
		while((line = new_in.readLine()) != null) {
			new_api.add(line);
		}
		
		Logger.getLogger(ListDiff.class).info("Printing results ...");
		
		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}
		
		ListDiffPrinter printer = new ListDiffPrinter(command_line.ToggleSwitch("compress"), command_line.SingleSwitch("dtd-prefix"));
		printer.Name(command_line.SingleSwitch("name"));
		printer.OldVersion(command_line.SingleSwitch("old-label"));
		printer.NewVersion(command_line.SingleSwitch("new-label"));
		
		Iterator i;

		i = old_api.iterator();
		while (i.hasNext()) {
			line = (String) i.next();
			if (!new_api.contains(line)) {
				printer.Remove(line);
			}
		}

		i = new_api.iterator();
		while (i.hasNext()) {
			line = (String) i.next();
			if (!old_api.contains(line)) {
				printer.Add(line);
			}
		}

		out.print(printer);

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(ListDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.flush();
		out.close();
	}
}
