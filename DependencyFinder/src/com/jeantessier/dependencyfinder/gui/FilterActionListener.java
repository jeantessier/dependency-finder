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

import com.jeantessier.classreader.*;
import com.jeantessier.metrics.*;

public class FilterActionListener implements Runnable, ActionListener {
	private static final Perl5Util perl = new Perl5Util();

	private OOMetrics model;

	public FilterActionListener(OOMetrics model) {
		this.model = model;
	}

	public void actionPerformed(ActionEvent event) {
		new Thread(this).start();
	}

	public void run() {
		try {
			Date start = new Date();
			
			model.StatusLine().ShowInfo("Filtering ...");
			model.GroupsModel().UpdateMetrics(FilterMetrics(model.MetricsFactory().GroupMetrics()));
			model.ClassesModel().UpdateMetrics(FilterMetrics(model.MetricsFactory().ClassMetrics()));
			model.MethodsModel().UpdateMetrics(FilterMetrics(model.MetricsFactory().MethodMetrics()));
			
			Date stop = new Date();
			
			model.StatusLine().ShowInfo("Done (" + ((stop.getTime() - start.getTime()) / (double) 1000) + " secs).");
			model.setTitle("OO Metrics - Extractor");
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

	private Collection FilterMetrics(Collection metrics_list) {
		Collection result = new ArrayList(metrics_list.size());

		Iterator i = metrics_list.iterator();
		while (i.hasNext()) {
			Metrics metrics = (Metrics) i.next();

			if (perl.match(model.FilterField().getText(), metrics.Name())) {
				result.add(metrics);
			}
		}

		return result;
	}
}
