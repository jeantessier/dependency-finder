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

public class DependencyReporter {
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

		command_line.AddToggleSwitch("xml");
		command_line.AddToggleSwitch("validate");
		command_line.AddSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
		command_line.AddSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
		command_line.AddSingleValueSwitch("indent-text");
		command_line.AddToggleSwitch("minimize");
		command_line.AddToggleSwitch("maximize");
		command_line.AddToggleSwitch("copy-only");
		command_line.AddToggleSwitch("time");
		command_line.AddSingleValueSwitch("out");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose",                  DEFAULT_LOGFILE);
		command_line.AddToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("DependencyReporter");
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
		}
		
		if (command_line.ToggleSwitch("version")) {
			Version();
		}

		if (command_line.ToggleSwitch("help") || command_line.ToggleSwitch("version")) {
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

		if (command_line.ToggleSwitch("maximize") && command_line.ToggleSwitch("minimize")) {
			Error(usage, "Only one of -maximize or -minimize allowed");
		}

		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		RegularExpressionSelectionCriteria scope_criteria = new RegularExpressionSelectionCriteria();
		
		scope_criteria.MatchPackage(command_line.ToggleSwitch("package-scope"));
		scope_criteria.MatchClass(command_line.ToggleSwitch("class-scope"));
		scope_criteria.MatchFeature(command_line.ToggleSwitch("feature-scope"));

		if (command_line.IsPresent("scope-includes") || (!command_line.IsPresent("package-scope-includes") && !command_line.IsPresent("class-scope-includes") && !command_line.IsPresent("feature-scope-includes"))) {
			// Only use the default if nothing else has been specified.
			scope_criteria.GlobalIncludes(command_line.MultipleSwitch("scope-includes"));
		}
		scope_criteria.GlobalExcludes(command_line.MultipleSwitch("scope-excludes"));
		scope_criteria.PackageIncludes(command_line.MultipleSwitch("package-scope-includes"));
		scope_criteria.PackageExcludes(command_line.MultipleSwitch("package-scope-excludes"));
		scope_criteria.ClassIncludes(command_line.MultipleSwitch("class-scope-includes"));
		scope_criteria.ClassExcludes(command_line.MultipleSwitch("class-scope-excludes"));
		scope_criteria.FeatureIncludes(command_line.MultipleSwitch("feature-scope-includes"));
		scope_criteria.FeatureExcludes(command_line.MultipleSwitch("feature-scope-excludes"));

		RegularExpressionSelectionCriteria filter_criteria = new RegularExpressionSelectionCriteria();

		filter_criteria.MatchPackage(command_line.ToggleSwitch("package-filter"));
		filter_criteria.MatchClass(command_line.ToggleSwitch("class-filter"));
		filter_criteria.MatchFeature(command_line.ToggleSwitch("feature-filter"));
		
		if (command_line.IsPresent("filter-includes") || (!command_line.IsPresent("package-filter-includes") && !command_line.IsPresent("class-filter-includes") && !command_line.IsPresent("feature-filter-includes"))) {
			// Only use the default if nothing else has been specified.
			filter_criteria.GlobalIncludes(command_line.MultipleSwitch("filter-includes"));
		}
		filter_criteria.GlobalExcludes(command_line.MultipleSwitch("filter-excludes"));
		filter_criteria.PackageIncludes(command_line.MultipleSwitch("package-filter-includes"));
		filter_criteria.PackageExcludes(command_line.MultipleSwitch("package-filter-excludes"));
		filter_criteria.ClassIncludes(command_line.MultipleSwitch("class-filter-includes"));
		filter_criteria.ClassExcludes(command_line.MultipleSwitch("class-filter-excludes"));
		filter_criteria.FeatureIncludes(command_line.MultipleSwitch("feature-filter-includes"));
		filter_criteria.FeatureExcludes(command_line.MultipleSwitch("feature-filter-excludes"));
	
		if (command_line.ToggleSwitch("all")) {
			scope_criteria.MatchPackage(true);
			scope_criteria.MatchClass(true);
			scope_criteria.MatchFeature(true);
			filter_criteria.MatchPackage(true);
			filter_criteria.MatchClass(true);
			filter_criteria.MatchFeature(true);
		}
	
		if (command_line.ToggleSwitch("p2p")) {
			scope_criteria.MatchPackage(true);
			filter_criteria.MatchPackage(true);
		}
	
		if (command_line.ToggleSwitch("c2p")) {
			scope_criteria.MatchClass(true);
			filter_criteria.MatchPackage(true);
		}
	
		if (command_line.ToggleSwitch("c2c")) {
			scope_criteria.MatchClass(true);
			filter_criteria.MatchClass(true);
		}
	
		if (command_line.ToggleSwitch("f2f")) {
			scope_criteria.MatchFeature(true);
			filter_criteria.MatchFeature(true);
		}
	
		if (command_line.IsPresent("includes")) {
			scope_criteria.GlobalIncludes(command_line.MultipleSwitch("includes"));
			filter_criteria.GlobalIncludes(command_line.MultipleSwitch("includes"));
		}
	
		if (command_line.IsPresent("excludes")) {
			scope_criteria.GlobalExcludes(command_line.MultipleSwitch("excludes"));
			filter_criteria.GlobalExcludes(command_line.MultipleSwitch("excludes"));
		}

		GraphCopier copier;
		if (command_line.ToggleSwitch("copy-only") || command_line.ToggleSwitch("maximize")) {
			SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scope_criteria, filter_criteria);
			copier = new GraphCopier(strategy);
		} else {
			copier = new GraphSummarizer(scope_criteria, filter_criteria);
		}
		
		Iterator i = command_line.Parameters().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Logger.getLogger(DependencyReporter.class).info("Reading " + filename);
			verbose_listener.Print("Reading " + filename);

			Collection packages;

			if (filename.endsWith(".xml")) {
				NodeLoader loader = new NodeLoader(command_line.ToggleSwitch("validate"));
				loader.addDependencyListener(verbose_listener);
				packages = loader.Load(filename).Packages().values();
			} else if (filename.endsWith(".ser")) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
				packages = (Collection) in.readObject();
			} else {
				packages = Collections.EMPTY_LIST;
			}

			Logger.getLogger(DependencyReporter.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");

			if (command_line.ToggleSwitch("maximize")) {
				new LinkMaximizer().TraverseNodes(packages);
			} else if (command_line.ToggleSwitch("minimize")) {
				new LinkMinimizer().TraverseNodes(packages);
			}

			copier.TraverseNodes(packages);
		}

		Logger.getLogger(DependencyReporter.class).info("Reporting " + copier.ScopeFactory().Packages().values().size() + " package(s) ...");
	
		verbose_listener.Print("Printing the graph ...");

		PrintWriter out;
		if (command_line.IsPresent("out")) {
			out = new PrintWriter(new FileWriter(command_line.SingleSwitch("out")));
		} else {
			out = new PrintWriter(System.out);
		}

		Printer printer;
		if (command_line.IsPresent("xml")) {
			printer = new XMLPrinter(out, command_line.SingleSwitch("encoding"), command_line.SingleSwitch("dtd-prefix"));
		} else {
			printer = new TextPrinter(out);
		}
			
		if (command_line.IsPresent("indent-text")) {
			printer.IndentText(command_line.SingleSwitch("indent-text"));
		}
	    
		printer.TraverseNodes(copier.ScopeFactory().Packages().values());

		out.close();

		Date end = new Date();

		if (command_line.ToggleSwitch("time")) {
			System.err.println(DependencyReporter.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verbose_listener.Close();
	}
}
