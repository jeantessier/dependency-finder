/*
 *  Dependency Finder - Comparing API differences between JAR files
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

package com.jeantessier.diff;

import java.util.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class JarDifferences implements Differences {
	private String     old_version;
	private String     new_version;

	private Collection package_differences = new LinkedList();

	public JarDifferences(String old_version, String new_version) {
		this.old_version = old_version;
		this.new_version = new_version;
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

	public boolean Compare(ClassfileLoader old_jar, ClassfileLoader new_jar) {
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
	    
			PackageDifferences differences = new PackageDifferences(package_name);
			if (differences.Compare(old_jar, old_package, new_jar, new_package)) {
				PackageDifferences().add(differences);
			}
		}
    
		return !IsEmpty();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitJarDifferences(this);
	}
}
