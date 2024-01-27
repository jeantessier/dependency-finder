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

package com.jeantessier.classreader;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.*;

public final class SignatureHelper {
    private static final Map<String, String> conversion = Map.of(
            "B", "byte",
            "C", "char",
            "D", "double",
            "F", "float",
            "I", "int",
            "J", "long",
            "S", "short",
            "V", "void",
            "Z", "boolean"
    );

    private SignatureHelper() {
        // Prevent instantiation
    }

    public static String convert(String type) {
        String result = null;

        LogManager.getLogger(SignatureHelper.class).debug("Begin Convert(\"{}\")", type);

        if (type.length() == 1) {
            result = conversion.get(type);
        } else if (type.charAt(0) == 'L' && type.indexOf(';') != -1) {
            result = ClassNameHelper.path2ClassName(type.substring(1, type.indexOf(';')));
        } else if (type.charAt(0) == 'T' && type.indexOf(';') != -1) {
            result = ClassNameHelper.path2ClassName(type.substring(1, type.indexOf(';')));
        } else if (type.charAt(0) == '[') {
            result = convert(type.substring(1)) + "[]";
        }

        LogManager.getLogger(SignatureHelper.class).debug("End   Convert(\"{}\"): \"{}\"", type, result);

        return result;
    }

    public static String getSignature(String descriptor) {
        StringBuilder result = new StringBuilder();

        LogManager.getLogger(SignatureHelper.class).debug("Begin Signature(\"{}\")", descriptor);

        result.append("(");

        int start = descriptor.indexOf("(") + 1;
        int end   = descriptor.indexOf(")");

        result.append(
                StreamSupport.stream(new SignatureSpliterator(descriptor.substring(start, end)), false)
                        .collect(Collectors.joining(", "))
        );

        result.append(")");

        LogManager.getLogger(SignatureHelper.class).debug("End   Signature(\"{}\"): \"{}\"", descriptor, result);

        return result.toString();
    }

    public static int getParameterCount(String descriptor) {
        LogManager.getLogger(SignatureHelper.class).debug("Begin ParameterCount(\"{}\")", descriptor);

        int start = descriptor.indexOf("(") + 1;
        int end   = descriptor.indexOf(")");

        var result = (int) StreamSupport.stream(new SignatureSpliterator(descriptor.substring(start, end)), false).count();

        LogManager.getLogger(SignatureHelper.class).debug("End   ParameterCount(\"{}\"): \"{}\"", descriptor, result);

        return result;
    }

    public static String getReturnType(String descriptor) {
        return convert(descriptor.substring(descriptor.lastIndexOf(")") + 1));
    }

    public static String getType(String descriptor) {
        return convert(descriptor);
    }

    private static class SignatureSpliterator implements Spliterator<String> {
        private final String descriptor;

        private int currentPos = 0;

        public SignatureSpliterator(String descriptor) {
            this.descriptor = descriptor;
        }

        public boolean tryAdvance(Consumer<? super String> action) {
            if (!hasMore()) {
                return false;
            }

            int nextPos = currentPos;

            while (descriptor.charAt(nextPos) == '[') {
                nextPos++;
            }

            if (descriptor.charAt(nextPos) == 'L') {
                nextPos = descriptor.indexOf(";", nextPos);
            }

            if (descriptor.charAt(nextPos) == 'T') {
                nextPos = descriptor.indexOf(";", nextPos);
            }

            action.accept(SignatureHelper.convert(descriptor.substring(currentPos, nextPos + 1)));

            currentPos = nextPos + 1;

            return true;
        }

        private boolean hasMore() {
            return currentPos < descriptor.length();
        }

        public Spliterator<String> trySplit() {
            return null;
        }

        public long estimateSize() {
            return descriptor.length() - currentPos;
        }

        public int characteristics() {
            return NONNULL + IMMUTABLE;
        }
    }
}
