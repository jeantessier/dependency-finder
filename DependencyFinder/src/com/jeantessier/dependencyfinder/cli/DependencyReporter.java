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

public class DependencyReporter {
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
		System.err.println("-show-all shorthand for the combination:");
		System.err.println("    -show-inbounds");
		System.err.println("    -show-outbounds");
		System.err.println("    -show-empty-nodes");
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

		commandLine.addMultipleValuesSwitch("scope-includes-list");
		commandLine.addMultipleValuesSwitch("scope-excludes-list");
		commandLine.addMultipleValuesSwitch("filter-includes-list");
		commandLine.addMultipleValuesSwitch("filter-excludes-list");

		commandLine.addToggleSwitch("show-all");
		commandLine.addToggleSwitch("show-inbounds");
		commandLine.addToggleSwitch("show-outbounds");
		commandLine.addToggleSwitch("show-empty-nodes");
		
		commandLine.addToggleSwitch("xml");
		commandLine.addToggleSwitch("validate");
		commandLine.addSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
		commandLine.addSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
		commandLine.addSingleValueSwitch("indent-text");
		commandLine.addToggleSwitch("minimize");
		commandLine.addToggleSwitch("maximize");
		commandLine.addToggleSwitch("copy-only");
		commandLine.addToggleSwitch("time");
		commandLine.addSingleValueSwitch("out");
		commandLine.addToggleSwitch("help");
		commandLine.addOptionalValueSwitch("verbose",                  DEFAULT_LOGFILE);
		commandLine.addToggleSwitch("version");

		CommandLineUsage usage = new CommandLineUsage("DependencyReporter");
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

		if (commandLine.getToggleSwitch("maximize") && commandLine.getToggleSwitch("minimize")) {
			showError(usage, "Only one of -maximize or -minimize allowed");
		}

		if (hasScopeRegularExpressionSwitches(commandLine) && hasScopeListSwitches(commandLine)) {
			showError(usage, "You can use switches for regular expressions or lists for scope, but not at the same time");
		}

		if (hasFilterRegularExpressionSwitches(commandLine) && hasFilterListSwitches(commandLine)) {
			showError(usage, "You can use switches for regular expressions or lists for filter, but not at the same time");
		}
		
		/*
		 *  Beginning of main processing
		 */

		Date start = new Date();

		SelectionCriteria scopeCriteria = new ComprehensiveSelectionCriteria();

		if (hasScopeRegularExpressionSwitches(commandLine)) {
			RegularExpressionSelectionCriteria regularExpressionScopeCriteria = new RegularExpressionSelectionCriteria();
			
			regularExpressionScopeCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-scope"));
			regularExpressionScopeCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-scope"));
			regularExpressionScopeCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-scope"));
			
			if (commandLine.isPresent("scope-includes") || (!commandLine.isPresent("package-scope-includes") && !commandLine.isPresent("class-scope-includes") && !commandLine.isPresent("feature-scope-includes"))) {
				// Only use the default if nothing else has been specified.
				regularExpressionScopeCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("scope-includes"));
			}
			regularExpressionScopeCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("scope-excludes"));
			regularExpressionScopeCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-scope-includes"));
			regularExpressionScopeCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-scope-excludes"));
			regularExpressionScopeCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-scope-includes"));
			regularExpressionScopeCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-scope-excludes"));
			regularExpressionScopeCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-scope-includes"));
			regularExpressionScopeCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-scope-excludes"));
			
			if (commandLine.getToggleSwitch("all")) {
				regularExpressionScopeCriteria.setMatchingPackages(true);
				regularExpressionScopeCriteria.setMatchingClasses(true);
				regularExpressionScopeCriteria.setMatchingFeatures(true);
			}
			
			if (commandLine.getToggleSwitch("p2p")) {
				regularExpressionScopeCriteria.setMatchingPackages(true);
			}
			
			if (commandLine.getToggleSwitch("c2p")) {
				regularExpressionScopeCriteria.setMatchingClasses(true);
			}
			
			if (commandLine.getToggleSwitch("c2c")) {
				regularExpressionScopeCriteria.setMatchingClasses(true);
			}
			
			if (commandLine.getToggleSwitch("f2f")) {
				regularExpressionScopeCriteria.setMatchingFeatures(true);
			}
			
			if (commandLine.isPresent("includes")) {
				regularExpressionScopeCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("includes"));
			}
			
			if (commandLine.isPresent("excludes")) {
				regularExpressionScopeCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("excludes"));
			}

			scopeCriteria = regularExpressionScopeCriteria;
		} else if (hasScopeListSwitches(commandLine)) {
			scopeCriteria = createCollectionSelectionCriteria(commandLine.getMultipleSwitch("scope-includes-list"), commandLine.getMultipleSwitch("scope-excludes-list"));
		}

		SelectionCriteria filterCriteria = new ComprehensiveSelectionCriteria();

		if (hasFilterRegularExpressionSwitches(commandLine)) {
			RegularExpressionSelectionCriteria regularExpressionFilterCriteria = new RegularExpressionSelectionCriteria();
			
			regularExpressionFilterCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-filter"));
			regularExpressionFilterCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-filter"));
			regularExpressionFilterCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-filter"));
			
			if (commandLine.isPresent("filter-includes") || (!commandLine.isPresent("package-filter-includes") && !commandLine.isPresent("class-filter-includes") && !commandLine.isPresent("feature-filter-includes"))) {
				// Only use the default if nothing else has been specified.
				regularExpressionFilterCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("filter-includes"));
			}
			regularExpressionFilterCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("filter-excludes"));
			regularExpressionFilterCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-filter-includes"));
			regularExpressionFilterCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-filter-excludes"));
			regularExpressionFilterCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-filter-includes"));
			regularExpressionFilterCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-filter-excludes"));
			regularExpressionFilterCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-filter-includes"));
			regularExpressionFilterCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-filter-excludes"));
			
			if (commandLine.getToggleSwitch("all")) {
				regularExpressionFilterCriteria.setMatchingPackages(true);
				regularExpressionFilterCriteria.setMatchingClasses(true);
				regularExpressionFilterCriteria.setMatchingFeatures(true);
			}
			
			if (commandLine.getToggleSwitch("p2p")) {
				regularExpressionFilterCriteria.setMatchingPackages(true);
			}
			
			if (commandLine.getToggleSwitch("c2p")) {
				regularExpressionFilterCriteria.setMatchingPackages(true);
			}
			
			if (commandLine.getToggleSwitch("c2c")) {
				regularExpressionFilterCriteria.setMatchingClasses(true);
			}
			
			if (commandLine.getToggleSwitch("f2f")) {
				regularExpressionFilterCriteria.setMatchingFeatures(true);
			}
			
			if (commandLine.isPresent("includes")) {
				regularExpressionFilterCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("includes"));
			}
			
			if (commandLine.isPresent("excludes")) {
				regularExpressionFilterCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("excludes"));
			}

			filterCriteria = regularExpressionFilterCriteria;
		} else if (hasFilterListSwitches(commandLine)) {
			filterCriteria = createCollectionSelectionCriteria(commandLine.getMultipleSwitch("filter-includes-list"), commandLine.getMultipleSwitch("filter-excludes-list"));
		}

		GraphCopier copier;
		if (commandLine.getToggleSwitch("copy-only") || commandLine.getToggleSwitch("maximize")) {
			copier = new GraphCopier(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
		} else {
			copier = new GraphSummarizer(scopeCriteria, filterCriteria);
		}
		
		Iterator i = commandLine.getParameters().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Logger.getLogger(DependencyReporter.class).info("Reading " + filename);
			verboseListener.print("Reading " + filename);

			Collection packages = Collections.EMPTY_LIST;

			if (filename.endsWith(".xml")) {
				NodeLoader loader = new NodeLoader(commandLine.getToggleSwitch("validate"));
				loader.addDependencyListener(verboseListener);
				packages = loader.load(filename).getPackages().values();
			}

			Logger.getLogger(DependencyReporter.class).info("Read in " + packages.size() + " package(s) from \"" + filename + "\".");

			if (commandLine.getToggleSwitch("maximize")) {
				new LinkMaximizer().traverseNodes(packages);
			} else if (commandLine.getToggleSwitch("minimize")) {
				new LinkMinimizer().traverseNodes(packages);
			}

			copier.traverseNodes(packages);
		}

		Logger.getLogger(DependencyReporter.class).info("Reporting " + copier.getScopeFactory().getPackages().values().size() + " package(s) ...");
	
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

		printer.setShowInbounds(commandLine.isPresent("show-all") || commandLine.isPresent("show-inbounds"));
		printer.setShowOutbounds(commandLine.isPresent("show-all") || commandLine.isPresent("show-outbounds"));
		printer.setShowEmptyNodes(commandLine.isPresent("show-all") || commandLine.isPresent("show-empty-nodes"));

		printer.traverseNodes(copier.getScopeFactory().getPackages().values());

		out.close();

		Date end = new Date();

		if (commandLine.getToggleSwitch("time")) {
			System.err.println(DependencyReporter.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
		}

		verboseListener.close();
	}

	private static boolean hasScopeRegularExpressionSwitches(CommandLine commandLine) {
		Collection switches = commandLine.getPresentSwitches();

		return
			switches.contains("scope-includes") ||
			switches.contains("scope-excludes") ||
			switches.contains("package-scope") ||
			switches.contains("package-scope-includes") ||
			switches.contains("package-scope-excludes") ||
			switches.contains("class-scope") ||
			switches.contains("class-scope-includes") ||
			switches.contains("class-scope-excludes") ||
			switches.contains("feature-scope") ||
			switches.contains("feature-scope-includes") ||
			switches.contains("feature-scope-excludes") ||
			switches.contains("all") ||
			switches.contains("p2p") ||
			switches.contains("c2p") ||
			switches.contains("c2c") ||
			switches.contains("f2f") ||
			switches.contains("includes") ||
			switches.contains("excludes");
	}

	private static boolean hasScopeListSwitches(CommandLine commandLine) {
		Collection switches = commandLine.getPresentSwitches();

		return
			switches.contains("scope-includes-list") ||
			switches.contains("scope-excludes-list");
	}

	private static boolean hasFilterRegularExpressionSwitches(CommandLine commandLine) {
		Collection switches = commandLine.getPresentSwitches();

		return
			switches.contains("filter-includes") ||
			switches.contains("filter-excludes") ||
			switches.contains("package-filter") ||
			switches.contains("package-filter-includes") ||
			switches.contains("package-filter-excludes") ||
			switches.contains("class-filter") ||
			switches.contains("class-filter-includes") ||
			switches.contains("class-filter-excludes") ||
			switches.contains("feature-filter") ||
			switches.contains("feature-filter-includes") ||
			switches.contains("feature-filter-excludes") ||
			switches.contains("all") ||
			switches.contains("p2p") ||
			switches.contains("c2p") ||
			switches.contains("c2c") ||
			switches.contains("f2f") ||
			switches.contains("includes") ||
			switches.contains("excludes");
	}

	private static boolean hasFilterListSwitches(CommandLine commandLine) {
		Collection switches = commandLine.getPresentSwitches();

		return
			switches.contains("filter-includes-list") ||
			switches.contains("filter-excludes-list");
	}

	private static CollectionSelectionCriteria createCollectionSelectionCriteria(Collection includes, Collection excludes) throws IOException {
		return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
	}

	private static Collection loadCollection(Collection filenames) {
		Collection result = null;

		if (!filenames.isEmpty()) {
			result = new HashSet();
			
			Iterator i = filenames.iterator();
			while (i.hasNext()) {
				String filename = i.next().toString();
				
				BufferedReader reader = null;
				String line;
				
				try {
					reader = new BufferedReader(new FileReader(filename));
					while ((line = reader.readLine()) != null) {
						result.add(line);
					}
				} catch (IOException ex) {
					Logger.getLogger(DependencyReporter.class).error("Couldn't read file " + filename, ex);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
					} catch (IOException ex) {
						Logger.getLogger(DependencyReporter.class).error("Couldn't close file " + filename, ex);
					}
				}
			}
		}
		
		return result;
	}
}
