/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import java.io.*;

import org.apache.logging.log4j.*;

public class StrictDispatcher implements ClassfileLoaderDispatcher {
    public ClassfileLoaderAction dispatch(String filename) {
        ClassfileLoaderAction result;
        
        if (filename.endsWith(".jar")) {
            result = ClassfileLoaderAction.JAR;
            LogManager.getLogger(getClass()).debug("Dispatching \"{}\": ACTION_JAR", filename);
        } else if (filename.endsWith(".zip")) {
            result = ClassfileLoaderAction.ZIP;
            LogManager.getLogger(getClass()).debug("Dispatching \"{}\": ACTION_ZIP", filename);
        } else if (filename.endsWith(".class")) {
            result = ClassfileLoaderAction.CLASS;
            LogManager.getLogger(getClass()).debug("Dispatching \"{}\": ACTION_CLASS", filename);
        } else if (new File(filename).exists()) {
            result = ClassfileLoaderAction.DIRECTORY;
            LogManager.getLogger(getClass()).debug("Dispatching \"{}\": ACTION_DIRECTORY", filename);
        } else {
            result = ClassfileLoaderAction.IGNORE;
            LogManager.getLogger(getClass()).debug("Dispatching \"{}\": ACTION_IGNORE", filename);
        }
        
        return result;
    }
}
