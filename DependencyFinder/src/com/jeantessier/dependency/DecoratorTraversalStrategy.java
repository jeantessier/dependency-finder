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

public abstract class DecoratorTraversalStrategy implements TraversalStrategy {
	private TraversalStrategy strategy;

	public DecoratorTraversalStrategy(TraversalStrategy strategy) {
		this.strategy = strategy;
	}
	
	public boolean PreOutboundTraversal() {
		return strategy.PreOutboundTraversal();
	}
	
	public void PreOutboundTraversal(boolean pre_outbound_traversal) {
		strategy.PreOutboundTraversal(pre_outbound_traversal);
	}
	
	public boolean PreInboundTraversal() {
		return strategy.PreInboundTraversal();
	}
	
	public void PreInboundTraversal(boolean pre_inbound_traversal) {
		strategy.PreInboundTraversal(pre_inbound_traversal);
	}
	
	public boolean PostOutboundTraversal() {
		return strategy.PostOutboundTraversal();
	}
	
	public void PostOutboundTraversal(boolean post_outbound_traversal) {
		strategy.PostOutboundTraversal(post_outbound_traversal);
	}
	
	public boolean PostInboundTraversal() {
		return strategy.PostInboundTraversal();
	}

	public void PostInboundTraversal(boolean post_inbound_traversal) {
		strategy.PostInboundTraversal(post_inbound_traversal);
	}
	
	public boolean InScope(PackageNode node) {
		return strategy.InScope(node);
	}

	public boolean InScope(ClassNode node) {
		return strategy.InScope(node);
	}

	public boolean InScope(FeatureNode node) {
		return strategy.InScope(node);
	}

	public boolean InFilter(PackageNode node) {
		return strategy.InFilter(node);
	}

	public boolean InFilter(ClassNode node) {
		return strategy.InFilter(node);
	}

	public boolean InFilter(FeatureNode node) {
		return strategy.InFilter(node);
	}

	public Collection Order(Collection collection) {
		return strategy.Order(collection);
	}
}
