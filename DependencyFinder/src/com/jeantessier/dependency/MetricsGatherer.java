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

package com.jeantessier.dependency;

import java.util.*;

public class MetricsGatherer extends VisitorBase {
	private Collection packages = new LinkedList();
	private Collection classes  = new LinkedList();
	private Collection features = new LinkedList();

	private long nb_outbound = 0;
	private long nb_inbound = 0;
	private long nb_outbound_packages = 0;
	private long nb_inbound_packages = 0;
	private long nb_outbound_classes = 0;
	private long nb_inbound_classes = 0;
	private long nb_outbound_features = 0;
	private long nb_inbound_features = 0;

	private Map chart_data = new TreeMap();
	private int chart_size = 0;
	public static final int CHART_INDEX           = 0;
	public static final int CLASSES_PER_PACKAGE   = 1;
	public static final int FEATURES_PER_CLASS    = 2;
	public static final int INBOUNDS_PER_PACKAGE  = 3;
	public static final int OUTBOUNDS_PER_PACKAGE = 4;
	public static final int INBOUNDS_PER_CLASS    = 5;
	public static final int OUTBOUNDS_PER_CLASS   = 6;
	public static final int INBOUNDS_PER_FEATURE  = 7;
	public static final int OUTBOUNDS_PER_FEATURE = 8;
	public static final int NB_CHARTS             = 9;

	private static final String[] CHART_NAMES = {"n",
												 "Classes per Package",
												 "Feafures per Class",
												 "Inbounds per Package",
												 "Outbounds per Package",
												 "Inbounds per Class",
												 "Outbounds per Class",
												 "Inbounds per Feature",
												 "Outbounds per Feature"};

	public static int NbCharts() {
		return NB_CHARTS;
	}
	
	public static String ChartName(int i) {
		return CHART_NAMES[i];
	}
	
	public MetricsGatherer() {
		super();
	}

	public MetricsGatherer(TraversalStrategy strategy) {
		super(strategy);
	}

	public long[] ChartData(int i) {
		long[] result = null;

		Integer key = new Integer(i);
		result = (long[]) chart_data.get(key);

		if (result == null) {
			result = new long[NB_CHARTS];
			result[CHART_INDEX] = i;
			chart_data.put(key, result);

			if (chart_size < i) {
				chart_size = i;
			}
		}

		return result;
	}

	public int ChartSize() {
		return chart_size;
	}
	
	public Collection Packages() {
		return packages;
	}

	public Collection Classes() {
		return classes;
	}

	public Collection Features() {
		return features;
	}

	public long NbOutbound() {
		return nb_outbound;
	}

	public long NbInbound() {
		return nb_inbound;
	}

	public long NbOutboundPackages() {
		return nb_outbound_packages;
	}

	public long NbInboundPackages() {
		return nb_inbound_packages;
	}

	public long NbOutboundClasses() {
		return nb_outbound_classes;
	}

	public long NbInboundClasses() {
		return nb_inbound_classes;
	}

	public long NbOutboundFeatures() {
		return nb_outbound_features;
	}

	public long NbInboundFeatures() {
		return nb_inbound_features;
	}
	
	public void PreprocessPackageNode(PackageNode node) {
		super.PreprocessPackageNode(node);

		packages.add(node);
		
		ChartData(node.Classes().size())[CLASSES_PER_PACKAGE]++;
		ChartData(node.Inbound().size())[INBOUNDS_PER_PACKAGE]++;
		ChartData(node.Outbound().size())[OUTBOUNDS_PER_PACKAGE]++;
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			nb_inbound++;
			nb_inbound_packages++;
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (Strategy().InFilter(node)) {
			nb_outbound++;
			nb_outbound_packages++;
		}
	}

	public void PreprocessClassNode(ClassNode node) {
		super.PreprocessClassNode(node);

		classes.add(node);
		
		ChartData(node.Features().size())[FEATURES_PER_CLASS]++;
		ChartData(node.Inbound().size())[INBOUNDS_PER_CLASS]++;
		ChartData(node.Outbound().size())[OUTBOUNDS_PER_CLASS]++;
	}

	public void VisitInboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			nb_inbound++;
			nb_inbound_classes++;
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (Strategy().InFilter(node)) {
			nb_outbound++;
			nb_outbound_classes++;
		}
	}

	public void PreprocessFeatureNode(FeatureNode node) {
		super.PreprocessFeatureNode(node);

		features.add(node);
		
		ChartData(node.Inbound().size())[INBOUNDS_PER_FEATURE]++;
		ChartData(node.Outbound().size())[OUTBOUNDS_PER_FEATURE]++;
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			nb_inbound++;
			nb_inbound_features++;
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (Strategy().InFilter(node)) {
			nb_outbound++;
			nb_outbound_features++;
		}
	}
}
