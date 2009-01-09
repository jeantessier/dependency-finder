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

import java.util.*;

import com.jeantessier.classreader.*;

public class Report extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private String name;
    private String oldVersion;
    private String newVersion;

    private Collection<Differences> removedPackages = new TreeSet<Differences>();

    private Collection<ClassDifferences> removedInterfaces = new TreeSet<ClassDifferences>();
    private Collection<ClassDifferences> removedClasses = new TreeSet<ClassDifferences>();

    private Collection<ClassDifferences> deprecatedInterfaces = new TreeSet<ClassDifferences>();
    private Collection<ClassDifferences> deprecatedClasses = new TreeSet<ClassDifferences>();
    
    private Collection<ClassReport> modifiedInterfaces = new TreeSet<ClassReport>();
    private Collection<ClassReport> modifiedClasses = new TreeSet<ClassReport>();

    private Collection<ClassDifferences> undeprecatedInterfaces = new TreeSet<ClassDifferences>();
    private Collection<ClassDifferences> undeprecatedClasses = new TreeSet<ClassDifferences>();
    
    private Collection<Differences> newPackages = new TreeSet<Differences>();

    private Collection<ClassDifferences> newInterfaces = new TreeSet<ClassDifferences>();
    private Collection<ClassDifferences> newClasses = new TreeSet<ClassDifferences>();

    public Report() {
        this(DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public Report(String encoding, String dtdPrefix) {
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

        for (Differences packageDifference : differences.getPackageDifferences()) {
            packageDifference.accept(this);
        }
    }

    public void visitPackageDifferences(PackageDifferences differences) {
        if (differences.isRemoved()) {
            removedPackages.add(differences);
        }
    
        for (Differences classDiffenrence : differences.getClassDifferences()) {
            classDiffenrence.accept(this);
        }

        if (differences.isNew()) {
            newPackages.add(differences);
        }
    }

    public void visitClassDifferences(ClassDifferences differences) {
        if (differences.isRemoved()) {
            removedClasses.add(differences);
        }
    
        if (differences.isModified()) {
            ClassReport visitor = new ClassReport();
            visitor.setIndentText(getIndentText());
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
            ClassReport classReport = new ClassReport();
            classReport.setIndentText(getIndentText());
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
    
        if (removedPackages.size() !=0) {
            indent().append("<removed-packages>").eol();
            raiseIndent();

            for (Differences removedPackage : removedPackages) {
                indent().append("<name>").append(removedPackage).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-packages>").eol();
        }

        if (removedInterfaces.size() !=0) {
            indent().append("<removed-interfaces>").eol();
            raiseIndent();

            for (ClassDifferences cd : removedInterfaces) {
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-interfaces>").eol();
        }

        if (removedClasses.size() !=0) {
            indent().append("<removed-classes>").eol();
            raiseIndent();

            for (ClassDifferences cd : removedClasses) {
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-classes>").eol();
        }

        if (deprecatedInterfaces.size() !=0) {
            indent().append("<deprecated-interfaces>").eol();
            raiseIndent();

            for (ClassDifferences cd : deprecatedInterfaces) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-interfaces>").eol();
        }

        if (deprecatedClasses.size() !=0) {
            indent().append("<deprecated-classes>").eol();
            raiseIndent();

            for (ClassDifferences cd : deprecatedClasses) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-classes>").eol();
        }

        if (modifiedInterfaces.size() !=0) {
            indent().append("<modified-interfaces>").eol();
            raiseIndent();

            for (ClassReport modifiedInterface : modifiedInterfaces) {
                append(modifiedInterface.render());
            }

            lowerIndent();
            indent().append("</modified-interfaces>").eol();
        }

        if (modifiedClasses.size() !=0) {
            indent().append("<modified-classes>").eol();
            raiseIndent();

            for (ClassReport modifiedClass : modifiedClasses) {
                append(modifiedClass.render());
            }

            lowerIndent();
            indent().append("</modified-classes>").eol();
        }

        if (undeprecatedInterfaces.size() !=0) {
            indent().append("<undeprecated-interfaces>").eol();
            raiseIndent();

            for (ClassDifferences cd : undeprecatedInterfaces) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-interfaces>").eol();
        }

        if (undeprecatedClasses.size() !=0) {
            indent().append("<undeprecated-classes>").eol();
            raiseIndent();

            for (ClassDifferences cd : undeprecatedClasses) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-classes>").eol();
        }

        if (newPackages.size() !=0) {
            indent().append("<new-packages>").eol();
            raiseIndent();

            for (Differences newPackage : newPackages) {
                indent().append("<name>").append(newPackage).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-packages>").eol();
        }

        if (newInterfaces.size() !=0) {
            indent().append("<new-interfaces>").eol();
            raiseIndent();

            for (ClassDifferences cd : newInterfaces) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-interfaces>").eol();
        }

        if (newClasses.size() !=0) {
            indent().append("<new-classes>").eol();
            raiseIndent();

            for (ClassDifferences cd : newClasses) {
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-classes>").eol();
        }

        lowerIndent();
        indent().append("</differences>").eol();

        return super.toString();
    }

    private String breakdownDeclaration(Classfile element) {
        StringBuffer result = new StringBuffer();

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
        
                result.append(" extends=\"");
                Iterator i = element.getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("\"");
            } else {
                if (element.isAbstract()) result.append(" abstract=\"yes\"");
        
                result.append(" extends=\"").append(element.getSuperclassName()).append("\"");
        
                result.append(" implements=\"");
                Iterator i = element.getAllInterfaces().iterator();
                while (i.hasNext()) {
                    result.append(i.next());
                    if (i.hasNext()) {
                        result.append(", ");
                    }
                }
                result.append("\"");
            }
        }

        return result.toString();
    }
}
