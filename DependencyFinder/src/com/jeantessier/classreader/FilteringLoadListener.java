// Copyright 2008 Google Inc.
// All Rights Reserved.
package com.jeantessier.classreader;

import java.util.*;

import org.apache.oro.text.perl.*;

import com.jeantessier.text.*;

/**
 * @author jeantessier
 */
public class FilteringLoadListener extends LoadListenerDecorator {
    private Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());

    protected List<String> includes;
    protected List<String> excludes;

    public FilteringLoadListener(LoadListener delegate, List<String> includes, List<String> excludes) {
        super(delegate);

        this.includes = includes;
        this.excludes = excludes;
    }

    protected boolean matches(String name) {
        return matches(includes, name) && !matches(excludes, name);
    }

    private boolean matches(List<String> regularExpressions, String name) {
        boolean found = false;

        Iterator<String> i = regularExpressions.iterator();
        while (!found && i.hasNext()) {
            String condition = i.next();
            found = perl.match(condition, name);
        }

        return found;
    }
}
