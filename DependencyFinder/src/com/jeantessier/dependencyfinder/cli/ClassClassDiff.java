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
		CommandLine command_line = new CommandLine(new NullParameterStrategy());
		command_line.addSingleValueSwitch("name");
		command_line.addMultipleValuesSwitch("old", true);
		command_line.addSingleValueSwitch("old-documentation", DEFAULT_OLD_DOCUMENTATION);
		command_line.addMultipleValuesSwitch("new", true);
		command_line.addSingleValueSwitch("new-documentation", DEFAULT_NEW_DOCUMENTATION);
		command_line.addSingleValueSwitch("encoding",          Report.DEFAULT_ENCODING);
		command_line.addSingleValueSwitch("dtd-prefix",        Report.DEFAULT_DTD_PREFIX);
		command_line.addSingleValueSwitch("indent-text");
		command_line.addToggleSwitch("time");
		command_line.addSingleValueSwitch("out");
		command_line.addToggleSwitch("help");
		command_line.addOptionalValueSwitch("verbose",         DEFAULT_LOGFILE);
		command_line.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("ClassClassDiff");
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

		// Collecting data, first classfiles from JARs,
		// then package/class trees using NodeFactory.

		Validator old_validator = new ListBasedValidator(command_line.getSingleSwitch("old-documentation"));
		ClassfileLoader old_jar = new AggregatingClassfileLoader();
		old_jar.addLoadListener(verbose_listener);
		old_jar.load(command_line.getMultipleSwitch("old"));

		Validator new_validator = new ListBasedValidator(command_line.getSingleSwitch("new-documentation"));
		ClassfileLoader new_jar = new AggregatingClassfileLoader();
		new_jar.addLoadListener(verbose_listener);
		new_jar.load(command_line.getMultipleSwitch("new"));

		// Starting to compare, first at package level,
		// then descending to class level for packages
		// that are in both the old and the new codebase.
	
		Logger.getLogger(JarJarDiff.class).info("Comparing ...");
		verbose_listener.Print("Comparing ...");

		String name = command_line.getSingleSwitch("name");
		Classfile old_class = (Classfile) old_jar.getAllClassfiles().iterator().next();
		Classfile new_class = (Classfile) new_jar.getAllClassfiles().iterator().next();

		DifferencesFactory factory = new DifferencesFactory(old_validator, new_validator);
		Differences differences = factory.CreateClassDifferences(name, old_class, new_class);

		Logger.getLogger(JarJarDiff.class).info("Printing results ...");
		verbose_listener.Print("Printing results ...");

		PrintWriter out;
		if (command_line.isPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		com.jeantessier.diff.Printer printer = new Report(command_line.getSingleSwitch("encoding"), command_line.getSingleSwitch("dtd-prefix"));
		if (command_line.isPresent("indent-text")) {
			printer.IndentText(command_line.getSingleSwitch("indent-text"));
		}

		differences.Accept(printer);
		out.print(printer);

		Date end = new Date();

		if (command_line.getToggleSwitch("time")) {
			System.err.println(JarJarDiff.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.close();

		verbose_listener.Close();
	}
}
