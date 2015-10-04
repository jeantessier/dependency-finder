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

package com.jeantessier.classreader;

 import java.io.*;

 import org.apache.log4j.*;

public class DirectoryClassfileLoader extends ClassfileLoaderDecorator {
    public DirectoryClassfileLoader(ClassfileLoader loader) {
        super(loader);
    }

    protected void load(String filename) {
        Logger.getLogger(getClass()).debug("Starting group from path \"" + filename + "\"");
        
        try {
            DirectoryExplorer explorer = new DirectoryExplorer(filename);

            fireBeginGroup(filename, explorer.getFiles().size());

            for (File file : explorer.getFiles()) {
                fireBeginFile(file.getPath());

                Logger.getLogger(getClass()).debug("Starting file \"" + file.getPath() + "\" (" + file.length() + " bytes)");

                if (!file.isDirectory()) {
                    // No need to close "in" in finally block.  Only problems can
                    // be with opening "file".
                    // Errors with contents format will be handled and logged by Load().
                    try {
                        InputStream in = new FileInputStream(file);
                        getLoader().load(file.getPath(), in);
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(getClass()).error("Cannot load file \"" + file.getPath() + "\"", ex);
                    }
                }

                fireEndFile(file.getPath());
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Cannot load group \"" + filename + "\"", ex);
        } finally {
            fireEndGroup(filename);
        }
    }
    
    protected void load(String filename, InputStream in) {
        // Do nothing
    }
}
