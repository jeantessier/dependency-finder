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

package com.jeantessier.metrics;

import java.lang.reflect.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

public class MetricsFactory {
	private static final Perl5Util perl = new Perl5Util();

	private String               project_name;
	private MetricsConfiguration configuration;

	private Map projects = new HashMap();
	private Map groups   = new HashMap();
	private Map classes  = new HashMap();
	private Map methods  = new HashMap();
	
	public MetricsFactory(String project_name, MetricsConfiguration configuration) {
		this.project_name  = project_name;
		this.configuration = configuration;
	}

	public String ProjectName() {
		return project_name;
	}

	public MetricsConfiguration Configuration() {
		return configuration;
	}
	
	public Metrics CreateProjectMetrics() {
		return CreateProjectMetrics(ProjectName());
	}
	
	public Metrics CreateProjectMetrics(String name) {
		Metrics result = (Metrics) projects.get(name);

		if (result == null) {
			result = BuildProjectMetrics(name);
			projects.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildProjectMetrics(String name) {
		Metrics result = new Metrics(name);

		PopulateMetrics(result, Configuration().ProjectMeasurements());
		
		return result;
	}

	public Collection ProjectNames() {
		return Collections.unmodifiableCollection(projects.keySet());
	}
	
	public Collection ProjectMetrics() {
		return Collections.unmodifiableCollection(projects.values());
	}
	
	public Metrics CreateGroupMetrics(String name) {
		Metrics result = (Metrics) groups.get(name);

		if (result == null) {
			result = BuildGroupMetrics(name);
			groups.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildGroupMetrics(String name) {
		Metrics project_metrics = CreateProjectMetrics();
		Metrics result          = new Metrics(project_metrics, name);
		project_metrics.AddSubMetrics(result);

		PopulateMetrics(result, Configuration().GroupMeasurements());

		return result;
	}

	public Collection GroupNames() {
		return Collections.unmodifiableCollection(groups.keySet());
	}

	public Collection GroupMetrics() {
		return Collections.unmodifiableCollection(groups.values());
	}

	public Metrics CreateClassMetrics(String name) {
		Metrics result = (Metrics) classes.get(name);

		if (result == null) {
			result = BuildClassMetrics(name);
			classes.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildClassMetrics(String name) {
		String package_name = "";
		int pos = name.lastIndexOf('.');
		if (pos != -1) {
			package_name = name.substring(0, pos);
		}
		Metrics package_metrics = CreateGroupMetrics(package_name);
		Metrics result          = new Metrics(package_metrics, name);
		package_metrics.AddSubMetrics(result);

		Iterator i = Configuration().Groups(name).iterator();
		while (i.hasNext()) {
			CreateGroupMetrics((String) i.next()).AddSubMetrics(result);
		}
		
		PopulateMetrics(result, Configuration().ClassMeasurements());

		return result;
	}

	public Collection ClassNames() {
		return Collections.unmodifiableCollection(classes.keySet());
	}

	public Collection ClassMetrics() {
		return Collections.unmodifiableCollection(classes.values());
	}
	
	public Metrics CreateMethodMetrics(String name) {
		Metrics result = (Metrics) methods.get(name);

		if (result == null) {
			result = BuildMethodMetrics(name);
			methods.put(name, result);
		}
		
		return result;
	}

	private Metrics BuildMethodMetrics(String name) {
		String class_name = "";
		if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", name)) {
			class_name = perl.group(1);
		} else if (perl.match("/^(.*)\\.[\\^.]*$/", name)) {
			class_name = perl.group(1);
		}
		Metrics class_metrics = CreateClassMetrics(class_name);
		Metrics result        = new Metrics(class_metrics, name);
		class_metrics.AddSubMetrics(result);

		PopulateMetrics(result, Configuration().MethodMeasurements());

		return result;
	}

	public Collection MethodNames() {
		return Collections.unmodifiableCollection(methods.keySet());
	}

	public Collection MethodMetrics() {
		return Collections.unmodifiableCollection(methods.values());
	}

	public void Clear() {
		projects.clear();
		groups.clear();
		classes.clear();
		methods.clear();
	}
	
	private void PopulateMetrics(Metrics metrics, Collection descriptors) {
		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();
			try {
				metrics.Track(descriptor.CreateMeasurement(metrics));
			} catch (InstantiationException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.ShortName() + "\"", ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.ShortName() + "\"", ex);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.ShortName() + "\"", ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.ShortName() + "\"", ex);
			}
		}
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append("Factory for project \"").append(ProjectName()).append("\"").append(System.getProperty("line.separator", "\n"));

		Iterator i;
		
		result.append("projects:").append(System.getProperty("line.separator", "\n"));
		i = projects.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).Name()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("groups:").append(System.getProperty("line.separator", "\n"));
		i = groups.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).Name()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("classes:").append(System.getProperty("line.separator", "\n"));
		i = classes.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).Name()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("methods:").append(System.getProperty("line.separator", "\n"));
		i = methods.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).Name()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		return result.toString();
	}
}
