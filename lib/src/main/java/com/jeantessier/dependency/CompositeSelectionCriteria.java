/*
 *  Copyright (c) 2001-2023, Jean Tessier
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
import java.util.function.*;

public abstract class CompositeSelectionCriteria implements SelectionCriteria {
    private final Collection<? extends SelectionCriteria> subcriteria;
    
    public CompositeSelectionCriteria(Collection<? extends SelectionCriteria> subcriteria) {
        this.subcriteria = Collections.unmodifiableCollection(subcriteria);
    }

    protected Collection<? extends SelectionCriteria> getSubcriteria() {
        return subcriteria;
    }

    public boolean isMatchingPackages() {
        return eval(SelectionCriteria::isMatchingPackages);
    }

    public boolean isMatchingClasses() {
        return eval(SelectionCriteria::isMatchingClasses);
    }

    public boolean isMatchingFeatures() {
        return eval(SelectionCriteria::isMatchingFeatures);
    }

    public boolean matches(PackageNode node) {
        return eval(subcriteria -> subcriteria.matches(node));
    }

    public boolean matches(ClassNode node) {
        return eval(subcriteria -> subcriteria.matches(node));
    }

    public boolean matches(FeatureNode node) {
        return eval(subcriteria -> subcriteria.matches(node));
    }

    public boolean matchesPackageName(String name) {
        return eval(subcriteria -> subcriteria.matchesPackageName(name));
    }

    public boolean matchesClassName(String name) {
        return eval(subcriteria -> subcriteria.matchesClassName(name));
    }

    public boolean matchesFeatureName(String name) {
        return eval(subcriteria -> subcriteria.matchesFeatureName(name));
    }

    protected abstract boolean eval(Predicate<SelectionCriteria> predicate);
}
