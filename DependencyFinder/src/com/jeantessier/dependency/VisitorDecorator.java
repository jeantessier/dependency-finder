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

public class VisitorDecorator implements Visitor {
    private Visitor delegate;

    public Visitor getDelegate() {
        return delegate;
    }

    public void setDelegate(Visitor delegate) {
        this.delegate = delegate;
    }

    public void traverseNodes(Collection<? extends Node> nodes) {
        getDelegate().traverseNodes(nodes);
    }

    public void traverseInbound(Collection<? extends Node> nodes) {
        getDelegate().traverseInbound(nodes);
    }

    public void traverseOutbound(Collection<? extends Node> nodes) {
        getDelegate().traverseOutbound(nodes);
    }

    public void visitPackageNode(PackageNode node) {
        node.accept(getDelegate());
    }

    public void visitInboundPackageNode(PackageNode node) {
        node.acceptInbound(getDelegate());
    }

    public void visitOutboundPackageNode(PackageNode node) {
        node.acceptOutbound(getDelegate());
    }

    public void visitClassNode(ClassNode node) {
        node.accept(getDelegate());
    }

    public void visitInboundClassNode(ClassNode node) {
        node.acceptInbound(getDelegate());
    }

    public void visitOutboundClassNode(ClassNode node) {
        node.acceptOutbound(getDelegate());
    }

    public void visitFeatureNode(FeatureNode node) {
        node.accept(getDelegate());
    }

    public void visitInboundFeatureNode(FeatureNode node) {
        node.acceptInbound(getDelegate());
    }

    public void visitOutboundFeatureNode(FeatureNode node) {
        node.acceptOutbound(getDelegate());
    }
}
