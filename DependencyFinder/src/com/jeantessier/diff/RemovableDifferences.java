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

public abstract class RemovableDifferences implements Differences, Comparable {
	private String name;

	private String old_declaration = null;
	private String new_declaration = null;

	public RemovableDifferences(String name) {
		this.name = name;
	}

	public String Name() {
		return name;
	}

	public String OldDeclaration() {
		return old_declaration;
	}

	public void OldDeclaration(String old_declaration) {
		this.old_declaration = old_declaration;
	}

	public String NewDeclaration() {
		return new_declaration;
	}

	public void NewDeclaration(String new_declaration) {
		this.new_declaration = new_declaration;
	}

	public boolean IsRemoved() {
		return (old_declaration != null) && (new_declaration == null);
	}
    
	public boolean IsModified() {
		return (old_declaration != null) && (new_declaration != null) && !old_declaration.equals(new_declaration);
	}
    
	public boolean IsNew() {
		return (old_declaration == null) && (new_declaration != null);
	}
    
	public boolean IsEmpty() {
		return
			!IsNew() &&
			!IsModified() &&
			!IsRemoved();
	}

	public String toString() {
		return Name();
	}

	public int compareTo(Object other) {
		int result = 0;

		if (other instanceof RemovableDifferences) {
			result = Name().compareTo(((RemovableDifferences) other).Name());
		} else {
			throw new ClassCastException("Unable to compare RemovableDifferences to " + other.getClass().getName());
		}

		return result;
	}
}
