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

package com.jeantessier.dependency;

import java.util.*;

public class SelectiveTraversalStrategy implements TraversalStrategy {
	private SelectionCriteria scope_criteria;
	private SelectionCriteria filter_criteria;

	private boolean pre_outbound_traversal  = true;
	private boolean pre_inbound_traversal   = true;
	private boolean post_outbound_traversal = false;
	private boolean post_inbound_traversal  = false;

	public SelectiveTraversalStrategy() {
		this(new ComprehensiveSelectionCriteria(), new ComprehensiveSelectionCriteria());
	}
	
	public SelectiveTraversalStrategy(SelectionCriteria scope_criteria, SelectionCriteria filter_criteria) {
		this.scope_criteria  = scope_criteria;
		this.filter_criteria = filter_criteria;
	}
	
	public boolean PreOutboundTraversal() {
		return pre_outbound_traversal;
	}

	public void PreOutboundTraversal(boolean pre_outbound_traversal) {
		this.pre_outbound_traversal = pre_outbound_traversal;
	}

	public boolean PreInboundTraversal() {
		return pre_inbound_traversal;
	}

	public void PreInboundTraversal(boolean pre_inbound_traversal) {
		this.pre_inbound_traversal = pre_inbound_traversal;
	}

	public boolean PostOutboundTraversal() {
		return post_outbound_traversal;
	}

	public void PostOutboundTraversal(boolean post_outbound_traversal) {
		this.post_outbound_traversal = post_outbound_traversal;
	}

	public boolean PostInboundTraversal() {
		return post_inbound_traversal;
	}

	public void PostInboundTraversal(boolean post_inbound_traversal) {
		this.post_inbound_traversal = post_inbound_traversal;
	}

	public boolean InScope(PackageNode node) {
		return scope_criteria.Match(node);
	}
	
	public boolean InScope(ClassNode node) {
		return scope_criteria.Match(node);
	}
	
	public boolean InScope(FeatureNode node) {
		return scope_criteria.Match(node);
	}

	public boolean InFilter(PackageNode node) {
		return filter_criteria.Match(node);
	}
	
	public boolean InFilter(ClassNode node) {
		return filter_criteria.Match(node);
	}
	
	public boolean InFilter(FeatureNode node) {
		return filter_criteria.Match(node);
	}

	public Collection Order(Collection collection) {
		return new ArrayList(collection);
	}
}
