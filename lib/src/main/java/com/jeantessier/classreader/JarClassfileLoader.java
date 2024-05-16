/*
 *  Copyright (c) 2001-2023, Jean Tessier
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
import java.util.jar.*;

public class JarClassfileLoader extends ZipClassfileLoader {
    public JarClassfileLoader(ClassfileLoader loader) {
        super(loader);
    }

    protected void load(String filename) {
        LogManager.getLogger(getClass()).debug("Reading {}", filename);

        try (var jarfile = new JarFile(filename)) {
            fireBeginGroup(filename, jarfile.size());
            load(jarfile);
            fireEndGroup(filename);
        } catch (IOException ex) {
            LogManager.getLogger(getClass()).error("Cannot load JAR file \"{}\"", filename, ex);
        }
    }

    protected void load(String filename, InputStream in) {
        LogManager.getLogger(getClass()).debug("Reading {}", filename);
        
        try (var jarfile = new JarInputStream(in)) {
            fireBeginGroup(filename, -1);
            load(jarfile);
            fireEndGroup(filename);
        } catch (IOException ex) {
            LogManager.getLogger(getClass()).error("Cannot load JAR file \"{}\"", filename, ex);
        }
    }

    private void load(JarFile jarfile) throws IOException {
        jarfile.stream()
                .forEach(entry -> {
                    fireBeginFile(entry.getName());

                    LogManager.getLogger(getClass()).debug("Starting file {} ({} bytes)", entry.getName(), entry.getSize());
                    try (InputStream in = jarfile.getInputStream(entry)) {
                        var bytes = in.readAllBytes();

                        LogManager.getLogger(getClass()).debug("Passing up file {} ({} bytes)", entry.getName(), bytes.length);
                        getLoader().load(entry.getName(), new ByteArrayInputStream(bytes));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    fireEndFile(entry.getName());
                });
    }

    private void load(JarInputStream in) throws IOException {
        JarEntry entry;
        while ((entry = in.getNextJarEntry()) != null) {
            fireBeginFile(entry.getName());

            LogManager.getLogger(getClass()).debug("Starting file {} ({} bytes)", entry.getName(), entry.getSize());
            var bytes = in.readAllBytes();

            LogManager.getLogger(getClass()).debug("Passing up file {} ({} bytes)", entry.getName(), bytes.length);
            getLoader().load(entry.getName(), new ByteArrayInputStream(bytes));

            fireEndFile(entry.getName());
        }
    }
}
