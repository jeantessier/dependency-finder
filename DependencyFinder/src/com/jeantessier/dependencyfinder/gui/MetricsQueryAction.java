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
import java.util.*;
import javax.swing.*;

import com.jeantessier.dependency.*;

public class MetricsQueryAction extends AbstractAction implements Runnable {
    private DependencyFinder model = null;

    public MetricsQueryAction(DependencyFinder model) {
        this.model = model;

        putValue(Action.LONG_DESCRIPTION, "Compute dependency metrics");
        putValue(Action.NAME, "Metrics");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/metrics-query.gif")));
    }

    public void actionPerformed(ActionEvent e) {
        new Thread(this).start();
    }

    public void run() {
        try {
            model.getStatusLine().showInfo("Processing metrics query ...");

            Date start = new Date();

            model.clearMetricsResult();
            model.doMetricsQuery();

            Date stop = new Date();

            model.getStatusLine().showInfo("Metrics query done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
        } catch (MatchException ex) {
            JOptionPane.showMessageDialog(model, ex.getMessage() + ": " + ex.getCause().getMessage(), "Malformed pattern", JOptionPane.ERROR_MESSAGE);
            model.getStatusLine().showInfo("Ready.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(model, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            model.getStatusLine().showInfo("Ready.");
        }
    }
}
