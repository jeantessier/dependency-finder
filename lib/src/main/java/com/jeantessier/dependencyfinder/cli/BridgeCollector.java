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

package com.jeantessier.dependencyfinder.cli;

import com.jeantessier.classreader.*;

import java.io.*;

public class BridgeCollector extends DirectoryExplorerCommand {
    private static class BridgeGatherer extends VisitorBase {
        private final PrintWriter out;

        public BridgeGatherer(PrintWriter out) {
            this.out = out;
        }

        public void visitMethod_info(Method_info entry) {
            if (entry.isBridge()) {
                print(entry.getReturnType(), entry.getFullSignature());

                entry.getAttributes().forEach(attributeInfo -> {
                    if (attributeInfo instanceof Code_attribute code) {
                        code.stream()
                                .filter(instruction -> instruction.getOpcode() == 0xb6 /* invokevirtual */ || instruction.getOpcode() == 0xb7 /* invokespecial */ || instruction.getOpcode() == 0xb8 /* invokestatic */ || instruction.getOpcode() == 0xb9 /* invokeinterface */)
                                .map(Instruction::getIndexedConstantPoolEntry)
                                .filter(constantPoolEntry -> constantPoolEntry instanceof MethodRef_info)
                                .map(constantPoolEntry -> (MethodRef_info) constantPoolEntry)
                                .forEach(methodRef -> print(methodRef.getReturnType(), methodRef.getFullSignature()));
                    }
                });

                out.println();
            }
        }

        public void print(String returnType, String signature) {
            out.print(signature);
            out.print(": ");
            out.print(returnType);
            out.println();
        }
    }

    public void doProcessing() throws Exception {
        BridgeGatherer gatherer = new BridgeGatherer(getOut());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(getVerboseListener());
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
        loader.load(getCommandLine().getParameters());
    }

    public static void main(String[] args) throws Exception {
        new BridgeCollector().run(args);
    }
}
