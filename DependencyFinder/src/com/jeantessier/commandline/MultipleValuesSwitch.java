/*
 *  Copyright (c) 2001-2003, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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
    public MultipleValuesSwitch() {
		this(new LinkedList(), false);
    }

    public MultipleValuesSwitch(String default_value) {
		this(Collections.singletonList(default_value), false);
    }

    public MultipleValuesSwitch(String[] default_value) {
		this(Arrays.asList(default_value), false);
    }

    public MultipleValuesSwitch(List default_value) {
		this(default_value, false);
    }

    public MultipleValuesSwitch(boolean mandatory) {
		this(new LinkedList(), mandatory);
    }

    public MultipleValuesSwitch(String default_value, boolean mandatory) {
		this(Collections.singletonList(default_value), mandatory);
    }

    public MultipleValuesSwitch(String[] default_value, boolean mandatory) {
		this(Arrays.asList(default_value), mandatory);
    }

    public MultipleValuesSwitch(List default_value, boolean mandatory) {
		super(new LinkedList(default_value), mandatory);

		this.value = new LinkedList();
    }

    public Object Value() {
		Object result = DefaultValue();

		if (!((List) value).isEmpty()) {
			result = value;
		}

		return result;
    }

    public void Value(Object value) {
		((List) this.value).add(value);
		super.Value(this.value);
    }

    public int Parse(String name, String value) throws CommandLineException {
		if (value == null) {
			throw new CommandLineException("Missing mandatory value for switch \"" + name + "\"");
		}

		Value(value);
	
		return 2;
    }

    public void Accept(Visitor visitor) {
		visitor.Visit(this);
    }
}
