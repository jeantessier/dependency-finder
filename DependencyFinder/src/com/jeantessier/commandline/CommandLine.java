/*
 *  Dependency Finder - Utilities for parsing command lines and logging
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.commandline;

import java.util.*;

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

    public static void Error(CommandLineUsage clu, Exception ex) {
		Error(clu, ex.toString());
    }

    public static void Error(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		Error(clu);
    }

    public static void Error(CommandLineUsage clu) {
		System.err.print(clu);
    }

    public static void main(String args[]) throws Exception {
		Iterator i;

		// CommandLine      cl  = new CommandLine(true, new AtMostParameterStrategy(3));
		// CommandLine      cl  = new CommandLine(false);
		// CommandLine      cl  = new CommandLine();
		CommandLine      cl  = new CommandLine(new NullParameterStrategy());
		CommandLineUsage clu = new CommandLineUsage("CommandLine");

		// cl.Strict(false);
		cl.AddToggleSwitch("verbose");
		cl.AddMultipleValuesSwitch("gaga");
		cl.Accept(clu);

		System.out.println("The program knows about the following switches:");
		i = cl.KnownSwitches().iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			System.out.println("\t" + name + " (" + cl.Switch(name) + ")");
		}
		System.out.println();

		try {
			cl.Parse(args);
		} catch (CommandLineException ex) {
			Error(clu, ex);
		}

		System.out.println("The program was called with the following switches:");
		i = cl.PresentSwitches().iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			System.out.println("\t" + name + " (" + cl.Switch(name) + ")");
		}
		System.out.println();

		System.out.println("The program knows about the following switches:");
		i = cl.KnownSwitches().iterator();
		while (i.hasNext()) {
			String name = (String) i.next();
			System.out.println("\t" + name + " (" + cl.Switch(name) + ")");
		}
		System.out.println();

		System.out.println("The program was called with the following parameters:");
		i = cl.Parameters().iterator();
		while (i.hasNext()) {
			System.out.println("\t" + i.next());
		}
    }
}
