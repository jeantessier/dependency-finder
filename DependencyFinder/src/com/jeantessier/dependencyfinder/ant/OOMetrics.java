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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.metrics.*;

public class OOMetrics extends Task {
	public static final String DEFAULT_PROJECT_NAME = "Project";
	public static final String DEFAULT_SORT         = "name";

	private String  project_name    = DEFAULT_PROJECT_NAME;
	private File    configuration;
	private boolean csv             = false;
	private boolean txt             = false;
	private boolean xml             = false;
	private boolean validate        = false;
	private String  dtd_prefix      = com.jeantessier.metrics.XMLPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;
	private boolean project_metrics = false;
	private boolean group_metrics   = false;
	private boolean class_metrics   = false;
	private boolean method_metrics  = false;
	private String  sort            = DEFAULT_SORT;
	private boolean expand          = false;
	private boolean reverse         = false;
	private File    destprefix;
	private Path    path;

	public String getProjectname() {
		return project_name;
	}
	
	public void setProjectname(String project_name) {
		this.project_name = project_name;
	}

	public File getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(File configuration) {
		this.configuration = configuration;
	}

	public boolean getCsv() {
		return csv;
	}
	
	public void setCsv(boolean csv) {
		this.csv = csv;
	}

	public boolean getTxt() {
		return txt;
	}
	
	public void setTxt(boolean txt) {
		this.txt = txt;
	}

	public boolean getXml() {
		return xml;
	}

	public void setXml(boolean xml) {
		this.xml = xml;
	}

	public boolean getValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getDtdprefix() {
		return dtd_prefix;
	}
	
	public void setDtdprefix(String dtd_prefix) {
		this.dtd_prefix = dtd_prefix;
	}

	public String getIndenttext() {
		return indent_text;
	}
	
	public void setIntenttext(String indent_text) {
		this.indent_text = indent_text;
	}

	public boolean getProjectmetrics() {
		return project_metrics;
	}
	
	public void setProjectmetrics(boolean project_metrics) {
		this.project_metrics = project_metrics;
	}

	public boolean getGroupmetrics() {
		return group_metrics;
	}
	
	public void setGroupmetrics(boolean group_metrics) {
		this.group_metrics = group_metrics;
	}

	public boolean getClassmetrics() {
		return class_metrics;
	}
	
	public void setClassmetrics(boolean class_metrics) {
		this.class_metrics = class_metrics;
	}

	public boolean getMethodmetrics() {
		return method_metrics;
	}
	
	public void setMethodmetrics(boolean method_metrics) {
		this.method_metrics = method_metrics;
	}

	public void setAllmetrics(boolean all_metrics) {
		setProjectmetrics(all_metrics);
		setGroupmetrics(all_metrics);
		setClassmetrics(all_metrics);
		setMethodmetrics(all_metrics);
	}
	
	public String getSort() {
		return sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean getExpand() {
		return expand;
	}
	
	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public boolean getReverse() {
		return reverse;
	}
	
	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public File getDestprefix() {
		return destprefix;
	}
	
	public void setDestprefix(File destprefix) {
		this.destprefix = destprefix;
	}
	
	public Path createPath() {
		if (path == null) {
			path = new Path(getProject());
		}

		return path;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void execute() throws BuildException {
		// first off, make sure that we've got what we need

		if (getConfiguration() == null) {
			throw new BuildException("configuration must be set!");
		}
		
		if (!getConfiguration().exists()) {
			throw new BuildException("configuration does not exist!");
		}
		
		if (!getConfiguration().isFile()) {
			throw new BuildException("configuration is not a file!");
		}

		if (getPath() == null) {
			throw new BuildException("path must be set!");
		}

		if (getDestprefix() == null) {
			throw new BuildException("destprefix must be set!");
		}

		try {
			log("Reading classes from path " + getPath());

			VerboseListener verbose_listener = new VerboseListener(this);
			
			MetricsFactory factory = new MetricsFactory(getProjectname(), new MetricsConfigurationLoader(getValidate()).Load(getConfiguration().getAbsolutePath()));
			
			ClassfileLoader loader = new AggregatingClassfileLoader();
			loader.addLoadListener(verbose_listener);
			loader.Load(Arrays.asList(getPath().list()));
			
			com.jeantessier.metrics.MetricsGatherer metrics = new com.jeantessier.metrics.MetricsGatherer(project_name, factory);
			metrics.addMetricsListener(verbose_listener);
			
			Iterator j = loader.Classfiles().iterator();
			while (j.hasNext()) {
				((Classfile) j.next()).Accept(metrics);
			}

			if (getCsv()) {
				PrintCSVFiles(metrics.MetricsFactory());
			} else if (getTxt()) {
				PrintTextFile(metrics.MetricsFactory());
			} else if (getXml()) {
				PrintXMLFile(metrics.MetricsFactory());
			}
		} catch (SAXException ex) {
			throw new BuildException(ex);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}

	private void PrintCSVFiles(MetricsFactory factory) throws IOException {
		MetricsComparator comparator = new MetricsComparator(getSort());
		if (getReverse()) {
			comparator.Reverse();
		}

		List               metrics;
		Iterator           i;
		com.jeantessier.metrics.Printer printer;

		if (getProjectmetrics()) {
			String filename = getDestprefix().getAbsolutePath() + "_project.csv";
			log("Saving metrics to " + filename);

			PrintWriter out = new PrintWriter(new FileWriter(filename));
			
			metrics = new ArrayList(factory.ProjectMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().ProjectMeasurements());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.close();
		}

		if (getGroupmetrics()) {
			String filename = getDestprefix().getAbsolutePath() + "_groups.csv";
			log("Saving metrics to " + filename);

			PrintWriter out = new PrintWriter(new FileWriter(filename));

			metrics = new ArrayList(factory.GroupMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().GroupMeasurements());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.close();
		}

		if (getClassmetrics()) {
			String filename = getDestprefix().getAbsolutePath() + "_classes.csv";
			log("Saving metrics to " + filename);

			PrintWriter out = new PrintWriter(new FileWriter(filename));

			metrics = new ArrayList(factory.ClassMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().ClassMeasurements());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.close();
		}

		if (getMethodmetrics()) {
			String filename = getDestprefix().getAbsolutePath() + "_methods.csv";
			log("Saving metrics to " + filename);

			PrintWriter out = new PrintWriter(new FileWriter(filename));

			metrics = new ArrayList(factory.MethodMetrics());
			Collections.sort(metrics, comparator);
			printer = new com.jeantessier.metrics.CSVPrinter(out, factory.Configuration().MethodMeasurements());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.close();
		}
	}

	private void PrintTextFile(MetricsFactory factory) throws IOException {
		MetricsComparator comparator = new MetricsComparator(getSort());
		if (getReverse()) {
			comparator.Reverse();
		}

		String filename = getDestprefix().getAbsolutePath() + ".txt";
		log("Saving metrics to " + filename);
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));

		List               metrics;
		Iterator           i;

		if (getProjectmetrics()) {
			out.println("Project metrics");
			out.println("---------------");
			metrics = new ArrayList(factory.ProjectMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().ProjectMeasurements());
			printer.ExpandCollectionMeasurements(getExpand());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.println();
		}

		if (getGroupmetrics()) {
			out.println("Group metrics");
			out.println("-------------");
			metrics = new ArrayList(factory.GroupMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().GroupMeasurements());
			printer.ExpandCollectionMeasurements(getExpand());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.println();
		}

		if (getClassmetrics()) {
			out.println("Class metrics");
			out.println("-------------");
			metrics = new ArrayList(factory.ClassMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().ClassMeasurements());
			printer.ExpandCollectionMeasurements(getExpand());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.println();
		}
		
		if (getMethodmetrics()) {
			out.println("Method metrics");
			out.println("--------------");
			metrics = new ArrayList(factory.MethodMetrics());
			Collections.sort(metrics, comparator);
			com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.Configuration().MethodMeasurements());
			printer.ExpandCollectionMeasurements(getExpand());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}

			printer.VisitMetrics(metrics);

			out.println();
		}
		
		out.close();
	}

	private void PrintXMLFile(MetricsFactory factory) throws IOException {
		MetricsComparator comparator = new MetricsComparator(getSort());
		if (getReverse()) {
			comparator.Reverse();
		}

		String filename = getDestprefix().getAbsolutePath() + ".xml";
		log("Saving metrics to " + filename);
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));

		List metrics = new ArrayList(factory.ProjectMetrics());
		Collections.sort(metrics, comparator);
		com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.XMLPrinter(out, factory.Configuration(), getDtdprefix());
		if (getIndenttext() != null) {
			printer.IndentText(getIndenttext());
		}

		printer.VisitMetrics(metrics);

		out.close();
	}
}
