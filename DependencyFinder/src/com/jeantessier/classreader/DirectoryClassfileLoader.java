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

import org.apache.log4j.*;

public class DirectoryClassfileLoader extends ClassfileLoaderBase {
	public DirectoryClassfileLoader(String[] filenames) {
		super(filenames);
	}

	public DirectoryClassfileLoader(Collection filenames) {
		super(filenames);
	}

	public void Start() throws IOException {
		Iterator i = new DirectoryExplorer(Filenames()).Collection().iterator();
		while (i.hasNext()) {
			String filename = (String) i.next();
			Category.getInstance(getClass().getName()).debug("Reading " + filename);
			fireLoadStart(filename);
			// fireLoadElement(filename, null);
			AddClass(filename);
			fireLoadStop(filename);
		}
	}
}
