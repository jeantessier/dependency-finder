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
import java.util.zip.*;

import org.apache.log4j.*;

public class ZipClassfileLoader extends ClassfileLoaderDecorator {
	public ZipClassfileLoader(ClassfileLoader loader) {
		super(loader);
	}

	public void Load(Collection filenames) throws IOException {
		Iterator i = filenames.iterator();
		while (i.hasNext()) {
			Load((String) i.next());
		}
	}

	public void Load(String filename) throws IOException {
		if (filename.endsWith(".zip")) {
			Logger.getLogger(getClass()).debug("Reading " + filename);

			ZipFile in = null;
			try {
				in = new ZipFile(filename);
				Load(in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	protected void Load(ZipFile zipfile) {
		fireLoadStart(zipfile.getName());
		
		Enumeration entries = zipfile.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (entry.getName().endsWith(".class")) {
				Logger.getLogger(getClass()).debug("Reading " + entry.getName());
				fireLoadElement(zipfile.getName(), entry.getName());
			
				try {
					byte[] bytes = new byte[(int) entry.getSize()];
					InputStream in = zipfile.getInputStream(entry);

					int read_so_far  = 0;
					int left_to_read = (int) entry.getSize();
					while (left_to_read > 0) {
						int read_this_turn = in.read(bytes, read_so_far, left_to_read);
						read_so_far  += read_this_turn;
						left_to_read -= read_this_turn;
					}

					fireLoadedClassfile(zipfile.getName(), Load(new DataInputStream(new ByteArrayInputStream(bytes))));
				} catch (IOException ex) {
					Logger.getLogger(getClass()).debug("Error loading " + entry.getName() + ": " + ex);
				}
			}
		}

		fireLoadStop(zipfile.getName());
	}
}
