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
import java.util.*;

import javax.swing.*;

import org.apache.oro.text.perl.*;

public class MetricsQueryAction extends AbstractAction implements Runnable {
	private DependencyFinder model = null;
	
	public MetricsQueryAction(DependencyFinder model) {
		this.model = model;

		putValue(Action.LONG_DESCRIPTION, "Perform the metrics query");
		putValue(Action.NAME, "Metrics");
		putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/metrics-query.gif")));
	}

	public void actionPerformed(ActionEvent e) {
		new Thread(this).start();
	}

	public void run() {
		try {
			model.StatusLine().ShowInfo("Processing metrics query ...");
			
			Date start = new Date();
			
			model.ClearMetricsResult();
			model.MetricsQuery();
			
			Date stop = new Date();
			
			model.StatusLine().ShowInfo("Metrics query done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
		} catch (MalformedPerl5PatternException ex) {
			JOptionPane dialog = new JOptionPane();
			dialog.showMessageDialog(model, ex.getMessage(), "Malformed pattern", JOptionPane.ERROR_MESSAGE);
			model.StatusLine().ShowInfo("Ready.");
		} catch (Exception ex) {
			JOptionPane dialog = new JOptionPane();
			dialog.showMessageDialog(model, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			model.StatusLine().ShowInfo("Ready.");
		}
	}
}
