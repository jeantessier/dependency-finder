/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

    private Map<String, Metrics> projects = new HashMap<String, Metrics>();
    private Map<String, Metrics> groups   = new HashMap<String, Metrics>();
    private Map<String, Metrics> classes  = new HashMap<String, Metrics>();
    private Map<String, Metrics> methods  = new HashMap<String, Metrics>();

    private Map<String, Metrics> includedProjects = new HashMap<String, Metrics>();
    private Map<String, Metrics> includedGroups   = new HashMap<String, Metrics>();
    private Map<String, Metrics> includedClasses  = new HashMap<String, Metrics>();
    private Map<String, Metrics> includedMethods  = new HashMap<String, Metrics>();

    private WordCounter counter = new WordCounter();
    
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
        Metrics result = projects.get(name);

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

    public Collection<String> getProjectNames() {
        return Collections.unmodifiableCollection(includedProjects.keySet());
    }
    
    public Collection<Metrics> getProjectMetrics() {
        return Collections.unmodifiableCollection(includedProjects.values());
    }
    
    public Collection<String> getAllProjectNames() {
        return Collections.unmodifiableCollection(projects.keySet());
    }
    
    public Collection<Metrics> getAllProjectMetrics() {
        return Collections.unmodifiableCollection(projects.values());
    }

    public Metrics createGroupMetrics(String name) {
        Metrics result = groups.get(name);

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
        initializeGroupMetrics(name, result);

        return result;
    }

    private void initializeGroupMetrics(String packageName, Metrics metrics) {
        computePackageNameCharacterCount(packageName, metrics);
        computePackageNameWordCount(packageName, metrics);
    }

    private void computePackageNameCharacterCount(String packageName, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.GROUP_NAME_CHARACTER_COUNT, packageName.length());
    }

    private void computePackageNameWordCount(String packageName, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.GROUP_NAME_WORD_COUNT, counter.countPackageName(packageName));
    }

    public void includeGroupMetrics(Metrics metrics) {
        includedGroups.put(metrics.getName(), metrics);
        metrics.getParent().addSubMetrics(metrics);
        includeProjectMetrics(metrics.getParent());
    }

    public Collection<String> getGroupNames() {
        return Collections.unmodifiableCollection(includedGroups.keySet());
    }

    public Collection<Metrics> getGroupMetrics() {
        return Collections.unmodifiableCollection(includedGroups.values());
    }

    public Collection<String> getAllGroupNames() {
        return Collections.unmodifiableCollection(groups.keySet());
    }

    public Collection<Metrics> getAllGroupMetrics() {
        return Collections.unmodifiableCollection(groups.values());
    }

    public Metrics createClassMetrics(String name) {
        Metrics result = classes.get(name);

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
        String className = name.substring(pos + 1);
        Metrics packageMetrics = createGroupMetrics(packageName);
        Metrics result         = new Metrics(packageMetrics, name);
        
        populateMetrics(result, getConfiguration().getClassMeasurements());
        initializeClassMetrics(className, result);

        return result;
    }

    private void initializeClassMetrics(String className, Metrics metrics) {
        computeClassNameCharacterCount(className, metrics);
        computeClassNameWordCount(className, metrics);
    }

    private void computeClassNameCharacterCount(String className, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.CLASS_NAME_CHARACTER_COUNT, className.length());
    }

    private void computeClassNameWordCount(String className, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.CLASS_NAME_WORD_COUNT, counter.countIdentifier(className));
    }

    public void includeClassMetrics(Metrics metrics) {
        includedClasses.put(metrics.getName(), metrics);
        metrics.getParent().addSubMetrics(metrics);
        includeGroupMetrics(metrics.getParent());

        for (String name : getConfiguration().getGroups(metrics.getName())) {
            Metrics groupMetrics = createGroupMetrics(name);
            groupMetrics.addSubMetrics(metrics);
            includeGroupMetrics(groupMetrics);
        }
    }

    public Collection<String> getClassNames() {
        return Collections.unmodifiableCollection(includedClasses.keySet());
    }

    public Collection<Metrics> getClassMetrics() {
        return Collections.unmodifiableCollection(includedClasses.values());
    }

    public Collection<String> getAllClassNames() {
        return Collections.unmodifiableCollection(classes.keySet());
    }
    
    public Collection<Metrics> getAllClassMetrics() {
        return Collections.unmodifiableCollection(classes.values());
    }

    public Metrics createMethodMetrics(String name) {
        Metrics result = methods.get(name);

        if (result == null) {
            result = buildMethodMetrics(name);
            methods.put(name, result);
        }
        
        return result;
    }

    private Metrics buildMethodMetrics(String name) {
        String className = "";
        String featureName = "";
        if (perl.match("/^(.*)\\.([^\\.]*)\\(.*\\)$/", name)) {
            className = perl.group(1);
            featureName = perl.group(2);
        } else if (perl.match("/^(.*)\\.(static) {}$/", name)) {
            className = perl.group(1);
            featureName = perl.group(2);
        } else if (perl.match("/^(.*)\\.([\\^.]*)$/", name)) {
            className = perl.group(1);
            featureName = perl.group(2);
        }
        Metrics classMetrics = createClassMetrics(className);
        Metrics result       = new Metrics(classMetrics, name);
        classMetrics.addSubMetrics(result);

        populateMetrics(result, getConfiguration().getMethodMeasurements());
        initializeMethodMetrics(featureName, result);

        return result;
    }

    private void initializeMethodMetrics(String featureName, Metrics metrics) {
        computeMethodNameCharacterCount(featureName, metrics);
        computeMethodNameWordCount(featureName, metrics);
    }

    private void computeMethodNameCharacterCount(String featureName, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.METHOD_NAME_CHARACTER_COUNT, featureName.length());
    }

    private void computeMethodNameWordCount(String featureName, Metrics metrics) {
        metrics.addToMeasurement(BasicMeasurements.METHOD_NAME_WORD_COUNT, counter.countIdentifier(featureName));
    }

    public void includeMethodMetrics(Metrics metrics) {
        includedMethods.put(metrics.getName(), metrics);
        metrics.getParent().addSubMetrics(metrics);
        includeClassMetrics(metrics.getParent());
    }
    
    public Collection<String> getMethodNames() {
        return Collections.unmodifiableCollection(includedMethods.keySet());
    }

    public Collection<Metrics> getMethodMetrics() {
        return Collections.unmodifiableCollection(includedMethods.values());
    }
    
    public Collection<String> getAllMethodNames() {
        return Collections.unmodifiableCollection(methods.keySet());
    }

    public Collection<Metrics> getAllMethodMetrics() {
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
    
    private void populateMetrics(Metrics metrics, Collection<MeasurementDescriptor> descriptors) {
        for (MeasurementDescriptor descriptor : descriptors) {
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

        result.append("projects:").append(System.getProperty("line.separator", "\n"));
        for (Map.Entry<String, Metrics> entry : projects.entrySet()) {
            result.append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue().getName()).append("").append(System.getProperty("line.separator", "\n"));
        }
        
        result.append("groups:").append(System.getProperty("line.separator", "\n"));
        for (Map.Entry<String, Metrics> entry : groups.entrySet()) {
            result.append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue().getName()).append("").append(System.getProperty("line.separator", "\n"));
        }
        
        result.append("classes:").append(System.getProperty("line.separator", "\n"));
        for (Map.Entry<String, Metrics> entry : classes.entrySet()) {
            result.append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue().getName()).append("").append(System.getProperty("line.separator", "\n"));
        }
        
        result.append("methods:").append(System.getProperty("line.separator", "\n"));
        for (Map.Entry<String, Metrics> entry : methods.entrySet()) {
            result.append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue().getName()).append("").append(System.getProperty("line.separator", "\n"));
        }
        
        return result.toString();
    }
}
