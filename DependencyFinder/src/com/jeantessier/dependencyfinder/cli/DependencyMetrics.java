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

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

public class DependencyMetrics {
	public static final String DEFAULT_INCLUDES        = "//";
	public static final String DEFAULT_SCOPE_INCLUDES  = "//";
	public static final String DEFAULT_FILTER_INCLUDES = "//";
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
		System.err.println("-all shorthand for the combination:");
		System.err.println("    -package-scope");
		System.err.println("    -class-scope");
		System.err.println("    -feature-scope");
		System.err.println("    -package-filter");
		System.err.println("    -class-filter");
		System.err.println("    -feature-filter");
		System.err.println();
		System.err.println("-p2p shorthand for the combination:");
		System.err.println("    -package-scope");
		System.err.println("    -package-filter");
		System.err.println();
		System.err.println("-c2p shorthand for the combination:");
		System.err.println("    -class-scope");
		System.err.println("    -package-filter");
		System.err.println();
		System.err.println("-c2c shorthand for the combination:");
		System.err.println("    -class-scope");
		System.err.println("    -class-filter");
		System.err.println();
		System.err.println("-f2f shorthand for the combination:");
		System.err.println("    -feature-scope");
		System.err.println("    -feature-filter");
		System.err.println();
		System.err.println("-includes \"str\" shorthand for the combination:");
		System.err.println("    -scope-incldues \"str\"");
		System.err.println("    -filter-incldues \"str\"");
		System.err.println();
		System.err.println("-excludes \"str\" shorthand for the combination:");
		System.err.println("    -scope-excldues \"str\"");
		System.err.println("    -filter-excldues \"str\"");
		System.err.println();
		System.err.println("-chart-all shorthand for the combination:");
		System.err.println("    -chart-classes-per-package");
		System.err.println("    -chart-features-per-class");
		System.err.println("    -chart-inbounds-per-package");
		System.err.println("    -chart-outbounds-per-package");
		System.err.println("    -chart-inbounds-per-class");
		System.err.println("    -chart-outbounds-per-class");
		System.err.println("    -chart-inbounds-per-feature");
		System.err.println("    -chart-outbounds-per-feature");
		System.err.println();
		System.err.println("-chart-inbounds shorthand for the combination:");
		System.err.println("    -chart-inbounds-per-package");
		System.err.println("    -chart-inbounds-per-class");
		System.err.println("    -chart-inbounds-per-feature");
		System.err.println();
		System.err.println("-chart-outbounds shorthand for the combination:");
		System.err.println("    -chart-outbounds-per-package");
		System.err.println("    -chart-outbounds-per-class");
		System.err.println("    -chart-outbounds-per-feature");
		System.err.println();
		System.err.println("-chart-packages shorthand for the combination:");
		System.err.println("    -chart-classes-per-package");
		System.err.println("    -chart-inbounds-per-package");
		System.err.println("    -chart-outbounds-per-package");
		System.err.println();
		System.err.println("-chart-classes shorthand for the combination:");
		System.err.println("    -chart-features-per-class");
		System.err.println("    -chart-inbounds-per-class");
		System.err.println("    -chart-outbounds-per-class");
		System.err.println();
		System.err.println("-chart-features shorthand for the combination:");
		System.err.println("    -chart-inbounds-per-feature");
		System.err.println("    -chart-outbounds-per-feature");
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
		CommandLine command_line = new CommandLine(new AtLeastParameterStrategy(1));
		command_line.AddMultipleValuesSwitch("scope-includes",          DEFAULT_SCOPE_INCLUDES);
		command_line.AddMultipleValuesSwitch("scope-excludes");
		command_line.AddToggleSwitch("package-scope");
		command_line.AddMultipleValuesSwitch("package-scope-includes");
		command_line.AddMultipleValuesSwitch("package-scope-excludes");
		command_line.AddToggleSwitch("class-scope");
		command_line.AddMultipleValuesSwitch("class-scope-includes");
		command_line.AddMultipleValuesSwitch("class-scope-excludes");
		command_line.AddToggleSwitch("feature-scope");
		command_line.AddMultipleValuesSwitch("feature-scope-includes");
		command_line.AddMultipleValuesSwitch("feature-scope-excludes");
		command_line.AddMultipleValuesSwitch("filter-includes",         DEFAULT_FILTER_INCLUDES);
		command_line.AddMultipleValuesSwitch("filter-excludes");
		command_line.AddToggleSwitch("package-filter");
		command_line.AddMultipleValuesSwitch("package-filter-includes");
		command_line.AddMultipleValuesSwitch("package-filter-excludes");
		command_line.AddToggleSwitch("class-filter");
		command_line.AddMultipleValuesSwitch("class-filter-includes");
		command_line.AddMultipleValuesSwitch("class-filter-excludes");
		command_line.AddToggleSwitch("feature-filter");
		command_line.AddMultipleValuesSwitch("feature-filter-includes");
		command_line.AddMultipleValuesSwitch("feature-filter-excludes");

		command_line.AddToggleSwitch("all");
		command_line.AddToggleSwitch("p2p");
		command_line.AddToggleSwitch("c2p");
		command_line.AddToggleSwitch("c2c");
		command_line.AddToggleSwitch("f2f");
		command_line.AddMultipleValuesSwitch("includes",                DEFAULT_INCLUDES);
		command_line.AddMultipleValuesSwitch("excludes");

		command_line.AddToggleSwitch("list");
		command_line.AddToggleSwitch("chart-classes-per-package");
		command_line.AddToggleSwitch("chart-features-per-class");
		command_line.AddToggleSwitch("chart-inbounds-per-package");
		command_line.AddToggleSwitch("chart-outbounds-per-package");
		command_line.AddToggleSwitch("chart-inbounds-per-class");
		command_line.AddToggleSwitch("chart-outbounds-per-class");
		command_line.AddToggleSwitch("chart-inbounds-per-feature");
		command_line.AddToggleSwitch("chart-outbounds-per-feature");
		command_line.AddToggleSwitch("chart-inbounds");
		command_line.AddToggleSwitch("chart-outbounds");
		command_line.AddToggleSwitch("chart-packages");
		command_line.AddToggleSwitch("chart-classes");
		command_line.AddToggleSwitch("chart-features");
		command_line.AddToggleSwitch("chart-all");
		command_line.AddToggleSwitch("time");
		command_line.AddToggleSwitch("validate");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",     DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("DependencyMetrics");
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
			Log(Logger.getLogger("com.jeantessier.dependency"), command_line.OptionalSwitch("verbose"));
		}

		if (command_line.IsPresent("trace")) {
			Log(Logger.getLogger("com.jeantessier.dependencyfinder.cli"), command_line.OptionalSwitch("verbose"));
			Log(Logger.getLogger("com.jeantessier.classreader"), command_line.OptionalSwitch("trace"));
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		MetricsReport reporter = new MetricsReport();
		
		reporter.ListElements(command_line.ToggleSwitch("list"));
		reporter.ClassesPerPackageChart(command_line.ToggleSwitch("chart-classes-per-package"));
		reporter.FeaturesPerClassChart(command_line.ToggleSwitch("chart-features-per-class"));
		reporter.InboundsPerPackageChart(command_line.ToggleSwitch("chart-inbounds-per-package"));
		reporter.OutboundsPerPackageChart(command_line.ToggleSwitch("chart-outbounds-per-package"));
		reporter.InboundsPerClassChart(command_line.ToggleSwitch("chart-inbounds-per-class"));
		reporter.OutboundsPerClassChart(command_line.ToggleSwitch("chart-outbounds-per-class"));
		reporter.InboundsPerFeatureChart(command_line.ToggleSwitch("chart-inbounds-per-feature"));
		reporter.OutboundsPerFeatureChart(command_line.ToggleSwitch("chart-outbounds-per-feature"));

		if (command_line.ToggleSwitch("chart-all")) {
			reporter.ClassesPerPackageChart(true);
			reporter.FeaturesPerClassChart(true);
			reporter.InboundsPerPackageChart(true);
			reporter.OutboundsPerPackageChart(true);
			reporter.InboundsPerClassChart(true);
			reporter.OutboundsPerClassChart(true);
			reporter.InboundsPerFeatureChart(true);
			reporter.OutboundsPerFeatureChart(true);
		}
		
		if (command_line.ToggleSwitch("chart-inbounds")) {
			reporter.InboundsPerPackageChart(true);
			reporter.InboundsPerClassChart(true);
			reporter.InboundsPerFeatureChart(true);
		}
		
		if (command_line.ToggleSwitch("chart-outbounds")) {
			reporter.OutboundsPerPackageChart(true);
			reporter.OutboundsPerClassChart(true);
			reporter.OutboundsPerFeatureChart(true);
		}
		
		if (command_line.ToggleSwitch("chart-packages")) {
			reporter.ClassesPerPackageChart(true);
			reporter.InboundsPerPackageChart(true);
			reporter.OutboundsPerPackageChart(true);
		}
		
		if (command_line.ToggleSwitch("chart-classes")) {
			reporter.FeaturesPerClassChart(true);
			reporter.InboundsPerClassChart(true);
			reporter.OutboundsPerClassChart(true);
		}
		
		if (command_line.ToggleSwitch("chart-features")) {
			reporter.InboundsPerFeatureChart(true);
			reporter.OutboundsPerFeatureChart(true);
		}

		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
	
		strategy.PackageScope(false);
		strategy.ClassScope(false);
		strategy.FeatureScope(false);
		strategy.PackageFilter(false);
		strategy.ClassFilter(false);
		strategy.FeatureFilter(false);

		if (command_line.IsPresent("scope-includes") || (!command_line.IsPresent("package-scope-includes") && !command_line.IsPresent("class-scope-includes") && !command_line.IsPresent("feature-scope-includes"))) {
			// Only use the default if nothing else has been specified.
			strategy.ScopeIncludes(command_line.MultipleSwitch("scope-includes"));
		}
		strategy.ScopeExcludes(command_line.MultipleSwitch("scope-excludes"));
		strategy.PackageScope(command_line.ToggleSwitch("package-scope"));
		strategy.PackageScopeIncludes(command_line.MultipleSwitch("package-scope-includes"));
		strategy.PackageScopeExcludes(command_line.MultipleSwitch("package-scope-excludes"));
		strategy.ClassScope(command_line.ToggleSwitch("class-scope"));
		strategy.ClassScopeIncludes(command_line.MultipleSwitch("class-scope-includes"));
		strategy.ClassScopeExcludes(command_line.MultipleSwitch("class-scope-excludes"));
		strategy.FeatureScope(command_line.ToggleSwitch("feature-scope"));
		strategy.FeatureScopeIncludes(command_line.MultipleSwitch("feature-scope-includes"));
		strategy.FeatureScopeExcludes(command_line.MultipleSwitch("feature-scope-excludes"));
	
		if (command_line.IsPresent("filter-includes") || (!command_line.IsPresent("package-filter-includes") && !command_line.IsPresent("class-filter-includes") && !command_line.IsPresent("feature-filter-includes"))) {
			// Only use the default if nothing else has been specified.
			strategy.FilterIncludes(command_line.MultipleSwitch("filter-includes"));
		}
		strategy.FilterExcludes(command_line.MultipleSwitch("filter-excludes"));
		strategy.PackageFilter(command_line.ToggleSwitch("package-filter"));
		strategy.PackageFilterIncludes(command_line.MultipleSwitch("package-filter-includes"));
		strategy.PackageFilterExcludes(command_line.MultipleSwitch("package-filter-excludes"));
		strategy.ClassFilter(command_line.ToggleSwitch("class-filter"));
		strategy.ClassFilterIncludes(command_line.MultipleSwitch("class-filter-includes"));
		strategy.ClassFilterExcludes(command_line.MultipleSwitch("class-filter-excludes"));
		strategy.FeatureFilter(command_line.ToggleSwitch("feature-filter"));
		strategy.FeatureFilterIncludes(command_line.MultipleSwitch("feature-filter-includes"));
		strategy.FeatureFilterExcludes(command_line.MultipleSwitch("feature-filter-excludes"));
	
		if (command_line.ToggleSwitch("all")) {
			strategy.PackageScope(true);
			strategy.ClassScope(true);
			strategy.FeatureScope(true);
			strategy.PackageFilter(true);
			strategy.ClassFilter(true);
			strategy.FeatureFilter(true);
		}
	
		if (command_line.ToggleSwitch("p2p")) {
			strategy.PackageScope(true);
			strategy.PackageFilter(true);
		}
	
		if (command_line.ToggleSwitch("c2p")) {
			strategy.ClassScope(true);
			strategy.PackageFilter(true);
		}
	
		if (command_line.ToggleSwitch("c2c")) {
			strategy.ClassScope(true);
			strategy.ClassFilter(true);
		}
	
		if (command_line.ToggleSwitch("f2f")) {
			strategy.FeatureScope(true);
			strategy.FeatureFilter(true);
		}
	
		if (command_line.IsPresent("includes")) {
			strategy.ScopeIncludes(command_line.MultipleSwitch("includes"));
			strategy.FilterIncludes(command_line.MultipleSwitch("includes"));
		}
	
		if (command_line.IsPresent("excludes")) {
			strategy.ScopeExcludes(command_line.MultipleSwitch("excludes"));
			strategy.FilterExcludes(command_line.MultipleSwitch("excludes"));
		}

		MetricsGatherer metrics = new MetricsGatherer(strategy);

		Iterator i = command_line.Parameters().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Logger.getLogger(DependencyMetrics.class).info("Reading " + filename);

			Collection packages;

			if (filename.endsWith(".xml")) {
				packages = new NodeLoader(command_line.ToggleSwitch("validate")).Load(filename).Packages().values();
			} else if (filename.endsWith(".ser")) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
				packages = (Collection) in.readObject();
			} else {
				packages = Collections.EMPTY_LIST;
			}

			Logger.getLogger(DependencyMetrics.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");
	    
			// Run it on separate input files instead!
			// new LinkMaximizer().TraverseNodes(packages);

			metrics.TraverseNodes(packages);
		}

		Logger.getLogger(DependencyMetrics.class).info("Reporting " + metrics.Packages().size() + " package(s) ...");

		reporter.Process(metrics);
		
		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		}

		out.print(reporter);
		
		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			out.println();
			out.println(DependencyMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		out.flush();
		out.close();
	}
}
