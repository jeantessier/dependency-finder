/*
 *  Copyright (c) 2001-2005, Jean Tessier
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
	private Collection allowedElements = new HashSet();

	public ListBasedValidator() {
		// Do nothing
	}

	public ListBasedValidator(String filename) throws IOException {
		load(filename);
	}

	public ListBasedValidator(File file) throws IOException {
		load(file);
	}

	public ListBasedValidator(BufferedReader in) throws IOException {
		load(in);
	}
	
	public void load(String filename) throws IOException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(filename));
			load(in);
		} catch (FileNotFoundException ex) {
			// Ignore
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
		
	public void load(File file) throws IOException {
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(file));
			load(in);
		} catch (FileNotFoundException ex) {
			// Ignore
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public void load(BufferedReader in) throws IOException {
		String line;
		while ((line = in.readLine()) != null) {
			if (line.length() > 0) {
				line = line.trim();
				int pos = line.lastIndexOf(" [");
				if (pos != -1) {
					allowedElements.add(line.substring(0, pos));
				} else {
					allowedElements.add(line);
				}
			}
		}
	}
	
	public boolean isPackageAllowed(String name) {
		return isAllowed(name);
	}

	public boolean isClassAllowed(String name) {
		return isAllowed(name);
	}

	public boolean isFeatureAllowed(String name) {
		return isAllowed(name);
	}

	public boolean isAllowed(String name) {
		Logger.getLogger(getClass()).debug("IsAllowed(\"" + name + "\")");
		Logger.getLogger(getClass()).debug("allowed_elements.size() = " + allowedElements.size());
		Logger.getLogger(getClass()).debug("allowed_elements.contains(\"" + name + "\") = " + allowedElements.contains(name));
		return allowedElements.size() == 0 || allowedElements.contains(name);
	}
}
