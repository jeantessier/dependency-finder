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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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
import org.apache.oro.text.perl.*;

public class NodeFactory {
	private static final Perl5Util perl = new Perl5Util();

	private Map packages = new HashMap();
	private Map classes  = new HashMap();
	private Map features = new HashMap();

	public PackageNode CreatePackage(String package_name) {
		Logger.getLogger(getClass()).debug("CreatePackage(" + package_name + ")");
		PackageNode result = (PackageNode) packages.get(package_name);

		if (result == null) {
			result = new PackageNode(package_name);
			packages.put(package_name, result);
			Logger.getLogger(getClass()).debug("Added package \"" + package_name + "\"");
		}

		return result;
	}

	public Map Packages() {
		return Collections.unmodifiableMap(packages);
	}

	public ClassNode CreateClass(String class_name) {
		Logger.getLogger(getClass()).debug("CreateClass(" + class_name + ")");
		ClassNode result = (ClassNode) classes.get(class_name);

		if (result == null) {
			String package_name = "";
			int pos = class_name.lastIndexOf('.');
			if (pos != -1) {
				package_name = class_name.substring(0, pos);
			}
			PackageNode parent = CreatePackage(package_name);
			result = new ClassNode(parent, class_name);
			parent.AddClass(result);
			classes.put(class_name, result);
			Logger.getLogger(getClass()).debug("Added class \"" + class_name + "\"");
		}

		return result;
	}

	public Map Classes() {
		return Collections.unmodifiableMap(classes);
	}

	public FeatureNode CreateFeature(String feature_name) {
		Logger.getLogger(getClass()).debug("CreateFeature(" + feature_name + ")");
		FeatureNode result = (FeatureNode) features.get(feature_name);

		if (result == null) {
			String parent_name = null;

			if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)$/", feature_name)) {
				parent_name = perl.group(1);
			} else if (perl.match("/^(.*)\\.[^\\.]*$/", feature_name)) {
				parent_name = perl.group(1);
			} else {
				parent_name = "";
			}

			ClassNode parent = CreateClass(parent_name);
			result = new FeatureNode(parent, feature_name);
			parent.AddFeature(result);
			features.put(feature_name, result);
			Logger.getLogger(getClass()).debug("Added feature \"" + feature_name + "\"");
		}

		return result;
	}

	public Map Features() {
		return Collections.unmodifiableMap(features);
	}
}
