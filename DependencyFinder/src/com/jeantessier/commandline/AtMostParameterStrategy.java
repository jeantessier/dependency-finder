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

public class AtMostParameterStrategy implements CountingParameterStrategy {
    private int nb_parameters;
    private int count;

    public AtMostParameterStrategy(int nb_parameters) {
		this.nb_parameters = nb_parameters;

		this.count = 0;
    }

    public boolean Accept(String param) {
		count++;

		return count <= nb_parameters;
    }

    public boolean Satisfied() {
		return count <= nb_parameters;
    }

    public int NbParameters() {
		return nb_parameters;
    }

    public int Count() {
		return count;
    }

    public void Accept(Visitor visitor) {
		visitor.Visit(this);
    }
}
