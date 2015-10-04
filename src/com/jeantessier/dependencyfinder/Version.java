/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

import org.apache.log4j.*;

public class Version {
    public static final String DEFAULT_URL              = "http://depfind.sourceforge.net/";
    public static final String DEFAULT_TITLE            = "Dependency Finder";
    public static final String DEFAULT_VERSION          = "<i>unknown</i>";
    public static final String DEFAULT_VENDOR           = "Jean Tessier";
    public static final String DEFAULT_DATE             = "<i>unknown</i>";
    public static final String DEFAULT_COPYRIGHT_HOLDER = "Jean Tessier";
    public static final String DEFAULT_COPYRIGHT_DATE   = "2001-2009";

    private String resourceURL = null;
    private String jarName     = null;

    private Attributes attributes = null;
    
    public Version() {
        resourceURL = getClass().getResource("Version.class").toString();
        
        if (resourceURL.startsWith("jar:file:")) {
            jarName = resourceURL.substring(9, resourceURL.indexOf(".jar!") + 4);

            try {
                JarFile  jar      = new JarFile(jarName);
                Manifest manifest = jar.getManifest();
                
                attributes = manifest.getMainAttributes();
            } catch (IOException ex) {
                Logger.getLogger(getClass()).error("Could not get version information, using defaults", ex);
            }
        }
    }
    
    public String getResourceURL() {
        return resourceURL;
    }

    public String getJarName() {
        return jarName;
    }

    public String getImplementationURL() {
        String result = DEFAULT_URL;

        if (attributes != null) {
            result = attributes.getValue("Implementation-URL");
        }

        return result;
    }

    public String getImplementationTitle() {
        String result = DEFAULT_TITLE;

        if (attributes != null) {
            result = attributes.getValue("Implementation-Title");
        }

        return result;
    }

    public String getImplementationVersion() {
        String result = DEFAULT_VERSION;

        if (attributes != null) {
            result = attributes.getValue("Implementation-Version");
        }

        return result;
    }

    public String getImplementationVendor() {
        String result = DEFAULT_VENDOR;

        if (attributes != null) {
            result = attributes.getValue("Implementation-Vendor");
        }

        return result;
    }

    public String getImplementationDate() {
        String result = DEFAULT_DATE;

        if (attributes != null) {
            result = attributes.getValue("Implementation-Date");
        }

        return result;
    }

    public String getSpecificationTitle() {
        String result = DEFAULT_TITLE;

        if (attributes != null) {
            result = attributes.getValue("Specification-Title");
        }

        return result;
    }

    public String getSpecificationVersion() {
        String result = DEFAULT_VERSION;

        if (attributes != null) {
            result = attributes.getValue("Specification-Version");
        }

        return result;
    }

    public String getSpecificationVendor() {
        String result = DEFAULT_VENDOR;

        if (attributes != null) {
            result = attributes.getValue("Specification-Vendor");
        }

        return result;
    }

    public String getSpecificationDate() {
        String result = DEFAULT_DATE;

        if (attributes != null) {
            result = attributes.getValue("Specification-Date");
        }

        return result;
    }

    public String getCopyrightHolder() {
        String result = DEFAULT_COPYRIGHT_HOLDER;

        if (attributes != null) {
            result = attributes.getValue("Copyright-Holder");
        }

        return result;
    }

    public String getCopyrightDate() {
        String result = DEFAULT_COPYRIGHT_DATE;

        if (attributes != null) {
            result = attributes.getValue("Copyright-Date");
        }

        return result;
    }
}
