/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import java.nio.file.*;
import java.util.*;

import com.jeantessier.classreader.*;
import org.jmock.imposters.*;
import org.jmock.integration.junit3.*;

public abstract class TestDifferencesFactoryBase extends MockObjectTestCase {
    public static final String OLD_CLASSPATH = Paths.get("jarjardiff/old/build/libs/old.jar").toString();
    public static final String NEW_CLASSPATH = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private static PackageMapper oldPackages;
    private static PackageMapper newPackages;

    private static ClassfileLoader oldJar;
    private static ClassfileLoader newJar;

    protected static PackageMapper getOldPackages() {
        if (oldPackages == null) {
            oldPackages = new PackageMapper();
        }

        return oldPackages;
    }

    protected static PackageMapper getNewPackages() {
        if (newPackages == null) {
            newPackages = new PackageMapper();
        }

        return newPackages;
    }

    protected static ClassfileLoader getOldJar() {
        if (oldJar == null) {
            oldJar = new AggregatingClassfileLoader();
            oldJar.addLoadListener(getOldPackages());
            oldJar.load(Collections.singleton(OLD_CLASSPATH));
        }

        return oldJar;
    }

    protected static ClassfileLoader getNewJar() {
        if (newJar == null) {
            newJar = new AggregatingClassfileLoader();
            newJar.addLoadListener(getNewPackages());
            newJar.load(Collections.singleton(NEW_CLASSPATH));
        }

        return newJar;
    }

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        // Make sure classes are loaded
        getOldJar();
        getNewJar();
    }

    protected Map<String, Classfile> findPackage(String name, PackageMapper packages) {
        return packages.getPackage(name);
    }

    protected Classfile findClass(String name, PackageMapper packages) {
        String packageName = name.substring(0, name.lastIndexOf("."));
        return findPackage(packageName, packages).get(name);
    }

    protected Field_info findField(String name, PackageMapper packages) {
        int pos = name.lastIndexOf(".");
        String className = name.substring(0, pos);
        Classfile classfile = findClass(className, packages);
        return classfile.getField(field -> field.getName().equals(name.substring(pos + 1)));
    }

    protected Method_info findMethod(String name, PackageMapper packages) {
        int pos = name.lastIndexOf(".");
        String className = name.substring(0, pos);
        Classfile classfile = findClass(className, packages);
        return classfile.getMethod(method -> method.getSignature().equals(name.substring(pos + 1)));
    }

    protected Differences find(String name, Collection<Differences> differences) {
        return differences.stream()
                .filter(candidate -> name.equals(candidate.getName()))
                .findAny()
                .orElse(null);
    }
}
