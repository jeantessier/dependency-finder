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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;

public class ClassReader {
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
