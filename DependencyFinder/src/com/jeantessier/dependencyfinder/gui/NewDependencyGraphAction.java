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

public class NewDependencyGraphAction extends AbstractAction implements Runnable {
	private DependencyFinder model = null;
	
	public NewDependencyGraphAction(DependencyFinder model) {
		this.model = model;

		putValue(Action.LONG_DESCRIPTION, "New dependency graph");
		putValue(Action.NAME, "New");
		putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/new.gif")));
	}

	public void actionPerformed(ActionEvent e) {
		new Thread(this).start();
	}

	public void run() {
		model.StatusLine().ShowInfo("");

		model.ClearDependencyResult();
		model.ClearClosureResult();
		model.ClearMetricsResult();

		model.NewDependencyGraph();
	}
}
