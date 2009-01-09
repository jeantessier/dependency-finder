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

package com.jeantessier.dependency;

import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

public class NodeFactory {
    private static final Perl5Util perl = new Perl5Util();

    private Map<String, PackageNode> packages = new HashMap<String, PackageNode>();
    private Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
    private Map<String, FeatureNode> features = new HashMap<String, FeatureNode>();

    public PackageNode createPackage(String packageName) {
        return createPackage(packageName, false);
    }
    
    public PackageNode createPackage(String packageName, boolean confirmed) {
        Logger.getLogger(getClass()).debug("Create package \"" + packageName + "\"");

        PackageNode result = packages.get(packageName);

        if (result == null) {
            result = new PackageNode(packageName, confirmed);
            packages.put(packageName, result);
            Logger.getLogger(getClass()).debug("Added package \"" + packageName + "\"");
        }

        if (confirmed && !result.isConfirmed()) {
            result.setConfirmed(confirmed);
            Logger.getLogger(getClass()).debug("Package \"" + packageName + "\" is confirmed");
        }

        return result;
    }
    
    // Only to be used by DeletingVisitor
    void deletePackage(PackageNode node) {
        Logger.getLogger(getClass()).debug("Delete package \"" + node + "\"");

        packages.remove(node.getName());
    }

    public Map<String, PackageNode> getPackages() {
        return Collections.unmodifiableMap(packages);
    }

    public ClassNode createClass(String className) {
        return createClass(className, false);
    }
    
    public ClassNode createClass(String className, boolean confirmed) {
        Logger.getLogger(getClass()).debug("Create class \"" + className + "\"");

        ClassNode result = classes.get(className);

        if (result == null) {
            String packageName = "";
            int pos = className.lastIndexOf('.');
            if (pos != -1) {
                packageName = className.substring(0, pos);
            }
            PackageNode parent = createPackage(packageName, confirmed);
            result = new ClassNode(parent, className, confirmed);
            parent.addClass(result);
            classes.put(className, result);
            Logger.getLogger(getClass()).debug("Added class \"" + className + "\"");
        }

        if (confirmed && !result.isConfirmed()) {
            result.setConfirmed(confirmed);
            Logger.getLogger(getClass()).debug("Class \"" + className + "\" is confirmed");
        }

        return result;
    }

    // Only to be used by DeletingVisitor
    void deleteClass(ClassNode node) {
        Logger.getLogger(getClass()).debug("Delete class \"" + node + "\"");

        node.getPackageNode().removeClass(node);
        classes.remove(node.getName());
    }

    public Map<String, ClassNode> getClasses() {
        return Collections.unmodifiableMap(classes);
    }

    public FeatureNode createFeature(String featureName) {
        return createFeature(featureName, false);
    }
    
    public FeatureNode createFeature(String featureName, boolean confirmed) {
        Logger.getLogger(getClass()).debug("Create feature \"" + featureName + "\"");

        FeatureNode result = features.get(featureName);

        if (result == null) {
            String parentName;

            if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", featureName)) {
                parentName = perl.group(1);
            } else if (perl.match("/^(.*)\\.[^\\.]*$/", featureName)) {
                parentName = perl.group(1);
            } else {
                parentName = "";
            }

            ClassNode parent = createClass(parentName, confirmed);
            result = new FeatureNode(parent, featureName, confirmed);
            parent.addFeature(result);
            features.put(featureName, result);
            Logger.getLogger(getClass()).debug("Added feature \"" + featureName + "\"");
        }

        if (confirmed && !result.isConfirmed()) {
            result.setConfirmed(confirmed);
            Logger.getLogger(getClass()).debug("Feature \"" + featureName + "\" is confirmed");
        }

        return result;
    }
    
    // Only to be used by DeletingVisitor
    void deleteFeature(FeatureNode node) {
        Logger.getLogger(getClass()).debug("Delete feature \"" + node + "\"");

        node.getClassNode().removeFeature(node);
        features.remove(node.getName());
    }
    
    public Map<String, FeatureNode> getFeatures() {
        return Collections.unmodifiableMap(features);
    }
}
