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
import com.jeantessier.metrics.*;

public class OOMetrics {
	public static final String DEFAULT_PROJECT_NAME = "Project";
	public static final String DEFAULT_SORT         = "name";
	public static final String DEFAULT_LOGFILE      = "System.out";

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
		CommandLine command_line = new CommandLine();
		command_line.addSingleValueSwitch("project-name",           DEFAULT_PROJECT_NAME);
		command_line.addSingleValueSwitch("default-configuration", true);
		command_line.addSingleValueSwitch("configuration");
		command_line.addToggleSwitch("csv");
		command_line.addToggleSwitch("txt");
		command_line.addToggleSwitch("xml");
		command_line.addToggleSwitch("validate");
		command_line.addSingleValueSwitch("encoding",               com.jeantessier.metrics.XMLPrinter.DEFAULT_ENCODING);
		command_line.addSingleValueSwitch("dtd-prefix",             com.jeantessier.metrics.XMLPrinter.DEFAULT_DTD_PREFIX);
		command_line.addSingleValueSwitch("indent-text");
		command_line.addToggleSwitch("all");
		command_line.addToggleSwitch("project");
		command_line.addToggleSwitch("groups");
		command_line.addToggleSwitch("classes");
		command_line.addToggleSwitch("methods");
		command_line.addMultipleValuesSwitch("scope-includes-list");
		command_line.addMultipleValuesSwitch("scope-excludes-list");
		command_line.addMultipleValuesSwitch("filter-includes-list");
		command_line.addMultipleValuesSwitch("filter-excludes-list");
		command_line.addToggleSwitch("show-all-metrics");
		command_line.addToggleSwitch("show-empty-metrics");
		command_line.addToggleSwitch("show-hidden-measurements");
		command_line.addSingleValueSwitch("sort",                   DEFAULT_SORT);
		command_line.addToggleSwitch("expand");
		command_line.addToggleSwitch("reverse");
		command_line.addToggleSwitch("time");
		command_line.addSingleValueSwitch("out");
		command_line.addToggleSwitch("help");
		command_line.addOptionalValueSwitch("verbose",              DEFAULT_LOGFILE);
		command_line.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("OOMetrics");
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

		if (!command_line.getToggleSwitch("all") && !command_line.getToggleSwitch("project") && !command_line.getToggleSwitch("groups") && !command_line.getToggleSwitch("classes") && !command_line.getToggleSwitch("methods")) {
			Error(usage, "Must have at least one of -all, -project, -groups, -classes, or -methods");
			System.exit(1);
		}

		int mode_switch = 0;
		
		if (command_line.getToggleSwitch("csv")) {
			mode_switch++;
		}
		if (command_line.getToggleSwitch("txt")) {
			mode_switch++;
		}
		if (command_line.getToggleSwitch("xml")) {
			mode_switch++;
		}
		if (mode_switch != 1) {
			Error(usage, "Must have one and only one of -csv, -txt, or -xml");
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

		Logger.getLogger(OOMetrics.class).debug("Reading sources ...");

		List parameters = command_line.getParameters();
		if (parameters.size() == 0) {
			parameters.add(".");
		}

		ClassfileLoader loader = new AggregatingClassfileLoader();
		loader.addLoadListener(verbose_listener);
		loader.load(parameters);

		Logger.getLogger(OOMetrics.class).debug("Reading configuration ...");

		String project_name = command_line.getSingleSwitch("project-name");
		
		MetricsFactory factory;
		
		if (command_line.isPresent("configuration")) {
			factory = new MetricsFactory(project_name, new MetricsConfigurationLoader(command_line.getToggleSwitch("validate")).Load(command_line.getSingleSwitch("configuration")));
		} else {
			factory = new MetricsFactory(project_name, new MetricsConfigurationLoader(command_line.getToggleSwitch("validate")).Load(command_line.getSingleSwitch("default-configuration")));
		}

		Logger.getLogger(OOMetrics.class).debug("Computing metrics ...");

		com.jeantessier.metrics.MetricsGatherer gatherer = new com.jeantessier.metrics.MetricsGatherer(project_name, factory);
		if (command_line.isPresent("scope-includes-list") || command_line.isPresent("scope-excludes-list")) {
			gatherer.ScopeIncludes(CreateCollection(command_line.getMultipleSwitch("scope-includes-list"), command_line.getMultipleSwitch("scope-excludes-list")));
		}
		if (command_line.isPresent("filter-includes-list") || command_line.isPresent("filter-excludes-list")) {
			gatherer.FilterIncludes(CreateCollection(command_line.getMultipleSwitch("filter-includes-list"), command_line.getMultipleSwitch("filter-excludes-list")));
		}
		gatherer.addMetricsListener(verbose_listener);
		gatherer.visitClassfiles(loader.getAllClassfiles());
		
		if (command_line.isPresent("show-all-metrics")) {
			Iterator i;

			i = gatherer.MetricsFactory().AllClassMetrics().iterator();
			while (i.hasNext()) {
				gatherer.MetricsFactory().IncludeClassMetrics((Metrics) i.next());
			}

			i = gatherer.MetricsFactory().AllMethodMetrics().iterator();
			while (i.hasNext()) {
				gatherer.MetricsFactory().IncludeMethodMetrics((Metrics) i.next());
			}
		}

		Logger.getLogger(OOMetrics.class).debug("Printing results ...");
		verbose_listener.Print("Printing results ...");
		
		if (command_line.isPresent("csv")) {
			PrintCSVFiles(start, command_line, gatherer.MetricsFactory());
		} else if (command_line.isPresent("txt")) {
			PrintTextFile(start, command_line, gatherer.MetricsFactory());
		} else if (command_line.isPresent("xml")) {
			PrintXMLFile(start, command_line, gatherer.MetricsFactory());
		}

		Logger.getLogger(OOMetrics.class).debug("Done.");

		Date end = new Date();

		if (command_line.getToggleSwitch("time")) {
			System.err.println(OOMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verbose_listener.Close();
	}

	private static Collection CreateCollection(Collection includes, Collection excludes) throws IOException {
		Collection result = new HashSet();
		Iterator   i;
			
		i = includes.iterator();
		while (i.hasNext()) {
			BufferedReader reader = new BufferedReader(new FileReader(i.next().toString()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.add(line);
			}
			reader.close();
		}
		
		i = excludes.iterator();
		while (i.hasNext()) {
			BufferedReader reader = new BufferedReader(new FileReader(i.next().toString()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.remove(line);
			}
			reader.close();
		}
		
		return result;
	}

	private static void PrintCSVFiles(Date start, CommandLine command_line, MetricsFactory factory) throws IOException {
		MetricsComparator comparator = new MetricsComparator(command_line.getSingleSwitch("sort"));
		if (command_line.getToggleSwitch("reverse")) {
			comparator.Reverse();
		}

		List               metrics;
		Iterator           i;
		com.jeantessier.metrics.Printer printer;
		PrintWriter        out = new PrintWriter(new OutputStreamWriter(System.out));

		if (command_line.getToggleSwitch("project") || command_line.getToggleSwitch("all")) {
			if (command_line.isPresent("out")) {
				out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + "_project.csv"));
			} else {
				out.println("Project:");
			}
			
			metrics = new ArrayList(factory.ProjectMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().ProjectMeasurements());
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);
			
			if (command_line.isPresent("out")) {
				out.close();
			} else {
				out.println();
			}
		}

		if (command_line.getToggleSwitch("groups") || command_line.getToggleSwitch("all")) {
			if (command_line.isPresent("out")) {
				out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + "_groups.csv"));
			} else {
				out.println("Packages:");
			}

			metrics = new ArrayList(factory.GroupMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().GroupMeasurements());
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);
			
			if (command_line.isPresent("out")) {
				out.close();
			} else {
				out.println();
			}
		}

		if (command_line.getToggleSwitch("classes") || command_line.getToggleSwitch("all")) {
			if (command_line.isPresent("out")) {
				out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + "_classes.csv"));
			} else {
				out.println("Classes:");
			}

			metrics = new ArrayList(factory.ClassMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().ClassMeasurements());
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);
			
			if (command_line.isPresent("out")) {
				out.close();
			} else {
				out.println();
			}
		}

		if (command_line.getToggleSwitch("methods") || command_line.getToggleSwitch("all")) {
			if (command_line.isPresent("out")) {
				out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + "_methods.csv"));
			} else {
				out.println("Methods:");
			}

			metrics = new ArrayList(factory.MethodMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().MethodMeasurements());
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);
		
			if (command_line.isPresent("out")) {
				out.close();
			}
		}
	}

	private static void PrintTextFile(Date start, CommandLine command_line, MetricsFactory factory) throws IOException {
		PrintWriter out;
		if (command_line.isPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + ".txt"));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		MetricsComparator comparator = new MetricsComparator(command_line.getSingleSwitch("sort"));
		if (command_line.getToggleSwitch("reverse")) {
			comparator.Reverse();
		}

		List               metrics;
		Iterator           i;

		if (command_line.getToggleSwitch("project") || command_line.getToggleSwitch("all")) {
			out.println("Project metrics");
			out.println("---------------");
			metrics = new ArrayList(factory.ProjectMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().ProjectMeasurements());
			printer.ExpandCollectionMeasurements(command_line.getToggleSwitch("expand"));
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);

			out.println();
		}

		if (command_line.getToggleSwitch("groups") || command_line.getToggleSwitch("all")) {
			out.println("Group metrics");
			out.println("-------------");
			metrics = new ArrayList(factory.GroupMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().GroupMeasurements());
			printer.ExpandCollectionMeasurements(command_line.getToggleSwitch("expand"));
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);

			out.println();
		}

		if (command_line.getToggleSwitch("classes") || command_line.getToggleSwitch("all")) {
			out.println("Class metrics");
			out.println("-------------");
			metrics = new ArrayList(factory.ClassMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().ClassMeasurements());
			printer.ExpandCollectionMeasurements(command_line.getToggleSwitch("expand"));
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);

			out.println();
		}
		
		if (command_line.getToggleSwitch("methods") || command_line.getToggleSwitch("all")) {
			out.println("Method metrics");
			out.println("--------------");
			metrics = new ArrayList(factory.MethodMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().MethodMeasurements());
			printer.ExpandCollectionMeasurements(command_line.getToggleSwitch("expand"));
			printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
			printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
			if (command_line.isPresent("indent-text")) {
				printer.IndentText(command_line.getSingleSwitch("indent-text"));
			}

			printer.VisitMetrics(metrics);

			out.println();
		}
		
		out.close();
	}

	private static void PrintXMLFile(Date start, CommandLine command_line, MetricsFactory factory) throws IOException {
		PrintWriter out;
		if (command_line.isPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out") + ".xml"));
		} else {
			out = new PrintWriter(System.out);
		}

		MetricsComparator comparator = new MetricsComparator(command_line.getSingleSwitch("sort"));
		if (command_line.getToggleSwitch("reverse")) {
			comparator.Reverse();
		}

		List               metrics;
		Iterator           i;
		com.jeantessier.metrics.Printer printer;

		metrics = new ArrayList(factory.ProjectMetrics());
		Collections.sort(metrics, comparator);
		printer = new com.jeantessier.metrics.XMLPrinter(out, factory.Configuration(), command_line.getSingleSwitch("encoding"), command_line.getSingleSwitch("dtd-prefix"));
		printer.ShowEmptyMetrics(command_line.isPresent("show-empty-metrics"));
		printer.ShowHiddenMeasurements(command_line.isPresent("show-hidden-measurements"));
		if (command_line.isPresent("indent-text")) {
			printer.IndentText(command_line.getSingleSwitch("indent-text"));
		}

		printer.VisitMetrics(metrics);

		out.close();
	}
}
