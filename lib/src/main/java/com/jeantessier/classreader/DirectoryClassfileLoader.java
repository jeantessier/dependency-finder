 /*
 *  Copyright (c) 2001-2024, Jean Tessier
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

 import org.apache.logging.log4j.*;

 import java.io.*;

public class DirectoryClassfileLoader extends ClassfileLoaderDecorator {
    public DirectoryClassfileLoader(ClassfileLoader loader) {
        super(loader);
    }

    protected void load(String filename) {
        LogManager.getLogger(getClass()).debug("Starting group from path \"{}\"", filename);
        
        DirectoryExplorer explorer = new DirectoryExplorer(filename);

        fireBeginGroup(filename, explorer.getFiles().size());

        explorer.getFiles().forEach(file -> {
            fireBeginFile(file.getPath());

            LogManager.getLogger(getClass()).debug("Starting file \"{}\" ({} bytes)", file.getPath(), file.length());

            if (!file.isDirectory()) {
                // Errors with contents format will be handled and logged by Load().
                try (var in = new FileInputStream(file)) {
                    getLoader().load(file.getPath(), in);
                } catch (IOException ex) {
                    LogManager.getLogger(getClass()).error("Cannot load file \"{}\"", file.getPath(), ex);
                }
            }

            fireEndFile(file.getPath());
        });

        fireEndGroup(filename);
    }
    
    protected void load(String filename, InputStream in) {
        // Do nothing
    }
}
