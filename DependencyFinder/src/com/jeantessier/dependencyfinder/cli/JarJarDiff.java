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

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.diff.*;

public class JarJarDiff {
	public static final String DEFAULT_LOGFILE   = "System.out";
	public static final String DEFAULT_TRACEFILE = "System.out";

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

		// Collecting data, first classfiles from JARs,
		// then package/class trees using NodeFactory.

		AggregatingClassfileLoader old_jar = new AggregatingClassfileLoader();
		Iterator old_sources = command_line.MultipleSwitch("old").iterator();
		while(old_sources.hasNext()) {
			String name = (String) old_sources.next();
			Category.getInstance(JarJarDiff.class.getName()).info("Reading old JAR: " + name);
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
			Category.getInstance(JarJarDiff.class.getName()).info("Reading new JAR: " + name);
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
	
		Category.getInstance(JarJarDiff.class.getName()).info("Comparing ...");

		String      old_label   = command_line.IsPresent("old-label") ? command_line.SingleSwitch("old-label") : command_line.Switch("old").toString();
		String      new_label   = command_line.IsPresent("new-label") ? command_line.SingleSwitch("new-label") : command_line.Switch("new").toString();
		JarDifferences differences = new JarDifferences(old_label, new_label);
		differences.Compare(old_jar, new_jar);

		Category.getInstance(JarJarDiff.class.getName()).info("Printing results ...");

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
