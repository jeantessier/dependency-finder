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

import java.beans.*;

public class VersionBeanInfo extends SimpleBeanInfo {
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] result = new PropertyDescriptor[13];
        
        try {
            result[0]  = new PropertyDescriptor("ResourceURL",           Version.class, "getResourceURL",           null);
            result[1]  = new PropertyDescriptor("JarName",               Version.class, "getJarName",               null);
            result[2]  = new PropertyDescriptor("ImplementationURL",     Version.class, "getImplementationURL",     null);
            result[3]  = new PropertyDescriptor("ImplementationTitle",   Version.class, "getImplementationTitle",   null);
            result[4]  = new PropertyDescriptor("ImplementationVersion", Version.class, "getImplementationVersion", null);
            result[5]  = new PropertyDescriptor("ImplementationVendor",  Version.class, "getImplementationVendor",  null);
            result[6]  = new PropertyDescriptor("ImplementationDate",    Version.class, "getImplementationDate",    null);
            result[7]  = new PropertyDescriptor("SpecificationTitle",    Version.class, "getSpecificationTitle",    null);
            result[8]  = new PropertyDescriptor("SpecificationVersion",  Version.class, "getSpecificationVersion",  null);
            result[9]  = new PropertyDescriptor("SpecificationVendor",   Version.class, "getSpecificationVendor",   null);
            result[10] = new PropertyDescriptor("SpecificationDate",     Version.class, "getSpecificationDate",     null);
            result[11] = new PropertyDescriptor("CopyrightHolder",       Version.class, "getCopyrightHolder",       null);
            result[12] = new PropertyDescriptor("CopyrightDate",         Version.class, "getCopyrightDate",         null);
        } catch (IntrospectionException ex) {
            result = null;
        }

        return result;
    }
}

