/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.jeantessier.diff.*;

public class ListDiff extends Task {
	private String  name        = "";
	private File    old_file;
	private String  old_label;
	private File    new_file;
	private String  new_label;
	private boolean compress    = false;
	private String  dtd_prefix  = ListDiffPrinter.DEFAULT_DTD_PREFIX;
	private String  indent_text;
	private File    destfile;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public File getOld() {
		return old_file;
	}
	
	public void setOld(File old_file) {
		this.old_file = old_file;
	}

	public String getOldlabel() {
		return old_label;
	}
	
	public void setOldlabel(String old_label) {
		this.old_label = old_label;
	}
	
	public File getNew() {
		return new_file;
	}
	
	public void setNew(File new_file) {
		this.new_file = new_file;
	}

	public String getNewlabel() {
		return new_label;
	}
	
	public void setNewlabel(String new_label) {
		this.new_label = new_label;
	}

	public boolean getCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}
	
	public String getDtdprefix() {
		return dtd_prefix;
	}
	
	public void setDtdprefix(String dtd_prefix) {
		this.dtd_prefix = dtd_prefix;
	}

	public String getIndenttext() {
		return indent_text;
	}
	
	public void setIntenttext(String indent_text) {
		this.indent_text = indent_text;
	}

	public File getDestfile() {
		return destfile;
	}
	
	public void setDestfile(File destfile) {
		this.destfile = destfile;
	}
	
	public void execute() throws BuildException {
        // first off, make sure that we've got what we need

        if (getOld() == null) {
            throw new BuildException("old must be set!");
        }
		
        if (!getOld().exists()) {
            throw new BuildException("old does not exist!");
        }
		
        if (!getOld().isFile()) {
            throw new BuildException("old is not a file!");
        }

        if (getNew() == null) {
            throw new BuildException("new must be set!");
        }
		
        if (!getNew().exists()) {
            throw new BuildException("new does not exist!");
        }
		
        if (!getNew().isFile()) {
            throw new BuildException("new is not a file!");
        }

        if (getDestfile() == null) {
            throw new BuildException("destfile must be set!");
        }

		VerboseListener verbose_listener = new VerboseListener(this);

		try {
			String line;
			
			Collection old_api = new TreeSet();
			BufferedReader old_in = new BufferedReader(new FileReader(getOld()));
			while((line = old_in.readLine()) != null) {
				old_api.add(line);
			}
			
			Collection new_api = new TreeSet();
			BufferedReader new_in = new BufferedReader(new FileReader(getNew()));
			while((line = new_in.readLine()) != null) {
				new_api.add(line);
			}
			
			ListDiffPrinter printer = new ListDiffPrinter(getCompress(), getDtdprefix());
			printer.Name(getName());
			printer.OldVersion(getOldlabel());
			printer.NewVersion(getNewlabel());
			if (getIndenttext() != null) {
				printer.IndentText(getIndenttext());
			}
			
			Iterator i;
			
			i = old_api.iterator();
			while (i.hasNext()) {
				line = (String) i.next();
				if (!new_api.contains(line)) {
					printer.Remove(line);
				}
			}
			
			i = new_api.iterator();
			while (i.hasNext()) {
				line = (String) i.next();
				if (!old_api.contains(line)) {
					printer.Add(line);
				}
			}

			log("Saving difference report to " + getDestfile().getAbsolutePath());

			PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));
			out.print(printer);
			out.close();
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}
}
