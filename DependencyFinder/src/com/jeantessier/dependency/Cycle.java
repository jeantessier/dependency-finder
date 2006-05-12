package com.jeantessier.dependency;

import java.util.*;

/**
 * TODO Class comment
 */
public class Cycle implements Comparable {
    private Set<Node> nodes;
    private List<Node> path;

    public Cycle(List<Node> path) {
        this.nodes = new TreeSet<Node>(path);

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
        return path.size();
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
        return nodes.hashCode();
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
}
