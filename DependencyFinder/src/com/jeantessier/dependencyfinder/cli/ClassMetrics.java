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

public class ClassMetrics {
	public static final String DEFAULT_LOGFILE   = "System.out";

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
		command_line.AddToggleSwitch("list");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);

		CommandLineUsage usage = new CommandLineUsage("ClassMetrics");
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

		boolean list = command_line.ToggleSwitch("list");

		List parameters = command_line.Parameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		ClassfileLoader loader = new AggregatingClassfileLoader();
		loader.addLoadListener(verbose_listener);
		loader.Load(parameters);

		MetricsGatherer metrics = new MetricsGatherer();
		Iterator i = loader.Classfiles().iterator();
		while (i.hasNext()) {
			((Classfile) i.next()).Accept(metrics);
		}

		verbose_listener.println("Printing report ...");
		
		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		out.println(metrics.Classes().size() + " class(es)");
		if (list) {
			Iterator j = metrics.Classes().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.Interfaces().size() + " interface(s)");
		if (list) {
			Iterator j = metrics.Interfaces().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println();
		out.println(metrics.Methods().size() + " method(s) (average " + (metrics.Methods().size() / (metrics.Classes().size() + (double) metrics.Interfaces().size())) + " per class/interface)");
		out.println(metrics.Fields().size() + " field(s) (average " + (metrics.Fields().size() / (metrics.Classes().size() + (double) metrics.Interfaces().size())) + " per class/interface)");
		out.println();

		PrintCFM(out, " synthetic element(s)", metrics.SyntheticClasses(), metrics.SyntheticFields(), metrics.SyntheticMethods(), list);
		PrintCFM(out, " deprecated element(s)", metrics.DeprecatedClasses(), metrics.DeprecatedFields(), metrics.DeprecatedMethods(), list);
		PrintCFMIC(out, " public element(s)", metrics.PublicClasses(), metrics.PublicFields(), metrics.PublicMethods(), metrics.PublicInnerClasses(), list);
		PrintFMIC(out, " protected element(s)", metrics.ProtectedFields(), metrics.ProtectedMethods(), metrics.ProtectedInnerClasses(), list);
		PrintFMIC(out, " private element(s)", metrics.PrivateFields(), metrics.PrivateMethods(), metrics.PrivateInnerClasses(), list);
		PrintCFMIC(out, " package element(s)", metrics.PackageClasses(), metrics.PackageFields(), metrics.PackageMethods(), metrics.PackageInnerClasses(), list);
		PrintCMIC(out, " abstract element(s)", metrics.AbstractClasses(), metrics.AbstractMethods(), metrics.AbstractInnerClasses(), list);

		PrintFMIC(out, " static element(s)", metrics.StaticFields(), metrics.StaticMethods(), metrics.StaticInnerClasses(), list);
		PrintCFMIC(out, " final element(s)", metrics.FinalClasses(), metrics.FinalFields(), metrics.FinalMethods(), metrics.FinalInnerClasses(), list);

		out.println(metrics.SynchronizedMethods().size() + " synchronized method(s)");
		if (list) {
			Iterator j = metrics.SynchronizedMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.NativeMethods().size() + " native method(s)");
		if (list) {
			Iterator j = metrics.NativeMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.VolatileFields().size() + " volatile field(s)");
		if (list) {
			Iterator j = metrics.VolatileFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.TransientFields().size() + " transient field(s)");
		if (list) {
			Iterator j = metrics.TransientFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.CustomAttributes().size() + " custom attribute(s)");
		if (list) {
			Iterator j = metrics.CustomAttributes().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			out.println();
			out.println(ClassMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.close();

		verbose_listener.close();
	}

	private static void PrintCMIC(PrintWriter out, String label, Collection classes, Collection methods, Collection inner_classes, boolean list) {
		out.println((classes.size() +
					 methods.size() +
					 inner_classes.size()) + label);
		if (list) {
			Iterator j;

			out.println("    " + classes.size() + " class(es)");
			j = classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + methods.size() + " method(s)");
			j = methods.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + inner_classes.size() + " inner class(es)");
			j = inner_classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + classes.size() + " class(es)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + inner_classes.size() + " inner class(es)");
		}
	}

	private static void PrintCFMIC(PrintWriter out, String label, Collection classes, Collection fields, Collection methods, Collection inner_classes, boolean list) {
		out.println((classes.size() +
					 fields.size() +
					 methods.size() +
					 inner_classes.size()) + label);
		if (list) {
			Iterator j;

			out.println("    " + classes.size() + " class(es)");
			j = classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + fields.size() + " field(s)");
			j = fields.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + methods.size() + " method(s)");
			j = methods.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + inner_classes.size() + " inner class(es)");
			j = inner_classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + classes.size() + " class(es)");
			out.println("    " + fields.size() + " fields(s)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + inner_classes.size() + " inner class(es)");
		}
	}

	private static void PrintCFM(PrintWriter out, String label, Collection classes, Collection fields, Collection methods, boolean list) {
		out.println((classes.size() +
					 fields.size() +
					 methods.size()) + label);
		if (list) {
			Iterator j;

			out.println("    " + classes.size() + " class(es)");
			j = classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + fields.size() + " field(s)");
			j = fields.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + methods.size() + " method(s)");
			j = methods.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + classes.size() + " class(es)");
			out.println("    " + fields.size() + " fields(s)");
			out.println("    " + methods.size() + " method(s)");
		}
	}

	private static void PrintFMIC(PrintWriter out, String label, Collection fields, Collection methods, Collection inner_classes, boolean list) {
		out.println((fields.size() +
					 methods.size() +
					 inner_classes.size()) + label);
		if (list) {
			Iterator j;

			out.println("    " + fields.size() + " field(s)");
			j = fields.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + methods.size() + " method(s)");
			j = methods.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}

			out.println("    " + inner_classes.size() + " inner class(es)");
			j = inner_classes.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + fields.size() + " fields(s)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + inner_classes.size() + " inner class(es)");
		}
	}
}
