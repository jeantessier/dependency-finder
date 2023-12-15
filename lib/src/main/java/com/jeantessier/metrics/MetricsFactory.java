/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static java.util.stream.Collectors.toCollection;

public class MetricsFactory {
    private static final String EOL = System.getProperty("line.separator", "\n");

    private static final Perl5Util perl = new Perl5Util();

    private final String projectName;
    private final MetricsConfiguration configuration;

    private final Map<String, Metrics> projects = new HashMap<>();
    private final Map<String, Metrics> groups = new HashMap<>();
    private final Map<String, Metrics> classes = new HashMap<>();
    private final Map<String, Metrics> methods = new HashMap<>();

    private final Map<String, Metrics> includedProjects = new HashMap<>();
    private final Map<String, Metrics> includedGroups = new HashMap<>();
    private final Map<String, Metrics> includedClasses = new HashMap<>();
    private final Map<String, Metrics> includedMethods = new HashMap<>();

    private final WordCounter counter = new WordCounter();
    
    public MetricsFactory(String projectName, MetricsConfiguration configuration) {
        this.projectName = projectName;
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
        Metrics result = new Metrics(projectMetrics, name);

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

    public Collection<Metrics> getGroupMetrics(String className) {
        return getConfiguration().getGroups(className).stream()
                .map(this::createGroupMetrics)
                .collect(toCollection(HashSet::new));
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
        Metrics result = new Metrics(packageMetrics, name);
        
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

        getConfiguration().getGroups(metrics.getName()).forEach(name -> {
            Metrics groupMetrics = createGroupMetrics(name);
            groupMetrics.addSubMetrics(metrics);
            includeGroupMetrics(groupMetrics);
        });
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
        Metrics result = new Metrics(classMetrics, name);
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
        descriptors.forEach(descriptor -> {
            try {
                metrics.track(descriptor.createMeasurement(metrics));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                Logger.getLogger(getClass()).warn("Unable to create measurement \"" + descriptor.getShortName() + "\"", ex);
            }
        });
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Factory for project \"").append(getProjectName()).append("\"").append(EOL);

        builder.append("projects:").append(EOL);
        projects.forEach((name, metrics) -> builder.append("    ").append(name).append(" -> ").append(metrics.getName()).append(EOL));

        builder.append("groups:").append(EOL);
        groups.forEach((name, metrics) -> builder.append("    ").append(name).append(" -> ").append(metrics.getName()).append(EOL));

        builder.append("classes:").append(EOL);
        classes.forEach((name, metrics) -> builder.append("    ").append(name).append(" -> ").append(metrics.getName()).append(EOL));

        builder.append("methods:").append(EOL);
        methods.forEach((name, metrics) -> builder.append("    ").append(name).append(" -> ").append(metrics.getName()).append(EOL));

        return builder.toString();
    }
}
