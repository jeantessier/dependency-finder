/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

/**
 *  Base class for implenting the <code>CommandLineSwitch</code> interface.
 */
public abstract class CommandLineSwitchBase implements CommandLineSwitch {
    private final String name;
    private final Object defaultValue;
    private final boolean mandatory;

    protected Object value = null;
    private boolean present = false;

    public CommandLineSwitchBase(String name) {
        this(name, null, false);
    }

    public CommandLineSwitchBase(String name, Object defaultValue) {
        this(name, defaultValue, false);
    }

    public CommandLineSwitchBase(String name, boolean mandatory) {
        this(name, null, mandatory);
    }

    public CommandLineSwitchBase(String name, Object defaultValue, boolean mandatory) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.mandatory = mandatory;
    }

    public String getName() {
        return name;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Object getValue() {
        Object result = defaultValue;

        if (value != null) {
            result = value;
        }

        return result;
    }

    public void setValue(Object value) {
        this.value = value;

        this.present = true;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void validate() throws CommandLineException {
        if (isMandatory() && !isPresent()) {
            throw new CommandLineException("Missing mandatory switch \"" + getName() + "\"");            
        }
    }

    public String toString() {
        return getValue().toString();
    }
}
