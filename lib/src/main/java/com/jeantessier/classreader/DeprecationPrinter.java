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

package com.jeantessier.classreader;

import java.io.*;

public class DeprecationPrinter extends Printer {
    public DeprecationPrinter(PrintWriter out) {
        super(out);
    }
    
    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        Object owner = attribute.getOwner();

        if (owner instanceof Feature_info feature_info) {
            if (!feature_info.getClassfile().isDeprecated()) {
                append(feature_info.getFullSignature()).eol();
            }
        } else if (owner instanceof Classfile classfile) {
            append(classfile).eol();
            classfile.getAllFields().forEach(field -> append(field.getFullSignature()).eol());
            classfile.getAllMethods().forEach(method -> append(method.getFullSignature()).eol());
        }

        // TODO: Replace with type pattern matching in switch expression in Java 21
        // switch (attribute.getOwner()) {
        //     case Feature_info feature_info -> {
        //         // There is probably a way to fold the if statement in the case above
        //         if (!feature_info.getClassfile().isDeprecated()) {
        //             append(feature_info.getFullSignature()).eol();
        //         }
        //     }
        //     case Classfile classfile -> {
        //         append(classfile).eol();
        //         classfile.getAllFields().forEach(field -> append(field.getFullSignature()).eol());
        //         classfile.getAllMethods().forEach(method -> append(method.getFullSignature()).eol());
        //     }
        //     default -> LogManager.getLogger(getClass()).warn("Deprecated attribute on unknown Visitable: {}", owner.getClass().getName());
        // }
    }
}
