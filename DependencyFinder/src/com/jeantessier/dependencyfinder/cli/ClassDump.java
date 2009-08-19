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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import com.jeantessier.text.*;

public class ClassDump {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println(ClassDump.class.getName() + ":");
            dumpClass(ClassDump.class);
        } else {
            for (String arg : args) {
                System.out.println(arg + ":");
                dumpClass(arg);
            }
        }
    }

    public static void dumpClass(String classname) throws ClassNotFoundException  {
        dumpClass(Class.forName(classname));
    }

    public static void dumpClass(Class c) {
        String resource = c.getSimpleName() + ".class";
        System.out.println(resource + " -> " + c.getResource(resource));
        dumpClass(c.getResourceAsStream(resource));
    }

    public static void dumpClass(InputStream in) {
        dumpClass((DataInput) new DataInputStream(in));
    }

    public static void dumpClass(DataInput in) {
        int count = 0;
        try {
            while (true) {
                byte b = in.readByte();

                Hex.print(System.out, b);

                count++;

                if ((count % 4) == 0) {
                    System.out.print(' ');
                }

                if ((count % 32) == 0) {
                    System.out.println();
                    count = 0;
                }
            }
        } catch (IOException ex) {
            // Ignore
        } finally {
            System.out.println();
        }
    }
}
