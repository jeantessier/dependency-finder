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

package com.jeantessier.classreader;

import org.apache.oro.text.perl.Perl5Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FilteringSymbolGathererStrategy extends SymbolGathererStrategyDecorator {
    private Perl5Util perl = new Perl5Util();

    private List<String> includes;
    private Collection<String> includesList;
    private List<String> excludes;
    private Collection<String> excludesList;

    public FilteringSymbolGathererStrategy(SymbolGathererStrategy delegate, List<String> includes, Collection<String> includesList, List<String> excludes, Collection<String> excludesList) {
        super(delegate);

        this.includes = includes;
        this.includesList = includesList;
        this.excludes = excludes;
        this.excludesList = excludesList;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public Collection<String> getIncludesList() {
        return includesList;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public Collection<String> getExcludesList() {
        return excludesList;
    }

    public boolean isMatching(Classfile classfile) {
        boolean result = false;

        if (matches(classfile.getClassName())) {
            result = super.isMatching(classfile);
        }

        return result;
    }

    public boolean isMatching(Field_info field) {
        boolean result = false;

        if (matches(field.getFullSignature())) {
            result = super.isMatching(field);
        }

        return result;
    }

    public boolean isMatching(Method_info method) {
        boolean result = false;

        if (matches(method.getFullSignature())) {
            result = super.isMatching(method);
        }

        return result;
    }

    public boolean isMatching(LocalVariable localVariable) {
        boolean result = false;

        if (matches(localVariable.getName())) {
            result = super.isMatching(localVariable);
        }

        return result;
    }

    private boolean matches(String name) {
        return matches(getIncludes(), getIncludesList(), name) && !matches(getExcludes(), getExcludesList(), name);
    }

    private boolean matches(List<String> regularExpressions, Collection<String> valueList, String name) {
        boolean result = false;

        Iterator<String> i = regularExpressions.iterator();
        while (!result && i.hasNext()) {
            result = perl.match(i.next(), name);
        }

        if (!result && valueList != null) {
            result = valueList.contains(name);
        }

        return result;
    }
}
