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

public class DirectoryExplorer {
	private Collection collection = new TreeSet();

	public DirectoryExplorer() {
		// Do nothing
	}

	public DirectoryExplorer(String[] filenames) throws IOException {
		for (int i=0; i<filenames.length; i++) {
			Explore(new File(filenames[i]));
		}
	}

	public DirectoryExplorer(Collection filenames) throws IOException {
		Iterator i = filenames.iterator();
		while (i.hasNext()) {
			Explore(new File(i.next().toString()));
		}
	}

	public DirectoryExplorer(String filename) throws IOException {
		this(new File(filename));
	}

	public DirectoryExplorer(File file) throws IOException {
		Explore(file);
	}

	public void Explore(File file) throws IOException {
		if (file.isDirectory()) {
			ExploreDirectory(file);
		} else {
			ExploreFile(file);
		}
	}

	public void ExploreDirectory(File dir) throws IOException {
		String[] entries = dir.list();
		for (int i=0; i<entries.length; i++) {
			Explore(new File(dir, entries[i]));
		}
	}

	public void ExploreFile(File file) throws IOException {
		if (file.getName().endsWith(".class")) {
			collection.add(file.getPath());
		}
	}

	public Collection Collection() {
		return collection;
	}

	public static void main(String[] args) throws IOException {
		DirectoryExplorer explorer;
		if (args.length != 0) {
			explorer = new DirectoryExplorer(args);
		} else {
			explorer = new DirectoryExplorer(".");
		}

		Iterator i = explorer.Collection().iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}
}
