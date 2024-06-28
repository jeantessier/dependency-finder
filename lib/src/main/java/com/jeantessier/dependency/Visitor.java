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

import java.util.*;

public interface Visitor {
    default void traverseNodes(Collection<? extends Node> nodes) {
        nodes.forEach(node -> node.accept(this));
    }

    default void traverseInbound(Collection<? extends Node> nodes) {
        nodes.forEach(node -> node.acceptInbound(this));
    }

    default void traverseOutbound(Collection<? extends Node> nodes) {
        nodes.forEach(node -> node.acceptOutbound(this));
    }

    void visitPackageNode(PackageNode node);

    default void visitInboundPackageNode(PackageNode node) {
        // Do nothing
    }

    default void visitOutboundPackageNode(PackageNode node) {
        // Do nothing
    }

    void visitClassNode(ClassNode node);

    default void visitInboundClassNode(ClassNode node) {
        // Do nothing
    }

    default void visitOutboundClassNode(ClassNode node) {
        // Do nothing
    }

    void visitFeatureNode(FeatureNode node);

    default void visitInboundFeatureNode(FeatureNode node) {
        // Do nothing
    }

    default void visitOutboundFeatureNode(FeatureNode node) {
        // Do nothing
    }
}
