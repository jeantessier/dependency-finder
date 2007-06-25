/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.text.*;

public class ClassMetrics extends DirectoryExplorerCommand {
    private boolean list;

    public ClassMetrics() throws CommandLineException {
        super("ClassMetrics");
    }

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

        out.println(metrics.getClasses().size() + " class(es)");
        if (list) {
            for (Object o : metrics.getClasses()) {
                out.println("        " + o);
            }
        }

        out.println(metrics.getInterfaces().size() + " interface(s)");
        if (list) {
            for (Object o : metrics.getInterfaces()) {
                out.println("        " + o);
            }
        }

        out.println();
        out.println(metrics.getMethods().size() + " method(s) (average " + (metrics.getMethods().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
        out.println(metrics.getFields().size() + " field(s) (average " + (metrics.getFields().size() / (metrics.getClasses().size() + (double) metrics.getInterfaces().size())) + " per class/interface)");
        out.println();

        printCFM(" synthetic element(s)", metrics.getSyntheticClasses(), metrics.getSyntheticFields(), metrics.getSyntheticMethods());
        printCFM(" deprecated element(s)", metrics.getDeprecatedClasses(), metrics.getDeprecatedFields(), metrics.getDeprecatedMethods());
        printCFMIC(" public element(s)", metrics.getPublicClasses(), metrics.getPublicFields(), metrics.getPublicMethods(), metrics.getPublicInnerClasses());
        printFMIC(" protected element(s)", metrics.getProtectedFields(), metrics.getProtectedMethods(), metrics.getProtectedInnerClasses());
        printFMIC(" private element(s)", metrics.getPrivateFields(), metrics.getPrivateMethods(), metrics.getPrivateInnerClasses());
        printCFMIC(" package element(s)", metrics.getPackageClasses(), metrics.getPackageFields(), metrics.getPackageMethods(), metrics.getPackageInnerClasses());
        printCMIC(" abstract element(s)", metrics.getAbstractClasses(), metrics.getAbstractMethods(), metrics.getAbstractInnerClasses());

        printFMIC(" static element(s)", metrics.getStaticFields(), metrics.getStaticMethods(), metrics.getStaticInnerClasses());
        printCFMIC(" final element(s)", metrics.getFinalClasses(), metrics.getFinalFields(), metrics.getFinalMethods(), metrics.getFinalInnerClasses());

        out.println(metrics.getSynchronizedMethods().size() + " synchronized method(s)");
        if (list) {
            for (Method_info method : metrics.getSynchronizedMethods()) {
                out.println("        " + method);
            }
        }

        out.println(metrics.getNativeMethods().size() + " native method(s)");
        if (list) {
            for (Method_info method : metrics.getNativeMethods()) {
                out.println("        " + method);
            }
        }

        out.println(metrics.getVolatileFields().size() + " volatile field(s)");
        if (list) {
            for (Field_info field : metrics.getVolatileFields()) {
                out.println("        " + field);
            }
        }

        out.println(metrics.getTransientFields().size() + " transient field(s)");
        if (list) {
            for (Field_info field : metrics.getTransientFields()) {
                out.println("        " + field);
            }
        }

        out.println(metrics.getCustomAttributes().size() + " custom attribute(s)");
        if (list) {
            for (Custom_attribute attribute : metrics.getCustomAttributes()) {
                out.println("        " + attribute);
            }
        }

        if (getCommandLine().getToggleSwitch("instruction-counts")) {
            out.println();
            out.println("Instruction counts:");
            for (int opcode=0; opcode<256; opcode++) {
                out.print("        0x");
                Hex.print(out, (byte) opcode);
                out.println(" " + Instruction.getMnemonic(opcode) + ": " + metrics.getInstructionCounts()[opcode]);
            }
        }
    }

    private void printCMIC(String label, Collection<Classfile> classes, Collection<Method_info> methods, Collection<InnerClass> innerClasses) {
        out.println((classes.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                out.println("        " + innerClass);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + methods.size() + " method(s)");
            out.println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFMIC(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) {
        out.println((classes.size() +
                     fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                out.println("        " + innerClass);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + fields.size() + " fields(s)");
            out.println("    " + methods.size() + " method(s)");
            out.println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFM(String label, Collection<Classfile> classes, Collection<Field_info> fields, Collection<Method_info> methods) {
        out.println((classes.size() +
                     fields.size() +
                     methods.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Classfile aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                out.println("        " + method);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + fields.size() + " fields(s)");
            out.println("    " + methods.size() + " method(s)");
        }
    }

    private void printFMIC(String label, Collection<Field_info> fields, Collection<Method_info> methods, Collection<InnerClass> innerClasses) {
        out.println((fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + fields.size() + " field(s)");
            for (Field_info field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Method_info method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (InnerClass innerClass : innerClasses) {
                out.println("        " + innerClass);
            }
        } else {
            out.println("    " + fields.size() + " fields(s)");
            out.println("    " + methods.size() + " method(s)");
            out.println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    public static void main(String[] args) throws Exception {
        new ClassMetrics().run(args);
    }
}
