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
	private static final Color PRIMARY_NORMAL_BACKGROUND        = new Color(247, 247, 247);
	private static final Color SECONDARY_NORMAL_BACKGROUND      = new Color(223, 223, 223);
	private static final Color NORMAL_FOREGROUND                = Color.black;

	private static final Color PRIMARY_HIGHLIGHTED_BACKGROUND   = new Color(255, 223, 223);
	private static final Color SECONDARY_HIGHLIGHTED_BACKGROUND = new Color(255, 207, 207);
	private static final Color HIGHLIGHTED_FOREGROUND           = Color.red;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if (column == 0) {
			result.setHorizontalAlignment(JLabel.LEFT);
		} else {
			result.setHorizontalAlignment(JLabel.CENTER);
		}
		
		if (value instanceof Measurement) {
			Measurement measurement = (Measurement) value;
			if (measurement.InRange()) {
				NormalCell(isSelected, row, result);
			} else {
				HighlightedCell(isSelected, row, result);
			}
			
			if (measurement instanceof StatisticalMeasurement) {
				StatisticalMeasurement stat = (StatisticalMeasurement) measurement;
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
					case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
						result.setText(String.valueOf(stat.StandardDeviation()));
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
				result.setText(measurement.Value().toString());
			}

			ToolTip(measurement, result);
		} else if (value instanceof Metrics) {
			Metrics metrics = (Metrics) value;
			
			if (metrics.InRange()) {
				NormalCell(isSelected, row, result);
			} else {
				HighlightedCell(isSelected, row, result);
			}

			result.setText(metrics.Name());
			result.setToolTipText(metrics.Name());
		} else {
			NormalCell(isSelected, row, result);
		}
		
		return result;
	}

	private void NormalCell(boolean isSelected, int row, JLabel result) {
		result.setForeground(NORMAL_FOREGROUND);

		if (!isSelected) {
			if (((row / 3) % 2) == 0) {
				result.setBackground(PRIMARY_NORMAL_BACKGROUND);
			} else {
				result.setBackground(SECONDARY_NORMAL_BACKGROUND);
			}
		}
	}

	private void HighlightedCell(boolean isSelected, int row, JLabel result) {
		result.setForeground(HIGHLIGHTED_FOREGROUND);

		if (!isSelected) {
			if (((row / 3) % 2) == 0) {
				result.setBackground(PRIMARY_HIGHLIGHTED_BACKGROUND);
			} else {
				result.setBackground(SECONDARY_HIGHLIGHTED_BACKGROUND);
			}
		}
	}

	private void ToolTip(Measurement measurement, JLabel result) {
		StringBuffer tooltip = new StringBuffer();
		tooltip.append("<html><body><p>");
		tooltip.append("<b>").append(measurement.Context().Name()).append("</b><br>");
		tooltip.append(measurement.LongName()).append(" (").append(measurement.ShortName()).append(")<br>");
		tooltip.append("valid range: [");

		Comparable lower_threshold = measurement.Descriptor().LowerThreshold();
		Comparable upper_threshold = measurement.Descriptor().UpperThreshold();
			
		tooltip.append((lower_threshold != null) ? lower_threshold.toString() : "*");
		tooltip.append(", ");
		tooltip.append((upper_threshold != null) ? upper_threshold.toString() : "*");
		tooltip.append("]<br>");
		tooltip.append("value: ").append(measurement);
		tooltip.append("</p></body></html>");

		result.setToolTipText(tooltip.toString());
	}
}
