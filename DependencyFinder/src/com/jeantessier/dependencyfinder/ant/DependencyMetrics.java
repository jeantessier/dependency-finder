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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyMetrics extends GraphTask {

	private boolean list                        = false;
	private boolean chart_classes_per_package   = false;
	private boolean chart_features_per_class    = false;
	private boolean chart_inbounds_per_package  = false;
	private boolean chart_outbounds_per_package = false;
	private boolean chart_inbounds_per_class    = false;
	private boolean chart_outbounds_per_class   = false;
	private boolean chart_inbounds_per_feature  = false;
	private boolean chart_outbounds_per_feature = false;

	public boolean getList() {
		return list;
	}
	
	public void setList(boolean list) {
		this.list = list;
	}

	public boolean getChartclassesperpackage() {
		return chart_classes_per_package;
	}
	
	public void setChartclassesperpackage(boolean chart_classes_per_package) {
		this.chart_classes_per_package = chart_classes_per_package;
	}

	public boolean getChartfeaturesperclass() {
		return chart_features_per_class;
	}
	
	public void setChartfeaturesperclass(boolean chart_features_per_class) {
		this.chart_features_per_class = chart_features_per_class;
	}

	public boolean getChartinboundsperpackage() {
		return chart_inbounds_per_package;
	}
	
	public void setChartinboundsperpackage(boolean chart_inbounds_per_package) {
		this.chart_inbounds_per_package = chart_inbounds_per_package;
	}

	public boolean getChartoutboundsperpackage() {
		return chart_outbounds_per_package;
	}
	
	public void setChartoutboundsperpackage(boolean chart_outbounds_per_package) {
		this.chart_outbounds_per_package = chart_outbounds_per_package;
	}

	public boolean getChartinboundsperclass() {
		return chart_inbounds_per_class;
	}
	
	public void setChartinboundsperclass(boolean chart_inbounds_per_class) {
		this.chart_inbounds_per_class = chart_inbounds_per_class;
	}

	public boolean getChartoutboundsperclass() {
		return chart_outbounds_per_class;
	}
	
	public void setChartoutboundsperclass(boolean chart_outbounds_per_class) {
		this.chart_outbounds_per_class = chart_outbounds_per_class;
	}
	
	public boolean getChartinboundsperfeature() {
		return chart_inbounds_per_feature;
	}
	
	public void setChartinboundsperfeature(boolean chart_inbounds_per_feature) {
		this.chart_inbounds_per_feature = chart_inbounds_per_feature;
	}

	public boolean getChartoutboundsperfeature() {
		return chart_outbounds_per_feature;
	}
	
	public void setChartoutboundsperfeature(boolean chart_outbounds_per_feature) {
		this.chart_outbounds_per_feature = chart_outbounds_per_feature;
	}

	public void setChartinbounds(boolean chart_inbounds) {
		setChartinboundsperpackage(chart_inbounds);
		setChartinboundsperclass(chart_inbounds);
		setChartinboundsperfeature(chart_inbounds);
	}

	public void setChartoutbounds(boolean chart_outbounds) {
		setChartoutboundsperpackage(chart_outbounds);
		setChartoutboundsperclass(chart_outbounds);
		setChartoutboundsperfeature(chart_outbounds);
	}

	public void setChartpackages(boolean chart_packages) {
		setChartclassesperpackage(chart_packages);
		setChartinboundsperpackage(chart_packages);
		setChartoutboundsperpackage(chart_packages);
	}

	public void setChartclasses(boolean chart_classes) {
		setChartfeaturesperclass(chart_classes);
		setChartinboundsperclass(chart_classes);
		setChartoutboundsperclass(chart_classes);
	}

	public void setChartfeatures(boolean chart_features) {
		setChartinboundsperfeature(chart_features);
		setChartoutboundsperfeature(chart_features);
	}

	public void setChartall(boolean chart_all) {
		setChartclassesperpackage(chart_all);
		setChartfeaturesperclass(chart_all);
		setChartinboundsperpackage(chart_all);
		setChartoutboundsperpackage(chart_all);
		setChartinboundsperclass(chart_all);
		setChartoutboundsperclass(chart_all);
		setChartinboundsperfeature(chart_all);
		setChartoutboundsperfeature(chart_all);
	}
	
	public void execute() throws BuildException {
        // first off, make sure that we've got what we need
		CheckParameters();

		VerboseListener verbose_listener = new VerboseListener(this);

		try {
			MetricsReport reporter = new MetricsReport();
			
			reporter.ListElements(getList());
			reporter.ClassesPerPackageChart(getChartclassesperpackage());
			reporter.FeaturesPerClassChart(getChartfeaturesperclass());
			reporter.InboundsPerPackageChart(getChartinboundsperpackage());
			reporter.OutboundsPerPackageChart(getChartoutboundsperpackage());
			reporter.InboundsPerClassChart(getChartinboundsperclass());
			reporter.OutboundsPerClassChart(getChartoutboundsperclass());
			reporter.InboundsPerFeatureChart(getChartinboundsperfeature());
			reporter.OutboundsPerFeatureChart(getChartoutboundsperfeature());

			MetricsGatherer metrics = new MetricsGatherer(Strategy());

			String filename = getSrcfile().getAbsolutePath();
			log("Reading " + filename);
				
			Collection packages;
			if (filename.endsWith(".xml")) {
				NodeLoader loader = new NodeLoader(getValidate());
				loader.addDependencyListener(verbose_listener);
				packages = loader.Load(filename).Packages().values();
			} else if (filename.endsWith(".ser")) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
				packages = (Collection) in.readObject();
			} else {
				packages = Collections.EMPTY_LIST;
			}
				
			metrics.TraverseNodes(packages);
			reporter.Process(metrics);

			log("Saving metrics report to " + getDestfile().getAbsolutePath());
		
			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
			out.print(reporter);
			out.close();
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (ClassNotFoundException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
