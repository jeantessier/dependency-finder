/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

public abstract class ClassfileLoaderBase implements ClassfileLoader {
	private Collection filenames;
	
	private Map classfiles = new TreeMap();
	private HashSet load_listeners = new HashSet();

	protected ClassfileLoaderBase() {
		this(Collections.EMPTY_LIST);
	}
	
	public ClassfileLoaderBase(String[] filenames) {
		this(Arrays.asList(filenames));
	}

	public ClassfileLoaderBase(Collection filenames) {
		this.filenames = filenames;
	}

	public Collection Filenames() {
		return filenames;
	}
	
	public Collection Classnames() {
		return Collections.unmodifiableCollection(classfiles.keySet());
	}

	public Classfile Classfile(String name) {
		return (Classfile) classfiles.get(name);
	}

	public Collection Classfiles() {
		return Collections.unmodifiableCollection(classfiles.values());
	}

	protected void AddClass(byte[] bytes) throws IOException {
		AddClassfile(new Classfile(this, bytes));
	}

	protected void AddClass(String filename) throws IOException {
		AddClassfile(new Classfile(this, filename));
	}

	protected void AddClass(File file) throws IOException {
		AddClassfile(new Classfile(this, file));
	}

	protected void AddClassfile(Classfile classfile) {
		classfiles.put(classfile.Class(), classfile);
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
}
