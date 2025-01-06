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

import org.jmock.imposters.*;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import com.jeantessier.classreader.*;

public abstract class TestDifferencesFactoryBase {
    public static final String OLD_CLASSPATH = Paths.get("jarjardiff/old/build/libs/old.jar").toString();
    public static final String NEW_CLASSPATH = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private final PackageMapper oldPackages = new PackageMapper();
    private final PackageMapper newPackages = new PackageMapper();

    protected final ClassfileLoader oldLoader = new AggregatingClassfileLoader();
    protected final ClassfileLoader newLoader = new AggregatingClassfileLoader();

    protected PackageMapper getOldPackages() {
        return oldPackages;
    }

    protected PackageMapper getNewPackages() {
        return newPackages;
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    @BeforeEach
    void setImposterizer() {
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }

    @BeforeEach
    void loadClassfiles() {
        oldLoader.addLoadListener(getOldPackages());
        oldLoader.load(Collections.singleton(OLD_CLASSPATH));

        newLoader.addLoadListener(getNewPackages());
        newLoader.load(Collections.singleton(NEW_CLASSPATH));
    }

    protected <T> T mock(Class<T> typeToMock) {
        return context.mock(typeToMock);
    }

    protected <T> T mock(Class<T> typeToMock, String name) {
        return context.mock(typeToMock, name);
    }

    protected void checking(ExpectationBuilder expectations) {
        context.checking(expectations);
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

    protected <T extends FeatureDifferences> T findFeatureDifferences(DifferencesFactory factory, String className, String featureName) {
        Classfile oldClass = oldLoader.getClassfile(className);
        Classfile newClass = newLoader.getClassfile(className);

        var classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        return (T) classDifferences.getFeatureDifferences().stream()
                .filter(feature -> feature.getName().equals(featureName))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(featureName + " not found"));
    }

    protected Differences find(String name, Collection<Differences> differences) {
        return differences.stream()
                .filter(candidate -> name.equals(candidate.getName()))
                .findAny()
                .orElse(null);
    }
}
