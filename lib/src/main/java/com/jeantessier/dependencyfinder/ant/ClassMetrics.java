/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.jeantessier.classreader.*;

import static java.util.stream.Collectors.*;

public class ClassMetrics extends Task {
    private static final String EOL = System.getProperty("line.separator", "\n");

    private boolean list = false;
    private boolean instructionCounts = false;
    private File destfile;
    private PrintWriter out;
    private Path path;

    public boolean getList() {
        return list;
    }
    
    public void setList(boolean list) {
        this.list = list;
    }

    public boolean getInstructioncounts() {
        return instructionCounts;
    }
    
    public void setInstructioncounts(boolean instructionCounts) {
        this.instructionCounts = instructionCounts;
    }

    public File getDestfile() {
        return destfile;
    }
    
    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }
    
    public Path createPath() {
        if (path == null) {
            path = new Path(getProject());
        }

        return path;
    }
    
    public Path getPath() {
        return path;
    }
    
    public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getPath() == null) {
            throw new BuildException("path must be set!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }

        log("Reading classes from path " + getPath());

        VerboseListener verboseListener = new VerboseListener(this);

        MetricsGatherer metrics = new MetricsGatherer();

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(verboseListener);
        loader.addLoadListener(new LoadListenerVisitorAdapter(metrics));
        loader.load(Arrays.asList(getPath().list()));

        log("Saving class metrics to " + getDestfile().getAbsolutePath());
        
        try {
            getOut().println(metrics.getClasses().size() + " class(es)");
            printCollection(metrics.getClasses());
            
            getOut().println(metrics.getInterfaces().size() + " interface(s)");
            printCollection(metrics.getInterfaces());

            getOut().println();
            getOut().println(metrics.getMethods().size() + " method(s) (average " + (metrics.getMethods().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
            getOut().println(metrics.getFields().size() + " field(s) (average " + (metrics.getFields().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
            getOut().println();
            
            printCFMIC(" synthetic element(s)", metrics.getSyntheticClasses(), metrics.getSyntheticFields(), metrics.getSyntheticMethods(), metrics.getSyntheticInnerClasses());
            printCFM(" deprecated element(s)", metrics.getDeprecatedClasses(), metrics.getDeprecatedFields(), metrics.getDeprecatedMethods());
            printCFMIC(" public element(s)", metrics.getPublicClasses(), metrics.getPublicFields(), metrics.getPublicMethods(), metrics.getPublicInnerClasses());
            printFMIC(" protected element(s)", metrics.getProtectedFields(), metrics.getProtectedMethods(), metrics.getProtectedInnerClasses());
            printFMIC(" private element(s)", metrics.getPrivateFields(), metrics.getPrivateMethods(), metrics.getPrivateInnerClasses());
            printCFMIC(" package element(s)", metrics.getPackageClasses(), metrics.getPackageFields(), metrics.getPackageMethods(), metrics.getPackageInnerClasses());
            printCMIC(" abstract element(s)", metrics.getAbstractClasses(), metrics.getAbstractMethods(), metrics.getAbstractInnerClasses());
            
            printFMIC(" static element(s)", metrics.getStaticFields(), metrics.getStaticMethods(), metrics.getStaticInnerClasses());
            printCFMIC(" final element(s)", metrics.getFinalClasses(), metrics.getFinalFields(), metrics.getFinalMethods(), metrics.getFinalInnerClasses());
            printCIC(" annotation element(s)", metrics.getAnnotationClasses(), metrics.getAnnotationInnerClasses());
            printCFIC(" enum element(s)", metrics.getEnumClasses(), metrics.getEnumFields(), metrics.getEnumInnerClasses());

            getOut().println(metrics.getSuperClasses().size() + " super class(es)");
            printCollection(metrics.getSuperClasses());

            getOut().println(metrics.getModuleClasses().size() + " module class(es)");
            printCollection(metrics.getModuleClasses());

            getOut().println(metrics.getSynchronizedMethods().size() + " synchronized method(s)");
            printCollection(metrics.getSynchronizedMethods());

            getOut().println(metrics.getNativeMethods().size() + " native method(s)");
            printCollection(metrics.getNativeMethods());

            getOut().println(metrics.getBridgeMethods().size() + " bridge method(s)");
            printCollection(metrics.getBridgeMethods());

            getOut().println(metrics.getVarargsMethods().size() + " varargs method(s)");
            printCollection(metrics.getVarargsMethods());

            getOut().println(metrics.getStrictMethods().size() + " strict method(s)");
            printCollection(metrics.getStrictMethods());

            getOut().println(metrics.getVolatileFields().size() + " volatile field(s)");
            printCollection(metrics.getVolatileFields());
            
            getOut().println(metrics.getTransientFields().size() + " transient field(s)");
            printCollection(metrics.getTransientFields());

            getOut().println(metrics.getConstantPoolEntryCounts().values().stream().reduce(0L, Long::sum) + " constant pool entry(ies)");
            if (getList()) {
                getOut().println(
                        metrics.getConstantPoolEntryCounts().entrySet().stream()
                                .map(entry -> String.format("%12d %s", entry.getValue(), com.jeantessier.classreader.impl.ConstantPoolEntry.stringValueOf(entry.getKey().byteValue())))
                                .collect(joining(EOL))
                );
            }

            getOut().println(metrics.getAttributeCounts().values().stream().reduce(0L, Long::sum) + " attribute(s)");
            getOut().println(
                    Arrays.stream(AttributeType.values())
                            .map(AttributeType::getAttributeName)
                            .map(name -> String.format("%12d %s attribute(s)", metrics.getAttributeCounts().get(name), name))
                            .collect(joining(EOL))
            );

            getOut().format("%12d custom attribute(s)%n", metrics.getCustomAttributes().size());
            if (getList()) {
                getOut().println(
                        metrics.getCustomAttributes().stream()
                                .collect(groupingBy(Custom_attribute::getName))
                                .entrySet().stream()
                                .flatMap(entry -> Stream.concat(
                                        Stream.of(String.format("%16d %s", entry.getValue().size(), entry.getKey())),
                                        entry.getValue().stream()
                                                .collect(groupingBy(attribute -> attribute.getInfo().length))
                                                .entrySet().stream()
                                                .sorted(Map.Entry.comparingByKey())
                                                .map(histoEntry -> String.format("%20dx %s", histoEntry.getValue().size(), histoEntry.getKey() + " bytes"))))
                                .collect(joining(EOL))
                );
            }

            if (getInstructioncounts()) {
                getOut().println();
                getOut().println("Instruction counts:");
                getOut().println(
                        IntStream.range(0, 256)
                                .mapToObj(opcode -> String.format("        0x%02X %s: %d", opcode, com.jeantessier.classreader.impl.Instruction.getMnemonic(opcode), metrics.getInstructionCounts()[opcode]))
                                .collect(joining(EOL))
                );
            }

            getOut().close();
        } catch (IOException ex) {
            throw new BuildException(ex);
        }
    }

    private void printCMIC(String label, Collection<Classfile> classes, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        print(label, classes, null, methods, innerClasses);
    }

    private void printCFMIC(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        print(label, classes, fields, methods, innerClasses);
    }

    private void printCFM(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods) throws IOException {
        print(label, classes, fields, methods, null);
    }

    private void printCFIC(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<InnerClass> innerClasses) throws IOException {
        print(label, classes, fields, null, innerClasses);
    }

    private void printCIC(String label, Collection<Classfile> classes, Collection<InnerClass> innerClasses) throws IOException {
        print(label, classes, null, null, innerClasses);
    }

    private void printFMIC(String label, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        print(label, null, fields, methods, innerClasses);
    }

    private void print(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        getOut().println(((classes != null ? classes.size() : 0) +
                (fields != null ? fields.size() : 0) +
                (methods != null ? methods.size() : 0) +
                (innerClasses != null ? innerClasses.size() : 0)) + label);
        if (classes != null) {
            getOut().println("    " + classes.size() + " class(es)");
            printCollection(classes);
        }

        if (fields != null) {
            getOut().println("    " + fields.size() + " field(s)");
            printCollection(fields);
        }

        if (methods != null) {
            getOut().println("    " + methods.size() + " method(s)");
            printCollection(methods);
        }

        if (innerClasses != null) {
            getOut().println("    " + innerClasses.size() + " inner class(es)");
            printCollection(innerClasses);
        }
    }

    private void printCollection(Collection<?> collection) throws IOException {
        if (getList() && collection != null) {
            getOut().println(
                    collection.stream()
                            .map(name -> "        " + name)
                            .collect(joining(EOL))
            );
        }
    }

    private PrintWriter getOut() throws IOException {
        if (out == null) {
            startOutput();
        }

        return out;
    }

    private void startOutput() throws IOException {
        out = new PrintWriter(new FileWriter(getDestfile()));
    }
}
