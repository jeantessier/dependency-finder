/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import org.apache.log4j.*;

public class DirectoryClassfileLoader extends ClassfileLoaderDecorator {
	public DirectoryClassfileLoader(ClassfileLoader loader) {
		super(loader);
	}

	protected void Load(String filename) {
		try {
			DirectoryExplorer explorer = new DirectoryExplorer(filename);

			fireBeginGroup(filename, explorer.Collection().size());

			Iterator i = explorer.Collection().iterator();
			while (i.hasNext()) {
				String classfilename = (String) i.next();
				Logger.getLogger(getClass()).debug("Reading " + classfilename);
				
				DataInputStream in        = null;
				Classfile       classfile = null;
				try {
					fireBeginClassfile(classfilename, null);
					in = new DataInputStream(new FileInputStream(classfilename));
					classfile = Load(in);
				} catch (IOException ex) {
					Logger.getLogger(getClass()).error("Cannot load classfile \"" + classfilename + "\"", ex);
				} finally {
					if (in != null) {
						in.close();
					}

					fireEndClassfile(classfilename, null, classfile);
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(getClass()).error("Cannot load group \"" + filename + "\"", ex);
		} finally {
			fireEndGroup(filename);
		}
	}
}
