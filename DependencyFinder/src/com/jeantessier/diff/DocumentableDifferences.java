/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
 *  element that can be added or removed from the published
 *  documentation for the API.
 */
public class DocumentableDifferences extends DecoratorDifferences {
    private boolean newDocumentation;
    private boolean removedDocumentation;

    /**
     *  Only the DifferencesFactory can create instances of this class.
     */
    DocumentableDifferences(Differences component, Validator oldValidator, Validator newValidator) {
        super(component);

        Logger.getLogger(getClass()).debug("Begin " + getName());

        setNewDocumentation(!oldValidator.isAllowed(component.getName()) && newValidator.isAllowed(component.getName()));
        setRemovedDocumentation(oldValidator.isAllowed(component.getName()) && !newValidator.isAllowed(component.getName()));

        Logger.getLogger(getClass()).debug("End   " + getName() + ": " + (isEmpty() ? "empty" : "not empty"));
    }

    public boolean isNewDocumentation() {
        Logger.getLogger(getClass()).debug(getName() + " NewDocumentation(): " + newDocumentation);
        return newDocumentation;
    }

    public void setNewDocumentation(boolean newDocumentation) {
        this.newDocumentation = newDocumentation;
    }

    public boolean isRemovedDocumentation() {
        Logger.getLogger(getClass()).debug(getName() + " RemovedDocumentation(): " + removedDocumentation);
        return removedDocumentation;
    }

    public void setRemovedDocumentation(boolean removedDocumentation) {
        this.removedDocumentation = removedDocumentation;
    }

    public boolean isEmpty() {
        return
            !isNewDocumentation() &&
            !isRemovedDocumentation() &&
            getComponent().isEmpty();
    }

    public void accept(Visitor visitor) {
        visitor.visitDocumentableDifferences(this);
    }
}
