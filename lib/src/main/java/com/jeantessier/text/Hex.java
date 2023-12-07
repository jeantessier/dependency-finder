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

package com.jeantessier.text;

import java.nio.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class Hex {
    private static final int DEFAULT_CHAR_GROUP_SIZE = 8;

    private Hex() {
        // Do nothing
    }

    public static String toString(byte[] bytes) {
        return toString(ByteBuffer.wrap(bytes), DEFAULT_CHAR_GROUP_SIZE);
    }

    public static String toString(byte[] bytes, int charGroupSize) {
        return toString(ByteBuffer.wrap(bytes), charGroupSize);
    }

    private static String toString(ByteBuffer buffer, int charGroupSize) {
        return Stream.generate(buffer::get)
                .limit(buffer.capacity())
                .map(b -> String.format("%02X", b))
                .map(s -> s.split(""))
                .flatMap(Arrays::stream)
                .collect(new TextCollector(charGroupSize));
    }

    private static class TextCollector implements Collector<String, StringBuilder, String> {
        private final int charGroupSize;

        private long currentGroupSize = 0;

        public TextCollector(int charGroupSize) {
            this.charGroupSize = charGroupSize;
        }

        public Supplier<StringBuilder> supplier() {
            return StringBuilder::new;
        }

        public BiConsumer<StringBuilder, String> accumulator() {
            return (builder, c) -> {
                builder.append(c);
                currentGroupSize += c.length();
                if (currentGroupSize % charGroupSize == 0) {
                    builder.append(" ");
                }
            };
        }

        public Function<StringBuilder, String> finisher() {
            return (builder) -> builder.toString().trim();
        }

        public BinaryOperator<StringBuilder> combiner() {
            return (builder1, builder2) -> {
                builder1.append(builder2);
                currentGroupSize += builder2.length();
                return builder1;
            };
        }

        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
