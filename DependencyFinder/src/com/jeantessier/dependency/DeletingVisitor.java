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

import com.jeantessier.classreader.*;

public class DeletingVisitor implements Visitor, RemoveVisitor {
    private NodeFactory factory;
    
    public DeletingVisitor(NodeFactory factory) {
        this.factory = factory;
    }

    public NodeFactory getFactory() {
        return factory;
    }
    
    public void traverseNodes(Collection nodes) {
// 		Iterator i = nodes.iterator();
// 		while (i.hasNext()) {
// 			((Node) i.next()).accept(this);
// 		}
    }

    /*
     *  Regular visits are used to completely delete sections
     */
    
    public void visitPackageNode(PackageNode node) {
        Logger.getLogger(getClass()).debug("visitPackageNode(" + node + ")");
        
        Iterator i = new ArrayList(node.getClasses()).iterator();
        while (i.hasNext()) {
            ((Node) i.next()).accept(this);
        }

        visitNode(node);
    }

    public void visitClassNode(ClassNode node) {
        Logger.getLogger(getClass()).debug("visitClassNode(" + node + ")");
        
        Iterator i = new ArrayList(node.getFeatures()).iterator();
        while (i.hasNext()) {
            ((Node) i.next()).accept(this);
        }

        visitNode(node);
    }

    public void visitFeatureNode(FeatureNode node) {
        Logger.getLogger(getClass()).debug("visitFeatureNode(" + node + ")");
        
        visitNode(node);
    }

    private void visitNode(Node node) {
        node.setConcrete(false);

        Iterator i = new ArrayList(node.getOutboundDependencies()).iterator();
        while (i.hasNext()) {
            Node outbound = (Node) i.next();
            node.removeDependency(outbound);
            outbound.acceptOutbound(this);
        }
        
        node.acceptOutbound(this);
    }

    /*
     *  Outbound visits are used to clean up sections
     */
    
    public void visitOutboundPackageNode(PackageNode node) {
        Logger.getLogger(getClass()).debug("visitOutboundPackageNode(" + node + ")");
        
        if (canDeletePackage(node)) {
            factory.deletePackage(node);
        }
    }
    
    public void visitOutboundClassNode(ClassNode node) {
        Logger.getLogger(getClass()).debug("visitOutboundClassNode(" + node + ")");
        
        if (canDeleteClass(node)) {
            factory.deleteClass(node);
        }

        node.getPackageNode().acceptOutbound(this);
    }
    
    public void visitOutboundFeatureNode(FeatureNode node) {
        Logger.getLogger(getClass()).debug("visitOutboundFeatureNode(" + node + ")");
        
        if (canDeleteFeature(node)) {
            factory.deleteFeature(node);
        }

        node.getClassNode().acceptOutbound(this);
    }

    private boolean canDelete(Node node) {
        return !node.isConcrete() && node.getInboundDependencies().isEmpty();
    }

    private boolean canDeletePackage(PackageNode node) {
        return canDelete(node) && node.getClasses().isEmpty();
    }

    private boolean canDeleteClass(ClassNode node) {
        return canDelete(node) && node.getFeatures().isEmpty();
    }

    private boolean canDeleteFeature(FeatureNode node) {
        return canDelete(node);
    }

    /*
     *  Inbound visits are not used
     */
    
    public void visitInboundPackageNode(PackageNode node) {
        // Do nothing
    }
    
    public void visitInboundClassNode(ClassNode node) {
        // Do nothing
    }
    
    public void visitInboundFeatureNode(FeatureNode node) {
        // Do nothing
    }

    /*
     * RemoveVisitor methods
     */
    
    public void removeClass(String classname) {
        Node node = (Node) factory.getClasses().get(classname);
        if (node != null) {
            node.accept(this);
        }
    }
}
