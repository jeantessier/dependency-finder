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

public class CollectionSelectionCriteria implements SelectionCriteria {
    private boolean matchingPackages = true;
    private boolean matchingClasses = true;
    private boolean matchingFeatures = true;

    private Collection<String> include;
    private Collection<String> exclude;

    public CollectionSelectionCriteria(Collection<String> include, Collection<String> exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    public boolean isMatchingPackages() {
        return matchingPackages;
    }

    public void setMatchingPackages(boolean matchingPackages) {
        this.matchingPackages = matchingPackages;
    }

    public boolean isMatchingClasses() {
        return matchingClasses;
    }

    public void setMatchingClasses(boolean matchingClasses) {
        this.matchingClasses = matchingClasses;
    }

    public boolean isMatchingFeatures() {
        return matchingFeatures;
    }

    public void setMatchingFeatures(boolean matchingFeatures) {
        this.matchingFeatures = matchingFeatures;
    }

    public boolean matches(PackageNode node) {
        return matchesName(node.getName());
    }

    public boolean matches(ClassNode node) {
        return matchesName(node.getName());
    }

    public boolean matches(FeatureNode node) {
        return matchesName(node.getName());
    }

    public boolean matchesPackageName(String name) {
        return matchesName(name);
    }

    public boolean matchesClassName(String name) {
        return matchesName(name);
    }

    public boolean matchesFeatureName(String name) {
        return matchesName(name);
    }

    private boolean matchesName(String name) {
        return (include == null || include.contains(name)) && (exclude == null || !exclude.contains(name));
    }
}
