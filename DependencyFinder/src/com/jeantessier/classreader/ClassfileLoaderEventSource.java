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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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

public abstract class ClassfileLoaderEventSource extends ClassfileLoader {
	private HashSet load_listeners = new HashSet();

	public void addLoadListener(LoadListener listener) {
		synchronized(load_listeners) {
			load_listeners.add(listener);
		}
	}

	public void removeLoadListener(LoadListener listener) {
		synchronized(load_listeners) {
			load_listeners.remove(listener);
		}
	}

	protected void fireLoadStart(String filename) {
		LoadEvent event = new LoadEvent(this, filename);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).LoadStart(event);
		}
	}
	
	protected void fireLoadElement(String filename, String element) {
		LoadEvent event = new LoadEvent(this, filename, element);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).LoadElement(event);
		}
	}

	protected void fireLoadedClassfile(String filename, Classfile classfile) {
		LoadEvent event = new LoadEvent(this, filename, classfile);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).LoadedClassfile(event);
		}
	}

	protected void fireLoadStop(String filename) {
		LoadEvent event = new LoadEvent(this, filename);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).LoadStop(event);
		}
	}
}
