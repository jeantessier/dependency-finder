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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyMetrics extends GraphTask {

	private boolean list                     = false;
	private boolean chartClassesPerPackage   = false;
	private boolean chartFeaturesPerClass    = false;
	private boolean chartInboundsPerPackage  = false;
	private boolean chartOutboundsPerPackage = false;
	private boolean chartInboundsPerClass    = false;
	private boolean chartOutboundsPerClass   = false;
	private boolean chartInboundsPerFeature  = false;
	private boolean chartOutboundsPerFeature = false;

	public boolean getList() {
		return list;
	}
	
	public void setList(boolean list) {
		this.list = list;
	}

	public boolean getChartclassesperpackage() {
		return chartClassesPerPackage;
	}
	
	public void setChartclassesperpackage(boolean chartClassesPerPackage) {
		this.chartClassesPerPackage = chartClassesPerPackage;
	}

	public boolean getChartfeaturesperclass() {
		return chartFeaturesPerClass;
	}
	
	public void setChartfeaturesperclass(boolean chartFeaturesPerClass) {
		this.chartFeaturesPerClass = chartFeaturesPerClass;
	}

	public boolean getChartinboundsperpackage() {
		return chartInboundsPerPackage;
	}
	
	public void setChartinboundsperpackage(boolean chartInboundsPerPackage) {
		this.chartInboundsPerPackage = chartInboundsPerPackage;
	}

	public boolean getChartoutboundsperpackage() {
		return chartOutboundsPerPackage;
	}
	
	public void setChartoutboundsperpackage(boolean chartOutboundsPerPackage) {
		this.chartOutboundsPerPackage = chartOutboundsPerPackage;
	}

	public boolean getChartinboundsperclass() {
		return chartInboundsPerClass;
	}
	
	public void setChartinboundsperclass(boolean chartInboundsPerClass) {
		this.chartInboundsPerClass = chartInboundsPerClass;
	}

	public boolean getChartoutboundsperclass() {
		return chartOutboundsPerClass;
	}
	
	public void setChartoutboundsperclass(boolean chartOutboundsPerClass) {
		this.chartOutboundsPerClass = chartOutboundsPerClass;
	}
	
	public boolean getChartinboundsperfeature() {
		return chartInboundsPerFeature;
	}
	
	public void setChartinboundsperfeature(boolean chartInboundsPerFeature) {
		this.chartInboundsPerFeature = chartInboundsPerFeature;
	}

	public boolean getChartoutboundsperfeature() {
		return chartOutboundsPerFeature;
	}
	
	public void setChartoutboundsperfeature(boolean chartOutboundsPerFeature) {
		this.chartOutboundsPerFeature = chartOutboundsPerFeature;
	}

	public void setChartinbounds(boolean chartInbounds) {
		setChartinboundsperpackage(chartInbounds);
		setChartinboundsperclass(chartInbounds);
		setChartinboundsperfeature(chartInbounds);
	}

	public void setChartoutbounds(boolean chartOutbounds) {
		setChartoutboundsperpackage(chartOutbounds);
		setChartoutboundsperclass(chartOutbounds);
		setChartoutboundsperfeature(chartOutbounds);
	}

	public void setChartpackages(boolean chartPackages) {
		setChartclassesperpackage(chartPackages);
		setChartinboundsperpackage(chartPackages);
		setChartoutboundsperpackage(chartPackages);
	}

	public void setChartclasses(boolean chartClasses) {
		setChartfeaturesperclass(chartClasses);
		setChartinboundsperclass(chartClasses);
		setChartoutboundsperclass(chartClasses);
	}

	public void setChartfeatures(boolean chartFeatures) {
		setChartinboundsperfeature(chartFeatures);
		setChartoutboundsperfeature(chartFeatures);
	}

	public void setChartall(boolean chartAll) {
		setChartclassesperpackage(chartAll);
		setChartfeaturesperclass(chartAll);
		setChartinboundsperpackage(chartAll);
		setChartoutboundsperpackage(chartAll);
		setChartinboundsperclass(chartAll);
		setChartoutboundsperclass(chartAll);
		setChartinboundsperfeature(chartAll);
		setChartoutboundsperfeature(chartAll);
	}
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need
		validateParameters();

		VerboseListener verboseListener = new VerboseListener(this);

		try {
			log("Saving metrics report to " + getDestfile().getAbsolutePath());
			
			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

			MetricsReport reporter = new MetricsReport(out);
			
			reporter.setListingElements(getList());
			reporter.setChartingClassesPerPackage(getChartclassesperpackage());
			reporter.setChartingFeaturesPerClass(getChartfeaturesperclass());
			reporter.setChartingInboundsPerPackage(getChartinboundsperpackage());
			reporter.setChartingOutboundsPerPackage(getChartoutboundsperpackage());
			reporter.setChartingInboundsPerClass(getChartinboundsperclass());
			reporter.setChartingOutboundsPerClass(getChartoutboundsperclass());
			reporter.setChartingInboundsPerFeature(getChartinboundsperfeature());
			reporter.setChartingOutboundsPerFeature(getChartoutboundsperfeature());

			MetricsGatherer metrics = new MetricsGatherer(getStrategy());

			String[] filenames = getSrc().list();
			for (int i=0; i<filenames.length; i++) {
				log("Reading graph from " + filenames[i]);
				
				Collection packages = Collections.EMPTY_LIST;
				
				if (filenames[i].endsWith(".xml")) {
					NodeLoader loader = new NodeLoader(getValidate());
					loader.addDependencyListener(verboseListener);
					packages = loader.load(filenames[i]).getPackages().values();
				}
				
				metrics.traverseNodes(packages);
			}
			
			reporter.process(metrics);

			out.close();
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
