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

public abstract class DecoratorTraversalStrategy implements TraversalStrategy {
    private TraversalStrategy strategy;

    public DecoratorTraversalStrategy(TraversalStrategy strategy) {
        this.strategy = strategy;
    }
    
    public boolean doPreOutboundTraversal() {
        return strategy.doPreOutboundTraversal();
    }
    
    public void setPreOutboundTraversal(boolean preOutboundTraversal) {
        strategy.setPreOutboundTraversal(preOutboundTraversal);
    }
    
    public boolean doPreInboundTraversal() {
        return strategy.doPreInboundTraversal();
    }
    
    public void setPreInboundTraversal(boolean preInboundTraversal) {
        strategy.setPreInboundTraversal(preInboundTraversal);
    }
    
    public boolean doPostOutboundTraversal() {
        return strategy.doPostOutboundTraversal();
    }
    
    public void setPostOutboundTraversal(boolean postOutboundTraversal) {
        strategy.setPostOutboundTraversal(postOutboundTraversal);
    }
    
    public boolean doPostInboundTraversal() {
        return strategy.doPostInboundTraversal();
    }

    public void setPostInboundTraversal(boolean postInboundTraversal) {
        strategy.setPostInboundTraversal(postInboundTraversal);
    }
    
    public boolean isInScope(PackageNode node) {
        return strategy.isInScope(node);
    }

    public boolean isInScope(ClassNode node) {
        return strategy.isInScope(node);
    }

    public boolean isInScope(FeatureNode node) {
        return strategy.isInScope(node);
    }

    public boolean isInFilter(PackageNode node) {
        return strategy.isInFilter(node);
    }

    public boolean isInFilter(ClassNode node) {
        return strategy.isInFilter(node);
    }

    public boolean isInFilter(FeatureNode node) {
        return strategy.isInFilter(node);
    }

    public <T extends Node> Collection<T> order(Collection<T> collection) {
        return strategy.order(collection);
    }
}
