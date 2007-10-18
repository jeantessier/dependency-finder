/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.*;

import com.jeantessier.text.*;

public class RegularExpressionSelectionCriteria implements SelectionCriteria {
    private Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());

    private List<String> globalIncludes = new LinkedList<String>();
    private List<String> globalExcludes = new LinkedList<String>();
    private boolean matchingPackages = true;
    private List<String> packageIncludes = new LinkedList<String>();
    private List<String> packageExcludes = new LinkedList<String>();
    private boolean matchingClasses  = true;
    private List<String> classIncludes = new LinkedList<String>();
    private List<String> classExcludes = new LinkedList<String>();
    private boolean matchingFeatures = true;
    private List<String> featureIncludes = new LinkedList<String>();
    private List<String> featureExcludes = new LinkedList<String>();
    
    public RegularExpressionSelectionCriteria() {
        // Do nothing
    }

    public RegularExpressionSelectionCriteria(String initialGlobalIncludes) {
        setGlobalIncludes(initialGlobalIncludes);
    }

    public List<String> getGlobalIncludes() {
        return globalIncludes;
    }

    public void setGlobalIncludes(String globalIncludes) {
        setGlobalIncludes(parseRE(globalIncludes));
    }
    
    public void setGlobalIncludes(List<String> globalIncludes) {
        this.globalIncludes = globalIncludes;
    }

    public List<String> getGlobalExcludes() {
        return globalExcludes;
    }

    public void setGlobalExcludes(String globalExcludes) {
        setGlobalExcludes(parseRE(globalExcludes));
    }

    public void setGlobalExcludes(List<String> globalExcludes) {
        this.globalExcludes = globalExcludes;
    }

    public boolean isMatchingPackages() {
        return matchingPackages;
    }

    public void setMatchingPackages(boolean matchingPackages) {
        this.matchingPackages = matchingPackages;
    }

    public List<String> getPackageIncludes() {
        return packageIncludes;
    }

    public void setPackageIncludes(String packageIncludes) {
        setPackageIncludes(parseRE(packageIncludes));
    }

    public void setPackageIncludes(List<String> packageIncludes) {
        this.packageIncludes = packageIncludes;
    }

    public List<String> getPackageExcludes() {
        return packageExcludes;
    }

    public void setPackageExcludes(String packageExcludes) {
        setPackageExcludes(parseRE(packageExcludes));
    }

    public void setPackageExcludes(List<String> packageExcludes) {
        this.packageExcludes = packageExcludes;
    }

    public boolean isMatchingClasses() {
        return matchingClasses;
    }

    public void setMatchingClasses(boolean matchingClasses) {
        this.matchingClasses = matchingClasses;
    }

    public List<String> getClassIncludes() {
        return classIncludes;
    }

    public void setClassIncludes(String classIncludes) {
        setClassIncludes(parseRE(classIncludes));
    }

    public void setClassIncludes(List<String> classIncludes) {
        this.classIncludes = classIncludes;
    }

    public List<String> getClassExcludes() {
        return classExcludes;
    }

    public void setClassExcludes(String classExcludes) {
        setClassExcludes(parseRE(classExcludes));
    }

    public void setClassExcludes(List<String> classExcludes) {
        this.classExcludes = classExcludes;
    }

    public boolean isMatchingFeatures() {
        return matchingFeatures;
    }

    public void setMatchingFeatures(boolean matchingFeatures) {
        this.matchingFeatures = matchingFeatures;
    }

    public List<String> getFeatureIncludes() {
        return featureIncludes;
    }

    public void setFeatureIncludes(String featureIncludes) {
        setFeatureIncludes(parseRE(featureIncludes));
    }

    public void setFeatureIncludes(List<String> featureIncludes) {
        this.featureIncludes = featureIncludes;
    }

    public List<String> getFeatureExcludes() {
        return featureExcludes;
    }

    public void setFeatureExcludes(String featureExcludes) {
        setFeatureExcludes(parseRE(featureExcludes));
    }

    public void setFeatureExcludes(List<String> featureExcludes) {
        this.featureExcludes = featureExcludes;
    }

    public boolean matches(PackageNode node) {
        return isMatchingPackages() && matchesPackageName(node.getName());
    }
    
    public boolean matches(ClassNode node) {
        return isMatchingClasses() && matchesClassName(node.getName());
    }
    
    public boolean matches(FeatureNode node) {
        return isMatchingFeatures() && matchesFeatureName(node.getName());
    }

    public boolean matchesPackageName(String name) {
        return matches(getGlobalIncludes(), getPackageIncludes(), name) &&
            !matches(getGlobalExcludes(), getPackageExcludes(), name);
    }

    public boolean matchesClassName(String name) {
        return matches(getGlobalIncludes(), getClassIncludes(), name) &&
            !matches(getGlobalExcludes(), getClassExcludes(), name);
    }

    public boolean matchesFeatureName(String name) {
        return matches(getGlobalIncludes(), getFeatureIncludes(), name) &&
            !matches(getGlobalExcludes(), getFeatureExcludes(), name);
    }

    private boolean matches(List<String> globalRegularExpressions, List<String> regularExpressions, String name) {
        boolean  found = false;
        Iterator<String> i;

        i = globalRegularExpressions.iterator();
        while (!found && i.hasNext()) {
            String regex = i.next();
            try {
                found = perl.match(regex, name);
            } catch (MalformedCachePatternException ex) {
                throw new MatchException(regex, ex);
            }
        }

        i = regularExpressions.iterator();
        while (!found && i.hasNext()) {
            String regex = i.next();
            try {
                found = perl.match(regex, name);
            } catch (MalformedCachePatternException ex) {
                throw new MatchException(regex, ex);
            }
        }

        return found;
    }

    // Should be private, but left at package-level for the unit tests.
    static List<String> parseRE(String re) {
        List<String> result = new LinkedList<String>();

        Logger logger = Logger.getLogger(RegularExpressionSelectionCriteria.class);
        logger.debug("ParseRE \"" + re + "\"");

        int length = re.length();
        int start  = 0;
        int stop   = -1;

        while (start < length && stop < length) {
            String separator = null;
            
            // Locate begining & determine separator
            while (start < length && stop < start) {
                if (re.charAt(start) == 'm' && (start + 1) < length) {
                    separator = re.substring(start + 1, start + 2);
                    stop = start + 2;
                } else if (re.charAt(start) == '/') {
                    separator = "/";
                    stop = start + 1;
                } else {
                    start++;
                }
            }

            logger.debug("start is " + start);
            logger.debug("separator is " + separator);
            
            // Locate end
            while (stop < length && start < stop) {
                stop = re.indexOf(separator, stop);
                logger.debug("indexOf() is " + stop);
                
                if (stop == -1 || re.charAt(stop - 1) != '\\') {

                    if (stop == -1) {
                        stop = length;
                    } else {
                        // Look for modifiers
                        stop++;
                        while (stop < length && (re.charAt(stop) == 'g' ||
                                                 re.charAt(stop) == 'i' ||
                                                 re.charAt(stop) == 'm' ||
                                                 re.charAt(stop) == 'o' ||
                                                 re.charAt(stop) == 's' ||
                                                 re.charAt(stop) == 'x')) {
                            stop++;
                        }
                    }

                    logger.debug("stop is " + stop);

                    // Add candidate
                    logger.debug("candidate is \"" + re.substring(start, stop) + "\"");
                    result.add(re.substring(start, stop));
            
                    // Move start
                    start = stop + 1;
                } else {
                    stop++;
                }
            }
        }
        
        return result;
    }
}
