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
