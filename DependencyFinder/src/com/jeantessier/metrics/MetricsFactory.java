/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

	private String               projectName;
	private MetricsConfiguration configuration;

	private Map projects = new HashMap();
	private Map groups   = new HashMap();
	private Map classes  = new HashMap();
	private Map methods  = new HashMap();

	private Map includedProjects = new HashMap();
	private Map includedGroups   = new HashMap();
	private Map includedClasses  = new HashMap();
	private Map includedMethods  = new HashMap();
	
	public MetricsFactory(String projectName, MetricsConfiguration configuration) {
		this.projectName   = projectName;
		this.configuration = configuration;
	}

	public String getProjectName() {
		return projectName;
	}

	public MetricsConfiguration getConfiguration() {
		return configuration;
	}
	
	public Metrics createProjectMetrics() {
		return createProjectMetrics(getProjectName());
	}
	
	public Metrics createProjectMetrics(String name) {
		Metrics result = (Metrics) projects.get(name);

		if (result == null) {
			result = buildProjectMetrics(name);
			projects.put(name, result);
		}
		
		return result;
	}

	private Metrics buildProjectMetrics(String name) {
		Metrics result = new Metrics(name);

		populateMetrics(result, getConfiguration().getProjectMeasurements());
		
		return result;
	}
	
	public void includeProjectMetrics(Metrics metrics) {
		includedProjects.put(metrics.getName(), metrics);
	}

	public Collection getProjectNames() {
		return Collections.unmodifiableCollection(includedProjects.keySet());
	}
	
	public Collection getProjectMetrics() {
		return Collections.unmodifiableCollection(includedProjects.values());
	}
	
	public Collection getAllProjectNames() {
		return Collections.unmodifiableCollection(projects.keySet());
	}
	
	public Collection getAllProjectMetrics() {
		return Collections.unmodifiableCollection(projects.values());
	}

	public Metrics createGroupMetrics(String name) {
		Metrics result = (Metrics) groups.get(name);

		if (result == null) {
			result = buildGroupMetrics(name);
			groups.put(name, result);
		}
		
		return result;
	}

	private Metrics buildGroupMetrics(String name) {
		Metrics projectMetrics = createProjectMetrics();
		Metrics result         = new Metrics(projectMetrics, name);

		populateMetrics(result, getConfiguration().getGroupMeasurements());

		return result;
	}
	
	public void includeGroupMetrics(Metrics metrics) {
		includedGroups.put(metrics.getName(), metrics);
		metrics.getParent().addSubMetrics(metrics);
		includeProjectMetrics(metrics.getParent());
	}

	public Collection getGroupNames() {
		return Collections.unmodifiableCollection(includedGroups.keySet());
	}

	public Collection getGroupMetrics() {
		return Collections.unmodifiableCollection(includedGroups.values());
	}

	public Collection getAllGroupNames() {
		return Collections.unmodifiableCollection(groups.keySet());
	}

	public Collection getAllGroupMetrics() {
		return Collections.unmodifiableCollection(groups.values());
	}

	public Metrics createClassMetrics(String name) {
		Metrics result = (Metrics) classes.get(name);

		if (result == null) {
			result = buildClassMetrics(name);
			classes.put(name, result);
		}
		
		return result;
	}

	private Metrics buildClassMetrics(String name) {
		String packageName = "";
		int pos = name.lastIndexOf('.');
		if (pos != -1) {
			packageName = name.substring(0, pos);
		}
		Metrics packageMetrics = createGroupMetrics(packageName);
		Metrics result         = new Metrics(packageMetrics, name);
		
		populateMetrics(result, getConfiguration().getClassMeasurements());

		return result;
	}
	
	public void includeClassMetrics(Metrics metrics) {
		includedClasses.put(metrics.getName(), metrics);
		metrics.getParent().addSubMetrics(metrics);
		includeGroupMetrics(metrics.getParent());

		Iterator i = getConfiguration().getGroups(metrics.getName()).iterator();
		while (i.hasNext()) {
			Metrics groupMetrics = createGroupMetrics((String) i.next());
			groupMetrics.addSubMetrics(metrics);
			includeGroupMetrics(groupMetrics);
		}
	}

	public Collection getClassNames() {
		return Collections.unmodifiableCollection(includedClasses.keySet());
	}

	public Collection getClassMetrics() {
		return Collections.unmodifiableCollection(includedClasses.values());
	}

	public Collection getAllClassNames() {
		return Collections.unmodifiableCollection(classes.keySet());
	}
	
	public Collection getAllClassMetrics() {
		return Collections.unmodifiableCollection(classes.values());
	}

	public Metrics createMethodMetrics(String name) {
		Metrics result = (Metrics) methods.get(name);

		if (result == null) {
			result = buildMethodMetrics(name);
			methods.put(name, result);
		}
		
		return result;
	}

	private Metrics buildMethodMetrics(String name) {
		String className = "";
		if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", name)) {
			className = perl.group(1);
		} else if (perl.match("/^(.*)\\.static {}$/", name)) {
			className = perl.group(1);
		} else if (perl.match("/^(.*)\\.[\\^.]*$/", name)) {
			className = perl.group(1);
		}
		Metrics classMetrics = createClassMetrics(className);
		Metrics result       = new Metrics(classMetrics, name);
		classMetrics.addSubMetrics(result);

		populateMetrics(result, getConfiguration().getMethodMeasurements());

		return result;
	}

	public void includeMethodMetrics(Metrics metrics) {
		includedMethods.put(metrics.getName(), metrics);
		metrics.getParent().addSubMetrics(metrics);
		includeClassMetrics(metrics.getParent());
	}
	
	public Collection getMethodNames() {
		return Collections.unmodifiableCollection(includedMethods.keySet());
	}

	public Collection getMethodMetrics() {
		return Collections.unmodifiableCollection(includedMethods.values());
	}
	
	public Collection getAllMethodNames() {
		return Collections.unmodifiableCollection(methods.keySet());
	}

	public Collection getAllMethodMetrics() {
		return Collections.unmodifiableCollection(methods.values());
	}

	public void clear() {
		projects.clear();
		groups.clear();
		classes.clear();
		methods.clear();
		
		includedProjects.clear();
		includedGroups.clear();
		includedClasses.clear();
		includedMethods.clear();
	}
	
	private void populateMetrics(Metrics metrics, Collection descriptors) {
		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();
			try {
				metrics.track(descriptor.createMeasurement(metrics));
			} catch (InstantiationException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.getShortName() + "\"", ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.getShortName() + "\"", ex);
			} catch (NoSuchMethodException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.getShortName() + "\"", ex);
			} catch (InvocationTargetException ex) {
				Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.getShortName() + "\"", ex);
			}
		}
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append("Factory for project \"").append(getProjectName()).append("\"").append(System.getProperty("line.separator", "\n"));

		Iterator i;
		
		result.append("projects:").append(System.getProperty("line.separator", "\n"));
		i = projects.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).getName()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("groups:").append(System.getProperty("line.separator", "\n"));
		i = groups.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).getName()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("classes:").append(System.getProperty("line.separator", "\n"));
		i = classes.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).getName()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		result.append("methods:").append(System.getProperty("line.separator", "\n"));
		i = methods.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			result.append("    ").append(entry.getKey()).append(" -> ").append(((Metrics) entry.getValue()).getName()).append("").append(System.getProperty("line.separator", "\n"));
		}
		
		return result.toString();
	}
}
