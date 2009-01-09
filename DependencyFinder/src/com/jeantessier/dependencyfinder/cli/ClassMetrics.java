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

package com.jeantessier.dependencyfinder.cli;

import java.util.*;
import java.io.*;

import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

public class ClassMetrics extends DirectoryExplorerCommand {
    private boolean list;

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addToggleSwitch("list");
        getCommandLine().addToggleSwitch("instruction-counts");
    }

    public void doProcessing() throws Exception {
        list = getCommandLine().getToggleSwitch("list");

        MetricsGatherer metrics = new MetricsGatherer();

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(getVerboseListener());
        loader.addLoadListener(new LoadListenerVisitorAdapter(metrics));
        loader.load(getCommandLine().getParameters());

        getVerboseListener().print("Printing report ...");

        getOut().println(metrics.getClasses().size() + " class(es)");
        if (list) {
            for (Object o : metrics.getClasses()) {
                getOut().println("        " + o);
            }
        }

        getOut().println(metrics.getInterfaces().size() + " interface(s)");
        if (list) {
            for (Object o : metrics.getInterfaces()) {
                getOut().println("        " + o);
            }
        }

        getOut().println();
        getOut().println(metrics.getMethods().size() + " method(s) (average " + (metrics.getMethods().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
        getOut().println(metrics.getFields().size() + " field(s) (average " + (metrics.getFields().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
        getOut().println();

        printCFM(" synthetic element(s)", metrics.getSyntheticClasses(), metrics.getSyntheticFields(), metrics.getSyntheticMethods());
        printCFM(" deprecated element(s)", metrics.getDeprecatedClasses(), metrics.getDeprecatedFields(), metrics.getDeprecatedMethods());
        printCFMIC(" public element(s)", metrics.getPublicClasses(), metrics.getPublicFields(), metrics.getPublicMethods(), metrics.getPublicInnerClasses());
        printFMIC(" protected element(s)", metrics.getProtectedFields(), metrics.getProtectedMethods(), metrics.getProtectedInnerClasses());
        printFMIC(" private element(s)", metrics.getPrivateFields(), metrics.getPrivateMethods(), metrics.getPrivateInnerClasses());
        printCFMIC(" package element(s)", metrics.getPackageClasses(), metrics.getPackageFields(), metrics.getPackageMethods(), metrics.getPackageInnerClasses());
        printCMIC(" abstract element(s)", metrics.getAbstractClasses(), metrics.getAbstractMethods(), metrics.getAbstractInnerClasses());

        printFMIC(" static element(s)", metrics.getStaticFields(), metrics.getStaticMethods(), metrics.getStaticInnerClasses());
        printCFMIC(" final element(s)", metrics.getFinalClasses(), metrics.getFinalFields(), metrics.getFinalMethods(), metrics.getFinalInnerClasses());

        getOut().println(metrics.getSynchronizedMethods().size() + " synchronized method(s)");
        if (list) {
            for (Method_info method : metrics.getSynchronizedMethods()) {
                getOut().println("        " + method);
            }
        }

        getOut().println(metrics.getNativeMethods().size() + " native method(s)");
        if (list) {
            for (Method_info method : metrics.getNativeMethods()) {
                getOut().println("        " + method);
            }
        }

        getOut().println(metrics.getVolatileFields().size() + " volatile field(s)");
        if (list) {
            for (Field_info field : metrics.getVolatileFields()) {
                getOut().println("        " + field);
            }
        }

        getOut().println(metrics.getTransientFields().size() + " transient field(s)");
        if (list) {
            for (Field_info field : metrics.getTransientFields()) {
                getOut().println("        " + field);
            }
        }

        for (AttributeType attributeType : AttributeType.values()) {
            getOut().println(metrics.getAttributeCounts().get(attributeType.getAttributeName()) + " " + attributeType.getAttributeName() + " attribute(s)");
        }

        getOut().println(metrics.getCustomAttributes().size() + " custom attribute(s)");
        if (list) {
            for (Custom_attribute attribute : metrics.getCustomAttributes()) {
                getOut().println("        " + attribute);
            }
        }

        if (getCommandLine().getToggleSwitch("instruction-counts")) {
            getOut().println();
            getOut().println("Instruction counts:");
            for (int opcode=0; opcode<256; opcode++) {
                getOut().print("        0x");
                Hex.print(getOut(), (byte) opcode);
                getOut().println(" " + com.jeantessier.classreader.impl.Instruction.getMnemonic(opcode) + ": " + metrics.getInstructionCounts()[opcode]);
            }
        }
    }

    private void printCMIC(String label, Collection<Classfile> classes, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        getOut().println((classes.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            getOut().println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                getOut().println("        " + aClass);
            }

            getOut().println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                getOut().println("        " + method);
            }

            getOut().println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                getOut().println("        " + innerClass);
            }
        } else {
            getOut().println("    " + classes.size() + " class(es)");
            getOut().println("    " + methods.size() + " method(s)");
            getOut().println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFMIC(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        getOut().println((classes.size() +
                     fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            getOut().println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                getOut().println("        " + aClass);
            }

            getOut().println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                getOut().println("        " + field);
            }

            getOut().println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                getOut().println("        " + method);
            }

            getOut().println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                getOut().println("        " + innerClass);
            }
        } else {
            getOut().println("    " + classes.size() + " class(es)");
            getOut().println("    " + fields.size() + " fields(s)");
            getOut().println("    " + methods.size() + " method(s)");
            getOut().println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFM(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods) throws IOException {
        getOut().println((classes.size() +
                     fields.size() +
                     methods.size()) + label);
        if (list) {
            getOut().println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                getOut().println("        " + aClass);
            }

            getOut().println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                getOut().println("        " + field);
            }

            getOut().println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                getOut().println("        " + method);
            }
        } else {
            getOut().println("    " + classes.size() + " class(es)");
            getOut().println("    " + fields.size() + " fields(s)");
            getOut().println("    " + methods.size() + " method(s)");
        }
    }

    private void printFMIC(String label, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) throws IOException {
        getOut().println((fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            getOut().println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                getOut().println("        " + field);
            }

            getOut().println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                getOut().println("        " + method);
            }

            getOut().println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                getOut().println("        " + innerClass);
            }
        } else {
            getOut().println("    " + fields.size() + " fields(s)");
            getOut().println("    " + methods.size() + " method(s)");
            getOut().println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    public static void main(String[] args) throws Exception {
        new ClassMetrics().run(args);
    }
}
