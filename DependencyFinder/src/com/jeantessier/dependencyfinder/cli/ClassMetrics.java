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
import com.jeantessier.dependencyfinder.*;

public class ClassMetrics {
	public static final String DEFAULT_LOGFILE = "System.out";

	public static void showError(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		showError(clu);
	}

	public static void showError(CommandLineUsage clu) {
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

	public static void showVersion() throws IOException {
		Version version = new Version();
		
		System.err.print(version.getImplementationTitle());
		System.err.print(" ");
		System.err.print(version.getImplementationVersion());
		System.err.print(" (c) ");
		System.err.print(version.getCopyrightDate());
		System.err.print(" ");
		System.err.print(version.getCopyrightHolder());
		System.err.println();
		
		System.err.print(version.getImplementationURL());
		System.err.println();
		
		System.err.print("Compiled on ");
		System.err.print(version.getImplementationDate());
		System.err.println();
	}

	public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine commandLine = new CommandLine();
		commandLine.addToggleSwitch("list");
		commandLine.addToggleSwitch("instruction-counts");
		commandLine.addToggleSwitch("time");
		commandLine.addSingleValueSwitch("out");
		commandLine.addToggleSwitch("help");
		commandLine.addOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
		commandLine.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("ClassMetrics");
		commandLine.accept(usage);

		try {
			commandLine.parse(args);
		} catch (IllegalArgumentException ex) {
			showError(usage, ex.toString());
			System.exit(1);
		} catch (CommandLineException ex) {
			showError(usage, ex.toString());
			System.exit(1);
		}

		if (commandLine.getToggleSwitch("help")) {
			showError(usage);
		}
		
		if (commandLine.getToggleSwitch("version")) {
			showVersion();
		}

		if (commandLine.getToggleSwitch("help") || commandLine.getToggleSwitch("version")) {
			System.exit(1);
		}

		VerboseListener verboseListener = new VerboseListener();
		if (commandLine.isPresent("verbose")) {
			if ("System.out".equals(commandLine.getOptionalSwitch("verbose"))) {
				verboseListener.setWriter(System.out);
			} else {
				verboseListener.setWriter(new FileWriter(commandLine.getOptionalSwitch("verbose")));
			}
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		boolean list              = commandLine.getToggleSwitch("list");
		boolean instructionCounts = commandLine.getToggleSwitch("instruction-counts");

		List parameters = commandLine.getParameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		ClassfileLoader loader = new AggregatingClassfileLoader();
		loader.addLoadListener(verboseListener);
		loader.load(parameters);

		MetricsGatherer metrics = new MetricsGatherer();
		metrics.visitClassfiles(loader.getAllClassfiles());

		verboseListener.print("Printing report ...");
		
		PrintWriter out;
		if (commandLine.isPresent("out")) {
			out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		out.println(metrics.getClasses().size() + " class(es)");
		if (list) {
			Iterator j = metrics.getClasses().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.getInterfaces().size() + " interface(s)");
		if (list) {
			Iterator j = metrics.getInterfaces().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println();
		out.println(metrics.getMethods().size() + " method(s) (average " + (metrics.getMethods().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
		out.println(metrics.getFields().size() + " field(s) (average " + (metrics.getFields().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
		out.println();

		printCFM(out, " synthetic element(s)", metrics.getSyntheticClasses(), metrics.getSyntheticFields(), metrics.getSyntheticMethods(), list);
		printCFM(out, " deprecated element(s)", metrics.getDeprecatedClasses(), metrics.getDeprecatedFields(), metrics.getDeprecatedMethods(), list);
		printCFMIC(out, " public element(s)", metrics.getPublicClasses(), metrics.getPublicFields(), metrics.getPublicMethods(), metrics.getPublicInnerClasses(), list);
		printFMIC(out, " protected element(s)", metrics.getProtectedFields(), metrics.getProtectedMethods(), metrics.getProtectedInnerClasses(), list);
		printFMIC(out, " private element(s)", metrics.getPrivateFields(), metrics.getPrivateMethods(), metrics.getPrivateInnerClasses(), list);
		printCFMIC(out, " package element(s)", metrics.getPackageClasses(), metrics.getPackageFields(), metrics.getPackageMethods(), metrics.getPackageInnerClasses(), list);
		printCMIC(out, " abstract element(s)", metrics.getAbstractClasses(), metrics.getAbstractMethods(), metrics.getAbstractInnerClasses(), list);

		printFMIC(out, " static element(s)", metrics.getStaticFields(), metrics.getStaticMethods(), metrics.getStaticInnerClasses(), list);
		printCFMIC(out, " final element(s)", metrics.getFinalClasses(), metrics.getFinalFields(), metrics.getFinalMethods(), metrics.getFinalInnerClasses(), list);

		out.println(metrics.getSynchronizedMethods().size() + " synchronized method(s)");
		if (list) {
			Iterator j = metrics.getSynchronizedMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.getNativeMethods().size() + " native method(s)");
		if (list) {
			Iterator j = metrics.getNativeMethods().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.getVolatileFields().size() + " volatile field(s)");
		if (list) {
			Iterator j = metrics.getVolatileFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.getTransientFields().size() + " transient field(s)");
		if (list) {
			Iterator j = metrics.getTransientFields().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		out.println(metrics.getCustomAttributes().size() + " custom attribute(s)");
		if (list) {
			Iterator j = metrics.getCustomAttributes().iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		}

		if (instructionCounts) {
			out.println();
			out.println("Instruction counts:");
			for (int opcode=0; opcode<256; opcode++) {
				out.print("        0x");
				Hex.print(out, (byte) opcode);
				out.println(" " + Instruction.getMnemonic(opcode) + ": " + metrics.getInstructionCounts()[opcode]);
			}
		}

		out.close();
		
		Date end = new Date();

		if (commandLine.getToggleSwitch("time")) {
			System.err.println(ClassMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verboseListener.close();
	}

	private static void printCMIC(PrintWriter out, String label, Collection classes, Collection methods, Collection innerClasses, boolean list) {
		out.println((classes.size() +
					 methods.size() +
					 innerClasses.size()) + label);
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

			out.println("    " + innerClasses.size() + " inner class(es)");
			j = innerClasses.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + classes.size() + " class(es)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + innerClasses.size() + " inner class(es)");
		}
	}

	private static void printCFMIC(PrintWriter out, String label, Collection classes, Collection fields, Collection methods, Collection innerClasses, boolean list) {
		out.println((classes.size() +
					 fields.size() +
					 methods.size() +
					 innerClasses.size()) + label);
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

			out.println("    " + innerClasses.size() + " inner class(es)");
			j = innerClasses.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + classes.size() + " class(es)");
			out.println("    " + fields.size() + " fields(s)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + innerClasses.size() + " inner class(es)");
		}
	}

	private static void printCFM(PrintWriter out, String label, Collection classes, Collection fields, Collection methods, boolean list) {
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

	private static void printFMIC(PrintWriter out, String label, Collection fields, Collection methods, Collection innerClasses, boolean list) {
		out.println((fields.size() +
					 methods.size() +
					 innerClasses.size()) + label);
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

			out.println("    " + innerClasses.size() + " inner class(es)");
			j = innerClasses.iterator();
			while (j.hasNext()) {
				out.println("        " + j.next());
			}
		} else {
			out.println("    " + fields.size() + " fields(s)");
			out.println("    " + methods.size() + " method(s)");
			out.println("    " + innerClasses.size() + " inner class(es)");
		}
	}
}
