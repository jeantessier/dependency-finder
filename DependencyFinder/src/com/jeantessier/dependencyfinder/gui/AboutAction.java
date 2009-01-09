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

import com.jeantessier.dependencyfinder.*;

public class AboutAction extends AbstractAction {
    private JFrame model;

    public AboutAction(JFrame model) {
        this.model = model;

        putValue(Action.LONG_DESCRIPTION, "Show version information");
        putValue(Action.NAME, "About");
        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("icons/about.gif")));
    }

    public void actionPerformed(ActionEvent e) {
        Version version = new Version();

        Object[] message = new Object[3];
        message[0] = "<html><b>" + version.getImplementationTitle() + " " + version.getImplementationVersion() + "</b></html>";
        message[1] = "<html>&copy; " + version.getCopyrightDate() + " " + version.getCopyrightHolder() + "</html>";
        message[2] = "Compiled on " + version.getImplementationDate();

        String title = "About " + version.getImplementationTitle();

        Icon icon = new ImageIcon(getClass().getResource("icons/logo.jpg"));

        JOptionPane.showMessageDialog(model, message, title, JOptionPane.INFORMATION_MESSAGE, icon);
    }
}
