/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

import java.io.*;
import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

public class ClassMetrics extends Command {
    private boolean list;

    public ClassMetrics() {
        super("ClassMetrics");
    }

    protected void showSpecificUsage(PrintStream out) {
        System.err.println();
        System.err.println("If no files are specified, it processes the current directory.");
        System.err.println();
        System.err.println("If file is a directory, it is recusively scanned for files");
        System.err.println("ending in \".class\".");
        System.err.println();
        System.err.println("Defaults is text output to the console.");
        System.err.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        getCommandLine().addToggleSwitch("list");
        getCommandLine().addToggleSwitch("instruction-counts");
    }

    public void doProcessing() throws IOException {
        list = getCommandLine().getToggleSwitch("list");

        List<String> parameters = getCommandLine().getParameters();
        if (parameters.size() == 0) {
            parameters.add(".");
        }

        MetricsGatherer metrics = new MetricsGatherer();

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(getVerboseListener());
        loader.addLoadListener(new LoadListenerVisitorAdapter(metrics));
        loader.load(parameters);

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
            for (Object o : metrics.getSynchronizedMethods()) {
                out.println("        " + o);
            }
        }

        out.println(metrics.getNativeMethods().size() + " native method(s)");
        if (list) {
            for (Object o : metrics.getNativeMethods()) {
                out.println("        " + o);
            }
        }

        out.println(metrics.getVolatileFields().size() + " volatile field(s)");
        if (list) {
            for (Object o : metrics.getVolatileFields()) {
                out.println("        " + o);
            }
        }

        out.println(metrics.getTransientFields().size() + " transient field(s)");
        if (list) {
            for (Object o : metrics.getTransientFields()) {
                out.println("        " + o);
            }
        }

        out.println(metrics.getCustomAttributes().size() + " custom attribute(s)");
        if (list) {
            for (Object o : metrics.getCustomAttributes()) {
                out.println("        " + o);
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

    private void printCMIC(String label, Collection classes, Collection methods, Collection innerClasses) {
        out.println((classes.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Object aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Object method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (Object innerClass : innerClasses) {
                out.println("        " + innerClass);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + methods.size() + " method(s)");
            out.println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFMIC(String label, Collection classes, Collection fields, Collection methods, Collection innerClasses) {
        out.println((classes.size() +
                     fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Object aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + fields.size() + " field(s)");
            for (Object field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Object method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (Object innerClass : innerClasses) {
                out.println("        " + innerClass);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + fields.size() + " fields(s)");
            out.println("    " + methods.size() + " method(s)");
            out.println("    " + innerClasses.size() + " inner class(es)");
        }
    }

    private void printCFM(String label, Collection classes, Collection fields, Collection methods) {
        out.println((classes.size() +
                     fields.size() +
                     methods.size()) + label);
        if (list) {
            out.println("    " + classes.size() + " class(es)");
            for (Object aClass : classes) {
                out.println("        " + aClass);
            }

            out.println("    " + fields.size() + " field(s)");
            for (Object field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Object method : methods) {
                out.println("        " + method);
            }
        } else {
            out.println("    " + classes.size() + " class(es)");
            out.println("    " + fields.size() + " fields(s)");
            out.println("    " + methods.size() + " method(s)");
        }
    }

    private void printFMIC(String label, Collection fields, Collection methods, Collection innerClasses) {
        out.println((fields.size() +
                     methods.size() +
                     innerClasses.size()) + label);
        if (list) {
            out.println("    " + fields.size() + " field(s)");
            for (Object field : fields) {
                out.println("        " + field);
            }

            out.println("    " + methods.size() + " method(s)");
            for (Object method : methods) {
                out.println("        " + method);
            }

            out.println("    " + innerClasses.size() + " inner class(es)");
            for (Object innerClass : innerClasses) {
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
