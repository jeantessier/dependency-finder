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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class StatusLine extends JTextField {
	public static final Font PLAIN_FONT = new Font("dialog", Font.PLAIN, 12);
	public static final Font BOLD_FONT  = new Font("dialog", Font.BOLD,  12);

	public StatusLine(int preferredWidth) {
		super();
		setFont(BOLD_FONT);
		setEditable(false);
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		Dimension d = getPreferredSize();
		d.width = preferredWidth;
		setPreferredSize(d);
	}
	
	public void ShowInfo(String message) {
		SwingUtilities.invokeLater(new StatusLineUpdater(this, PLAIN_FONT, Color.black, message, message));
	}
	
	public void ShowError(String status) {
		SwingUtilities.invokeLater(new StatusLineUpdater(this, BOLD_FONT, Color.red, status, status));
	}
	
	public void Clear() {
		SwingUtilities.invokeLater(new StatusLineUpdater(this, PLAIN_FONT, Color.black, "", null));
	}
}

class StatusLineUpdater implements Runnable {
	private StatusLine status_line;
	private Font       font;
	private Color      color;
	private String     message;
	private String     tooltip;

	public StatusLineUpdater(StatusLine status_line, Font font, Color color, String message, String tooltip) {
		this.status_line = status_line;
		this.font        = font;
		this.color       = color;
		this.message     = message;
		this.tooltip     = tooltip;
	}
	
	public void run() {
		status_line.setFont(font);
		status_line.setForeground(color);
		status_line.setText(message);
		status_line.setToolTipText(tooltip);
	}
}
