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
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

import com.jeantessier.commandline.*;
import com.jeantessier.metrics.*;
import org.apache.log4j.*;

public class OOMetrics extends JFrame {
    private static final TableCellRenderer RENDERER = new MeasurementTableCellRenderer();

    private MetricsFactory factory;

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu();
    private JMenu helpMenu = new JMenu();
    private JToolBar toolbar = new JToolBar();
    private JTextArea projectArea = new JTextArea();
    private JButton filterButton = new JButton("Filter:");
    private JTextField filterField = new JTextField("//");
    private StatusLine statusLine = new StatusLine(420);
    private JProgressBar progressBar = new JProgressBar();

    private OOMetricsTableModel groupsModel;
    private OOMetricsTableModel classesModel;
    private OOMetricsTableModel methodsModel;

    private File inputFile = new File(".");

    private boolean enableCrossClassMeasurements;

    public OOMetrics(MetricsFactory factory, boolean enableCrossClassMeasurements) {
        this.factory = factory;
        this.enableCrossClassMeasurements = enableCrossClassMeasurements;

        this.setSize(new Dimension(800, 600));
        this.setTitle("OO Metrics");
        this.setIconImage(new ImageIcon(getClass().getResource("icons/logoicon.gif")).getImage());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowKiller());

        groupsModel = new OOMetricsTableModel(factory.getConfiguration().getGroupMeasurements());
        classesModel = new OOMetricsTableModel(factory.getConfiguration().getClassMeasurements());
        methodsModel = new OOMetricsTableModel(factory.getConfiguration().getMethodMeasurements());

        buildMenus();
        buildUI();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            Logger.getLogger(OOMetrics.class).error("Unable to set look and feel", ex);
        }

        statusLine.showInfo("Ready.");
    }

    MetricsFactory getMetricsFactory() {
        return factory;
    }

    void setMetricsFactory(MetricsFactory factory) {
        this.factory = factory;
    }

    JTextArea getProjectArea() {
        return projectArea;
    }

    OOMetricsTableModel getGroupsModel() {
        return groupsModel;
    }

    OOMetricsTableModel getClassesModel() {
        return classesModel;
    }

    OOMetricsTableModel getMethodsModel() {
        return methodsModel;
    }

    File getInputFile() {
        return inputFile;
    }

    void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    JTextComponent getFilterField() {
        return filterField;
    }

    StatusLine getStatusLine() {
        return statusLine;
    }

    JProgressBar getProgressBar() {
        return progressBar;
    }

    public boolean isEnableCrossClassMeasurements() {
        return enableCrossClassMeasurements;
    }

    private void buildMenus() {
        buildFileMenu();
        buildHelpMenu();

        this.setJMenuBar(menuBar);
    }

    private void buildFileMenu() {
        menuBar.add(fileMenu);

        fileMenu.setText("File");

        Action action;
        JMenuItem menuItem;
        JButton button;

        action = new MetricsExtractAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
        menuItem.setMnemonic('e');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        toolbar.addSeparator();
        fileMenu.addSeparator();

        action = new NewMetricsAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        menuItem.setMnemonic('n');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        toolbar.addSeparator();
        fileMenu.addSeparator();

        action = new ExitAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        menuItem.setMnemonic('x');

        this.setJMenuBar(menuBar);
    }

    private void buildHelpMenu() {
        menuBar.add(helpMenu);

        helpMenu.setText("Help");

        Action action;
        JMenuItem menuItem;

        action = new AboutAction(this);
        menuItem = helpMenu.add(action);
        menuItem.setMnemonic('a');
    }
    
    private void buildUI() {
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(buildControlPanel(), BorderLayout.NORTH);
        this.getContentPane().add(buildResultPanel(), BorderLayout.CENTER);
        this.getContentPane().add(buildStatusPanel(), BorderLayout.SOUTH);
    }

    private JComponent buildControlPanel() {
        return toolbar;
    }
    
    private JComponent buildResultPanel() {
        JPanel result = new JPanel();

        result.setLayout(new BorderLayout());
        result.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildProjectPanel(), buildChartsPanel()), BorderLayout.CENTER);
        result.add(buildFilterPanel(), BorderLayout.SOUTH);

        return result;
    }

    private JComponent buildProjectPanel() {
        JComponent result = new JScrollPane(projectArea);

        projectArea.setEditable(false);

        return result;
    }

    private JComponent buildChartsPanel() {
        JTabbedPane result = new JTabbedPane();

        // result.setBorder(BorderFactory.createTitledBorder("Data"));
        result.addTab("Groups",  buildGroupsChartPanel());
        result.addTab("Classes", buildClassesChartPanel());
        result.addTab("Methods", buildMethodsChartPanel());

        return result;
    }

    private JComponent buildGroupsChartPanel() {
        return buildChartPanel(getGroupsModel());
    }

    private JComponent buildClassesChartPanel() {
        return buildChartPanel(getClassesModel());
    }

    private JComponent buildMethodsChartPanel() {
        return buildChartPanel(getMethodsModel());
    }

    private JComponent buildChartPanel(OOMetricsTableModel model) {
        JComponent result;

        JTable table = new JTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(true);
        table.setDefaultRenderer(Object.class, RENDERER);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        TableHeaderListener listener = new TableHeaderListener(table, model);
        table.getTableHeader().addMouseListener(listener);
        table.getTableHeader().addMouseMotionListener(listener);

        result = new JScrollPane(table);

        return result;
    }

    private JComponent buildFilterPanel() {
        JPanel result = new JPanel();

        result.setLayout(new BorderLayout());
        result.add(filterButton, BorderLayout.WEST);
        result.add(filterField,  BorderLayout.CENTER);

        filterButton.addActionListener(new FilterActionListener(this));

        return result;
    }

    private JComponent buildStatusPanel() {
        JPanel result = new JPanel();

        Dimension size = getProgressBar().getPreferredSize();
        size.width = 100;
        getProgressBar().setPreferredSize(size);
        getProgressBar().setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        result.setLayout(new BorderLayout());
        result.add(getStatusLine(),  BorderLayout.CENTER);
        result.add(getProgressBar(), BorderLayout.EAST);

        return result;
    }

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
    }

    public static void main(String[] args) throws Exception {
        // Parsing the command line
        CommandLine commandLine = new CommandLine(new NullParameterStrategy());
        commandLine.addSingleValueSwitch("default-configuration", true);
        commandLine.addSingleValueSwitch("configuration");
        commandLine.addToggleSwitch("validate");
        commandLine.addToggleSwitch("enable-cross-class-measurements");
        commandLine.addToggleSwitch("help");

        CommandLineUsage usage = new CommandLineUsage("OOMetrics");
        commandLine.accept(usage);

        try {
            commandLine.parse(args);
        } catch (IllegalArgumentException ex) {
            showError(usage, ex.toString());
            System.exit(1);
        }

        if (commandLine.getToggleSwitch("help")) {
            showError(usage);
            System.exit(1);
        }

        MetricsFactory factory;

        if (commandLine.isPresent("configuration")) {
            factory = new MetricsFactory("Project", new MetricsConfigurationLoader(commandLine.getToggleSwitch("validate")).load(commandLine.getSingleSwitch("configuration")));
        } else {
            factory = new MetricsFactory("Project", new MetricsConfigurationLoader(commandLine.getToggleSwitch("validate")).load(commandLine.getSingleSwitch("default-configuration")));
        }

        /*
         *  Beginning of main processing
         */

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Ignore
        }

        OOMetrics model = new OOMetrics(factory, commandLine.isPresent("enable-cross-class-measurements"));
        model.setVisible(true);
    }
}
