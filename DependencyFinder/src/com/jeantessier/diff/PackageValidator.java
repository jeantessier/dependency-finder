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

import java.io.*;
import java.util.*;

import org.apache.oro.text.perl.*;

public class PackageValidator implements Validator {
	private static final Perl5Util perl = new Perl5Util();

	private Collection allowed_packages = new HashSet();

	public PackageValidator(String filename) throws IOException {
		try {
			Initialize(new BufferedReader(new InputStreamReader(new FileInputStream(filename))));
		} catch (FileNotFoundException ex) {
			// Ignore
		}
	}

	public PackageValidator(BufferedReader in) throws IOException {
		Initialize(in);
	}
	
	private void Initialize(BufferedReader in) throws IOException {
		try {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() > 0) {
					allowed_packages.add(line.trim());
				}
			}
		} catch (FileNotFoundException ex) {
			// Ignore
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public boolean IsPackageAllowed(String name) {
		return IsAllowed(name);
	}
    
	public boolean IsClassAllowed(String name) {
		String package_name = "";
		int pos = name.lastIndexOf('.');
		if (pos != -1) {
			package_name = name.substring(0, pos);
		}
		
		return IsPackageAllowed(package_name);
	}
    
	public boolean IsFeatureAllowed(String name) {
		boolean result = false;
		
		String class_name = "";
		synchronized (perl) {
			if (perl.match("/^(.+)\\.[^\\.]+\\(.*\\)$/", name)) {
				class_name = perl.group(1);
			} else if (perl.match("/^(.+)\\.[^\\.]+$/", name)) {
				class_name = perl.group(1);
			}
		}

		if (!class_name.equals("")) {
			result = IsClassAllowed(class_name);
		}

		return result;
	}

	public boolean IsAllowed(String name) {
		return allowed_packages.size() == 0 || allowed_packages.contains(name);
	}
}
