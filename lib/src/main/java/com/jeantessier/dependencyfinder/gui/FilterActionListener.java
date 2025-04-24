/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import com.jeantessier.metrics.Metrics;
import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.perl.Perl5Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class FilterActionListener implements Runnable, ActionListener {
    private static final Perl5Util perl = new Perl5Util();

    private final OOMetrics model;

    public FilterActionListener(OOMetrics model) {
        this.model = model;
    }

    public void actionPerformed(ActionEvent event) {
        new Thread(this).start();
    }

    public void run() {
        try {
            Date start = new Date();

            model.getStatusLine().showInfo("Filtering ...");
            model.getGroupsModel().updateMetrics(getFilterMetrics(model.getMetricsFactory().getGroupMetrics()));
            model.getClassesModel().updateMetrics(getFilterMetrics(model.getMetricsFactory().getClassMetrics()));
            model.getMethodsModel().updateMetrics(getFilterMetrics(model.getMetricsFactory().getMethodMetrics()));

            Date stop = new Date();

            model.getStatusLine().showInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
            model.setTitle("OO Metrics - Extractor");
        } catch (MalformedCachePatternException ex) {
            JOptionPane.showMessageDialog(model, ex.getMessage(), "Malformed pattern", JOptionPane.ERROR_MESSAGE);
            model.getStatusLine().showInfo("Ready.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(model, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            model.getStatusLine().showInfo("Ready.");
        }
    }

    private Collection<Metrics> getFilterMetrics(Collection<Metrics> metricsList) {
        return metricsList.stream()
                .filter(metrics -> perl.match(model.getFilterField().getText(), metrics.getName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
