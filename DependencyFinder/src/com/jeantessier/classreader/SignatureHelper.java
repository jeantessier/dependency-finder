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

import java.util.*;

import org.apache.log4j.*;
import org.apache.oro.text.perl.*;

public final class SignatureHelper {
	private static final Perl5Util perl = new Perl5Util();

	private static Map conversion = new HashMap();

	static {
		conversion.put("B", "byte");
		conversion.put("C", "char");
		conversion.put("D", "double");
		conversion.put("F", "float");
		conversion.put("I", "int");
		conversion.put("J", "long");
		conversion.put("S", "short");
		conversion.put("Z", "boolean");
	}

	static String Convert(String type) {
		String result = null;

		Category.getInstance(SignatureHelper.class.getName()).debug("Begin Convert(\"" + type + "\")");

		if (type.length() == 1) {
			result = (String) conversion.get(type);
		} else if (type.charAt(0) == 'L') {
			result = Path2ClassName(type.substring(1, type.indexOf(';')));
		} else if (type.charAt(0) == '[') {
			result = Convert(type.substring(1)) + "[]";
		}

		Category.getInstance(SignatureHelper.class.getName()).debug("End   Convert(\"" + type + "\"): \"" + result + "\"");

		return result;
	}

	public static String Path2ClassName(String path) {
		return perl.substitute("s/\\//./g", path);
	}
	
	public static String Signature(String descriptor) {
		StringBuffer result = new StringBuffer();

		Category.getInstance(SignatureHelper.class.getName()).debug("Begin Signature(\"" + descriptor + "\")");

		result.append("(");

		int start = descriptor.indexOf("(") + 1;
		int end   = descriptor.indexOf(")");

		SignatureIterator i = new SignatureIterator(descriptor.substring(start, end));
		while (i.hasNext()) {
			result.append(i.next());
			if (i.hasNext()) {
				result.append(", ");
			}
		}

		result.append(")");

		Category.getInstance(SignatureHelper.class.getName()).debug("End   Signature(\"" + descriptor + "\"): \"" + result + "\"");

		return result.toString();
	}

	public static int ParameterCount(String descriptor) {
		int result = 0;

		Category.getInstance(SignatureHelper.class.getName()).debug("Begin ParameterCount(\"" + descriptor + "\")");

		int start = descriptor.indexOf("(") + 1;
		int end   = descriptor.indexOf(")");

		SignatureIterator i = new SignatureIterator(descriptor.substring(start, end));
		while (i.hasNext()) {
			i.next();
			result++;
		}

		Category.getInstance(SignatureHelper.class.getName()).debug("End   ParameterCount(\"" + descriptor + "\"): \"" + result + "\"");

		return result;
	}
    
	public static String ReturnType(String descriptor) {
		return Convert(descriptor.substring(descriptor.lastIndexOf(")") + 1));
	}
    
	public static String Type(String descriptor) {
		return Convert(descriptor);
	}
}

class SignatureIterator implements Iterator {
	private String descriptor;
	private int    current_pos = 0;
    
	public SignatureIterator(String descriptor) {
		this.descriptor = descriptor;
	}

	public boolean hasNext() {
		return current_pos < descriptor.length();
	}

	public Object next() {
		String result;

		if (hasNext()) {
			int next_pos = current_pos;

			while (descriptor.charAt(next_pos) == '[') {
				next_pos++;
			}

			if (descriptor.charAt(next_pos) == 'L') {
				next_pos = descriptor.indexOf(";", next_pos);
			}

			result = SignatureHelper.Convert(descriptor.substring(current_pos, next_pos + 1));
	    
			current_pos = next_pos + 1;
		} else {
			throw new NoSuchElementException();
		}

		return result;
	}
    
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
