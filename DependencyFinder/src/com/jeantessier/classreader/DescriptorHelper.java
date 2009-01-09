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

package com.jeantessier.classreader;

import java.util.*;

import org.apache.log4j.*;

public final class DescriptorHelper {
    private static Map<String, String> conversion = new HashMap<String, String>();

    static {
        conversion.put("B", "byte");
        conversion.put("C", "char");
        conversion.put("D", "double");
        conversion.put("F", "float");
        conversion.put("I", "int");
        conversion.put("J", "long");
        conversion.put("S", "short");
        conversion.put("V", "void");
        conversion.put("Z", "boolean");
    }

    private DescriptorHelper() {
        // Prevent instantiation
    }

    public static String convert(String type) {
        String result = null;

        Logger.getLogger(DescriptorHelper.class).debug("Begin Convert(\"" + type + "\")");

        if (type.length() == 1) {
            result = conversion.get(type);
        } else if (type.charAt(0) == 'L') {
            result = ClassNameHelper.path2ClassName(type.substring(1, type.indexOf(';')));
        } else if (type.charAt(0) == '[') {
            result = convert(type.substring(1)) + "[]";
        }

        Logger.getLogger(DescriptorHelper.class).debug("End   Convert(\"" + type + "\"): \"" + result + "\"");

        return result;
    }

    public static String getSignature(String descriptor) {
        StringBuffer result = new StringBuffer();

        Logger.getLogger(DescriptorHelper.class).debug("Begin Signature(\"" + descriptor + "\")");

        result.append("(");

        int start = descriptor.indexOf("(") + 1;
        int end   = descriptor.indexOf(")");

        Iterator i = new DescriptorIterator(descriptor.substring(start, end));
        while (i.hasNext()) {
            result.append(i.next());
            if (i.hasNext()) {
                result.append(", ");
            }
        }

        result.append(")");

        Logger.getLogger(DescriptorHelper.class).debug("End   Signature(\"" + descriptor + "\"): \"" + result + "\"");

        return result.toString();
    }

    public static int getParameterCount(String descriptor) {
        int result = 0;

        Logger.getLogger(DescriptorHelper.class).debug("Begin ParameterCount(\"" + descriptor + "\")");

        int start = descriptor.indexOf("(") + 1;
        int end   = descriptor.indexOf(")");

        Iterator i = new DescriptorIterator(descriptor.substring(start, end));
        while (i.hasNext()) {
            i.next();
            result++;
        }

        Logger.getLogger(DescriptorHelper.class).debug("End   ParameterCount(\"" + descriptor + "\"): \"" + result + "\"");

        return result;
    }

    public static String getReturnType(String descriptor) {
        return convert(descriptor.substring(descriptor.lastIndexOf(")") + 1));
    }

    public static String getType(String descriptor) {
        return convert(descriptor);
    }
}

class DescriptorIterator implements Iterator {
    private String descriptor;
    private int currentPos = 0;

    public DescriptorIterator(String descriptor) {
        this.descriptor = descriptor;
    }

    public boolean hasNext() {
        return currentPos < descriptor.length();
    }

    public Object next() {
        String result;

        if (hasNext()) {
            int nextPos = currentPos;

            while (descriptor.charAt(nextPos) == '[') {
                nextPos++;
            }

            if (descriptor.charAt(nextPos) == 'L') {
                nextPos = descriptor.indexOf(";", nextPos);
            }

            result = DescriptorHelper.convert(descriptor.substring(currentPos, nextPos + 1));

            currentPos = nextPos + 1;
        } else {
            throw new NoSuchElementException();
        }

        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
