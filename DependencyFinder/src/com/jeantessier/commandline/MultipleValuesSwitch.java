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
