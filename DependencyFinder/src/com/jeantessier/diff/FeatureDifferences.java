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

package com.jeantessier.diff;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

/**
 * Documents the difference, if any, for a given feature
 * (field, constructor, or method).  Its subclasses only
 * differ in which Visitor callback they invoke.
 *
 * @see Visitor
 */
public abstract class FeatureDifferences extends RemovableDifferences {
    private Feature_info oldFeature;
    private Feature_info newFeature;

    private boolean inherited = false;

    /**
     * Only the DifferencesFactory can create instances of this class.
     */
    protected FeatureDifferences(String name, Feature_info oldFeature, Feature_info newFeature) {
        super(name);

        setOldFeature(oldFeature);
        setNewFeature(newFeature);

        if (isModified()) {
            Logger.getLogger(getClass()).debug(getName() + " declaration has been modified.");
        } else {
            Logger.getLogger(getClass()).debug(getName() + " declaration has not been modified.");
        }
    }

    public Feature_info getOldFeature() {
        return oldFeature;
    }

    protected void setOldFeature(Feature_info oldFeature) {
        this.oldFeature = oldFeature;
    }

    public Feature_info getNewFeature() {
        return newFeature;
    }

    protected void setNewFeature(Feature_info newFeature) {
        this.newFeature = newFeature;
    }

    public String getOldDeclaration() {
        String result = null;

        if (getOldFeature() != null) {
            result = getOldFeature().getDeclaration();
        }

        return result;
    }

    public String getNewDeclaration() {
        String result = null;

        if (getNewFeature() != null) {
            result = getNewFeature().getDeclaration();
        }

        return result;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }
}
