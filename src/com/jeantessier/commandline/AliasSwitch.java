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
 * Passes its values to the switches it is an alias for.  Use an alias switch
 * to group many switches together and set them all at once with just one name.
 */
public class AliasSwitch implements CommandLineSwitch {
    private Collection<CommandLineSwitch> switches = new LinkedList<CommandLineSwitch>();

    private String name;

    public AliasSwitch(String name, CommandLineSwitch ... switches) {
        this.name = name;
        for (CommandLineSwitch commandLineSwitch : switches) {
            this.switches.add(commandLineSwitch);
        }
    }

    public String getName() {
        return name;
    }

    public Collection<CommandLineSwitch> getSwitches() {
        return switches;
    }

    public Object getDefaultValue() {
        return "";
    }

    public Object getValue() {
        return null;
    }

    public void setValue(Object value) {
        for (CommandLineSwitch commandLineSwitch : getSwitches()) {
            commandLineSwitch.setValue(value);
        }
    }

    public boolean isPresent() {
        boolean result = !getSwitches().isEmpty();

        for (CommandLineSwitch commandLineSwitch : getSwitches()) {
            result = result && commandLineSwitch.isPresent();
        }

        return result;
    }

    public boolean isMandatory() {
        return false;
    }

    public void validate() throws CommandLineException {
        // Do nothing
    }

    public int parse(String value) throws CommandLineException {
        int result = 1;

        for (CommandLineSwitch commandLineSwitch : getSwitches()) {
            result = Math.max(result, commandLineSwitch.parse(value));
        }

        return result;
    }

    public void accept(Visitor visitor) {
        visitor.visitAliasSwitch(this);
    }
}
