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

public class ClassMetrics {
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
		command_line.AddToggleSwitch("list");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",     DEFAULT_TRACEFILE);

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

		boolean list = command_line.ToggleSwitch("list");

		List parameters = command_line.Parameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		MetricsGatherer metrics = new MetricsGatherer();
		Iterator        i       = parameters.iterator();
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
				((Classfile) j.next()).Accept(metrics);
			}
		}

		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		Iterator j;

		out.println(metrics.Classes().size() + " class(es)");
		if (list) {
			j = metrics.Classes().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.Interfaces().size() + " interface(s)");
		if (list) {
			j = metrics.Interfaces().iterator();
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
			j = metrics.SynchronizedMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.NativeMethods().size() + " native method(s)");
		if (list) {
			j = metrics.NativeMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.VolatileFields().size() + " volatile field(s)");
		if (list) {
			j = metrics.VolatileFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.TransientFields().size() + " transient field(s)");
		if (list) {
			j = metrics.TransientFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.CustomAttributes().size() + " custom attribute(s)");
		if (list) {
			j = metrics.CustomAttributes().iterator();
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
