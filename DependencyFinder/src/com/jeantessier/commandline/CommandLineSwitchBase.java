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
