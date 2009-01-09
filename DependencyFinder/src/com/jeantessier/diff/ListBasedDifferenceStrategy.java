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

import java.io.*;
import java.util.*;

import com.jeantessier.classreader.*;

public class ListBasedDifferenceStrategy extends DifferenceStrategyDecorator {
    private Collection<String> allowedElements = new HashSet<String>();

    public ListBasedDifferenceStrategy(DifferenceStrategy delegate, String filename) throws IOException {
        super(delegate);

        load(filename);
    }

    public ListBasedDifferenceStrategy(DifferenceStrategy delegate, File file) throws IOException {
        super(delegate);

        load(file);
    }

    public ListBasedDifferenceStrategy(DifferenceStrategy delegate, BufferedReader in) throws IOException {
        super(delegate);

        load(in);
    }

    public void load(String filename) throws IOException {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(filename));
            load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void load(File file) throws IOException {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(file));
            load(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void load(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.length() > 0) {
                line = line.trim();
                int pos = line.lastIndexOf(" [");
                if (pos != -1) {
                    allowedElements.add(line.substring(0, pos));
                } else {
                    allowedElements.add(line);
                }
            }
        }
    }

    public boolean isPackageAllowed(String name) {
        return isAllowed(name);
    }

    public boolean isClassAllowed(String name) {
        return isAllowed(name);
    }

    public boolean isFeatureAllowed(String name) {
        return isAllowed(name);
    }

    public boolean isAllowed(String name) {
        return allowedElements.contains(name);
    }

    public boolean isPackageDifferent(Map<String, Classfile> oldPackage, Map<String, Classfile> newPackage) {
        Map<String, Classfile> nonEmptyPackage = oldPackage.isEmpty() ? newPackage : oldPackage;
        String className = nonEmptyPackage.keySet().iterator().next();
        String packageName = "";
        int pos = className.lastIndexOf('.');
        if (pos != -1) {
            packageName = className.substring(0, pos);
        }

        boolean result = isPackageAllowed(packageName);
        if (result) {
            result = super.isPackageDifferent(oldPackage, newPackage);
        }

        return result;
    }

    public boolean isClassDifferent(Classfile oldClass, Classfile newClass) {
        Classfile classfile = oldClass != null ? oldClass : newClass;

        boolean result = isClassAllowed(classfile.getClassName());
        if (result) {
            result = super.isClassDifferent(oldClass, newClass);
        }

        return result;
    }

    public boolean isFieldDifferent(Field_info oldField, Field_info newField) {
        Field_info field = oldField != null ? oldField : newField;

        boolean result = isFeatureAllowed(field.getFullSignature());
        if (result) {
            result = super.isFieldDifferent(oldField, newField);
        }

        return result;
    }

    public boolean isMethodDifferent(Method_info oldMethod, Method_info newMethod) {
        Method_info method = oldMethod != null ? oldMethod : newMethod;

        boolean result = isFeatureAllowed(method.getFullSignature());
        if (result) {
            result = super.isMethodDifferent(oldMethod, newMethod);
        }

        return result;
    }
}
