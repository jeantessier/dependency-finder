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

public class CommandLineUsage implements Visitor {
    private String       command;
    private StringBuffer usage   = new StringBuffer();
    private String       switch_name;

    public CommandLineUsage(String command) {
		this.command = command;
    }

    public void Visit(CommandLine command_line) {
		usage.append("USAGE: ").append(command).append("\n");

		Iterator i = command_line.KnownSwitches().iterator();
		while (i.hasNext()) {
			switch_name = (String) i.next();

			command_line.Switch(switch_name).Accept(this);
		}

		command_line.ParameterStrategy().Accept(this);
    }

    public void Visit(ToggleSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" (defaults to ").append(cls.DefaultValue()).append(")\n");
		} else {
			usage.append("       [-").append(switch_name).append("] (defaults to ").append(cls.DefaultValue()).append(")\n");
		}
    }

    public void Visit(SingleValueSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" value (defaults to ").append(cls.DefaultValue()).append(")\n");
		} else {
			usage.append("       [-").append(switch_name).append(" value] (defaults to ").append(cls.DefaultValue()).append(")\n");
		}
    }

    public void Visit(OptionalValueSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       -").append(switch_name).append(" [value] (defaults to ").append(cls.DefaultValue()).append(")\n");
		} else {
			usage.append("       [-").append(switch_name).append(" [value]] (defaults to ").append(cls.DefaultValue()).append(")\n");
		}
    }

    public void Visit(MultipleValuesSwitch cls) {
		if (cls.Mandatory()) {
			usage.append("       (-").append(switch_name).append(" value)+ (defaults to ").append(cls.DefaultValue()).append(")\n");
		} else {
			usage.append("       (-").append(switch_name).append(" value)* (defaults to ").append(cls.DefaultValue()).append(")\n");
		}
    }

    public void Visit(NullParameterStrategy ps) {
    }

    public void Visit(AnyParameterStrategy ps) {
		usage.append("       [param ...]\n");
    }

    public void Visit(AtLeastParameterStrategy ps) {
		for (int i=1; i<=ps.NbParameters(); i++) {
			usage.append("       ").append("param").append(i).append("\n");
		}

		usage.append("       ...\n");
    }

    public void Visit(ExactlyParameterStrategy ps) {
		for (int i=1; i<=ps.NbParameters(); i++) {
			usage.append("       ").append("param").append(i).append("\n");
		}
    }

    public void Visit(AtMostParameterStrategy ps) {
		usage.append("       ");

		for (int i=1; i<=ps.NbParameters(); i++) {
			usage.append("[param").append(i);
			if (i < ps.NbParameters()) {
				usage.append(" ");
			}
		}

		for (int i=1; i<=ps.NbParameters(); i++) {
			usage.append("]");
		}

		usage.append("\n");
    }

    public String toString() {
		return usage.toString();
    }
}
