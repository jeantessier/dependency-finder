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

import java.awt.event.*;

import javax.swing.*;

import org.apache.log4j.*;

import com.jeantessier.metrics.*;

class TableHeaderListener implements MouseListener, MouseMotionListener {
    private JTable table;
    private OOMetricsTableModel model;

    public TableHeaderListener(JTable table, OOMetricsTableModel model) {
        this.table = table;
        this.model = model;
    }

    public void mouseClicked(MouseEvent event) {
        int viewColumn = table.getColumnModel().getColumnIndexAtX(event.getX());
        int column = table.convertColumnIndexToModel(viewColumn);
        String columnName = model.getRawColumnName(column);
        int columnDispose = model.getRawColumnDispose(column);

        Logger.getLogger(getClass()).debug("mouseClicked");
        Logger.getLogger(getClass()).debug("event.getX()       = " + event.getX());
        Logger.getLogger(getClass()).debug("view column        = " + viewColumn);
        Logger.getLogger(getClass()).debug("column             = " + column);
        Logger.getLogger(getClass()).debug("raw column name    = " + columnName);
        Logger.getLogger(getClass()).debug("raw column dispose = " + columnDispose);

        model.sortOn(columnName, columnDispose);
    }

    public void mouseEntered(MouseEvent event) {
        Logger.getLogger(getClass()).debug("mouseEntered");
    }

    public void mouseExited(MouseEvent event) {
        Logger.getLogger(getClass()).debug("mouseExited");
    }

    public void mousePressed(MouseEvent event) {
        Logger.getLogger(getClass()).debug("mousePressed");
    }

    public void mouseReleased(MouseEvent event) {
        Logger.getLogger(getClass()).debug("mouseReleased");
    }

    public void mouseDragged(MouseEvent event) {
        Logger.getLogger(getClass()).debug("mouseDragged");
    }

    public void mouseMoved(MouseEvent event) {
        if (event.getComponent() instanceof JComponent) {
            JComponent component = (JComponent) event.getComponent();

            int viewColumn = table.getColumnModel().getColumnIndexAtX(event.getX());
            int column = table.convertColumnIndexToModel(viewColumn);
            MeasurementDescriptor descriptor = model.getColumnDescriptor(column);
            int columnDispose = model.getRawColumnDispose(column);

            String text = null;

            if (descriptor != null) {
                StringBuffer tooltip = new StringBuffer();
                tooltip.append("<html><body><p>");
                tooltip.append(descriptor.getLongName());

                if (descriptor.getClassFor().equals(StatisticalMeasurement.class)) {
                    switch (columnDispose) {
                        case StatisticalMeasurement.DISPOSE_MINIMUM:
                        case StatisticalMeasurement.DISPOSE_MEDIAN:
                        case StatisticalMeasurement.DISPOSE_AVERAGE:
                        case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
                        case StatisticalMeasurement.DISPOSE_MAXIMUM:
                        case StatisticalMeasurement.DISPOSE_SUM:
                            tooltip.append(", ").append(StatisticalMeasurement.getDisposeLabel(columnDispose));
                            break;
                        case StatisticalMeasurement.DISPOSE_IGNORE:
                        case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
                        default:
                            // Ignore
                            break;
                    }
                }

                tooltip.append("<br>valid range: ").append(descriptor.getRangeAsString());

                tooltip.append("</p></body></html>");

                text = tooltip.toString();
            }

            component.setToolTipText(text);
        }
    }
}
