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
import java.util.*;

public final class Hex {
	private Hex() {
		// Do nothing
	}
    
	public static void Print(PrintStream out, byte[] bytes) {
		for (int i=0; i<bytes.length; i++) {
			Print(out, bytes[i]);
		}
	}

	public static void Print(PrintWriter out, byte[] bytes) {
		for (int i=0; i<bytes.length; i++) {
			Print(out, bytes[i]);
		}
	}

	public static void Print(PrintStream out, byte b) {
		int high_bits = (b & 0xF0) >> 4;
		int low_bits  = (b & 0x0F);
	
		Print(out, high_bits);
		Print(out, low_bits);
	}

	public static void Print(PrintWriter out, byte b) {
		int high_bits = (b & 0xF0) >> 4;
		int low_bits  = (b & 0x0F);
	
		Print(out, high_bits);
		Print(out, low_bits);
	}

	public static void Print(PrintStream out, int n) {
		out.print(HexChar(n));
	}
    
	public static void Print(PrintWriter out, int n) {
		out.print(HexChar(n));
	}

	public static String HexChar(int n) {
		return Integer.toHexString(n).toUpperCase();
	}

	/*
	public static char HexChar(int n) {
		char result = '?';

		switch (n) {
			case 0:
				result = '0';
				break;
			case 1:
				result = '1';
				break;
			case 2:
				result = '2';
				break;
			case 3:
				result = '3';
				break;
			case 4:
				result = '4';
				break;
			case 5:
				result = '5';
				break;
			case 6:
				result = '6';
				break;
			case 7:
				result = '7';
				break;
			case 8:
				result = '8';
				break;
			case 9:
				result = '9';
				break;
			case 10:
				result = 'A';
				break;
			case 11:
				result = 'B';
				break;
			case 12:
				result = 'C';
				break;
			case 13:
				result = 'D';
				break;
			case 14:
				result = 'E';
				break;
			case 15:
				result = 'F';
				break;
			default:
				result = '?';
				break;
		}

		return result;
	}
	*/
}
