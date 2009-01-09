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

/**
 * TODO Class comment
 */
public class Cycle implements Comparable {
    private List<Node> path;

    public Cycle(List<Node> path) {
        Set<Node> nodes = new TreeSet<Node>(path);
        Node first = nodes.iterator().next();
        LinkedList<Node> rawPath = new LinkedList<Node>(path);
        while (!rawPath.getFirst().equals(first)) {
            rawPath.addLast(rawPath.removeFirst());
        }

        this.path = rawPath;
    }

    public List<Node> getPath() {
        return Collections.unmodifiableList(path);
    }

    public int getLength() {
        return getPath().size();
    }

    public boolean equals(Object object) {
        boolean result;

        if (this == object) {
            result = true;
        } else if (object == null || getClass() != object.getClass()) {
            result = false;
        } else {
            Cycle other = (Cycle) object;
            result = compareTo(other) == 0;
        }

        return result;
    }

    public int hashCode() {
        return getPath().hashCode();
    }

    public int compareTo(Object object) {
        int result;

        if (this == object) {
            result = 0;
        } else if (object == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        } else if (!(object instanceof Cycle)) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got a " + object.getClass().getName());
        } else {
            Cycle other = (Cycle) object;

            result = getLength() - other.getLength();
            Iterator<Node> theseNodes = getPath().iterator();
            Iterator<Node> otherNodes = other.getPath().iterator();
            while (result == 0 && theseNodes.hasNext() && otherNodes.hasNext()) {
                result = theseNodes.next().compareTo(otherNodes.next());
            }
        }

        return result;
    }

    public String toString() {
        return getPath().toString();
    }
}
