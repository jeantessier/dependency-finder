/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

package com.jeantessier.dependencyfinder.cli;

import java.util.*;

import org.apache.oro.text.perl.*;

import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

public class ClassMatcher extends LoadListenerBase {
    private static final Perl5Util perl = new Perl5Util(new MaximumCapacityPatternCache());

    private final Collection<String> includes;
    private final Collection<String> excludes;

    private final Map<String, List<String>> results = new TreeMap<>();

    public ClassMatcher(Collection<String> includes, Collection<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    public Map<String, List<String>> getResults() {
        return results;
    }

    public void endClassfile(LoadEvent event) {
        super.endClassfile(event);

        String className = event.getClassfile().getClassName();
        String groupName = event.getGroupName();

        if (matches(className)) {
            results.computeIfAbsent(className, k -> new LinkedList<>()).add(groupName);
        }
    }

    private boolean matches(String name) {
        return matches(includes, name) && !matches(excludes, name);
    }

    private boolean matches(Collection<String> regularExpressions, String name) {
        return regularExpressions.stream().anyMatch(condition -> perl.match(condition, name));
    }
}
