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

import com.jeantessier.classreader.Class_info;
import com.jeantessier.classreader.Classfile;

import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Report extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "https://jeantessier.github.io/dependency-finder/dtd";

    private String name;
    private String oldVersion;
    private String newVersion;

    private final Collection<Differences> removedPackages = new TreeSet<>();

    private final Collection<ClassDifferences> removedInterfaces = new TreeSet<>();
    private final Collection<ClassDifferences> removedClasses = new TreeSet<>();

    private final Collection<ClassDifferences> deprecatedInterfaces = new TreeSet<>();
    private final Collection<ClassDifferences> deprecatedClasses = new TreeSet<>();
    
    private final Collection<ClassReport> modifiedInterfaces = new TreeSet<>();
    private final Collection<ClassReport> modifiedClasses = new TreeSet<>();

    private final Collection<ClassDifferences> undeprecatedInterfaces = new TreeSet<>();
    private final Collection<ClassDifferences> undeprecatedClasses = new TreeSet<>();
    
    private final Collection<Differences> newPackages = new TreeSet<>();

    private final Collection<ClassDifferences> newInterfaces = new TreeSet<>();
    private final Collection<ClassDifferences> newClasses = new TreeSet<>();

    public Report() {
        this(DEFAULT_INDENT_TEXT, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public Report(String indentText, String encoding, String dtdPrefix) {
        super(indentText);
        appendHeader(encoding, dtdPrefix);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE differences SYSTEM \"").append(dtdPrefix).append("/differences.dtd\">").eol();
        eol();
    }

    public void visitProjectDifferences(ProjectDifferences differences) {
        setName(differences.getName());
        setOldVersion(differences.getOldVersion());
        setNewVersion(differences.getNewVersion());

        differences.getPackageDifferences().forEach(packageDifference -> packageDifference.accept(this));
    }

    public void visitPackageDifferences(PackageDifferences differences) {
        if (differences.isRemoved()) {
            removedPackages.add(differences);
        }

        differences.getClassDifferences().forEach(classDifference -> classDifference.accept(this));

        if (differences.isNew()) {
            newPackages.add(differences);
        }
    }

    public void visitClassDifferences(ClassDifferences differences) {
        if (differences.isRemoved()) {
            removedClasses.add(differences);
        }
    
        if (differences.isModified()) {
            ClassReport visitor = new ClassReport(getIndentText());
            differences.accept(visitor);
            modifiedClasses.add(visitor);
        }
    
        if (differences.isNew()) {
            newClasses.add(differences);
        }

        if (isDeprecated()) {
            deprecatedClasses.add(differences);
        }

        if (isUndeprecated()) {
            undeprecatedClasses.add(differences);
        }
    }

    public void visitInterfaceDifferences(InterfaceDifferences differences) {
        if (differences.isRemoved()) {
            removedInterfaces.add(differences);
        }
    
        if (differences.isModified()) {
            ClassReport classReport = new ClassReport(getIndentText());
            differences.accept(classReport);
            modifiedInterfaces.add(classReport);
        }
    
        if (differences.isNew()) {
            newInterfaces.add(differences);
        }

        if (isDeprecated()) {
            deprecatedInterfaces.add(differences);
        }

        if (isUndeprecated()) {
            undeprecatedInterfaces.add(differences);
        }
    }

    public String render() {
        indent().append("<differences>").eol();
        raiseIndent();

        indent().append("<name>").append(name).append("</name>").eol();
        indent().append("<old>").append(oldVersion).append("</old>").eol();
        indent().append("<new>").append(newVersion).append("</new>").eol();
    
        if (!removedPackages.isEmpty()) {
            indent().append("<removed-packages>").eol();
            raiseIndent();

            removedPackages.forEach(removedPackage -> {
                indent().append("<name>").append(removedPackage).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</removed-packages>").eol();
        }

        if (!removedInterfaces.isEmpty()) {
            indent().append("<removed-interfaces>").eol();
            raiseIndent();

            removedInterfaces.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</removed-interfaces>").eol();
        }

        if (!removedClasses.isEmpty()) {
            indent().append("<removed-classes>").eol();
            raiseIndent();

            removedClasses.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</removed-classes>").eol();
        }

        if (!deprecatedInterfaces.isEmpty()) {
            indent().append("<deprecated-interfaces>").eol();
            raiseIndent();

            deprecatedInterfaces.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</deprecated-interfaces>").eol();
        }

        if (!deprecatedClasses.isEmpty()) {
            indent().append("<deprecated-classes>").eol();
            raiseIndent();

            deprecatedClasses.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</deprecated-classes>").eol();
        }

        if (!modifiedInterfaces.isEmpty()) {
            indent().append("<modified-interfaces>").eol();
            raiseIndent();

            modifiedInterfaces.forEach(modifiedInterface -> {
                append(modifiedInterface.render());
            });

            lowerIndent();
            indent().append("</modified-interfaces>").eol();
        }

        if (!modifiedClasses.isEmpty()) {
            indent().append("<modified-classes>").eol();
            raiseIndent();

            modifiedClasses.forEach(modifiedClass -> {
                append(modifiedClass.render());
            });

            lowerIndent();
            indent().append("</modified-classes>").eol();
        }

        if (!undeprecatedInterfaces.isEmpty()) {
            indent().append("<undeprecated-interfaces>").eol();
            raiseIndent();

            undeprecatedClasses.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</undeprecated-interfaces>").eol();
        }

        if (!undeprecatedClasses.isEmpty()) {
            indent().append("<undeprecated-classes>").eol();
            raiseIndent();

            undeprecatedClasses.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</undeprecated-classes>").eol();
        }

        if (!newPackages.isEmpty()) {
            indent().append("<new-packages>").eol();
            raiseIndent();

            newPackages.forEach(newPackage -> {
                indent().append("<name>").append(newPackage).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</new-packages>").eol();
        }

        if (!newInterfaces.isEmpty()) {
            indent().append("<new-interfaces>").eol();
            raiseIndent();

            newInterfaces.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</new-interfaces>").eol();
        }

        if (!newClasses.isEmpty()) {
            indent().append("<new-classes>").eol();
            raiseIndent();

            newClasses.forEach(cd -> {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            });

            lowerIndent();
            indent().append("</new-classes>").eol();
        }

        lowerIndent();
        indent().append("</differences>").eol();

        return super.toString();
    }

    private String breakdownDeclaration(Classfile element) {
        StringBuilder result = new StringBuilder();

        if (element != null) {
            if (element.isPublic())     result.append(" visibility=\"public\"");
            if (element.isPackage())    result.append(" visibility=\"package\"");
            if (element.isFinal())      result.append(" final=\"yes\"");
            if (element.isSuper())      result.append(" super=\"yes\"");
            if (element.isSynthetic())  result.append(" synthetic=\"yes\"");
            if (element.isDeprecated()) result.append(" deprecated=\"yes\"");

            result.append(" name=\"").append(element.getClassName()).append("\"");

            if (element.isInterface()) {
                result.append(" interface=\"yes\"");
        
                result.append(" extends=\"").append(interfacesFor(element)).append("\"");
            } else {
                if (element.isAbstract()) result.append(" abstract=\"yes\"");
        
                result.append(" extends=\"").append(element.getSuperclassName()).append("\"");
        
                result.append(" implements=\"").append(interfacesFor(element)).append("\"");
            }
        }

        return result.toString();
    }

    private String interfacesFor(Classfile classfile) {
        return classfile.getAllInterfaces().stream()
                .map(Class_info::getName)
                .collect(Collectors.joining(", "));
    }
}
