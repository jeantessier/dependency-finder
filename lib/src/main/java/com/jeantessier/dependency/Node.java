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

public abstract class Node implements Comparable<Node> {
    private final String name;
    private boolean confirmed;
    
    private final Collection<Node> inbound  = new HashSet<>();
    private final Collection<Node> outbound = new HashSet<>();

    public Node(String name, boolean confirmed) {
        this.name = name;
        this.confirmed = confirmed;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return getName();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // Only to be used by NodeFactory and DeletingVisitor
    void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
    
    public boolean canAddDependencyTo(Node node) {
        return !equals(node);
    }
    
    public void addDependency(Node node) {
        if (canAddDependencyTo(node) && node.canAddDependencyTo(this)) {
            outbound.add(node);
            node.inbound.add(this);
        }
    }

    public void addDependencies(Collection<Node> nodes) {
        nodes.forEach(this::addDependency);
    }

    public void removeDependency(Node node) {
        outbound.remove(node);
        node.inbound.remove(this);
    }

    public void removeDependencies(Collection<? extends Node> nodes) {
        nodes.forEach(this::removeDependency);
    }

    public Collection<Node> getInboundDependencies() {
        return Collections.unmodifiableCollection(inbound);
    }

    public Collection<Node> getOutboundDependencies() {
        return Collections.unmodifiableCollection(outbound);
    }

    public abstract void accept(Visitor visitor);
    public abstract void acceptInbound(Visitor visitor);
    public abstract void acceptOutbound(Visitor visitor);

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((Node) object) == 0;
    }

    public int compareTo(Node other) {
        if (this == other) {
            return 0;
        }

        if (other == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        }

        return getName().compareTo(other.getName());
    }

    public String toString() {
        return getName();
    }
}
