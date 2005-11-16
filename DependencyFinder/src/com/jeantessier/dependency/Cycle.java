package com.jeantessier.dependency;

import java.util.*;

/**
 * TODO Class comment
 */
public class Cycle {
    private List path;
    private Set  nodes;

    public Cycle(List path) {
        this.path  = new ArrayList(path);
        this.nodes = new TreeSet(path);
    }

    public boolean equals(Object object) {
        boolean result;

        if (this == object) {
            result = true;
        } else if (object == null || getClass() != object.getClass()) {
            result = false;
        } else {
            Cycle other = (Cycle) object;
            result = nodes.size() == other.nodes.size() &&
                    nodes.containsAll(other.nodes) &&
                    other.nodes.containsAll(nodes);
        }

        return result;
    }

    public int hashCode() {
        return nodes.hashCode();
    }
}
