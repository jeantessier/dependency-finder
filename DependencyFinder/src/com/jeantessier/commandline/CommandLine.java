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
 *  Command-line parser.
 */
public class CommandLine implements Visitable {
    private static final boolean           DEFAULT_STRICT             = true;
    private static final ParameterStrategy DEFAULT_PARAMETER_STRATEGY = new AnyParameterStrategy();

    private boolean           strict;
    private ParameterStrategy parameter_strategy;

    private List              parameters = new LinkedList();
    private Map               map        = new TreeMap();
    
    public CommandLine() {
		this(DEFAULT_STRICT, DEFAULT_PARAMETER_STRATEGY);
    }

    public CommandLine(boolean strict) {
		this(strict, DEFAULT_PARAMETER_STRATEGY);
    }

    public CommandLine(ParameterStrategy parameter_strategy) {
		this(DEFAULT_STRICT, parameter_strategy);
    }

    public CommandLine(boolean strict, ParameterStrategy parameter_strategy) {
		Strict(strict);
		ParameterStrategy(parameter_strategy);
    }

    public boolean Strict() {
		return strict;
    }

    public void Strict(boolean strict) {
		this.strict = strict;
    }

    public ParameterStrategy ParameterStrategy() {
		return parameter_strategy;
    }

    public void ParameterStrategy(ParameterStrategy parameter_strategy) {
		this.parameter_strategy = parameter_strategy;
    }

    public void AddSwitch(String name, CommandLineSwitch cls) {
		map.put(name, cls);
    }

    public void AddToggleSwitch(String name) {
		AddSwitch(name, new ToggleSwitch());
    }

    public void AddToggleSwitch(String name, boolean default_value) {
		AddSwitch(name, new ToggleSwitch(default_value));
    }

    public void AddSingleValueSwitch(String name) {
		AddSwitch(name, new SingleValueSwitch());
    }

    public void AddSingleValueSwitch(String name, boolean mandatory) {
		AddSwitch(name, new SingleValueSwitch(mandatory));
    }

    public void AddSingleValueSwitch(String name, String default_value) {
		AddSwitch(name, new SingleValueSwitch(default_value));
    }

    public void AddSingleValueSwitch(String name, String default_value, boolean mandatory) {
		AddSwitch(name, new SingleValueSwitch(default_value, mandatory));
    }

    public void AddOptionalValueSwitch(String name) {
		AddSwitch(name, new OptionalValueSwitch());
    }

    public void AddOptionalValueSwitch(String name, boolean mandatory) {
		AddSwitch(name, new OptionalValueSwitch(mandatory));
    }

    public void AddOptionalValueSwitch(String name, String default_value) {
		AddSwitch(name, new OptionalValueSwitch(default_value));
    }

    public void AddOptionalValueSwitch(String name, String default_value, boolean mandatory) {
		AddSwitch(name, new OptionalValueSwitch(default_value, mandatory));
    }

    public void AddMultipleValuesSwitch(String name) {
		map.put(name, new MultipleValuesSwitch());
    }

    public void AddMultipleValuesSwitch(String name, boolean mandatory) {
		map.put(name, new MultipleValuesSwitch(mandatory));
    }

    public void AddMultipleValuesSwitch(String name, String default_value) {
		map.put(name, new MultipleValuesSwitch(default_value));
    }

    public void AddMultipleValuesSwitch(String name, String default_value, boolean mandatory) {
		map.put(name, new MultipleValuesSwitch(default_value, mandatory));
    }

    public CommandLineSwitch Switch(String name) {
		return (CommandLineSwitch) map.get(name);
    }

    public boolean ToggleSwitch(String name) {
		boolean result = false;

		CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
		if (cls != null) {
			result = ((Boolean) cls.Value()).booleanValue();
		}

		return result;
    }

    public String SingleSwitch(String name) {
		return StringSwitch(name);
    }

    public String OptionalSwitch(String name) {
		return StringSwitch(name);
    }

    public List MultipleSwitch(String name) {
		return ListSwitch(name);
    }

    private String StringSwitch(String name) {
		String result = null;

		CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
		if (cls != null) {
			result =  cls.Value().toString();
		}

		return result;
    }

    private List ListSwitch(String name) {
		List result = null;

		CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
		if (cls != null && cls.Value() instanceof List) {
			result =  (List) cls.Value();
		}

		return result;
    }

    public boolean IsPresent(String name) {
		boolean result = false;

		CommandLineSwitch cls = (CommandLineSwitch) map.get(name);
		if (cls != null) {
			result = cls.Present();
		}

		return result;
    }

    public Set KnownSwitches() {
		return map.keySet();
    }

    public Set PresentSwitches() {
		Set result = new TreeSet();

		Iterator i = KnownSwitches().iterator();
		while (i.hasNext()) {
			String            name = (String) i.next();
			CommandLineSwitch cls  = (CommandLineSwitch) map.get(name);

			if (cls.Present()) {
				result.add(name);
			}
		}

		return result;
    }

    public List Parameters() {
		return parameters;
    }

    public void Parse(String args[]) throws CommandLineException {
		parameters = new LinkedList();

		int i=0;
		while (i < args.length) {
			if (args[i].startsWith("-")) {
				String name  = args[i].substring(1);
				String value = null;

				if (i+1 < args.length && !map.containsKey(args[i+1].substring(1))) {
					value = args[i+1];
				}

				CommandLineSwitch cls = (CommandLineSwitch) map.get(name);

				if (cls == null) {
					if (Strict()) {
						throw new CommandLineException("Unknown switch \"" + args[i] + "\"");
					} else {
						cls = new OptionalValueSwitch();
						map.put(name, cls);
					}
				}

				i += cls.Parse(name, value);
			} else if (parameter_strategy.Accept(args[i])) {
				parameters.add(args[i]);
				i++;
			} else {
				throw new CommandLineException("Invalid parameter \"" + args[i] + "\"");
			}
		}

		// Checking that all manadatory switches are present
		Iterator j = map.keySet().iterator();
		while (j.hasNext()) {
			String            name = (String) j.next();
			CommandLineSwitch cls  = (CommandLineSwitch) map.get(name);

			if (cls.Mandatory() && !cls.Present()) {
				throw new CommandLineException("Missing mandatory switch \"" + name + "\"");
			}
		}

		// Checking that all mandatory parameters are present
		if (!parameter_strategy.Satisfied()) {
			throw new CommandLineException("Missing mandatory parameters");
		}
    }

    public void Accept(Visitor visitor) {
		visitor.Visit(this);
    }
}
