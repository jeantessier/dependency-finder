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

package com.jeantessier.diff;

import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

/**
 *  Documents the difference, if any, between two codebases.
 */
public class JarDifferences implements Differences {
	private String     product;
	private String     old_version;
	private String     new_version;

	private Collection package_differences = new LinkedList();

	public JarDifferences(String product, String old_version, String new_version, ClassfileLoader old_jar, ClassfileLoader new_jar) {
		this.product     = product;
		this.old_version = old_version;
		this.new_version = new_version;

		Iterator   i;

		NodeFactory old_factory = new NodeFactory();
		i = old_jar.Classfiles().iterator();
		while (i.hasNext()) {
			old_factory.CreateClass(i.next().toString());
		}

		NodeFactory new_factory = new NodeFactory();
		i = new_jar.Classfiles().iterator();
		while (i.hasNext()) {
			new_factory.CreateClass(i.next().toString());
		}

		Collection package_level = new TreeSet();
		package_level.addAll(old_factory.Packages().keySet());
		package_level.addAll(new_factory.Packages().keySet());
	
		i = package_level.iterator();
		while (i.hasNext()) {
			String package_name = (String) i.next();
	    
			PackageNode old_package = (PackageNode) old_factory.Packages().get(package_name);
			PackageNode new_package = (PackageNode) new_factory.Packages().get(package_name);
	    
			PackageDifferences differences = new PackageDifferences(package_name, old_jar, old_package, new_jar, new_package);
			if (!differences.IsEmpty()) {
				PackageDifferences().add(differences);
			}
		}
	}

	public String Product() {
		return product;
	}

	public String OldVersion() {
		return old_version;
	}

	public String NewVersion() {
		return new_version;
	}

	public Collection PackageDifferences() {
		return package_differences;
	}

	public boolean IsEmpty() {
		return PackageDifferences().size() == 0;
	}

	public void Accept(Visitor visitor) {
		visitor.VisitJarDifferences(this);
	}

	public String toString() {
		return Product();
	}
}
