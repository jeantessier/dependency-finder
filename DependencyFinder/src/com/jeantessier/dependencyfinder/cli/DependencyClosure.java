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
		CommandLine command_line = new CommandLine(new AtLeastParameterStrategy(1));
		command_line.addMultipleValuesSwitch("scope-includes",          DEFAULT_SCOPE_INCLUDES);
		command_line.addMultipleValuesSwitch("scope-excludes");
		command_line.addToggleSwitch("package-scope");
		command_line.addMultipleValuesSwitch("package-scope-includes");
		command_line.addMultipleValuesSwitch("package-scope-excludes");
		command_line.addToggleSwitch("class-scope");
		command_line.addMultipleValuesSwitch("class-scope-includes");
		command_line.addMultipleValuesSwitch("class-scope-excludes");
		command_line.addToggleSwitch("feature-scope");
		command_line.addMultipleValuesSwitch("feature-scope-includes");
		command_line.addMultipleValuesSwitch("feature-scope-excludes");
		command_line.addMultipleValuesSwitch("filter-includes",         DEFAULT_FILTER_INCLUDES);
		command_line.addMultipleValuesSwitch("filter-excludes");
		command_line.addToggleSwitch("package-filter");
		command_line.addMultipleValuesSwitch("package-filter-includes");
		command_line.addMultipleValuesSwitch("package-filter-excludes");
		command_line.addToggleSwitch("class-filter");
		command_line.addMultipleValuesSwitch("class-filter-includes");
		command_line.addMultipleValuesSwitch("class-filter-excludes");
		command_line.addToggleSwitch("feature-filter");
		command_line.addMultipleValuesSwitch("feature-filter-includes");
		command_line.addMultipleValuesSwitch("feature-filter-excludes");

		command_line.addToggleSwitch("all");
		command_line.addToggleSwitch("p2p");
		command_line.addToggleSwitch("c2p");
		command_line.addToggleSwitch("c2c");
		command_line.addToggleSwitch("f2f");
		command_line.addMultipleValuesSwitch("includes",                DEFAULT_INCLUDES);
		command_line.addMultipleValuesSwitch("excludes");

		command_line.addOptionalValueSwitch("maximum-inbound-depth");
		command_line.addOptionalValueSwitch("maximum-outbound-depth");
		
		command_line.addToggleSwitch("xml");
		command_line.addToggleSwitch("validate");
		command_line.addSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
		command_line.addSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
		command_line.addSingleValueSwitch("indent-text");
		command_line.addToggleSwitch("time");
		command_line.addSingleValueSwitch("out");
		command_line.addToggleSwitch("help");
		command_line.addOptionalValueSwitch("verbose",                  DEFAULT_LOGFILE);
		command_line.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("DependencyClosure");
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

		RegularExpressionSelectionCriteria scope_criteria = new RegularExpressionSelectionCriteria();
		
		scope_criteria.setMatchingPackages(command_line.getToggleSwitch("package-scope"));
		scope_criteria.setMatchingClasses(command_line.getToggleSwitch("class-scope"));
		scope_criteria.setMatchingFeatures(command_line.getToggleSwitch("feature-scope"));

		if (command_line.isPresent("scope-includes") || (!command_line.isPresent("package-scope-includes") && !command_line.isPresent("class-scope-includes") && !command_line.isPresent("feature-scope-includes"))) {
			// Only use the default if nothing else has been specified.
			scope_criteria.setGlobalIncludes(command_line.getMultipleSwitch("scope-includes"));
		}
		scope_criteria.setGlobalExcludes(command_line.getMultipleSwitch("scope-excludes"));
		scope_criteria.setPackageIncludes(command_line.getMultipleSwitch("package-scope-includes"));
		scope_criteria.setPackageExcludes(command_line.getMultipleSwitch("package-scope-excludes"));
		scope_criteria.setClassIncludes(command_line.getMultipleSwitch("class-scope-includes"));
		scope_criteria.setClassExcludes(command_line.getMultipleSwitch("class-scope-excludes"));
		scope_criteria.setFeatureIncludes(command_line.getMultipleSwitch("feature-scope-includes"));
		scope_criteria.setFeatureExcludes(command_line.getMultipleSwitch("feature-scope-excludes"));

		RegularExpressionSelectionCriteria filter_criteria = new RegularExpressionSelectionCriteria();

		filter_criteria.setMatchingPackages(command_line.getToggleSwitch("package-filter"));
		filter_criteria.setMatchingClasses(command_line.getToggleSwitch("class-filter"));
		filter_criteria.setMatchingFeatures(command_line.getToggleSwitch("feature-filter"));
		
		if (command_line.isPresent("filter-includes") || (!command_line.isPresent("package-filter-includes") && !command_line.isPresent("class-filter-includes") && !command_line.isPresent("feature-filter-includes"))) {
			// Only use the default if nothing else has been specified.
			filter_criteria.setGlobalIncludes(command_line.getMultipleSwitch("filter-includes"));
		}
		filter_criteria.setGlobalExcludes(command_line.getMultipleSwitch("filter-excludes"));
		filter_criteria.setPackageIncludes(command_line.getMultipleSwitch("package-filter-includes"));
		filter_criteria.setPackageExcludes(command_line.getMultipleSwitch("package-filter-excludes"));
		filter_criteria.setClassIncludes(command_line.getMultipleSwitch("class-filter-includes"));
		filter_criteria.setClassExcludes(command_line.getMultipleSwitch("class-filter-excludes"));
		filter_criteria.setFeatureIncludes(command_line.getMultipleSwitch("feature-filter-includes"));
		filter_criteria.setFeatureExcludes(command_line.getMultipleSwitch("feature-filter-excludes"));
	
		if (command_line.getToggleSwitch("all")) {
			scope_criteria.setMatchingPackages(true);
			scope_criteria.setMatchingClasses(true);
			scope_criteria.setMatchingFeatures(true);
			filter_criteria.setMatchingPackages(true);
			filter_criteria.setMatchingClasses(true);
			filter_criteria.setMatchingFeatures(true);
		}
	
		if (command_line.getToggleSwitch("p2p")) {
			scope_criteria.setMatchingPackages(true);
			filter_criteria.setMatchingPackages(true);
		}
	
		if (command_line.getToggleSwitch("c2p")) {
			scope_criteria.setMatchingClasses(true);
			filter_criteria.setMatchingPackages(true);
		}
	
		if (command_line.getToggleSwitch("c2c")) {
			scope_criteria.setMatchingClasses(true);
			filter_criteria.setMatchingClasses(true);
		}
	
		if (command_line.getToggleSwitch("f2f")) {
			scope_criteria.setMatchingFeatures(true);
			filter_criteria.setMatchingFeatures(true);
		}
	
		if (command_line.isPresent("includes")) {
			scope_criteria.setGlobalIncludes(command_line.getMultipleSwitch("includes"));
			filter_criteria.setGlobalIncludes(command_line.getMultipleSwitch("includes"));
		}
	
		if (command_line.isPresent("excludes")) {
			scope_criteria.setGlobalExcludes(command_line.getMultipleSwitch("excludes"));
			filter_criteria.setGlobalExcludes(command_line.getMultipleSwitch("excludes"));
		}

		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scope_criteria, filter_criteria);
		TransitiveClosure selector = new TransitiveClosure(strategy);

		try {
			if (command_line.isPresent("maximum-inbound-depth")) {
				selector.setMaximumInboundDepth(Long.parseLong(command_line.getSingleSwitch("maximum-inbound-depth")));
			}
		} catch (NumberFormatException ex) {
			selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		}

		try {
			if (command_line.isPresent("maximum-outbound-depth")) {
				selector.setMaximumOutboundDepth(Long.parseLong(command_line.getSingleSwitch("maximum-outbound-depth")));
			}
		} catch (NumberFormatException ex) {
			selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
		}
		
		Iterator i = command_line.getParameters().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Logger.getLogger(DependencyClosure.class).info("Reading " + filename);
			verbose_listener.Print("Reading " + filename);

			Collection packages = Collections.EMPTY_LIST;

			if (filename.endsWith(".xml")) {
				NodeLoader loader = new NodeLoader(command_line.getToggleSwitch("validate"));
				loader.addDependencyListener(verbose_listener);
				packages = loader.load(filename).getPackages().values();
			}

			Logger.getLogger(DependencyClosure.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");

			LinkMaximizer maximizer = new LinkMaximizer();
			maximizer.traverseNodes(packages);

			selector.traverseNodes(packages);
		}


		Logger.getLogger(DependencyClosure.class).info("Reporting " + selector.getFactory().getPackages().values().size() + " package(s) ...");
	
		verbose_listener.Print("Printing the graph ...");

		PrintWriter out;
		if (command_line.isPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.getSingleSwitch("out")));
		} else {
			out = new PrintWriter(System.out);
		}

		Printer printer;
		if (command_line.isPresent("xml")) {
			printer = new XMLPrinter(out, command_line.getSingleSwitch("encoding"), command_line.getSingleSwitch("dtd-prefix"));
		} else {
			printer = new TextPrinter(out);
		}

		if (command_line.isPresent("indent-text")) {
			printer.setIndentText(command_line.getSingleSwitch("indent-text"));
		}

		printer.traverseNodes(selector.getFactory().getPackages().values());

		out.close();

		Date end = new Date();

		if (command_line.getToggleSwitch("time")) {
			System.err.println(DependencyClosure.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verbose_listener.Close();
	}
}
