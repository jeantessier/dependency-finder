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

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

public class TextPrinter extends Printer {
	private static final Perl5Util perl = new Perl5Util();

	protected static Perl5Util Perl() {
		return perl;
	}

	private Map dependencies = new TreeMap();

	public TextPrinter(PrintWriter out) {
		super(out);
	}

	public TextPrinter(TraversalStrategy strategy, PrintWriter out) {
		super(strategy, out);
	}
	
	protected void PreprocessPackageNode(PackageNode node) {
		Logger.getLogger(getClass()).debug("Printing package \"" + node + "\" and its " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds");
		
		super.PreprocessPackageNode(node);

		RaiseIndent();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesPackageNode(PackageNode node) {
		Logger.getLogger(getClass()).debug("Package \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		if (ShowPackageNode(node) || !dependencies.isEmpty()) {
			LowerIndent();
			Indent().Append(node.Name()).EOL();
			RaiseIndent();
		}
		
		PrintDependencies(dependencies);
	}
	
	protected void PostprocessPackageNode(PackageNode node) {
		LowerIndent();

		super.PostprocessPackageNode(node);
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
		
		super.PreprocessClassNode(node);

		RaiseIndent();

		dependencies.clear();
	}

	protected void PreprocessAfterDependenciesClassNode(ClassNode node) {
		Logger.getLogger(getClass()).debug("Class \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		if (ShowClassNode(node) || !dependencies.isEmpty()) {
			LowerIndent();
			Indent().Append(node.Name().substring(node.Name().lastIndexOf('.') + 1)).EOL();
			RaiseIndent();
		}

		PrintDependencies(dependencies);
	}

	protected void PostprocessClassNode(ClassNode node) {
		LowerIndent();

		super.PostprocessClassNode(node);
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
		
		super.PreprocessFeatureNode(node);

		RaiseIndent();

		dependencies.clear();
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		Logger.getLogger(getClass()).debug("Feature \"" + node + "\" with " + node.Inbound().size() + " inbounds and " + node.Outbound().size() + " outbounds had " + dependencies.size() + " dependencies.");
		
		if (ShowFeatureNode(node) || !dependencies.isEmpty()) {
			LowerIndent();
			if (Perl().match("/([^\\.]*\\(.*\\))$/", node.Name())) {
				Indent().Append(Perl().group(1)).EOL();
			} else if (Perl().match("/([^\\.]*)$/", node.Name())) {
				Indent().Append(Perl().group(1)).EOL();
			} else {
				Indent().Append(node.Name().substring(node.Name().lastIndexOf('.') + 1)).EOL();
			}
			RaiseIndent();
		}
		
		PrintDependencies(dependencies);

		LowerIndent();

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
				Indent().Append("<-- ").Append(entry.getKey()).EOL();
			} else if (((Integer) entry.getValue()).intValue() > 0) {
				Indent().Append("--> ").Append(entry.getKey()).EOL();
			} else {
				Indent().Append("<-> ").Append(entry.getKey()).EOL();
			}
		}
	}
}
