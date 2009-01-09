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

public class FeatureNode extends Node {
    private ClassNode parent;

    public FeatureNode(ClassNode parent, String name, boolean concrete) {
        super(name, concrete);
        this.parent = parent;
    }

    // Only to be used by NodeFactory and DeletingVisitor
    void setConfirmed(boolean confirmed) {
        super.setConfirmed(confirmed);
        if (confirmed) {
            getClassNode().setConfirmed(confirmed);
        }
    }

    public ClassNode getClassNode() {
        return parent;
    }

    public boolean canAddDependencyTo(Node node) {
        return super.canAddDependencyTo(node) && getClassNode().canAddDependencyTo(node);
    }

    public void accept(Visitor visitor) {
        visitor.visitFeatureNode(this);
    }

    public void acceptInbound(Visitor visitor) {
        visitor.visitInboundFeatureNode(this);
    }

    public void acceptOutbound(Visitor visitor) {
        visitor.visitOutboundFeatureNode(this);
    }

    public String getSimpleName() {
        return getName().substring(getClassNode().getName().length() + 1);
    }
}
