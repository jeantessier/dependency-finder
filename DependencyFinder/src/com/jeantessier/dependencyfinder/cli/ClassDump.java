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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;

public class ClassDump {
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println(ClassDump.class.getName() + ":");
			DumpClass(ClassDump.class);
		} else {
			for (int i=0; i<args.length; i++) {
				System.out.println(args[i] + ":");
				DumpClass(args[i]);
			}
		}
	}

	public static void DumpClass(String classname) throws ClassNotFoundException, IOException  {
		DumpClass(Class.forName(classname));
	}

	public static void DumpClass(Class c) throws IOException {
		String resource = c.getName().substring(c.getName().lastIndexOf(".") + 1) + ".class";
		System.out.println(resource + " -> " + c.getResource(resource));
		DumpClass(c.getResourceAsStream(resource));
	}

	public static void DumpClass(InputStream in) throws IOException {
		DumpClass(new DataInputStream(in));
	}

	public static void DumpClass(DataInputStream in) {
		int count = 0;
		try {
			while (true) {
				byte b = in.readByte();
		
				Hex.Print(System.out, b);

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
