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

import com.jeantessier.classreader.ClassfileLoaderDispatcher;
import com.jeantessier.classreader.ClassfileLoaderEventSource;
import com.jeantessier.classreader.ModifiedOnlyDispatcher;
import com.jeantessier.classreader.Monitor;
import com.jeantessier.commandline.CommandLine;
import com.jeantessier.commandline.CommandLineUsage;
import com.jeantessier.commandline.NullParameterStrategy;
import com.jeantessier.dependency.*;
import com.jeantessier.dependency.TextMetricsReport;
import org.apache.logging.log4j.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class DependencyFinder extends JFrame {
    private static final String DEFAULT_FONT_NAME = "Monospaced";
    private static final String DESIRED_FONT_NAME = "Courier";

    private boolean minimize;
    private boolean maximize;

    private boolean advancedMode;
    private final JPanel  queryPanel   = new JPanel();
    
    private final JMenuBar          menuBar                = new JMenuBar();
    private final JMenu             fileMenu               = new JMenu();
    private final JMenu             viewMenu               = new JMenu();
    private final JMenu             helpMenu               = new JMenu();
    private final JToolBar          toolbar                = new JToolBar();
    private final JTextArea         dependenciesResultArea = new JTextArea();
    private final JTextArea         closureResultArea      = new JTextArea();
    private final JTextArea         metricsResultArea      = new JTextArea();
    private final MetricsTableModel metricsChartModel      = new MetricsTableModel();
    private final StatusLine        statusLine             = new StatusLine(420);
    private final JProgressBar      progressBar            = new JProgressBar();

    private Collection<String>        inputFiles  = null;
    private ClassfileLoaderDispatcher dispatcher  = null;
    private NodeFactory               nodeFactory = null;
    private Monitor                   monitor     = null;

    private final JCheckBox  packageScope          = new JCheckBox("packages");
    private final JCheckBox  classScope            = new JCheckBox("classes");
    private final JCheckBox  featureScope          = new JCheckBox("features");
    private final JTextField scopeIncludes         = new JTextField();
    private final JTextField packageScopeIncludes  = new JTextField();
    private final JTextField classScopeIncludes    = new JTextField();
    private final JTextField featureScopeIncludes  = new JTextField();
    private final JTextField scopeExcludes         = new JTextField();
    private final JTextField packageScopeExcludes  = new JTextField();
    private final JTextField classScopeExcludes    = new JTextField();
    private final JTextField featureScopeExcludes  = new JTextField();
    
    private final JCheckBox  packageFilter         = new JCheckBox("packages");
    private final JCheckBox  classFilter           = new JCheckBox("classes");
    private final JCheckBox  featureFilter         = new JCheckBox("features");
    private final JTextField filterIncludes        = new JTextField();
    private final JTextField packageFilterIncludes = new JTextField();
    private final JTextField classFilterIncludes   = new JTextField();
    private final JTextField featureFilterIncludes = new JTextField();
    private final JTextField filterExcludes        = new JTextField();
    private final JTextField packageFilterExcludes = new JTextField();
    private final JTextField classFilterExcludes   = new JTextField();
    private final JTextField featureFilterExcludes = new JTextField();

    private final JCheckBox  showInbounds          = new JCheckBox("<--");
    private final JCheckBox  showOutbounds         = new JCheckBox("-->");
    private final JCheckBox  showEmptyNodes        = new JCheckBox("empty elements");
    private final JCheckBox  copyOnly              = new JCheckBox("copy only");
    
    private final JTextField maximumInboundDepth   = new JTextField("0", 2);
    private final JTextField maximumOutboundDepth  = new JTextField(2);

    private GraphCopier dependenciesQuery    = null;
    
    public DependencyFinder(CommandLine commandLine) {
        this.setSize(new Dimension(800, 600));
        this.setTitle("Dependency Finder");
        this.setIconImage(new ImageIcon(getClass().getResource("icons/logoicon.gif")).getImage());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowKiller());

        setNewDependencyGraph();
        
        packageScope.setToolTipText("Select packages");
        classScope.setToolTipText("Select classes (with their package)");
        featureScope.setToolTipText("Select methods and fields (with their class and package)");
        scopeIncludes.setToolTipText("Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        packageScopeIncludes.setToolTipText("Package must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        classScopeIncludes.setToolTipText("Class must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        featureScopeIncludes.setToolTipText("Method or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        scopeExcludes.setToolTipText("Package, class, method, or field must NOT match any of these expressions. E.g., /Test/");
        packageScopeExcludes.setToolTipText("Package must NOT match any of these expressions. E.g., /Test/");
        classScopeExcludes.setToolTipText("Class must NOT match any of these expressions. E.g., /Test/");
        featureScopeExcludes.setToolTipText("Method or field must NOT match any of these expressions. E.g., /Test/");
        
        packageFilter.setToolTipText("Show dependencies to/from packages");
        classFilter.setToolTipText("Show dependencies to/from classes");
        featureFilter.setToolTipText("Show dependencies to/from methods and fields");
        filterIncludes.setToolTipText("Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        packageFilterIncludes.setToolTipText("Package at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        classFilterIncludes.setToolTipText("Class at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        featureFilterIncludes.setToolTipText("Method or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/");
        filterExcludes.setToolTipText("Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/");
        packageFilterExcludes.setToolTipText("Package at the other end of the dependency must NOT match any of these expressions. E.g., /Test/");
        classFilterExcludes.setToolTipText("Class at the other end of the dependency must NOT match any of these expressions. E.g., /Test/");
        featureFilterExcludes.setToolTipText("Method or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/");
        
        showInbounds.setToolTipText("Show dependencies that point to the selected packages, classes, methods, or fields");
        showOutbounds.setToolTipText("Show dependencies that originate from the selected packages, classes, methods, or fields");
        showEmptyNodes.setToolTipText("Show selected packages, classes, methods, and fields even if they do not have dependencies");
        copyOnly.setToolTipText("<html>Only copy explicit dependencies to the result graph.<br>Do not introduce implicit dependencies<br>where explicit dependencies match the regular expressions<br>but are otherwise out of scope</html>");

        showInbounds.setFont(getCodeFont(Font.BOLD, 14));
        showOutbounds.setFont(getCodeFont(Font.BOLD, 14));
        
        maximumInboundDepth.setToolTipText("Maximum hops against the direction dependencies.  Empty field means no limit.");
        maximumOutboundDepth.setToolTipText("Maximum hops in the direction of dependencies.  Empty field means no limit.");

        setAdvancedMode(false);
        
        buildMenus(commandLine);
        buildUI();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            LogManager.getLogger(DependencyFinder.class).error("Unable to set look and feel", ex);
        }
        
        statusLine.showInfo("Ready.");
    }

    private Font getCodeFont(int style, int size) {
        String fontName = Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .filter(name -> name.contains(DESIRED_FONT_NAME))
                .findFirst()
                .orElse(DEFAULT_FONT_NAME);

        return new Font(fontName, style, size);
    }

    private boolean isAdvancedMode() {
        return advancedMode;
    }

    void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;

        copyOnly.setVisible(advancedMode);
    }
    
    public boolean getMaximize() {
        return maximize;
    }

    private void setMaximize(boolean maximize) {
        this.maximize = maximize;
    }

    public boolean getMinimize() {
        return minimize;
    }

    private void setMinimize(boolean minimize) {
        this.minimize = minimize;
    }
    
    public ClassfileLoaderDispatcher getClassfileLoaderDispatcher() {
        return dispatcher;
    }

    private void setClassfileLoaderDispatcher(ClassfileLoaderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    private void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }
    
    public Collection<String> getInputFiles() {
        return inputFiles;
    }

    private void setInputFiles(Collection<String> inputFiles) {
        this.inputFiles = inputFiles;
    }
    
    public void addInputFile(File file) {
        inputFiles.add(file.toString());
    }

    public Collection<PackageNode> getPackages() {
        return getNodeFactory().getPackages().values();
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    private void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }
    
    StatusLine getStatusLine() {
        return statusLine;
    }
        
    JProgressBar getProgressBar() {
        return progressBar;
    }

    private void buildMenus(CommandLine commandLine) {
        buildFileMenu(commandLine);
        buildViewMenu();
        buildHelpMenu();

        this.setJMenuBar(menuBar);
    }
    
    private void buildFileMenu(CommandLine commandLine) {
        menuBar.add(fileMenu);

        fileMenu.setText("File");

        Action    action;
        JMenuItem menuItem;
        JButton   button;

        action = new DependencyExtractAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('e');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        action = new RefreshDependencyGraphAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('r');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        toolbar.addSeparator();
        fileMenu.addSeparator();
        
        action = new OpenFileAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('o');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));
        
        action = new SaveFileAction(this, commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('s');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        if (commandLine.isPresent("indent-text")) {
            ((SaveFileAction) action).setIndentText(commandLine.getSingleSwitch("indent-text"));
        }
        
        toolbar.addSeparator();
        fileMenu.addSeparator();
        
        action = new NewDependencyGraphAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('n');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        toolbar.addSeparator();
        fileMenu.addSeparator();
        
        action = new DependencyQueryAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('d');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        action = new ClosureQueryAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('c');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        action = new MetricsQueryAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('m');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        action = new AllQueriesAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('a');
        button = toolbar.add(action);
        button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

        toolbar.addSeparator();
        fileMenu.addSeparator();

        action = new ExitAction(this);
        menuItem = fileMenu.add(action);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        menuItem.setMnemonic('x');
    }
    
    private void buildViewMenu() {
        menuBar.add(viewMenu);

        viewMenu.setText("View");

        ButtonGroup group = new ButtonGroup();
        JMenuItem menuItem;
        
        menuItem = new JRadioButtonMenuItem(new SimpleQueryPanelAction(this));
        menuItem.setSelected(true);
        group.add(menuItem);
        viewMenu.add(menuItem);
        
        menuItem = new JRadioButtonMenuItem(new AdvancedQueryPanelAction(this));
        group.add(menuItem);
        viewMenu.add(menuItem);
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
        JPanel result = new JPanel();

        result.setLayout(new BorderLayout());
        result.add(toolbar, BorderLayout.NORTH);
        result.add(buildQueryPanel(), BorderLayout.CENTER);
        
        return result;
    }
    
    JComponent buildQueryPanel() {
        if (isAdvancedMode()) {
            buildAdvancedQueryPanel();
        } else {
            buildSimpleQueryPanel();
        }
        
        return queryPanel;
    }
    
    private void buildSimpleQueryPanel() {
        queryPanel.removeAll();
        queryPanel.setLayout(new GridLayout(1, 2));
        queryPanel.add(buildSimpleScopePanel());
        queryPanel.add(buildSimpleFilterPanel());
        queryPanel.revalidate();
    }
    
    private void buildAdvancedQueryPanel() {
        queryPanel.removeAll();
        queryPanel.setLayout(new GridLayout(1, 2));
        queryPanel.add(buildAdvancedScopePanel());
        queryPanel.add(buildAdvancedFilterPanel());
        queryPanel.revalidate();
    }

    private JComponent buildSimpleScopePanel() {
        JPanel result = new JPanel();

        result.setBorder(BorderFactory.createTitledBorder("Select programming elements"));

        result.setLayout(new BorderLayout());

        result.add(buildSimpleScopePanelCheckboxes(), BorderLayout.NORTH);
        result.add(buildSimpleScopePanelTextFields(), BorderLayout.SOUTH);

        return result;
    }
    
    private JComponent buildSimpleScopePanelTextFields() {
        JPanel result = new JPanel();

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 2);
        
        result.setLayout(gbl);

        JLabel scopeIncludesLabel = new JLabel("including:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(scopeIncludesLabel);
        gbl.setConstraints(scopeIncludesLabel, c);

        JLabel scopeExcludesLabel = new JLabel("excluding:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(scopeExcludesLabel);
        gbl.setConstraints(scopeExcludesLabel, c);

        // -scope-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(scopeIncludes);
        gbl.setConstraints(scopeIncludes, c);
        
        // -scope-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(scopeExcludes);
        gbl.setConstraints(scopeExcludes, c);

        return result;
    }

    private JComponent buildSimpleScopePanelCheckboxes() {
        JPanel result = new JPanel();

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 2);
        
        result.setLayout(gbl);

        // -package-scope
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageScope);
        gbl.setConstraints(packageScope, c);

        // -class-scope
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(classScope);
        gbl.setConstraints(classScope, c);

        // -feature-scope
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureScope);
        gbl.setConstraints(featureScope, c);

        return result;
    }
    
    private JComponent buildSimpleFilterPanel() {
        JPanel result = new JPanel();

        result.setBorder(BorderFactory.createTitledBorder("Show dependencies (stop for closure)"));

        result.setLayout(new BorderLayout());

        result.add(buildSimpleFilterPanelCheckboxes(), BorderLayout.NORTH);
        result.add(buildSimpleFilterPanelTextFields(), BorderLayout.SOUTH);

        return result;
    }
    
    private JComponent buildSimpleFilterPanelTextFields() {
        JPanel result = new JPanel();

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 2);
        
        result.setLayout(gbl);

        JLabel filterIncludesLabel = new JLabel("including:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(filterIncludesLabel);
        gbl.setConstraints(filterIncludesLabel, c);

        JLabel filterExcludesLabel = new JLabel("excluding:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(filterExcludesLabel);
        gbl.setConstraints(filterExcludesLabel, c);

        // -filter-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(filterIncludes);
        gbl.setConstraints(filterIncludes, c);

        // -filter-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(filterExcludes);
        gbl.setConstraints(filterExcludes, c);

        return result;
    }
    
    private JComponent buildSimpleFilterPanelCheckboxes() {
        JPanel result = new JPanel();

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(0, 2, 0, 2);
        
        result.setLayout(gbl);

        // -package-filter
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageFilter);
        gbl.setConstraints(packageFilter, c);

        // -class-filter
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(classFilter);
        gbl.setConstraints(classFilter, c);

        // -feature-filter
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureFilter);
        gbl.setConstraints(featureFilter, c);

        return result;
    }

    private JComponent buildAdvancedScopePanel() {
        JPanel result = new JPanel();

        result.setBorder(BorderFactory.createTitledBorder("Select programming elements"));

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(2, 0, 2, 5);
        
        result.setLayout(gbl);

        // -package-scope
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        result.add(packageScope);
        gbl.setConstraints(packageScope, c);

        // -class-scope
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.weighty = 0;
        result.add(classScope);
        gbl.setConstraints(classScope, c);

        // -feature-scope
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0;
        c.weighty = 0;
        result.add(featureScope);
        gbl.setConstraints(featureScope, c);

        JLabel scopeIncludesLabel = new JLabel("including:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(scopeIncludesLabel);
        gbl.setConstraints(scopeIncludesLabel, c);

        // -scope-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(scopeIncludes);
        gbl.setConstraints(scopeIncludes, c);

        // -package-scope-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageScopeIncludes);
        gbl.setConstraints(packageScopeIncludes, c);

        // -class-scope-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        result.add(classScopeIncludes);
        gbl.setConstraints(classScopeIncludes, c);

        // -feature-scope-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureScopeIncludes);
        gbl.setConstraints(featureScopeIncludes, c);

        JLabel scopeExcludesLabel = new JLabel("excluding:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(scopeExcludesLabel);
        gbl.setConstraints(scopeExcludesLabel, c);

        // -scope-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(scopeExcludes);
        gbl.setConstraints(scopeExcludes, c);

        // -package-scope-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageScopeExcludes);
        gbl.setConstraints(packageScopeExcludes, c);

        // -class-scope-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        result.add(classScopeExcludes);
        gbl.setConstraints(classScopeExcludes, c);

        // -feature-scope-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureScopeExcludes);
        gbl.setConstraints(featureScopeExcludes, c);

        return result;
    }
    
    private JComponent buildAdvancedFilterPanel() {
        JPanel result = new JPanel();

        result.setBorder(BorderFactory.createTitledBorder("Show dependencies (stop for closure)"));

        GridBagLayout      gbl = new GridBagLayout();
        GridBagConstraints c   = new GridBagConstraints();
        c.insets = new Insets(2, 0, 2, 5);
        
        result.setLayout(gbl);

        // -package-filter
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        result.add(packageFilter);
        gbl.setConstraints(packageFilter, c);

        // -class-filter
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.weighty = 0;
        result.add(classFilter);
        gbl.setConstraints(classFilter, c);

        // -feature-filter
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0;
        c.weighty = 0;
        result.add(featureFilter);
        gbl.setConstraints(featureFilter, c);

        JLabel filterIncludesLabel = new JLabel("including:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(filterIncludesLabel);
        gbl.setConstraints(filterIncludesLabel, c);

        // -filter-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(filterIncludes);
        gbl.setConstraints(filterIncludes, c);

        // -package-filter-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageFilterIncludes);
        gbl.setConstraints(packageFilterIncludes, c);

        // -class-filter-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        result.add(classFilterIncludes);
        gbl.setConstraints(classFilterIncludes, c);

        // -feature-filter-includes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureFilterIncludes);
        gbl.setConstraints(featureFilterIncludes, c);

        JLabel filterExcludesLabel = new JLabel("excluding:");
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        result.add(filterExcludesLabel);
        gbl.setConstraints(filterExcludesLabel, c);

        // -filter-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        result.add(filterExcludes);
        gbl.setConstraints(filterExcludes, c);

        // -package-filter-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0;
        result.add(packageFilterExcludes);
        gbl.setConstraints(packageFilterExcludes, c);

        // -class-filter-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        result.add(classFilterExcludes);
        gbl.setConstraints(classFilterExcludes, c);

        // -feature-filter-excludes
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 0;
        result.add(featureFilterExcludes);
        gbl.setConstraints(featureFilterExcludes, c);

        return result;
    }
    
    private JComponent buildResultPanel() {
        JTabbedPane result = new JTabbedPane();

        result.setBorder(BorderFactory.createTitledBorder("Results"));
        result.addTab("Dependencies", buildDependenciesPanel());
        result.addTab("Closure",      buildClosurePanel());
        result.addTab("Metrics",      buildMetricsPanel());
        
        return result;
    }

    private JComponent buildDependenciesPanel() {
        JPanel result = new JPanel();

        result.setLayout(new BorderLayout());
        result.add(buildPrinterControlPanel(),     BorderLayout.NORTH);
        result.add(buildDependenciesResultPanel(), BorderLayout.CENTER);
        
        return result;
    }
    
    private JComponent buildPrinterControlPanel() {
        JPanel result = new JPanel();

        result.add(new JLabel("Show "));
        result.add(showInbounds);
        result.add(showOutbounds);
        result.add(showEmptyNodes);
        result.add(copyOnly);

        PrinterControlAction action = new PrinterControlAction(this);
        showInbounds.addActionListener(action);
        showOutbounds.addActionListener(action);
        showEmptyNodes.addActionListener(action);

        return result;
    }
    
    private JComponent buildDependenciesResultPanel() {
        JComponent result = new JScrollPane(dependenciesResultArea);
        
        dependenciesResultArea.setEditable(false);
        dependenciesResultArea.setFont(getCodeFont(Font.PLAIN, 12));
        
        return result;
    }
    
    private JComponent buildClosurePanel() {
        JPanel result = new JPanel();
        
        result.setLayout(new BorderLayout());
        result.add(buildClosureControlPanel(), BorderLayout.NORTH);
        result.add(buildClosureResultPanel(),  BorderLayout.CENTER);
        
        return result;
    }
    
    private JComponent buildClosureControlPanel() {
        JPanel result = new JPanel();
        
        result.add(new JLabel("Follow inbounds: "));
        result.add(maximumInboundDepth);
        result.add(new JLabel("Follow outbounds: "));
        result.add(maximumOutboundDepth);
        
        return result;
    }

    private JComponent buildClosureResultPanel() {
        JComponent result = new JScrollPane(closureResultArea);
        
        closureResultArea.setEditable(false);
        closureResultArea.setFont(getCodeFont(Font.PLAIN, 12));
        
        return result;
    }
    
    private JComponent buildMetricsPanel() {
        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildMetricsResultPanel(), buildMetricsChartPanel());
    }

    private JComponent buildMetricsResultPanel() {
        JComponent result = new JScrollPane(metricsResultArea);

        metricsResultArea.setEditable(false);
        
        return result;
    }

    private JComponent buildMetricsChartPanel() {
        JComponent result;

        JTable table = new JTable(metricsChartModel);

        table.setCellSelectionEnabled(true);
        table.setColumnSelectionAllowed(true);
        
        result = new JScrollPane(table);

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
    
    public void resetQuery() {
        packageScope.setSelected(true);
        classScope.setSelected(false);
        featureScope.setSelected(false);
        scopeIncludes.setText("//");
        packageScopeIncludes.setText("");
        classScopeIncludes.setText("");
        featureScopeIncludes.setText("");
        scopeExcludes.setText("");
        packageScopeExcludes.setText("");
        classScopeExcludes.setText("");
        featureScopeExcludes.setText("");
    
        packageFilter.setSelected(true);
        classFilter.setSelected(false);
        featureFilter.setSelected(false);
        filterIncludes.setText("//");
        packageFilterIncludes.setText("");
        classFilterIncludes.setText("");
        featureFilterIncludes.setText("");
        filterExcludes.setText("");
        packageFilterExcludes.setText("");
        classFilterExcludes.setText("");
        featureFilterExcludes.setText("");

        showInbounds.setSelected(true);
        showOutbounds.setSelected(true);
        showEmptyNodes.setSelected(true);
        copyOnly.setSelected(false);
    }
    
    void clearDependencyResult() {
        dependenciesQuery = null;
        dependenciesResultArea.setText("");
    }
    
    void doDependencyQuery() {
        var scopeCriteria = new RegularExpressionSelectionCriteria();
        
        scopeCriteria.setMatchingPackages(packageScope.isSelected());
        scopeCriteria.setMatchingClasses(classScope.isSelected());
        scopeCriteria.setMatchingFeatures(featureScope.isSelected());
        scopeCriteria.setGlobalIncludes(scopeIncludes.getText());
        scopeCriteria.setGlobalExcludes(scopeExcludes.getText());

        if (isAdvancedMode()) {
            scopeCriteria.setPackageIncludes(packageScopeIncludes.getText());
            scopeCriteria.setClassIncludes(classScopeIncludes.getText());
            scopeCriteria.setFeatureIncludes(featureScopeIncludes.getText());
            scopeCriteria.setPackageExcludes(packageScopeExcludes.getText());
            scopeCriteria.setClassExcludes(classScopeExcludes.getText());
            scopeCriteria.setFeatureExcludes(featureScopeExcludes.getText());
        }
    
        var filterCriteria = new RegularExpressionSelectionCriteria();
        
        filterCriteria.setMatchingPackages(packageFilter.isSelected());
        filterCriteria.setMatchingClasses(classFilter.isSelected());
        filterCriteria.setMatchingFeatures(featureFilter.isSelected());
        filterCriteria.setGlobalIncludes(filterIncludes.getText());
        filterCriteria.setGlobalExcludes(filterExcludes.getText());

        if (isAdvancedMode()) {
            filterCriteria.setPackageIncludes(packageFilterIncludes.getText());
            filterCriteria.setClassIncludes(classFilterIncludes.getText());
            filterCriteria.setFeatureIncludes(featureFilterIncludes.getText());
            filterCriteria.setPackageExcludes(packageFilterExcludes.getText());
            filterCriteria.setClassExcludes(classFilterExcludes.getText());
            filterCriteria.setFeatureExcludes(featureFilterExcludes.getText());
        }

        if ((isAdvancedMode() && copyOnly.isSelected()) || getMaximize()) {
            var strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
            dependenciesQuery = new GraphCopier(strategy);
        } else {
            dependenciesQuery = new GraphSummarizer(scopeCriteria, filterCriteria);
        }
        
        dependenciesQuery.traverseNodes(getPackages());

        refreshDependenciesDisplay();
    }

    void refreshDependenciesDisplay() {
        if (dependenciesQuery != null) {
            var out = new StringWriter();
            var printer = new com.jeantessier.dependency.TextPrinter(new PrintWriter(out));

            printer.setShowInbounds(showInbounds.isSelected());
            printer.setShowOutbounds(showOutbounds.isSelected());
            printer.setShowEmptyNodes(showEmptyNodes.isSelected());

            printer.traverseNodes(dependenciesQuery.getScopeFactory().getPackages().values());

            dependenciesResultArea.setText(out.toString());
        }
    }
    
    void clearClosureResult() {
        closureResultArea.setText("");
    }
    
    void doClosureQuery() {
        var startCriteria = new RegularExpressionSelectionCriteria();
        
        startCriteria.setGlobalIncludes(scopeIncludes.getText());
        startCriteria.setGlobalExcludes(scopeExcludes.getText());

        if (isAdvancedMode()) {
            startCriteria.setPackageIncludes(packageScopeIncludes.getText());
            startCriteria.setClassIncludes(classScopeIncludes.getText());
            startCriteria.setFeatureIncludes(featureScopeIncludes.getText());
            startCriteria.setPackageExcludes(packageScopeExcludes.getText());
            startCriteria.setClassExcludes(classScopeExcludes.getText());
            startCriteria.setFeatureExcludes(featureScopeExcludes.getText());
        }
    
        var stopCriteria = new RegularExpressionSelectionCriteria();
        
        stopCriteria.setGlobalIncludes(filterIncludes.getText());
        stopCriteria.setGlobalExcludes(filterExcludes.getText());

        if (isAdvancedMode()) {
            stopCriteria.setPackageIncludes(packageFilterIncludes.getText());
            stopCriteria.setClassIncludes(classFilterIncludes.getText());
            stopCriteria.setFeatureIncludes(featureFilterIncludes.getText());
            stopCriteria.setPackageExcludes(packageFilterExcludes.getText());
            stopCriteria.setClassExcludes(classFilterExcludes.getText());
            stopCriteria.setFeatureExcludes(featureFilterExcludes.getText());
        }

        var selector = new TransitiveClosure(startCriteria, stopCriteria);

        try {
            selector.setMaximumInboundDepth(Long.parseLong(maximumInboundDepth.getText()));
        } catch (NumberFormatException ex) {
            selector.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }

        try {
            selector.setMaximumOutboundDepth(Long.parseLong(maximumOutboundDepth.getText()));
        } catch (NumberFormatException ex) {
            selector.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
        }
        
        selector.traverseNodes(getPackages());

        var scopeCriteria = new RegularExpressionSelectionCriteria();
        
        scopeCriteria.setMatchingPackages(packageScope.isSelected());
        scopeCriteria.setMatchingClasses(classScope.isSelected());
        scopeCriteria.setMatchingFeatures(featureScope.isSelected());
        scopeCriteria.setGlobalIncludes("//");

        var filterCriteria = new RegularExpressionSelectionCriteria();
        
        filterCriteria.setMatchingPackages(packageFilter.isSelected());
        filterCriteria.setMatchingClasses(classFilter.isSelected());
        filterCriteria.setMatchingFeatures(featureFilter.isSelected());
        filterCriteria.setGlobalIncludes("//");

        var summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
        summarizer.traverseNodes(selector.getFactory().getPackages().values());
        
        var out = new StringWriter();
        var printer = new com.jeantessier.dependency.TextPrinter(new PrintWriter(out));
        printer.traverseNodes(summarizer.getScopeFactory().getPackages().values());
        closureResultArea.setText(out.toString());
    }
    
    void clearMetricsResult() {
        metricsResultArea.setText("");
    }
    
    void doMetricsQuery() {
        var scopeCriteria = new RegularExpressionSelectionCriteria();
        
        scopeCriteria.setMatchingPackages(packageScope.isSelected());
        scopeCriteria.setMatchingClasses(classScope.isSelected());
        scopeCriteria.setMatchingFeatures(featureScope.isSelected());
        scopeCriteria.setGlobalIncludes(scopeIncludes.getText());
        scopeCriteria.setGlobalExcludes(scopeExcludes.getText());

        if (isAdvancedMode()) {
            scopeCriteria.setPackageIncludes(packageScopeIncludes.getText());
            scopeCriteria.setClassIncludes(classScopeIncludes.getText());
            scopeCriteria.setFeatureIncludes(featureScopeIncludes.getText());
            scopeCriteria.setPackageExcludes(packageScopeExcludes.getText());
            scopeCriteria.setClassExcludes(classScopeExcludes.getText());
            scopeCriteria.setFeatureExcludes(featureScopeExcludes.getText());
        }
    
        var filterCriteria = new RegularExpressionSelectionCriteria();
        
        filterCriteria.setMatchingPackages(packageFilter.isSelected());
        filterCriteria.setMatchingClasses(classFilter.isSelected());
        filterCriteria.setMatchingFeatures(featureFilter.isSelected());
        filterCriteria.setGlobalIncludes(filterIncludes.getText());
        filterCriteria.setGlobalExcludes(filterExcludes.getText());

        if (isAdvancedMode()) {
            filterCriteria.setPackageIncludes(packageFilterIncludes.getText());
            filterCriteria.setClassIncludes(classFilterIncludes.getText());
            filterCriteria.setFeatureIncludes(featureFilterIncludes.getText());
            filterCriteria.setPackageExcludes(packageFilterExcludes.getText());
            filterCriteria.setClassExcludes(classFilterExcludes.getText());
            filterCriteria.setFeatureExcludes(featureFilterExcludes.getText());
        }

        var strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
        var metrics  = new com.jeantessier.dependency.MetricsGatherer(strategy);
        
        metrics.traverseNodes(getPackages());

        var out = new StringWriter();
        var report = new TextMetricsReport(new PrintWriter(out));
        report.process(metrics);
        
        metricsResultArea.setText(out.toString());
        metricsChartModel.setMetrics(metrics);
    }

    void setNewDependencyGraph() {
        setInputFiles(new LinkedList<>());
        setClassfileLoaderDispatcher(new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER));

        var factory = new NodeFactory();
        setNodeFactory(factory);

        var collector = new CodeDependencyCollector(factory);
        var deletingVisitor = new DeletingVisitor(factory);
        setMonitor(new Monitor(collector, deletingVisitor));

        resetQuery();
    }

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
    }

    public static void main(String[] args) {
        // Parsing the command line
        CommandLine commandLine = new CommandLine(new NullParameterStrategy());
        commandLine.addToggleSwitch("minimize");
        commandLine.addToggleSwitch("maximize");
        commandLine.addSingleValueSwitch("encoding", com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix", com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text", com.jeantessier.dependency.XMLPrinter.DEFAULT_INDENT_TEXT);
        commandLine.addToggleSwitch("help");

        CommandLineUsage usage = new CommandLineUsage("DependencyFinder");
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

        if (commandLine.getToggleSwitch("maximize") && commandLine.getToggleSwitch("minimize")) {
            showError(usage, "Only one of -maximize or -minimize allowed");
        }

        /*
         *  Beginning of main processing
         */

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // Ignore
        }

        DependencyFinder model = new DependencyFinder(commandLine);
        model.setMaximize(commandLine.getToggleSwitch("maximize"));
        model.setMinimize(commandLine.getToggleSwitch("minimize"));
        model.setVisible(true);
    }
}
