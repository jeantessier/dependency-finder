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

public abstract class CommandLineSwitchBase implements CommandLineSwitch {
    private   Object  default_value;
    protected Object  value;
    private   boolean present;
    private   boolean mandatory;

    public CommandLineSwitchBase() {
		this(null, false);
    }

    public CommandLineSwitchBase(Object default_value) {
		this(default_value, false);
    }

    public CommandLineSwitchBase(boolean mandatory) {
		this(null, mandatory);
    }

    public CommandLineSwitchBase(Object default_value, boolean mandatory) {
		this.default_value = default_value;
		this.mandatory     = mandatory;

		this.value = null;

		Present(false);
    }

    public Object DefaultValue() {
		return default_value;
    }

    public Object Value() {
		Object result = default_value;

		if (value != null) {
			result = value;
		}

		return result;
    }

    public void Value(Object new_value) {
		value = new_value;

		Present(true);
    }

    public boolean Present() {
		return present;
    }

    protected void Present(boolean present) {
		this.present = present;
    }

    public boolean Mandatory() {
		return mandatory;
    }

    public String toString() {
		return Value().toString();
    }
}
