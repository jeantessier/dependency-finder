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
		Logger.getLogger(getClass()).debug("Printing package \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		PushNode(node);

		RaiseIndent();

		PushBuffer();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesPackageNode(PackageNode node) {
		Logger.getLogger(getClass()).debug("Package \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
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
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		if (ShowOutbounds()) {
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
		}
	}

	protected void PreprocessClassNode(ClassNode node) {
		Logger.getLogger(getClass()).debug("Printing class \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		PushNode(node);

		RaiseIndent();

		PushBuffer();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesClassNode(ClassNode node) {
		Logger.getLogger(getClass()).debug("Class \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
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
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundClassNode(ClassNode node) {
		if (ShowOutbounds()) {
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
		}
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		Logger.getLogger(getClass()).debug("Printing feature \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		PushNode(node);

		RaiseIndent();

		dependencies.clear();
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		Logger.getLogger(getClass()).debug("Feature \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
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
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() - 1));
			} else {
				dependencies.put(node, new Integer(-1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" <-- \"" + node + "\"");
		}
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		if (ShowOutbounds()) {
			Logger.getLogger(getClass()).debug("Printing \"" + CurrentNode() + "\" --> \"" + node + "\"");
		
			Integer i = (Integer) dependencies.get(node);
			
			if (i != null) {
				dependencies.put(node, new Integer(i.intValue() + 1));
			} else {
				dependencies.put(node, new Integer(1));
			}
		} else {
			Logger.getLogger(getClass()).debug("Ignoring \"" + CurrentNode() + "\" --> \"" + node + "\"");
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
