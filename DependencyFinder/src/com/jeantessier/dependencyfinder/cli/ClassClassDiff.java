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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.cli.*;
import com.jeantessier.diff.*;

public class ClassClassDiff {
	public static final String DEFAULT_OLD_DOCUMENTATION = "old_documentation.txt";
	public static final String DEFAULT_NEW_DOCUMENTATION = "new_documentation.txt";
	public static final String DEFAULT_LOGFILE           = "System.out";

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
		command_line.AddMultipleValuesSwitch("old", true);
		command_line.AddSingleValueSwitch("old-documentation", DEFAULT_OLD_DOCUMENTATION);
		command_line.AddMultipleValuesSwitch("new", true);
		command_line.AddSingleValueSwitch("new-documentation", DEFAULT_NEW_DOCUMENTATION);
		command_line.AddSingleValueSwitch("dtd-prefix",        Report.DEFAULT_DTD_PREFIX);
		command_line.AddSingleValueSwitch("indent-text");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",         DEFAULT_LOGFILE);

		CommandLineUsage usage = new CommandLineUsage("ClassClassDiff");
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

		VerboseListener verbose_listener = new VerboseListener();
		if (command_line.IsPresent("verbose")) {
			if ("System.out".equals(command_line.OptionalSwitch("verbose"))) {
				verbose_listener.Writer(System.out);
			} else {
				verbose_listener.Writer(new FileWriter(command_line.OptionalSwitch("verbose")));
			}
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		// Collecting data, first classfiles from JARs,
		// then package/class trees using NodeFactory.

		Validator old_validator = new ListBasedValidator(command_line.SingleSwitch("old-documentation"));
		ClassfileLoader old_jar = new AggregatingClassfileLoader();
		old_jar.addLoadListener(verbose_listener);
		old_jar.Load(command_line.MultipleSwitch("old"));

		Validator new_validator = new ListBasedValidator(command_line.SingleSwitch("new-documentation"));
		ClassfileLoader new_jar = new AggregatingClassfileLoader();
		new_jar.addLoadListener(verbose_listener);
		new_jar.Load(command_line.MultipleSwitch("new"));

		// Starting to compare, first at package level,
		// then descending to class level for packages
		// that are in both the old and the new codebase.
	
		Logger.getLogger(JarJarDiff.class).info("Comparing ...");
		verbose_listener.println("Comparing ...");

		String name = command_line.SingleSwitch("name");
		Classfile old_class = (Classfile) old_jar.Classfiles().iterator().next();
		Classfile new_class = (Classfile) new_jar.Classfiles().iterator().next();

		DifferencesFactory factory = new DifferencesFactory(old_validator, new_validator);
		Differences differences = factory.CreateClassDifferences(name, old_class, new_class);

		Logger.getLogger(JarJarDiff.class).info("Printing results ...");
		verbose_listener.println("Printing results ...");

		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		com.jeantessier.diff.Printer printer = new Report(command_line.SingleSwitch("dtd-prefix"));
		if (command_line.IsPresent("indent-text")) {
			printer.IndentText(command_line.SingleSwitch("indent-text"));
		}

		differences.Accept(printer);
		out.print(printer);

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(JarJarDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.flush();
		out.close();

		verbose_listener.close();
	}
}
