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

package com.jeantessier.dependencyfinder.gui;

import java.awt.event.*;

import javax.swing.*;

class TableHeaderListener extends MouseAdapter {
	private JTable            table; 
	private OOMetricsTableModel model; 

	public TableHeaderListener(JTable table, OOMetricsTableModel model) {
		this.table = table;
		this.model = model;
	}
	
	public void mouseClicked(MouseEvent event) {
		System.out.println(getClass().getName() + ".mouseClicked()");
		
		int    view_column    = table.getColumnModel().getColumnIndexAtX(event.getX()); 
		int    column         = table.convertColumnIndexToModel(view_column); 
		String column_name    = model.RawColumnName(column);
		int    column_dispose = model.RawColumnDispose(column);

		System.out.println("event.getX()       = " + event.getX());
		System.out.println("view_column        = " + view_column);
		System.out.println("column             = " + column);
		System.out.println("raw column_name    = " + column_name);
		System.out.println("raw column_dispose = " + column_dispose);
		System.out.println("column_name        = " + model.getColumnName(column));
		System.out.println();
		
		model.SortOn(column_name, column_dispose
					 );
	}
}
