/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.*;
import org.apache.oro.text.perl.*;

public class TextPrinter extends Printer {
    private static final Perl5Util perl = new Perl5Util();

    protected static Perl5Util perl() {
        return perl;
    }

    private boolean showInferred = true;
    private final Map<Node, Integer> dependencies = new TreeMap<>();

    public TextPrinter(PrintWriter out) {
        super(out);
    }

    public TextPrinter(TraversalStrategy strategy, PrintWriter out) {
        super(strategy, out);
    }

    public boolean isShowInferred() {
        return showInferred;
    }

    public void setShowInferred(boolean showInferred) {
        this.showInferred = showInferred;
    }
    
    protected void preprocessPackageNode(PackageNode node) {
        LogManager.getLogger(getClass()).debug("Printing package \"{}\" and its {} inbounds and {} outbounds", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size());
        
        super.preprocessPackageNode(node);

        raiseIndent();

        dependencies.clear();
    }

    protected void preprocessAfterDependenciesPackageNode(PackageNode node) {
        LogManager.getLogger(getClass()).debug("Package \"{}\" with {} inbounds and {} outbounds had {} dependencies.", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size(), () -> dependencies.size());
        
        if (shouldShowPackageNode(node) || !dependencies.isEmpty()) {
            lowerIndent();
            indent().printScopeNodeName(node).eol();
            raiseIndent();
        }
        
        printDependencies(node, dependencies);
    }
    
    protected void postprocessPackageNode(PackageNode node) {
        lowerIndent();

        super.postprocessPackageNode(node);
    }

    public void visitInboundPackageNode(PackageNode node) {
        if (isShowInbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" <-- \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, -1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" <-- \"{}\"", getCurrentNode(), node);
        }
    }

    public void visitOutboundPackageNode(PackageNode node) {
        if (isShowOutbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" --> \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, 1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" --> \"{}\"", getCurrentNode(), node);
        }
    }

    protected void preprocessClassNode(ClassNode node) {
        LogManager.getLogger(getClass()).debug("Printing class \"{}\" and its {} inbounds and {} outbounds", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size());
        
        super.preprocessClassNode(node);

        raiseIndent();

        dependencies.clear();
    }

    protected void preprocessAfterDependenciesClassNode(ClassNode node) {
        LogManager.getLogger(getClass()).debug("Class \"{}\" with {} inbounds and {} outbounds had {} dependencies.", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size(), () -> dependencies.size());
        
        if (shouldShowClassNode(node) || !dependencies.isEmpty()) {
            lowerIndent();
            indent().printScopeNodeName(node, node.getSimpleName()).eol();
            raiseIndent();
        }

        printDependencies(node, dependencies);
    }

    protected void postprocessClassNode(ClassNode node) {
        lowerIndent();

        super.postprocessClassNode(node);
    }
    
    public void visitInboundClassNode(ClassNode node) {
        if (isShowInbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" <-- \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, -1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" <-- \"{}\"", getCurrentNode(), node);
        }
    }

    public void visitOutboundClassNode(ClassNode node) {
        if (isShowOutbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" --> \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, 1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" --> \"{}\"", getCurrentNode(), node);
        }
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        LogManager.getLogger(getClass()).debug("Printing feature \"{}\" and its {} inbounds and {} outbounds", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size());
        
        super.preprocessFeatureNode(node);

        raiseIndent();

        dependencies.clear();
    }

    protected void postprocessFeatureNode(FeatureNode node) {
        LogManager.getLogger(getClass()).debug("Feature \"{}\" with {} inbounds and {} outbounds had {} dependencies.", () -> node, () -> node.getInboundDependencies().size(), () -> node.getOutboundDependencies().size(), () -> dependencies.size());
        
        if (shouldShowFeatureNode(node) || !dependencies.isEmpty()) {
            lowerIndent();
            indent().printScopeNodeName(node, node.getSimpleName()).eol();
            raiseIndent();
        }
        
        printDependencies(node, dependencies);

        lowerIndent();

        super.postprocessFeatureNode(node);
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        if (isShowInbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" <-- \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, -1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" <-- \"{}\"", getCurrentNode(), node);
        }
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        if (isShowOutbounds()) {
            LogManager.getLogger(getClass()).debug("Printing \"{}\" --> \"{}\"", getCurrentNode(), node);
            dependencies.merge(node, 1, Integer::sum);
        } else {
            LogManager.getLogger(getClass()).debug("Ignoring \"{}\" --> \"{}\"", getCurrentNode(), node);
        }
    }

    protected Printer printNodeName(Node node, String name) {
        super.printNodeName(node, name);

        if (isShowInferred() && !node.isConfirmed()) {
            append(" *");
        }
        
        return this;
    }
    
    protected void printDependencies(Node node, Map<Node, Integer> dependencies) {
        dependencies.forEach((dependency, value) -> {
            if (value < 0) {
                indent().append("<-- ").printDependencyNodeName(dependency).eol();
            } else if (value > 0) {
                indent().append("--> ").printDependencyNodeName(dependency).eol();
            } else {
                indent().append("<-> ").printDependencyNodeName(dependency).eol();
            }
        });
    }
}
