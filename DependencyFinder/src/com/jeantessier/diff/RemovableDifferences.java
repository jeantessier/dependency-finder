/*
 *  Copyright (c) 2001-2002, Jean Tessier
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

package com.jeantessier.diff;

import org.apache.log4j.*;

/**
 *  Documents the difference, if any, for a given programming
 *  element that can be absent in either the old or the new
 *  codebase.  This includes classes, interfaces, fields,
 *  constructors, and methods.
 */
public abstract class RemovableDifferences implements Differences, Comparable {
	private String name;

	private String old_declaration = null;
	private String new_declaration = null;

	protected RemovableDifferences(String name) {
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
		boolean result = (old_declaration != null) && (new_declaration == null);

		Logger.getLogger(getClass()).debug(Name() + " IsRemoved(): " + result);
		
		return result;
	}
    
	public boolean IsModified() {
		boolean result = (old_declaration != null) && (new_declaration != null) && !old_declaration.equals(new_declaration);

		Logger.getLogger(getClass()).debug(Name() + " IsModified(): " + result);
		
		return result;
	}
    
	public boolean IsNew() {
		boolean result = (old_declaration == null) && (new_declaration != null);

		Logger.getLogger(getClass()).debug(Name() + " IsNew(): " + result);
		
		return result;
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
