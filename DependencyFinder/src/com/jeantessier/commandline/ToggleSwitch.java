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

public class ToggleSwitch extends CommandLineSwitchBase {
    public ToggleSwitch() {
		this(false, false);
    }

    public ToggleSwitch(boolean default_value) {
		this(default_value, false);
    }

    public ToggleSwitch(boolean default_value, boolean mandatory) {
		super(new Boolean(default_value), mandatory);
    }

    public int Parse(String name, String value) throws CommandLineException {
		Value(new Boolean(true));
	
		return 1;
    }

    public void Accept(Visitor visitor) {
		visitor.Visit(this);
    }
}
