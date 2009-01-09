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
import javax.swing.border.*;

public class StatusLine extends JTextField {
    public static final Font PLAIN_FONT = new Font("dialog", Font.PLAIN, 12);
    public static final Font BOLD_FONT = new Font("dialog", Font.BOLD,  12);

    public StatusLine(int preferredWidth) {
        setFont(BOLD_FONT);
        setEditable(false);
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        Dimension d = getPreferredSize();
        d.width = preferredWidth;
        setPreferredSize(d);
    }

    public void showInfo(String message) {
        SwingUtilities.invokeLater(new StatusLineUpdater(this, PLAIN_FONT, Color.black, message, message));
    }

    public void showError(String status) {
        SwingUtilities.invokeLater(new StatusLineUpdater(this, BOLD_FONT, Color.red, status, status));
    }

    public void clear() {
        SwingUtilities.invokeLater(new StatusLineUpdater(this, PLAIN_FONT, Color.black, "", null));
    }
}

class StatusLineUpdater implements Runnable {
    private StatusLine statusLine;
    private Font font;
    private Color color;
    private String message;
    private String tooltip;

    public StatusLineUpdater(StatusLine statusLine, Font font, Color color, String message, String tooltip) {
        this.statusLine = statusLine;
        this.font = font;
        this.color = color;
        this.message = message;
        this.tooltip = tooltip;
    }

    public void run() {
        statusLine.setFont(font);
        statusLine.setForeground(color);
        statusLine.setText(message);
        statusLine.setToolTipText(tooltip);
    }
}
