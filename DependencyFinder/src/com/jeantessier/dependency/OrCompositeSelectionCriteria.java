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

package com.jeantessier.dependency;

import java.util.*;

public class OrCompositeSelectionCriteria extends CompositeSelectionCriteria {
	public OrCompositeSelectionCriteria(Collection subcriteria) {
		super(subcriteria);
	}
	
	public boolean MatchPackage() {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).MatchPackage();
		}
		
		return result;
	}
	
	public boolean MatchClass() {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).MatchClass();
		}
		
		return result;
	}
	
	public boolean MatchFeature() {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).MatchFeature();
		}
		
		return result;
	}

	public boolean Match(PackageNode node) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).Match(node);
		}
		
		return result;
	}
	
	public boolean Match(ClassNode node) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).Match(node);
		}
		
		return result;
	}
	
	public boolean Match(FeatureNode node) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).Match(node);
		}
		
		return result;
	}

	public boolean PackageMatch(String name) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).PackageMatch(name);
		}
		
		return result;
	}
	
	public boolean ClassMatch(String name) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).ClassMatch(name);
		}
		
		return result;
	}
	
	public boolean FeatureMatch(String name) {
		boolean result = Subcriteria().isEmpty();

		Iterator i = Subcriteria().iterator();
		while (!result && i.hasNext()) {
			result = ((SelectionCriteria) i.next()).FeatureMatch(name);
		}
		
		return result;
	}
}
