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
 *  Generates summary information about the command-line specification.
 */
public class CommandLineUsage implements Visitor {
	private final static String EOL = System.getProperty("line.separator", "\n");
	
    private String       command;
    private StringBuffer usage   = new StringBuffer();
    private String       switch_name;

    public CommandLineUsage(String command) {
		this.command = command;
    }

    public void Visit(CommandLine command_line) {
		usage.append("USAGE: ").append(command).append(EOL);

		Iterator i = command_line.KnownSwitches().iterator();
		while (i.hasNext()) {
			switch_name = (String) i.next();

			command_line.Switch(switch_name).Accept(this);
		}

		command_line.ParameterStrategy().Accept(this);
    }

    public void Visit(ToggleSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		} else {
			usage.append("       [-").append(switch_name).append("] (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		}
    }

    public void Visit(SingleValueSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" value (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		} else {
			usage.append("       [-").append(switch_name).append(" value] (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		}
    }

    public void Visit(OptionalValueSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" [value] (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		} else {
			usage.append("       [-").append(switch_name).append(" [value]] (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		}
    }

    public void Visit(MultipleValuesSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       (-").append(switch_name).append(" value)+ (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		} else {
			usage.append("       (-").append(switch_name).append(" value)* (defaults to ").append(cls.DefaultValue()).append(")").append(EOL);
		}
    }

    public void Visit(NullParameterStrategy strategy) {
    }

    public void Visit(AnyParameterStrategy strategy) {
		usage.append("       [param ...]").append(EOL);
    }

    public void Visit(AtLeastParameterStrategy strategy) {
		for (int i=1; i<=strategy.NbParameters(); i++) {
			usage.append("       ").append("param").append(i).append(EOL);
		}

		usage.append("       ...").append(EOL);
    }

    public void Visit(ExactlyParameterStrategy strategy) {
		for (int i=1; i<=strategy.NbParameters(); i++) {
			usage.append("       ").append("param").append(i).append(EOL);
		}
    }

    public void Visit(AtMostParameterStrategy strategy) {
		usage.append("       ");

		for (int i=1; i<=strategy.NbParameters(); i++) {
			usage.append("[param").append(i);
			if (i < strategy.NbParameters()) {
				usage.append(" ");
			}
		}

		for (int i=1; i<=strategy.NbParameters(); i++) {
			usage.append("]");
		}

		usage.append(EOL);
    }

    public String toString() {
		return usage.toString();
    }
}
