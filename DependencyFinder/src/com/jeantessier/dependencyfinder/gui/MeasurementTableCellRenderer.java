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

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import com.jeantessier.metrics.*;

public class MeasurementTableCellRenderer extends DefaultTableCellRenderer {
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column == 0) {
			result.setHorizontalAlignment(JLabel.LEFT);
		} else {
			result.setHorizontalAlignment(JLabel.CENTER);
		}

		if (!isSelected) {
			if (((row / 3) % 2) == 0) {
				result.setBackground(Color.white);
			} else {
				result.setBackground(Color.cyan);
			}
		}
		
		if (value instanceof Measurement) {
			if (((Measurement) value).InRange()) {
				result.setForeground(Color.black);
			} else {
				result.setForeground(Color.red);
			}
			
			if (value instanceof StatisticalMeasurement) {
				StatisticalMeasurement stat = (StatisticalMeasurement) value;
				switch (((OOMetricsTableModel) table.getModel()).RawColumnDispose(column)) {
					case StatisticalMeasurement.DISPOSE_MINIMUM:
						result.setText(String.valueOf(stat.Minimum()));
						break;
					case StatisticalMeasurement.DISPOSE_MEDIAN:
						result.setText(String.valueOf(stat.Median()));
						break;
					case StatisticalMeasurement.DISPOSE_AVERAGE:
						result.setText(String.valueOf(stat.Average()));
						break;
					case StatisticalMeasurement.DISPOSE_MAXIMUM:
						result.setText(String.valueOf(stat.Maximum()));
						break;
					case StatisticalMeasurement.DISPOSE_SUM:
						result.setText(String.valueOf(stat.Sum()));
						break;
					case StatisticalMeasurement.DISPOSE_IGNORE:
					case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
					default:
						result.setText("n/a");
						break;
				}
			} else {
				result.setText(((Measurement) value).Value().toString());
			}
		}
		
		return result;
	}
}
