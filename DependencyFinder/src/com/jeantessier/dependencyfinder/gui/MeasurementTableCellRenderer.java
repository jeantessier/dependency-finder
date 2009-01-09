/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
            if (measurement.isInRange()) {
                formatAsNormalCell(isSelected, row, result);
            } else {
                formatAsHighlightedCell(isSelected, row, result);
            }

            String text;
            int dispose = ((OOMetricsTableModel) table.getModel()).getRawColumnDispose(column);

            if (measurement instanceof StatisticalMeasurement) {
                StatisticalMeasurement stat    = (StatisticalMeasurement) measurement;
                switch (dispose) {
                    case StatisticalMeasurement.DISPOSE_MINIMUM:
                        text = String.valueOf(stat.getMinimum());
                        break;
                    case StatisticalMeasurement.DISPOSE_MEDIAN:
                        text = String.valueOf(stat.getMedian());
                        break;
                    case StatisticalMeasurement.DISPOSE_AVERAGE:
                        text = String.valueOf(stat.getAverage());
                        break;
                    case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
                        text = String.valueOf(stat.getStandardDeviation());
                        break;
                    case StatisticalMeasurement.DISPOSE_MAXIMUM:
                        text = String.valueOf(stat.getMaximum());
                        break;
                    case StatisticalMeasurement.DISPOSE_SUM:
                        text = String.valueOf(stat.getSum());
                        break;
                    case StatisticalMeasurement.DISPOSE_IGNORE:
                    case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
                    default:
                        text = "n/a";
                        break;
                }
            } else {
                text = measurement.getValue().toString();
            }

            setCellContent(result, measurement, dispose, text);
        } else if (value instanceof Metrics) {
            Metrics metrics = (Metrics) value;

            if (metrics.isInRange()) {
                formatAsNormalCell(isSelected, row, result);
            } else {
                formatAsHighlightedCell(isSelected, row, result);
            }

            result.setText(metrics.getName());
            result.setToolTipText(metrics.getName());
        } else {
            formatAsNormalCell(isSelected, row, result);
        }

        return result;
    }

    private void formatAsNormalCell(boolean isSelected, int row, JLabel result) {
        result.setForeground(NORMAL_FOREGROUND);

        if (!isSelected) {
            if (((row / 3) % 2) == 0) {
                result.setBackground(PRIMARY_NORMAL_BACKGROUND);
            } else {
                result.setBackground(SECONDARY_NORMAL_BACKGROUND);
            }
        }
    }

    private void formatAsHighlightedCell(boolean isSelected, int row, JLabel result) {
        result.setForeground(HIGHLIGHTED_FOREGROUND);

        if (!isSelected) {
            if (((row / 3) % 2) == 0) {
                result.setBackground(PRIMARY_HIGHLIGHTED_BACKGROUND);
            } else {
                result.setBackground(SECONDARY_HIGHLIGHTED_BACKGROUND);
            }
        }
    }

    private void setCellContent(JLabel result, Measurement measurement, int dispose, String text) {
        StringBuffer tooltip = new StringBuffer();
        tooltip.append("<html><body><p>");
        tooltip.append("<b>").append(measurement.getContext().getName()).append("</b><br>");
        tooltip.append(measurement.getLongName()).append(" (").append(measurement.getShortName()).append(")");
        if (measurement instanceof StatisticalMeasurement) {
            tooltip.append(", ").append(StatisticalMeasurement.getDisposeLabel(dispose));
        }
        tooltip.append("<br>");
        tooltip.append("valid range: ").append(measurement.getDescriptor().getRangeAsString()).append("<br>");
        tooltip.append("value: ").append(text);
        if (!measurement.isInRange()) {
            tooltip.append(" <b>*** out of range ***</b>");
        }
        if (measurement instanceof StatisticalMeasurement) {
            tooltip.append("<br>").append(measurement);
        }
        tooltip.append("</p></body></html>");

        result.setText(text);
        result.setToolTipText(tooltip.toString());
    }
}
