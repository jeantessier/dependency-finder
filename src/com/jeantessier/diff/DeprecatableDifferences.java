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
 *  Documents the difference, if any, for a given programming
 *  element that can be deprecated through the use of javadoc
 *  tags.
 */
public class DeprecatableDifferences extends DecoratorDifferences {
    private boolean newDeprecation;
    private boolean removedDeprecation;

    /**
     *  Only the DifferencesFactory can create instances of this class.
     */
    DeprecatableDifferences(Differences component, Deprecatable oldDeprecatable, Deprecatable newDeprecatable) {
        super(component);

        Logger.getLogger(getClass()).debug("Begin " + getName());

        if (oldDeprecatable != null && newDeprecatable != null) {
            Logger.getLogger(getClass()).debug("      old deprecatable: " + oldDeprecatable.isDeprecated());
            Logger.getLogger(getClass()).debug("      new deprecatable: " + newDeprecatable.isDeprecated());

            setRemovedDeprecation(oldDeprecatable.isDeprecated() && !newDeprecatable.isDeprecated());
            setNewDeprecation(!oldDeprecatable.isDeprecated() && newDeprecatable.isDeprecated());
        }

        Logger.getLogger(getClass()).debug("End   " + getName());
    }

    public boolean isNewDeprecation() {
        Logger.getLogger(getClass()).debug(getName() + " NewDeprecation(): " + newDeprecation);
        return newDeprecation;
    }

    public void setNewDeprecation(boolean newDeprecation) {
        this.newDeprecation = newDeprecation;
    }

    public boolean isRemovedDeprecation() {
        Logger.getLogger(getClass()).debug(getName() + " RemovedDeprecation(): " + removedDeprecation);
        return removedDeprecation;
    }

    public void setRemovedDeprecation(boolean removedDeprecation) {
        this.removedDeprecation = removedDeprecation;
    }

    public void accept(Visitor visitor) {
        visitor.visitDeprecatableDifferences(this);
    }
}
