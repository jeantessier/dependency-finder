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
import java.util.*;
import java.util.zip.*;

import org.apache.log4j.*;

public class ZipClassfileLoader extends ClassfileLoaderDecorator {
    private static final int BUFFER_SIZE = 16 * 1024;
    
    public ZipClassfileLoader(ClassfileLoader loader) {
        super(loader);
    }

    protected void load(String filename) {
        Logger.getLogger(getClass()).debug("Starting group in file " + filename);
        
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(filename);

            fireBeginGroup(filename, zipfile.size());

            Logger.getLogger(getClass()).debug("Loading ZipFile " + filename);
            load(zipfile);
            Logger.getLogger(getClass()).debug("Loaded ZipFile " + filename);

            fireEndGroup(filename);
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Cannot load Zip file \"" + filename + "\"", ex);
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        }
    }

    protected void load(String filename, InputStream in) {
        Logger.getLogger(getClass()).debug("Starting group in stream " + filename);
        
        ZipInputStream zipfile = null;
        try {
            zipfile = new ZipInputStream(in);

            fireBeginGroup(filename, -1);

            Logger.getLogger(getClass()).debug("Loading ZipInputStream " + filename);
            load(zipfile);
            Logger.getLogger(getClass()).debug("Loaded ZipInputStream " + filename);

            fireEndGroup(filename);
        } catch (IOException ex) {
            Logger.getLogger(getClass()).error("Cannot load Zip file \"" + filename + "\"", ex);
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
        }
    }

    protected void load(ZipFile zipfile) throws IOException {
        Enumeration entries = zipfile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            fireBeginFile(entry.getName());
                
            Logger.getLogger(getClass()).debug("Starting file " + entry.getName() + " (" + entry.getSize() + " bytes)");

            byte[]      bytes = null;
            InputStream in    = null;
            try {
                in    = zipfile.getInputStream(entry);
                bytes = readBytes(in);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        // Ignore
                    }
                }
            }
            
            Logger.getLogger(getClass()).debug("Passing up file " + entry.getName() + " (" + bytes.length + " bytes)");
            getLoader().load(entry.getName(), new ByteArrayInputStream(bytes));
            
            fireEndFile(entry.getName());
        }
    }

    protected void load(ZipInputStream in) throws IOException {
        ZipEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            fireBeginFile(entry.getName());
                
            Logger.getLogger(getClass()).debug("Starting file " + entry.getName() + " (" + entry.getSize() + " bytes)");
            byte[] bytes = readBytes(in);
            
            Logger.getLogger(getClass()).debug("Passing up file " + entry.getName() + " (" + bytes.length + " bytes)");
            getLoader().load(entry.getName(), new ByteArrayInputStream(bytes));
            
            fireEndFile(entry.getName());
        }
    }

    private byte[] readBytes(InputStream in) {
        byte[] result = null;
        
        try {
            ByteArrayOutputStream out        = new ByteArrayOutputStream();
            byte[]                buffer     = new byte[BUFFER_SIZE];
            int                   bytesRead = 0;
            while ((bytesRead = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
                
            result = out.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(getClass()).debug("Error loading Zip entry", ex);
        }
        
        return(result);
    }
}
