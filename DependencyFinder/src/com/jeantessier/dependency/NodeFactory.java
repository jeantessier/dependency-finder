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
import org.apache.oro.text.perl.*;

public class NodeFactory {
	private static final Perl5Util perl = new Perl5Util();

	private Map packages = new HashMap();
	private Map classes  = new HashMap();
	private Map features = new HashMap();

	public PackageNode CreatePackage(String package_name) {
		Category.getInstance(getClass().getName()).debug("CreatePackage(" + package_name + ")");
		PackageNode result = (PackageNode) packages.get(package_name);

		if (result == null) {
			result = new PackageNode(package_name);
			packages.put(package_name, result);
			Category.getInstance(getClass().getName()).debug("Added package \"" + package_name + "\"");
		}

		return result;
	}

	public Map Packages() {
		return Collections.unmodifiableMap(packages);
	}

	public ClassNode CreateClass(String class_name) {
		Category.getInstance(getClass().getName()).debug("CreateClass(" + class_name + ")");
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
			Category.getInstance(getClass().getName()).debug("Added class \"" + class_name + "\"");
		}

		return result;
	}

	public Map Classes() {
		return Collections.unmodifiableMap(classes);
	}

	public FeatureNode CreateFeature(String feature_name) {
		Category.getInstance(getClass().getName()).debug("CreateFeature(" + feature_name + ")");
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
			Category.getInstance(getClass().getName()).debug("Added feature \"" + feature_name + "\"");
		}

		return result;
	}

	public Map Features() {
		return Collections.unmodifiableMap(features);
	}
}
