/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

public abstract class ClassfileLoaderEventSource extends ClassfileLoader {
	private static final ClassfileLoaderDispatcher DEFAULT_DISPATCHER = new PermissiveDispatcher();

	private ClassfileLoaderDispatcher dispatcher;
	
	private ClassfileLoader dir_loader = new DirectoryClassfileLoader(this);
	private ClassfileLoader jar_loader = new JarClassfileLoader(this);
	private ClassfileLoader zip_loader = new ZipClassfileLoader(this);

	private HashSet    load_listeners = new HashSet();

	private LinkedList group_names = new LinkedList();

	public ClassfileLoaderEventSource() {
		this(DEFAULT_DISPATCHER);
	}

	public ClassfileLoaderEventSource(ClassfileLoaderDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	protected void Load(String filename) {
		switch (dispatcher.Dispatch(filename)) {
			case ClassfileLoaderDispatcher.ACTION_IGNORE:
				break;

			case ClassfileLoaderDispatcher.ACTION_CLASS:
			case ClassfileLoaderDispatcher.ACTION_DIRECTORY:
				dir_loader.Load(filename);
				break;

			case ClassfileLoaderDispatcher.ACTION_ZIP:
				zip_loader.Load(filename);
				break;

			case ClassfileLoaderDispatcher.ACTION_JAR:
				jar_loader.Load(filename);
				break;

			default:
				break;
		}
	}

	protected void Load(String filename, InputStream in) {
		switch (dispatcher.Dispatch(filename)) {
			case ClassfileLoaderDispatcher.ACTION_IGNORE:
				break;

			case ClassfileLoaderDispatcher.ACTION_DIRECTORY:
				dir_loader.Load(filename, in);
				break;

			case ClassfileLoaderDispatcher.ACTION_ZIP:
				zip_loader.Load(filename, in);
				break;

			case ClassfileLoaderDispatcher.ACTION_JAR:
				jar_loader.Load(filename, in);
				break;

			case ClassfileLoaderDispatcher.ACTION_CLASS:
				try {
					fireBeginClassfile(filename);
					Classfile classfile = Load(new DataInputStream(in));
					fireEndClassfile(filename, classfile);
				} catch (IOException ex) {
					Logger.getLogger(getClass()).warn("Cannot load class from file \"" + filename + "\"", ex);
				}
				break;
				
			default:
				break;
		}
	}

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

	protected void fireBeginSession() {
		LoadEvent event = new LoadEvent(this, null, null, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).BeginSession(event);
		}
	}

	protected void fireBeginGroup(String group_name, int size) {
		LoadEvent event = new LoadEvent(this, group_name, size);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).BeginGroup(event);
		}

		PushGroupName(group_name);
	}
	
	protected void fireBeginFile(String filename) {
		LoadEvent event = new LoadEvent(this, TopGroupName(), filename, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).BeginFile(event);
		}
	}
	
	protected void fireBeginClassfile(String filename) {
		LoadEvent event = new LoadEvent(this, TopGroupName(), filename, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).BeginClassfile(event);
		}
	}

	protected void fireEndClassfile(String filename, Classfile classfile) {
		LoadEvent event = new LoadEvent(this, TopGroupName(), filename, classfile);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).EndClassfile(event);
		}
	}

	protected void fireEndFile(String filename) {
		LoadEvent event = new LoadEvent(this, TopGroupName(), filename, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).EndFile(event);
		}
	}

	protected void fireEndGroup(String group_name) {
		LoadEvent event = new LoadEvent(this, group_name, null, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).EndGroup(event);
		}

		PopGroupName();
	}

	protected void fireEndSession() {
		LoadEvent event = new LoadEvent(this, null, null, null);

		HashSet listeners;
		synchronized(load_listeners) {
			listeners = (HashSet) load_listeners.clone();
		}

		Iterator i = listeners.iterator();
		while(i.hasNext()) {
			((LoadListener) i.next()).EndSession(event);
		}
	}

	private String TopGroupName() {
		String result = null;

		if (!group_names.isEmpty()) {
			result = (String) group_names.getLast();
		}

		return result;
	}

	private void PushGroupName(String group_name) {
		group_names.addLast(group_name);
	}

	private String PopGroupName() {
		return (String) group_names.removeLast();
	}
}
