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

public interface TraversalStrategy {
	public boolean PreOutboundTraversal();
	public void PreOutboundTraversal(boolean pre_outbound_traversal);
	public boolean PreInboundTraversal();
	public void PreInboundTraversal(boolean pre_inbound_traversal);
	public boolean PostOutboundTraversal();
	public void PostOutboundTraversal(boolean post_outbound_traversal);
	public boolean PostInboundTraversal();
	public void PostInboundTraversal(boolean post_inbound_traversal);

	public boolean InScope(PackageNode node);
	public boolean InScope(ClassNode node);
	public boolean InScope(FeatureNode node);
	
	public boolean InFilter(PackageNode node);
	public boolean InFilter(ClassNode node);
	public boolean InFilter(FeatureNode node);

	public Collection Order(Collection collection);
}
