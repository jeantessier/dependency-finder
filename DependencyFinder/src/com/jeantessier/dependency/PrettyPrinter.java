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

import org.apache.log4j.*;

public class PrettyPrinter extends TextPrinter {
	private boolean show_inbounds    = true;
	private boolean show_outbounds   = true;
	private boolean show_empty_nodes = true;
	
	private Map dependencies = new TreeMap();

	public PrettyPrinter() {
		super();
	}

	public PrettyPrinter(TraversalStrategy strategy) {
		super(strategy);
	}

	public PrettyPrinter(String indent_text) {
		super(indent_text);
	}

	public PrettyPrinter(TraversalStrategy strategy, String indent_text) {
		super(strategy, indent_text);
	}

	public boolean ShowInbounds() {
		return show_inbounds;
	}

	public void ShowInbounds(boolean show_inbounds) {
		this.show_inbounds = show_inbounds;
	}
	
	public boolean ShowOutbounds() {
		return show_outbounds;
	}

	public void ShowOutbounds(boolean show_outbounds) {
		this.show_outbounds = show_outbounds;
	}
	
	public boolean ShowEmptyNodes() {
		return show_empty_nodes;
	}

	public void ShowEmptyNodes(boolean show_empty_nodes) {
		this.show_empty_nodes = show_empty_nodes;
	}
	
	protected void PreprocessPackageNode(PackageNode node) {
		Category.getInstance(getClass().getName()).debug("Printing package \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		Scope().add(node);
		PushNode(node);

		RaiseIndent();

		PushBuffer();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesPackageNode(PackageNode node) {
		Category.getInstance(getClass().getName()).debug("Package \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		PrintDependencies(dependencies);
	}
	
	protected void PostprocessPackageNode(PackageNode node) {
		super.PostprocessPackageNode(node);
		if (ShowEmptyNodes() || !dependencies.isEmpty() || CurrentBufferLength() > 0) {
			PopBuffer(node.Name());
		} else {
			KillBuffer();
		}
	}

	public void VisitInboundPackageNode(PackageNode node) {
		if (ShowInbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (ShowOutbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
		}
	}

	protected void PreprocessClassNode(ClassNode node) {
		Category.getInstance(getClass().getName()).debug("Printing class \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		Scope().add(node);
		PushNode(node);

		RaiseIndent();

		PushBuffer();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesClassNode(ClassNode node) {
		Category.getInstance(getClass().getName()).debug("Class \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		PrintDependencies(dependencies);
	}

	protected void PostprocessClassNode(ClassNode node) {
		super.PostprocessClassNode(node);
		if (ShowEmptyNodes() || !dependencies.isEmpty() || CurrentBufferLength() > 0) {
			PopBuffer(node.Name().substring(node.Name().lastIndexOf('.') + 1));
		} else {
			KillBuffer();
		}
	}
	
	public void VisitInboundClassNode(ClassNode node) {
		if (ShowInbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (ShowOutbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
		}
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		Category.getInstance(getClass().getName()).debug("Printing feature \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		Scope().add(node);
		PushNode(node);

		RaiseIndent();

		dependencies.clear();
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		Category.getInstance(getClass().getName()).debug("Feature \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		if (ShowEmptyNodes() || !dependencies.isEmpty()) {
			LowerIndent();
			if (Perl().match("/([^\\.]*\\(.*\\))$/", node.Name())) {
				Indent().Append(Perl().group(1)).Append("\n");
			} else if (Perl().match("/([^\\.]*)$/", node.Name())) {
				Indent().Append(Perl().group(1)).Append("\n");
			} else {
				Indent().Append(node.Name().substring(node.Name().lastIndexOf('.') + 1)).Append("\n");
			}
			RaiseIndent();
		}
		
		PrintDependencies(dependencies);
		super.PostprocessFeatureNode(node);
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		if (ShowInbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (ShowOutbounds()) {
			Category.getInstance(getClass().getName()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Category.getInstance(getClass().getName()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
		}
	}
	
	private void PrintDependencies(Map dependencies) {
		Iterator i = dependencies.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			if (((Integer) entry.getValue()).intValue() < 0) {
				Indent().Append("<-- ").Append(entry.getKey()).Append("\n");
			} else if (((Integer) entry.getValue()).intValue() > 0) {
				Indent().Append("--> ").Append(entry.getKey()).Append("\n");
			} else {
				Indent().Append("<-> ").Append(entry.getKey()).Append("\n");
			}
		}
	}
}
