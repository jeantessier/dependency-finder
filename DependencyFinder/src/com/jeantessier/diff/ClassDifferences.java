/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

/**
 *  Documents the difference, if any, for a given object
 *  type (class or interface).  Its subclasses only
 *  differ in which Visitor callback they invoke.
 *
 *  @see Visitor
 */
public class ClassDifferences extends RemovableDifferences {
	private Classfile old_class;
	private Classfile new_class;

	private Collection feature_differences = new LinkedList();

	/**
	 *  Only the DifferencesFactory can create instances of this class.
	 */
	ClassDifferences(String name, Classfile old_class, Classfile new_class) {
		super(name);

		OldClass(old_class);
		NewClass(new_class);
		
		if (old_class != null) {
			OldDeclaration(old_class.Declaration());
		}

		if (new_class != null) {
			NewDeclaration(new_class.Declaration());
		}
	
		if (IsModified()) {
			Logger.getLogger(getClass()).debug(Name() + " declaration has been modified.");
		} else {
			Logger.getLogger(getClass()).debug(Name() + " declaration has not been modified.");
		}
	}

	public Classfile OldClass() {
		return old_class;
	}

	protected void OldClass(Classfile old_class) {
		this.old_class = old_class;
	}

	public Classfile NewClass() {
		return new_class;
	}

	protected void NewClass(Classfile new_class) {
		this.new_class = new_class;
	}

	public Collection FeatureDifferences() {
		return feature_differences;
	}

	public boolean IsModified() {
		return super.IsModified() || (FeatureDifferences().size() != 0);
	}

	public void Accept(Visitor visitor) {
		visitor.VisitClassDifferences(this);
	}
}
