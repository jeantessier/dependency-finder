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

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class DependencyClosure {
	public static final String DEFAULT_INCLUDES        = "//";
	public static final String DEFAULT_SCOPE_INCLUDES  = "//";
	public static final String DEFAULT_FILTER_INCLUDES = "//";
	public static final String DEFAULT_LOGFILE         = "System.out";

	public static void showError(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		showError(clu);
	}

	public static void showError(CommandLineUsage clu) {
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
		System.err.println("    -scope-includes \"str\"");
		System.err.println("    -filter-includes \"str\"");
		System.err.println();
		System.err.println("-excludes \"str\" shorthand for the combination:");
		System.err.println("    -scope-excludes \"str\"");
		System.err.println("    -filter-excludes \"str\"");
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
		CommandLine commandLine = new CommandLine(new AtLeastParameterStrategy(1));
		commandLine.addMultipleValuesSwitch("scope-includes",          DEFAULT_SCOPE_INCLUDES);
		commandLine.addMultipleValuesSwitch("scope-excludes");
		commandLine.addToggleSwitch("package-scope");
		commandLine.addMultipleValuesSwitch("package-scope-includes");
		commandLine.addMultipleValuesSwitch("package-scope-excludes");
		commandLine.addToggleSwitch("class-scope");
		commandLine.addMultipleValuesSwitch("class-scope-includes");
		commandLine.addMultipleValuesSwitch("class-scope-excludes");
		commandLine.addToggleSwitch("feature-scope");
		commandLine.addMultipleValuesSwitch("feature-scope-includes");
		commandLine.addMultipleValuesSwitch("feature-scope-excludes");
		commandLine.addMultipleValuesSwitch("filter-includes",         DEFAULT_FILTER_INCLUDES);
		commandLine.addMultipleValuesSwitch("filter-excludes");
		commandLine.addToggleSwitch("package-filter");
		commandLine.addMultipleValuesSwitch("package-filter-includes");
		commandLine.addMultipleValuesSwitch("package-filter-excludes");
		commandLine.addToggleSwitch("class-filter");
		commandLine.addMultipleValuesSwitch("class-filter-includes");
		commandLine.addMultipleValuesSwitch("class-filter-excludes");
		commandLine.addToggleSwitch("feature-filter");
		commandLine.addMultipleValuesSwitch("feature-filter-includes");
		commandLine.addMultipleValuesSwitch("feature-filter-excludes");

		commandLine.addToggleSwitch("all");
		commandLine.addToggleSwitch("p2p");
		commandLine.addToggleSwitch("c2p");
		commandLine.addToggleSwitch("c2c");
		commandLine.addToggleSwitch("f2f");
		commandLine.addMultipleValuesSwitch("includes",                DEFAULT_INCLUDES);
		commandLine.addMultipleValuesSwitch("excludes");

		commandLine.addOptionalValueSwitch("maximum-inbound-depth");
		commandLine.addOptionalValueSwitch("maximum-outbound-depth");
		
		commandLine.addToggleSwitch("xml");
		commandLine.addToggleSwitch("validate");
		commandLine.addSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
		commandLine.addSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
		commandLine.addSingleValueSwitch("indent-text");
		commandLine.addToggleSwitch("time");
		commandLine.addSingleValueSwitch("out");
		commandLine.addToggleSwitch("help");
		commandLine.addOptionalValueSwitch("verbose",                  DEFAULT_LOGFILE);
		commandLine.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("DependencyClosure");
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
				verboseListener.getWriter(System.out);
			} else {
				verboseListener.getWriter(new FileWriter(commandLine.getOptionalSwitch("verbose")));
			}
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		RegularExpressionSelectionCriteria scopeCriteria = new RegularExpressionSelectionCriteria();
		
		scopeCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-scope"));
		scopeCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-scope"));
		scopeCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-scope"));

		if (commandLine.isPresent("scope-includes") || (!commandLine.isPresent("package-scope-includes") && !commandLine.isPresent("class-scope-includes") && !commandLine.isPresent("feature-scope-includes"))) {
			// Only use the default if nothing else has been specified.
			scopeCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("scope-includes"));
		}
		scopeCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("scope-excludes"));
		scopeCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-scope-includes"));
		scopeCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-scope-excludes"));
		scopeCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-scope-includes"));
		scopeCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-scope-excludes"));
		scopeCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-scope-includes"));
		scopeCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-scope-excludes"));

		RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();

		filterCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-filter"));
		filterCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-filter"));
		filterCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-filter"));
		
		if (commandLine.isPresent("filter-includes") || (!commandLine.isPresent("package-filter-includes") && !commandLine.isPresent("class-filter-includes") && !commandLine.isPresent("feature-filter-includes"))) {
			// Only use the default if nothing else has been specified.
			filterCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("filter-includes"));
		}
		filterCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("filter-excludes"));
		filterCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-filter-includes"));
		filterCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-filter-excludes"));
		filterCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-filter-includes"));
		filterCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-filter-excludes"));
		filterCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-filter-includes"));
		filterCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-filter-excludes"));
	
		if (commandLine.getToggleSwitch("all")) {
			scopeCriteria.setMatchingPackages(true);
			scopeCriteria.setMatchingClasses(true);
			scopeCriteria.setMatchingFeatures(true);
			filterCriteria.setMatchingPackages(true);
			filterCriteria.setMatchingClasses(true);
			filterCriteria.setMatchingFeatures(true);
		}
	
		if (commandLine.getToggleSwitch("p2p")) {
			scopeCriteria.setMatchingPackages(true);
			filterCriteria.setMatchingPackages(true);
		}
	
		if (commandLine.getToggleSwitch("c2p")) {
			scopeCriteria.setMatchingClasses(true);
			filterCriteria.setMatchingPackages(true);
		}
	
		if (commandLine.getToggleSwitch("c2c")) {
			scopeCriteria.setMatchingClasses(true);
			filterCriteria.setMatchingClasses(true);
		}
	
		if (commandLine.getToggleSwitch("f2f")) {
			scopeCriteria.setMatchingFeatures(true);
			filterCriteria.setMatchingFeatures(true);
		}
	
		if (commandLine.isPresent("includes")) {
			scopeCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("includes"));
			filterCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("includes"));
		}
	
		if (commandLine.isPresent("excludes")) {
			scopeCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("excludes"));
			filterCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("excludes"));
		}

		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
		TransitiveClosure selector = new TransitiveClosure(strategy);

		try {
			if (commandLine.isPresent("maximum-inbound-depth")) {
				selector.setMaximumInboundDepth(Long.parseLong(commandLine.getSingleSwitch("maximum-inbound-depth")));
			}
		} catch (NumberFormatException ex) {
			selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		}

		try {
			if (commandLine.isPresent("maximum-outbound-depth")) {
				selector.setMaximumOutboundDepth(Long.parseLong(commandLine.getSingleSwitch("maximum-outbound-depth")));
			}
		} catch (NumberFormatException ex) {
			selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		}
		
		Iterator i = commandLine.getParameters().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Logger.getLogger(DependencyClosure.class).info("Reading " + filename);
			verboseListener.print("Reading " + filename);

			Collection packages = Collections.EMPTY_LIST;

			if (filename.endsWith(".xml")) {
				NodeLoader loader = new NodeLoader(commandLine.getToggleSwitch("validate"));
				loader.addDependencyListener(verboseListener);
				packages = loader.load(filename).getPackages().values();
			}

			Logger.getLogger(DependencyClosure.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");

			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.traverseNodes(packages);

			selector.traverseNodes(packages);
		}


		Logger.getLogger(DependencyClosure.class).info("Reporting " + selector.getFactory().getPackages().values().size() + " package(s) ...");
	
		verboseListener.print("Printing the graph ...");

		PrintWriter out;
		if (commandLine.isPresent("out")) {
			out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(System.out);
		}

		Printer printer;
		if (commandLine.isPresent("xml")) {
			printer = new XMLPrinter(out, commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
		} else {
			printer = new TextPrinter(out);
		}

		if (commandLine.isPresent("indent-text")) {
			printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
		}

		printer.traverseNodes(selector.getFactory().getPackages().values());

		out.close();

		Date end = new Date();

		if (commandLine.getToggleSwitch("time")) {
			System.err.println(DependencyClosure.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verboseListener.close();
	}
}
