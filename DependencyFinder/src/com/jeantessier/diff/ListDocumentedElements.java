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

package com.jeantessier.diff;

import java.io.*;
import java.util.*;

import com.sun.javadoc.*;

public class ListDocumentedElements {
    private static String tagName = null;
    private static Collection<String> validValues = new HashSet<String>();
    private static Collection<String> invalidValues = new HashSet<String>();
    private static PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
    
    public static boolean start(RootDoc root) {
        process(root.specifiedPackages());
        process(root.classes());
        out.close();
        return true;
    }

    public static int optionLength(String option) {
        int result = 0;

        if (option.equals("-tag")) {
            result = 2;
        } else if (option.equals("-valid")) {
            result = 2;
        } else if (option.equals("-invalid")) {
            result = 2;
        } else if (option.equals("-out")) {
            result = 2;
        }

        return result;
    }

    public static boolean validOptions(String options[][], DocErrorReporter reporter) {
        boolean valid = true;
        
        for (int i=0; valid && i<options.length; i++) {
            if (options[i][0].equals("-tag")) {
                if (tagName == null) {
                    tagName = options[i][1];
                } else {
                    reporter.printError("Only one -tag option allowed.");
                    valid = false;
                }
            } else if (options[i][0].equals("-valid")) {
                validValues.add(options[i][1]);
            } else if (options[i][0].equals("-invalid")) {
                invalidValues.add(options[i][1]);
            } else if (options[i][0].equals("-out")) {
                try {
                    out = new PrintWriter(new FileWriter(options[i][1]));
                } catch (IOException ex) {
                    reporter.printError("Could not open output file \"" + options[i][1] + "\": " + ex);
                    valid = false;
                }
            }
        }

        valid = valid && tagName != null;
        
        if (!valid) {
            reporter.printError("Usage: javadoc -tag mytag [-valid value]* [-invalid value]* -doclet ListPublicElements ...");
        }
        
        return valid;
    }
    
    private static void process(PackageDoc[] docs) {
        for (PackageDoc doc : docs) {
            process(doc);
        }
    }
    
    private static void process(PackageDoc doc) {
        out.print(doc.name());
        out.println(" [P]");
    }
    
    private static void process(ProgramElementDoc[] docs) {
        for (ProgramElementDoc doc : docs) {
            process(doc);
        }
    }

    private static void process(ProgramElementDoc doc) {
        boolean isVisible = !doc.name().equals("<clinit>");

        Tag[] tags = doc.tags(tagName);

        if (isVisible) {
            if (!validValues.isEmpty()) {
                // If it contains at least one valid value, then it will be visible
                isVisible = false;
                for (Tag tag : tags) {
                    if (validValues.contains(tag.text())) {
                        isVisible = true;
                    }
                }
            } else if (!invalidValues.isEmpty()) {
                // Else if it contains at least one invalid value, then it will not be visible
                for (Tag tag : tags) {
                    if (invalidValues.contains(tag.text())) {
                        isVisible = false;
                    }
                }
            }
        }

        if (isVisible) {
            out.print(doc.qualifiedName());
            if (doc instanceof ConstructorDoc) {
                out.print(".");
                out.print(doc.name());
            }
            if (doc instanceof ExecutableMemberDoc) {
                out.print(((ExecutableMemberDoc) doc).signature());
            }

            if (doc instanceof ClassDoc) {
                out.println(" [C]");
                process(((ClassDoc) doc).fields());
                process(((ClassDoc) doc).constructors());
                process(((ClassDoc) doc).methods());
                process(((ClassDoc) doc).innerClasses());
            } else {
                out.println(" [F]");
            }
        }
    }
}
