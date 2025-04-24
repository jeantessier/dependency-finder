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

package com.jeantessier.diff;

import java.util.*;

import com.jeantessier.classreader.Classfile;
import org.apache.logging.log4j.*;

/**
 *  Documents the difference, if any, for a given package.
 */
public class PackageDifferences extends RemovableDifferences {
    private final Collection<Differences> classDifferences = new LinkedList<>();

    private final String oldDeclaration;
    private final String newDeclaration;

    /**
     *  Only the DifferencesFactory can create instances of this class.
     */
    PackageDifferences(String name, Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        super(name);

        oldDeclaration = (oldPackage != null && !oldPackage.isEmpty()) ? name : null;
        newDeclaration = (newPackage != null && !newPackage.isEmpty()) ? name : null;

        if (isModified()) {
            LogManager.getLogger(getClass()).debug("{} declaration has been modified.", getName());
        } else {
            LogManager.getLogger(getClass()).debug("{} declaration has not been modified.", getName());
        }
    }

    public Collection<Differences> getClassDifferences() {
        return classDifferences;
    }

    public String getOldDeclaration() {
        return oldDeclaration;
    }

    public String getNewDeclaration() {
        return newDeclaration;
    }

    public boolean isModified() {
        return super.isModified() || !getClassDifferences().isEmpty();
    }

    public void accept(Visitor visitor) {
        visitor.visitPackageDifferences(this);
    }
}
