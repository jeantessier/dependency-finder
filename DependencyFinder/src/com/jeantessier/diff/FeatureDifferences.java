/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

import com.jeantessier.classreader.*;

/**
 *  Documents the difference, if any, for a given feature
 *  (field, constructor, or method).  Its subclasses only
 *  differ in which Visitor callback they invoke.
 *
 *  @see Visitor
 */
public abstract class FeatureDifferences extends RemovableDifferences {
	private Feature_info old_feature;
	private Feature_info new_feature;
	
	private boolean inherited = false;

	/**
	 *  Only the DifferencesFactory can create instances of this class.
	 */
	protected FeatureDifferences(String name, Feature_info old_feature, Feature_info new_feature) {
		super(name);

		OldFeature(old_feature);
		NewFeature(new_feature);
					
		if (old_feature != null) {
			OldDeclaration(old_feature.Declaration());
		}
		
		if (new_feature != null) {
			NewDeclaration(new_feature.Declaration());
		}
	
		if (IsModified()) {
			Logger.getLogger(getClass()).debug(Name() + " declaration has been modified.");
		} else {
			Logger.getLogger(getClass()).debug(Name() + " declaration has not been modified.");
		}
	}

	public Feature_info OldFeature() {
		return old_feature;
	}

	protected void OldFeature(Feature_info old_feature) {
		this.old_feature = old_feature;
	}

	public Feature_info NewFeature() {
		return new_feature;
	}

	protected void NewFeature(Feature_info new_feature) {
		this.new_feature = new_feature;
	}

	public boolean Inherited() {
		return inherited;
	}

	public void Inherited(boolean inherited) {
		this.inherited = inherited;
	}
}
