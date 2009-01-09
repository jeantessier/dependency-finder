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

package com.jeantessier.text;

import java.util.*;

import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;

public class MaximumCapacityPatternCache implements PatternCache {
    private PatternCompiler compiler;

    private Map<String, Pattern> map = new HashMap<String, Pattern>();

    public MaximumCapacityPatternCache() {
        this(new Perl5Compiler());
    }

    public MaximumCapacityPatternCache(PatternCompiler compiler) {
        this.compiler = compiler;
    }

    public Pattern addPattern(String expression) throws MalformedPatternException {
        Pattern result = map.get(expression);

        if (result == null) {
            result = compiler.compile(expression);
            map.put(expression, result);
        }

        return result;
    }

    public Pattern addPattern(String expression, int options) throws MalformedPatternException {
        Pattern result = map.get(expression);

        if (result == null) {
            result = compiler.compile(expression, options);
            map.put(expression, result);
        }

        return result;
    }

    public Pattern getPattern(String expression) throws MalformedCachePatternException {
        Pattern result = map.get(expression);

        if (result == null) {
            try {
                result = compiler.compile(expression);
            } catch (MalformedPatternException ex) {
                throw new MalformedCachePatternException(ex.getMessage());
            }
            map.put(expression, result);
        }

        return result;
    }

    public Pattern getPattern(String expression, int options) throws MalformedCachePatternException {
        Pattern result = map.get(expression);

        if (result == null) {
            try {
                result = compiler.compile(expression, options);
            } catch (MalformedPatternException ex) {
                throw new MalformedCachePatternException(ex.getMessage());
            }
            map.put(expression, result);
        }

        return result;
    }

    public int size() {
        return map.size();
    }

    public int capacity() {
        return 20;
    }
}
