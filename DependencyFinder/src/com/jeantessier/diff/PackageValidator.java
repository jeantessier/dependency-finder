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

import java.io.*;
import java.util.*;

public class PackageValidator {
	private Collection allowed_packages = new HashSet();

	public PackageValidator(String filename) throws IOException {
		BufferedReader in = null;

		// System.err.println("PackageValidator.PackageValidator(" + filename + ")");
		// System.out.println("PackageValidator.PackageValidator(" + filename + ")");

		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() > 0) {
					allowed_packages.add(line.trim());
					// System.out.println("\t\"" + line.trim() + "\"");
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public boolean IsPackageAllowed(String package_name) {
		boolean result = allowed_packages.size() == 0 || allowed_packages.contains(package_name);
	
		// System.err.println("PackageValidator.IsPackageAllowed(" + package_name + "): " + result);
		// System.out.println("PackageValidator.IsPackageAllowed(" + package_name + "): " + result);
		return result;
	}
    
	public boolean IsClassAllowed(String class_name) {
		boolean result = false;
	
		if (class_name.lastIndexOf(".") != -1) {
			result = IsPackageAllowed(class_name.substring(0, class_name.lastIndexOf(".")));
		}

		// System.err.println("PackageValidator.IsClassAllowed(" + class_name + "): " + result);
		// System.out.println("PackageValidator.IsClassAllowed(" + class_name + "): " + result);
		return result;
	}
    
	public static void main(String[] args) throws Exception {
		PackageValidator pv = new PackageValidator(args[0]);
		System.out.println("IsPackageAllowed(): " + pv.IsPackageAllowed(args[1]));
		System.out.println("IsClassAllowed(): " + pv.IsClassAllowed(args[1]));
	}
}
