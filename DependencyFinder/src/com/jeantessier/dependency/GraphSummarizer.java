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

import org.apache.log4j.*;

public class GraphSummarizer extends GraphCopier {
    private SelectionCriteria scopeCriteria;
    private SelectionCriteria filterCriteria;

    public GraphSummarizer(SelectionCriteria scopeCriteria, SelectionCriteria filterCriteria) {
        super(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));

        this.scopeCriteria  = scopeCriteria;
        this.filterCriteria = filterCriteria;
    }
    
    public void visitPackageNode(PackageNode node) {
        Logger.getLogger(getClass()).debug("node = " + node);
        
        boolean inScope = scopeCriteria.matchesPackageName(node.getName());

        if (inScope) {
            preprocessPackageNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            preprocessAfterDependenciesPackageNode(node);
        }
            
        traverseNodes(node.getClasses());

        if (inScope) {
            postprocessBeforeDependenciesPackageNode(node);

            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }

            postprocessPackageNode(node);
        }
    }

    protected void preprocessPackageNode(PackageNode node) {
        if (scopeCriteria.isMatchingPackages()) {
            super.preprocessPackageNode(node);
        }
    }
    
    protected void postprocessPackageNode(PackageNode node) {
        if (scopeCriteria.isMatchingPackages()) {
            super.postprocessPackageNode(node);
        }
    }

    public void visitInboundPackageNode(PackageNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesPackageName(node.getName())) {
            if (filterCriteria.isMatchingPackages()) {
                getFilterFactory().createPackage(node.getName(), node.isConfirmed()).addDependency(getCurrentNode());
            }
        }
    }

    public void visitOutboundPackageNode(PackageNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesPackageName(node.getName())) {
            if (filterCriteria.isMatchingPackages()) {
                getCurrentNode().addDependency(getFilterFactory().createPackage(node.getName(), node.isConfirmed()));
            }
        }
    }

    public void visitClassNode(ClassNode node) {
        boolean inScope = scopeCriteria.matchesClassName(node.getName());
        
        if (inScope) {
            preprocessClassNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
        
            preprocessAfterDependenciesClassNode(node);
        }
        
        traverseNodes(node.getFeatures());
            
        if (inScope) {
            postprocessBeforeDependenciesClassNode(node);

            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            postprocessClassNode(node);
        }
    }

    protected void preprocessClassNode(ClassNode node) {
        if (scopeCriteria.isMatchingClasses()) {
            super.preprocessClassNode(node);
        }
    }
    
    protected void postprocessClassNode(ClassNode node) {
        if (scopeCriteria.isMatchingClasses()) {
            super.postprocessClassNode(node);
        }
    }

    public void visitInboundClassNode(ClassNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesClassName(node.getName())) {
            if (filterCriteria.isMatchingClasses()) {
                getFilterFactory().createClass(node.getName(), node.isConfirmed()).addDependency(getCurrentNode());
            } else if (filterCriteria.isMatchingPackages()) {
                getFilterFactory().createPackage(node.getPackageNode().getName(), node.getPackageNode().isConfirmed()).addDependency(getCurrentNode());
            }
        }
    }

    public void visitOutboundClassNode(ClassNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesClassName(node.getName())) {
            if (filterCriteria.isMatchingClasses()) {
                getCurrentNode().addDependency(getFilterFactory().createClass(node.getName(), node.isConfirmed()));
            } else if (filterCriteria.isMatchingPackages()) {
                getCurrentNode().addDependency(getFilterFactory().createPackage(node.getPackageNode().getName(), node.getPackageNode().isConfirmed()));
            }
        }
    }

    public void visitFeatureNode(FeatureNode node) {
        if (scopeCriteria.matchesFeatureName(node.getName())) {
            preprocessFeatureNode(node);
            
            if (getStrategy().doPreOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPreInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            if (getStrategy().doPostOutboundTraversal()) {
                traverseOutbound(node.getOutboundDependencies());
            }
            
            if (getStrategy().doPostInboundTraversal()) {
                traverseInbound(node.getInboundDependencies());
            }
            
            postprocessFeatureNode(node);
        }
    }

    protected void preprocessFeatureNode(FeatureNode node) {
        if (scopeCriteria.isMatchingFeatures()) {
            super.preprocessFeatureNode(node);
        }
    }
    
    protected void postprocessFeatureNode(FeatureNode node) {
        if (scopeCriteria.isMatchingFeatures()) {
            super.postprocessFeatureNode(node);
        }
    }
    
    public void visitInboundFeatureNode(FeatureNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesFeatureName(node.getName())) {
            if (filterCriteria.isMatchingFeatures()) {
                getFilterFactory().createFeature(node.getName(), node.isConfirmed()).addDependency(getCurrentNode());
            } else if (filterCriteria.isMatchingClasses()) {
                getFilterFactory().createClass(node.getClassNode().getName(), node.getClassNode().isConfirmed()).addDependency(getCurrentNode());
            } else if (filterCriteria.isMatchingPackages()) {
                getFilterFactory().createPackage(node.getClassNode().getPackageNode().getName(), node.getClassNode().getPackageNode().isConfirmed()).addDependency(getCurrentNode());
            }
        }
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        if (getCurrentNode() != null && filterCriteria.matchesFeatureName(node.getName())) {
            if (filterCriteria.isMatchingFeatures()) {
                getCurrentNode().addDependency(getFilterFactory().createFeature(node.getName(), node.isConfirmed()));
            } else if (filterCriteria.isMatchingClasses()) {
                getCurrentNode().addDependency(getFilterFactory().createClass(node.getClassNode().getName(), node.getClassNode().isConfirmed()));
            } else if (filterCriteria.isMatchingPackages()) {
                getCurrentNode().addDependency(getFilterFactory().createPackage(node.getClassNode().getPackageNode().getName(), node.getClassNode().getPackageNode().isConfirmed()));
            }
        }
    }
}
