/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

package com.jeantessier.classreader;

import java.util.*;

public class PackageMapper implements LoadListener {
    private Map map = new HashMap();

    public Collection getPackageNames() {
        return map.keySet();
    }

    public Map getPackage(String packageName) {
        return (Map) map.get(packageName);
    }

    public void beginSession(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void beginGroup(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void beginFile(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void beginClassfile(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void endClassfile(LoadEvent event) {
        Classfile classfile = event.getClassfile();

        String packageName = "";
        int pos = classfile.getClassName().lastIndexOf(".");
        if (pos != -1) {
            packageName = classfile.getClassName().substring(0, pos);
        }

        Map map = (Map) this.map.get(packageName);
        if (map == null) {
            map = new HashMap();
            this.map.put(packageName, map);
        }

        map.put(classfile.getClassName(), classfile);
    }

    public void endFile(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void endGroup(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void endSession(LoadEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
