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
