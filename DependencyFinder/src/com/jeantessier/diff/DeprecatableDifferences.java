/*
 *  Dependency Finder - Comparing API differences between JAR files
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

package com.jeantessier.diff;

public abstract class DeprecatableDifferences extends RemovableDifferences {
	private boolean    new_deprecation         = false;
	private boolean    removed_deprecation     = false;

	public DeprecatableDifferences(String name) {
		super(name);
	}

	public boolean NewDeprecation() {
		return new_deprecation;
	}

	public void NewDeprecation(boolean new_deprecation) {
		this.new_deprecation = new_deprecation;
	}

	public boolean RemovedDeprecation() {
		return removed_deprecation;
	}

	public void RemovedDeprecation(boolean removed_deprecation) {
		this.removed_deprecation = removed_deprecation;
	}
}
