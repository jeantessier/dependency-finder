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

public class CollectionSelectionCriteria implements SelectionCriteria {
	private boolean match_package    = true;
	private boolean match_class      = true;
	private boolean match_feature    = true;

	Collection include;
	Collection exclude;

	public CollectionSelectionCriteria(Collection include, Collection exclude) {
		this.include = include;
		this.exclude = exclude;
	}
	
	public boolean MatchPackage() {
		return match_package;
	}

	public void MatchPackage(boolean match_package) {
		this.match_package = match_package;
	}

	public boolean MatchClass() {
		return match_class;
	}

	public void MatchClass(boolean match_class) {
		this.match_class = match_class;
	}
	
	public boolean MatchFeature() {
		return match_feature;
	}

	public void MatchFeature(boolean match_feature) {
		this.match_feature = match_feature;
	}

	public boolean Match(PackageNode node) {
		return Match(node.Name());
	}
	
	public boolean Match(ClassNode node) {
		return Match(node.Name());
	}
	
	public boolean Match(FeatureNode node) {
		return Match(node.Name());
	}

	public boolean PackageMatch(String name) {
		return Match(name);
	}
	
	public boolean ClassMatch(String name) {
		return Match(name);
	}
	
	public boolean FeatureMatch(String name) {
		return Match(name);
	}

	private boolean Match(String name) {
		return (include == null || include.contains(name)) && (exclude == null || !exclude.contains(name));
	}
}
