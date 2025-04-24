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

import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;

public class JarClassfileLoader extends ClassfileLoaderDecorator {
    public static final Pattern VERSION_REGEX = Pattern.compile("META-INF/versions/(\\d+)/(.*)");

    private final int targetJdk;

    public JarClassfileLoader(ClassfileLoader loader) {
        this(loader, Integer.MAX_VALUE);
    }

    public JarClassfileLoader(ClassfileLoader loader, int targetJdk) {
        super(loader);

        this.targetJdk = targetJdk;
    }

    protected void load(String filename) {
        LogManager.getLogger(getClass()).debug("Reading file {}", filename);

        try (var jarfile = new JarFile(filename)) {
            fireBeginGroup(filename, jarfile.size());
            load(jarfile);
            fireEndGroup(filename);
        } catch (IOException ex) {
            LogManager.getLogger(getClass()).error("Cannot load JAR file \"{}\"", filename, ex);
        }
    }

    protected void load(String filename, InputStream in) {
        LogManager.getLogger(getClass()).debug("Reading input stream {}", filename);

        var basename = new File(filename).getName();
        if (basename.endsWith(".jar")) {
            basename = basename.substring(0, basename.length() - 4);
        }

        try {
            var tempFile = File.createTempFile(basename, ".jar");
            tempFile.deleteOnExit();

            try (var out = new FileOutputStream(tempFile)) {
                LogManager.getLogger(getClass()).debug("Reading JAR file {} from input stream", filename);
                var bytes = in.readAllBytes();
                LogManager.getLogger(getClass()).debug("Writing JAR file {} ({} bytes) to temporary file {}", filename, bytes.length, tempFile);
                out.write(bytes);
            } catch (IOException ex) {
                LogManager.getLogger(getClass()).error("Cannot load JAR file from input stream", ex);
            }

            load(tempFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load(JarFile jarfile) throws IOException {
        var seen = new HashSet<String>();

        jarfile.stream()
                .sorted(this::compareJarEntries)
                .forEach(entry -> {
                    fireBeginFile(entry.getName());

                    try (InputStream in = jarfile.getInputStream(entry)) {
                        var filename = getFilename(entry, seen);

                        if (filename != null && !seen.contains(filename)) {
                            seen.add(filename);

                            LogManager.getLogger(getClass()).debug("Reading JAR entry {} ({} bytes)", entry.getName(), entry.getSize());
                            var bytes = in.readAllBytes();

                            LogManager.getLogger(getClass()).debug("Passing up JAR entry {} ({} bytes)", entry.getName(), bytes.length);
                            getLoader().load(entry.getName(), new ByteArrayInputStream(bytes));
                        } else {
                            LogManager.getLogger(getClass()).debug("Skipping JAR entry {} ({} bytes)", entry.getName(), entry.getSize());
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    fireEndFile(entry.getName());
                });
    }

    private int compareJarEntries(JarEntry a, JarEntry b) {
        var matchA = VERSION_REGEX.matcher(a.getName());
        var matchB = VERSION_REGEX.matcher(b.getName());

        // Version folders are sorted largest version first
        if (matchA.matches() && matchB.matches() && !matchA.group(1).equals(matchB.group(1))) {
            return Integer.valueOf(matchB.group(1)).compareTo(Integer.valueOf(matchA.group(1)));
        }

        // Files in META-INF come before other files
        if (a.getName().startsWith("META-INF") && !b.getName().startsWith("META-INF")) {
            return -1;
        }

        if (!a.getName().startsWith("META-INF") && b.getName().startsWith("META-INF")) {
            return 1;
        }

        // All else being equal, sort alphabetically
        return a.getName().compareTo(b.getName());
    }

    private String getFilename(JarEntry entry, Set<String> seen) {
        var match = VERSION_REGEX.matcher(entry.getName());
        if (match.matches()) {
            if (seen.contains(match.group(2))) {
                return null;
            } else if (targetJdk < Integer.parseInt(match.group(1))) {
                return null;
            } else {
                return match.group(2);
            }
        }

        return entry.getName();
    }
}
