/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

import org.apache.log4j.*;

public class ListBasedValidator implements Validator {
	private Collection allowed_elements = new HashSet();

	public ListBasedValidator() {
		// Do nothing
	}

	public ListBasedValidator(String filename) throws IOException {
		Load(filename);
	}

	public ListBasedValidator(File file) throws IOException {
		Load(file);
	}

	public ListBasedValidator(BufferedReader in) throws IOException {
		Load(in);
	}
	
	public void Load(String filename) throws IOException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(filename));
			Load(in);
		} catch (FileNotFoundException ex) {
			// Ignore
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
		
	public void Load(File file) throws IOException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			Load(in);
		} catch (FileNotFoundException ex) {
			// Ignore
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public void Load(BufferedReader in) throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				line = line.trim();
				int pos = line.lastIndexOf(" [");
				if (pos != -1) {
					allowed_elements.add(line.substring(0, pos));
				} else {
					allowed_elements.add(line);
				}
			}
		}
	}
	
	public boolean IsPackageAllowed(String name) {
		return IsAllowed(name);
	}

	public boolean IsClassAllowed(String name) {
		return IsAllowed(name);
	}

	public boolean IsFeatureAllowed(String name) {
		return IsAllowed(name);
	}

	public boolean IsAllowed(String name) {
		Logger.getLogger(getClass()).debug("IsAllowed(\"" + name + "\")");
		Logger.getLogger(getClass()).debug("allowed_elements.size() = " + allowed_elements.size());
		Logger.getLogger(getClass()).debug("allowed_elements.contains(\"" + name + "\") = " + allowed_elements.contains(name));
		return allowed_elements.size() == 0 || allowed_elements.contains(name);
	}
}
