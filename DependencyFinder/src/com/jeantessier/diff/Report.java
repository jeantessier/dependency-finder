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

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class Report extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private String name;
    private String oldVersion;
    private String newVersion;

    private Collection removedPackages        = new TreeSet();
    private Collection undocumentedPackages   = new TreeSet();

    private Collection removedInterfaces      = new TreeSet();
    private Collection removedClasses         = new TreeSet();

    private Collection deprecatedInterfaces   = new TreeSet();
    private Collection deprecatedClasses      = new TreeSet();
    
    private Collection undocumentedInterfaces = new TreeSet();
    private Collection undocumentedClasses    = new TreeSet();

    private Collection modifiedInterfaces     = new TreeSet();
    private Collection modifiedClasses        = new TreeSet();

    private Collection documentedInterfaces   = new TreeSet();
    private Collection documentedClasses      = new TreeSet();
    
    private Collection undeprecatedInterfaces = new TreeSet();
    private Collection undeprecatedClasses    = new TreeSet();
    
    private Collection newPackages            = new TreeSet();
    private Collection documentedPackages     = new TreeSet();

    private Collection newInterfaces          = new TreeSet();
    private Collection newClasses             = new TreeSet();

    public Report() {
        this(DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public Report(String encoding, String dtdPrefix) {
        appendHeader(encoding, dtdPrefix);
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE differences SYSTEM \"").append(dtdPrefix).append("/differences.dtd\">").eol();
        eol();
    }

    public void visitJarDifferences(JarDifferences differences) {
        name       = differences.getName();
        oldVersion = differences.getOldVersion();
        newVersion = differences.getNewVersion();

        Iterator i = differences.getPackageDifferences().iterator();
        while (i.hasNext()) {
            ((Differences) i.next()).accept(this);
        }
    }

    public void visitPackageDifferences(PackageDifferences differences) {
        if (differences.isRemoved()) {
            removedPackages.add(differences);
        }
    
        Iterator i = differences.getClassDifferences().iterator();
        while (i.hasNext()) {
            ((Differences) i.next()).accept(this);
        }

        if (differences.isNew()) {
            newPackages.add(differences);
        }

        if (isDocumented()) {
            documentedPackages.add(differences);
        }

        if (isUndocumented()) {
            undocumentedPackages.add(differences);
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

        if (isDocumented()) {
            documentedClasses.add(differences);
        }

        if (isUndocumented()) {
            undocumentedClasses.add(differences);
        }
    }

    public void visitInterfaceDifferences(InterfaceDifferences differences) {
        if (differences.isRemoved()) {
            removedInterfaces.add(differences);
        }
    
        if (differences.isModified()) {
            ClassReport visitor = new ClassReport();
            visitor.setIndentText(getIndentText());
            differences.accept(visitor);
            modifiedInterfaces.add(visitor);
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

        if (isDocumented()) {
            documentedInterfaces.add(differences);
        }

        if (isUndocumented()) {
            undocumentedInterfaces.add(differences);
        }
    }

    public String toString() {
        indent().append("<differences>").eol();
        raiseIndent();

        indent().append("<name>").append(name).append("</name>").eol();
        indent().append("<old>").append(oldVersion).append("</old>").eol();
        indent().append("<new>").append(newVersion).append("</new>").eol();
    
        if (removedPackages.size() !=0) {
            indent().append("<removed-packages>").eol();
            raiseIndent();

            Iterator i = removedPackages.iterator();
            while (i.hasNext()) {
                indent().append("<name>").append(i.next()).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-packages>").eol();
        }
    
        if (undocumentedPackages.size() !=0) {
            indent().append("<undocumented-packages>").eol();
            raiseIndent();

            Iterator i = undocumentedPackages.iterator();
            while (i.hasNext()) {
                indent().append("<name>").append(i.next()).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undocumented-packages>").eol();
        }

        if (removedInterfaces.size() !=0) {
            indent().append("<removed-interfaces>").eol();
            raiseIndent();

            Iterator i = removedInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-interfaces>").eol();
        }

        if (removedClasses.size() !=0) {
            indent().append("<removed-classes>").eol();
            raiseIndent();

            Iterator i = removedClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getOldClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</removed-classes>").eol();
        }

        if (deprecatedInterfaces.size() !=0) {
            indent().append("<deprecated-interfaces>").eol();
            raiseIndent();

            Iterator i = deprecatedInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-interfaces>").eol();
        }

        if (deprecatedClasses.size() !=0) {
            indent().append("<deprecated-classes>").eol();
            raiseIndent();

            Iterator i = deprecatedClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</deprecated-classes>").eol();
        }

        if (undocumentedInterfaces.size() !=0) {
            indent().append("<undocumented-interfaces>").eol();
            raiseIndent();

            Iterator i = undocumentedInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undocumented-interfaces>").eol();
        }

        if (undocumentedClasses.size() !=0) {
            indent().append("<undocumented-classes>").eol();
            raiseIndent();

            Iterator i = undocumentedClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undocumented-classes>").eol();
        }

        if (modifiedInterfaces.size() !=0) {
            indent().append("<modified-interfaces>").eol();
            raiseIndent();

            Iterator i = modifiedInterfaces.iterator();
            while (i.hasNext()) {
                append(i.next());
            }

            lowerIndent();
            indent().append("</modified-interfaces>").eol();
        }

        if (modifiedClasses.size() !=0) {
            indent().append("<modified-classes>").eol();
            raiseIndent();

            Iterator i = modifiedClasses.iterator();
            while (i.hasNext()) {
                append(i.next());
            }

            lowerIndent();
            indent().append("</modified-classes>").eol();
        }

        if (documentedInterfaces.size() !=0) {
            indent().append("<documented-interfaces>").eol();
            raiseIndent();

            Iterator i = documentedInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</documented-interfaces>").eol();
        }

        if (documentedClasses.size() !=0) {
            indent().append("<documented-classes>").eol();
            raiseIndent();

            Iterator i = documentedClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</documented-classes>").eol();
        }

        if (undeprecatedInterfaces.size() !=0) {
            indent().append("<undeprecated-interfaces>").eol();
            raiseIndent();

            Iterator i = undeprecatedInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-interfaces>").eol();
        }

        if (undeprecatedClasses.size() !=0) {
            indent().append("<undeprecated-classes>").eol();
            raiseIndent();

            Iterator i = undeprecatedClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</undeprecated-classes>").eol();
        }

        if (newPackages.size() !=0) {
            indent().append("<new-packages>").eol();
            raiseIndent();

            Iterator i = newPackages.iterator();
            while (i.hasNext()) {
                indent().append("<name>").append(i.next()).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-packages>").eol();
        }
    
        if (documentedPackages.size() !=0) {
            indent().append("<documented-packages>").eol();
            raiseIndent();

            Iterator i = documentedPackages.iterator();
            while (i.hasNext()) {
                indent().append("<name>").append(i.next()).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</documented-packages>").eol();
        }

        if (newInterfaces.size() !=0) {
            indent().append("<new-interfaces>").eol();
            raiseIndent();

            Iterator i = newInterfaces.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-interfaces>").eol();
        }

        if (newClasses.size() !=0) {
            indent().append("<new-classes>").eol();
            raiseIndent();

            Iterator i = newClasses.iterator();
            while (i.hasNext()) {
                ClassDifferences cd = (ClassDifferences) i.next();
                indent().append("<name").append(breakdownDeclaration(cd.getNewClass())).append(">").append(cd).append("</name>").eol();
            }

            lowerIndent();
            indent().append("</new-classes>").eol();
        }

        lowerIndent();
        indent().append("</differences>").eol();

        return super.toString();
    }

    private static final String breakdownDeclaration(Classfile element) {
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
