/*
 *  Dependency Finder - Computes quality factors from compiled Java code
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

package com.jeantessier.dependency;

import java.util.*;

import com.jeantessier.classreader.*;

public class DependencyEvent extends EventObject {
	private String classname;
	private Node   dependent;
	private Node   dependable;

	public DependencyEvent(Object source, String classname) {
		this(source, classname, null, null);
	}

	public DependencyEvent(Object source, Node dependent, Node dependable) {
		this(source, null, dependent, dependable);
	}
		
	private DependencyEvent(Object source, String classname, Node dependent, Node dependable) {
		super(source);

		this.classname  = classname;
		this.dependent  = dependent;
		this.dependable = dependable;
	}

	public String Classname() {
		return classname;
	}

	public Node Dependent() {
		return dependent;
	}

	public Node Dependable() {
		return dependable;
	}
}
