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

package com.jeantessier.commandline;

import java.util.*;

/**
 *  The switch must be followed by a value, but it can occur multiple times
 *  on the command-line.  The values are accumulated in the same order as on
 *  the command-line and you retrieve them as a single <code>java.util.List</code>.
 */
public class MultipleValuesSwitch extends CommandLineSwitchBase {
    public MultipleValuesSwitch(String name) {
        this(name, new LinkedList<String>(), false);
    }

    public MultipleValuesSwitch(String name, String defaultValue) {
        this(name, Collections.singletonList(defaultValue), false);
    }

    public MultipleValuesSwitch(String name, String[] defaultValue) {
        this(name, Arrays.asList(defaultValue), false);
    }

    public MultipleValuesSwitch(String name, List<String> defaultValue) {
        this(name, defaultValue, false);
    }

    public MultipleValuesSwitch(String name, boolean mandatory) {
        this(name, new LinkedList<String>(), mandatory);
    }

    public MultipleValuesSwitch(String name, String defaultValue, boolean mandatory) {
        this(name, Collections.singletonList(defaultValue), mandatory);
    }

    public MultipleValuesSwitch(String name, String[] defaultValue, boolean mandatory) {
        this(name, Arrays.asList(defaultValue), mandatory);
    }

    public MultipleValuesSwitch(String name, List<String> defaultValue, boolean mandatory) {
        super(name, new LinkedList<String>(defaultValue), mandatory);

        this.value = new LinkedList();
    }

    public Object getValue() {
        Object result = getDefaultValue();

        if (!((List<String>) value).isEmpty()) {
            result = value;
        }

        return result;
    }

    public void setValue(Object value) {
        ((List<String>) this.value).add((String) value);
        super.setValue(this.value);
    }

    public int parse(String value) throws CommandLineException {
        if (value == null) {
            throw new CommandLineException("Missing mandatory value for switch \"" + getName() + "\"");
        }

        setValue(value);
    
        return 2;
    }

    public void accept(Visitor visitor) {
        visitor.visitMultipleValuesSwitch(this);
    }
}
