/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class NodeFactory {
    private static final Perl5Util perl = new Perl5Util();

    private Map packages = new HashMap();
    private Map classes  = new HashMap();
    private Map features = new HashMap();

    public PackageNode createPackage(String packageName) {
        return createPackage(packageName, false);
    }
    
    public PackageNode createPackage(String packageName, boolean concrete) {
        Logger.getLogger(getClass()).debug("Create package \"" + packageName + "\"");

        PackageNode result = (PackageNode) packages.get(packageName);

        if (result == null) {
            result = new PackageNode(packageName, concrete);
            packages.put(packageName, result);
            Logger.getLogger(getClass()).debug("Added package \"" + packageName + "\"");
        }

        if (concrete && !result.isConcrete()) {
            result.setConcrete(concrete);
        }

        return result;
    }
    
    // Only to be used by DeletingVisitor
    void deletePackage(PackageNode node) {
        Logger.getLogger(getClass()).debug("Delete package \"" + node + "\"");

        packages.remove(node.getName());
    }

    public Map getPackages() {
        return Collections.unmodifiableMap(packages);
    }

    public ClassNode createClass(String className) {
        return createClass(className, false);
    }
    
    public ClassNode createClass(String className, boolean concrete) {
        Logger.getLogger(getClass()).debug("Create class \"" + className + "\"");

        ClassNode result = (ClassNode) classes.get(className);

        if (result == null) {
            String packageName = "";
            int pos = className.lastIndexOf('.');
            if (pos != -1) {
                packageName = className.substring(0, pos);
            }
            PackageNode parent = createPackage(packageName, concrete);
            result = new ClassNode(parent, className, concrete);
            parent.addClass(result);
            classes.put(className, result);
            Logger.getLogger(getClass()).debug("Added class \"" + className + "\"");
        }

        if (concrete && !result.isConcrete()) {
            result.setConcrete(concrete);
        }

        return result;
    }

    // Only to be used by DeletingVisitor
    void deleteClass(ClassNode node) {
        Logger.getLogger(getClass()).debug("Delete class \"" + node + "\"");

        node.getPackageNode().removeClass(node);
        classes.remove(node.getName());
    }

    public Map getClasses() {
        return Collections.unmodifiableMap(classes);
    }

    public FeatureNode createFeature(String featureName) {
        return createFeature(featureName, false);
    }
    
    public FeatureNode createFeature(String featureName, boolean concrete) {
        Logger.getLogger(getClass()).debug("Create feature \"" + featureName + "\"");

        FeatureNode result = (FeatureNode) features.get(featureName);

        if (result == null) {
            String parentName = null;

            if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", featureName)) {
                parentName = perl.group(1);
            } else if (perl.match("/^(.*)\\.[^\\.]*$/", featureName)) {
                parentName = perl.group(1);
            } else {
                parentName = "";
            }

            ClassNode parent = createClass(parentName, concrete);
            result = new FeatureNode(parent, featureName, concrete);
            parent.addFeature(result);
            features.put(featureName, result);
            Logger.getLogger(getClass()).debug("Added feature \"" + featureName + "\"");
        }

        if (concrete && !result.isConcrete()) {
            result.setConcrete(concrete);
        }

        return result;
    }
    
    // Only to be used by DeletingVisitor
    void deleteFeature(FeatureNode node) {
        Logger.getLogger(getClass()).debug("Delete feature \"" + node + "\"");

        node.getClassNode().removeFeature(node);
        features.remove(node.getName());
    }
    
    public Map getFeatures() {
        return Collections.unmodifiableMap(features);
    }
}
