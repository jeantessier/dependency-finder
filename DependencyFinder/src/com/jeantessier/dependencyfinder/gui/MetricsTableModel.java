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

import javax.swing.table.*;

import com.jeantessier.dependency.*;

public class MetricsTableModel extends AbstractTableModel {
	MetricsGatherer metrics = new MetricsGatherer();

	public MetricsGatherer Metrics() {
		return metrics;
	}

	public void Metrics(MetricsGatherer metrics) {
		this.metrics = metrics;
		fireTableStructureChanged();
	}
	
	public int getColumnCount() {
		return MetricsGatherer.NbCharts();
	}

	public int getRowCount() {
		return Metrics().ChartSize();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return new Long(Metrics().ChartData(rowIndex)[columnIndex]);
	}

	public String getColumnName(int column) {
		return MetricsGatherer.ChartName(column);
	}
}
