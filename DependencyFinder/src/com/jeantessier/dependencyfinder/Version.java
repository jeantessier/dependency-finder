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

package com.jeantessier.dependencyfinder;

import java.io.*;
import java.util.jar.*;

public class Version {
	public static final String DEFAULT_URL              = "http://depfind.sourceforge.net/";
	public static final String DEFAULT_TITLE            = "Dependency Finder";
	public static final String DEFAULT_VERSION          = "<i>unknown</i>";
	public static final String DEFAULT_VENDOR           = "Jean Tessier";
	public static final String DEFAULT_DATE             = "<i>unknown</i>";
	public static final String DEFAULT_COPYRIGHT_HOLDER = "Jean Tessier";
	public static final String DEFAULT_COPYRIGHT_DATE   = "2001-2003";

    private String resource_url = null;
	private String jar_name     = null;

	private Attributes attributes = null;
	
	public Version() throws IOException {
		resource_url = getClass().getResource("Version.class").toString();
		
		if (resource_url.startsWith("jar:file:")) {
			jar_name = resource_url.substring(9, resource_url.indexOf(".jar!") + 4);
			
			JarFile  jar      = new JarFile(jar_name);
			Manifest manifest = jar.getManifest();
			
			attributes = manifest.getMainAttributes();
		}
	}
	
	public String ResourceURL() {
		return resource_url;
	}

	public String JarName() {
		return jar_name;
	}

	public String ImplementationURL() {
		String result = DEFAULT_URL;

		if (attributes != null) {
			result = attributes.getValue("Implementation-URL");
		}

		return result;
	}

	public String ImplementationTitle() {
		String result = DEFAULT_TITLE;

		if (attributes != null) {
			result = attributes.getValue("Implementation-Title");
		}

		return result;
	}

	public String ImplementationVersion() {
		String result = DEFAULT_VERSION;

		if (attributes != null) {
			result = attributes.getValue("Implementation-Version");
		}

		return result;
	}

	public String ImplementationVendor() {
		String result = DEFAULT_VENDOR;

		if (attributes != null) {
			result = attributes.getValue("Implementation-Vendor");
		}

		return result;
	}

	public String ImplementationDate() {
		String result = DEFAULT_DATE;

		if (attributes != null) {
			result = attributes.getValue("Implementation-Date");
		}

		return result;
	}

	public String SpecificationTitle() {
		String result = DEFAULT_TITLE;

		if (attributes != null) {
			result = attributes.getValue("Specification-Title");
		}

		return result;
	}

	public String SpecificationVersion() {
		String result = DEFAULT_VERSION;

		if (attributes != null) {
			result = attributes.getValue("Specification-Version");
		}

		return result;
	}

	public String SpecificationVendor() {
		String result = DEFAULT_VENDOR;

		if (attributes != null) {
			result = attributes.getValue("Specification-Vendor");
		}

		return result;
	}

	public String SpecificationDate() {
		String result = DEFAULT_DATE;

		if (attributes != null) {
			result = attributes.getValue("Specification-Date");
		}

		return result;
	}

	public String CopyrightHolder() {
		String result = DEFAULT_COPYRIGHT_HOLDER;

		if (attributes != null) {
			result = attributes.getValue("Copyright-Holder");
		}

		return result;
	}

	public String CopyrightDate() {
		String result = DEFAULT_COPYRIGHT_DATE;

		if (attributes != null) {
			result = attributes.getValue("Copyright-Date");
		}

		return result;
	}
}
