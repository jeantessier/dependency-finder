/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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
import java.util.*;

import javax.swing.*;

import org.apache.log4j.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

public class DependencyFinder extends JFrame {
    public static final String DEFAULT_LOGFILE   = "System.out";
    public static final String DEFAULT_TRACEFILE = "System.out";

	private static final Layout DEFAULT_LOG_LAYOUT = new PatternLayout("[%d{yyyy/MM/dd HH:mm:ss.SSS}] %c %m%n");

    private boolean minimize;
    private boolean maximize;
	
    private JMenuBar          menu_bar                 = new JMenuBar();
    private JMenu             file_menu                = new JMenu();
    private JToolBar          toolbar                  = new JToolBar();
    private JTextArea         dependencies_result_area = new JTextArea();
    private JTextArea         closure_result_area      = new JTextArea();
    private JTextArea         metrics_result_area      = new JTextArea();
    private MetricsTableModel metrics_chart_model      = new MetricsTableModel();
    private StatusLine        status_line              = new StatusLine(420);

    private File        input_file   = new File(".");
    private Collection  packages     = Collections.EMPTY_LIST;
    private NodeFactory node_factory = null;
    private Collector   collector    = null;

    private JCheckBox  package_scope           = new JCheckBox("packages", true);
    private JCheckBox  class_scope             = new JCheckBox("classes",  true);
    private JCheckBox  feature_scope           = new JCheckBox("features", true);
    private JTextField scope_includes          = new JTextField("//");
    private JTextField package_scope_includes  = new JTextField();
    private JTextField class_scope_includes    = new JTextField();
    private JTextField feature_scope_includes  = new JTextField();
    private JTextField scope_excludes          = new JTextField();
    private JTextField package_scope_excludes  = new JTextField();
    private JTextField class_scope_excludes    = new JTextField();
    private JTextField feature_scope_excludes  = new JTextField();
	
    private JCheckBox  package_filter          = new JCheckBox("packages", true);
    private JCheckBox  class_filter            = new JCheckBox("classes",  true);
    private JCheckBox  feature_filter          = new JCheckBox("features", true);
    private JTextField filter_includes         = new JTextField("//");
    private JTextField package_filter_includes = new JTextField();
    private JTextField class_filter_includes   = new JTextField();
    private JTextField feature_filter_includes = new JTextField();
    private JTextField filter_excludes         = new JTextField();
    private JTextField package_filter_excludes = new JTextField();
    private JTextField class_filter_excludes   = new JTextField();
    private JTextField feature_filter_excludes = new JTextField();

    private JCheckBox  show_inbounds           = new JCheckBox("inbounds",    true);
    private JCheckBox  show_outbounds          = new JCheckBox("outbounds",   false);
    private JCheckBox  show_empty_nodes        = new JCheckBox("empty nodes", false);

    private GraphCopier dependencies_query     = null;
	
    public DependencyFinder() {
		this.setSize(new Dimension(800, 600));
		this.setTitle("Dependency Finder");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					System.exit(0);
				}
			});

		BuildMenus();
		BuildUI();

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception ex) {
			System.err.println("Trying to set look and feel: " + ex);
		}
		
		status_line.ShowInfo("Ready.");
    }

    public boolean Maximize() {
		return maximize;
    }

    public void Maximize(boolean maximize) {
		this.maximize = maximize;
    }

    public boolean Minimize() {
		return minimize;
    }

    public void Minimize(boolean minimize) {
		this.minimize = minimize;
    }
	
    public File InputFile() {
		return input_file;
    }

    public void InputFile(File input_file) {
		this.input_file = input_file;
    }

    public Collection Packages() {
		return packages;
    }

    public void Packages(Collection packages) {
		this.packages = packages;
    }

    public NodeFactory NodeFactory() {
		return node_factory;
    }

    public void NodeFactory(NodeFactory node_factory) {
		this.node_factory = node_factory;
    }

    public Collector Collector() {
		return collector;
    }

    public void Collector(Collector collector) {
		this.collector = collector;
    }
	
    StatusLine StatusLine() {
		return status_line;
    }
	
    private void BuildMenus() {
		menu_bar.add(file_menu);

		file_menu.setText("File");

		Action action;
		JMenuItem menu_item;
		JButton button;
		
		action = new OpenFileAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		menu_item.setMnemonic('o');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		action = new DependencyExtractAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		menu_item.setMnemonic('e');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		toolbar.addSeparator();
		file_menu.addSeparator();
		
		action = new SaveFileAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		menu_item.setMnemonic('s');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		toolbar.addSeparator();
		file_menu.addSeparator();
		
		action = new NewDependencyGraphAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menu_item.setMnemonic('n');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		toolbar.addSeparator();
		file_menu.addSeparator();
		
		action = new DependencyQueryAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
		menu_item.setMnemonic('d');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		action = new ClosureQueryAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
		menu_item.setMnemonic('c');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		action = new MetricsQueryAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
		menu_item.setMnemonic('m');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		action = new AllQueriesAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		menu_item.setMnemonic('q');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		toolbar.addSeparator();
		file_menu.addSeparator();

		action = new ExitAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
		menu_item.setMnemonic('x');
		// button = toolbar.add(action);
		// button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		this.setJMenuBar(menu_bar);
    }
	
    private void BuildUI() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(BuildControlPanel(), BorderLayout.NORTH);
		this.getContentPane().add(BuildResultPanel(), BorderLayout.CENTER);
		this.getContentPane().add(BuildStatusPanel(), BorderLayout.SOUTH);
    }

    private JComponent BuildControlPanel() {
		JPanel result = new JPanel();

		result.setLayout(new BorderLayout());
		result.add(toolbar, BorderLayout.NORTH);
		result.add(BuildQueryPanel(), BorderLayout.CENTER);
		
		return result;
    }
	
    private JComponent BuildQueryPanel() {
		JPanel result = new JPanel();

		result.setLayout(new GridLayout(1, 2));
		result.add(BuildScopePanel());
		result.add(BuildFilterPanel());

		return result;
    }

    private JComponent BuildScopePanel() {
		JPanel result = new JPanel();

		result.setBorder(BorderFactory.createTitledBorder("Scope"));

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
		result.add(package_scope);
		gbl.setConstraints(package_scope, c);

		// -class-scope
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.weighty = 0;
		result.add(class_scope);
		gbl.setConstraints(class_scope, c);

		// -feature-scope
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.weighty = 0;
		result.add(feature_scope);
		gbl.setConstraints(feature_scope, c);

		JLabel scope_includes_label = new JLabel("includes");
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		result.add(scope_includes_label);
		gbl.setConstraints(scope_includes_label, c);

		// -scope-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		result.add(scope_includes);
		gbl.setConstraints(scope_includes, c);

		// -package-scope-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0;
		result.add(package_scope_includes);
		gbl.setConstraints(package_scope_includes, c);

		// -class-scope-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 0;
		result.add(class_scope_includes);
		gbl.setConstraints(class_scope_includes, c);

		// -feature-scope-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 0;
		result.add(feature_scope_includes);
		gbl.setConstraints(feature_scope_includes, c);

		JLabel scope_excludes_label = new JLabel("excludes");
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		result.add(scope_excludes_label);
		gbl.setConstraints(scope_excludes_label, c);

		// -scope-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		result.add(scope_excludes);
		gbl.setConstraints(scope_excludes, c);

		// -package-scope-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0;
		result.add(package_scope_excludes);
		gbl.setConstraints(package_scope_excludes, c);

		// -class-scope-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 0;
		result.add(class_scope_excludes);
		gbl.setConstraints(class_scope_excludes, c);

		// -feature-scope-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 0;
		result.add(feature_scope_excludes);
		gbl.setConstraints(feature_scope_excludes, c);

		return result;
    }
	
    private JComponent BuildFilterPanel() {
		JPanel result = new JPanel();

		result.setBorder(BorderFactory.createTitledBorder("Filter"));

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
		result.add(package_filter);
		gbl.setConstraints(package_filter, c);

		// -class-filter
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0;
		c.weighty = 0;
		result.add(class_filter);
		gbl.setConstraints(class_filter, c);

		// -feature-filter
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.weighty = 0;
		result.add(feature_filter);
		gbl.setConstraints(feature_filter, c);

		JLabel filter_includes_label = new JLabel("includes");
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		result.add(filter_includes_label);
		gbl.setConstraints(filter_includes_label, c);

		// -filter-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		result.add(filter_includes);
		gbl.setConstraints(filter_includes, c);

		// -package-filter-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0;
		result.add(package_filter_includes);
		gbl.setConstraints(package_filter_includes, c);

		// -class-filter-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 0;
		result.add(class_filter_includes);
		gbl.setConstraints(class_filter_includes, c);

		// -feature-filter-includes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 0;
		result.add(feature_filter_includes);
		gbl.setConstraints(feature_filter_includes, c);

		JLabel filter_excludes_label = new JLabel("excludes");
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		result.add(filter_excludes_label);
		gbl.setConstraints(filter_excludes_label, c);

		// -filter-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0;
		result.add(filter_excludes);
		gbl.setConstraints(filter_excludes, c);

		// -package-filter-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 0;
		result.add(package_filter_excludes);
		gbl.setConstraints(package_filter_excludes, c);

		// -class-filter-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 1;
		c.weighty = 0;
		result.add(class_filter_excludes);
		gbl.setConstraints(class_filter_excludes, c);

		// -feature-filter-excludes
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 4;
		c.weightx = 1;
		c.weighty = 0;
		result.add(feature_filter_excludes);
		gbl.setConstraints(feature_filter_excludes, c);

		return result;
    }
	
    private JComponent BuildResultPanel() {
		JTabbedPane result = new JTabbedPane();

		result.setBorder(BorderFactory.createTitledBorder("Results"));
		result.addTab("Dependencies", BuildDependenciesPanel());
		result.addTab("Closure",      BuildClosurePanel());
		result.addTab("Metrics",      BuildMetricsPanel());
		
		return result;
    }

    private JComponent BuildDependenciesPanel() {
		JPanel result = new JPanel();
		
		result.setLayout(new BorderLayout());
		result.add(BuildPrinterControlPanel(),     BorderLayout.NORTH);
		result.add(BuildDependenciesResultPanel(), BorderLayout.CENTER);
		
		return result;
    }
	
    private JComponent BuildPrinterControlPanel() {
		JPanel result = new JPanel();
		
		result.add(new JLabel("Show: "));
		result.add(show_inbounds);
		result.add(show_outbounds);
		result.add(show_empty_nodes);

		PrinterControlAction action = new PrinterControlAction(this);
		show_inbounds.addActionListener(action);
		show_outbounds.addActionListener(action);
		show_empty_nodes.addActionListener(action);
		
		return result;
    }
	
    private JComponent BuildDependenciesResultPanel() {
		JComponent result = new JScrollPane(dependencies_result_area);
		
		dependencies_result_area.setEditable(false);
		
		return result;
    }
	
    private JComponent BuildClosurePanel() {
		JComponent result = new JScrollPane(closure_result_area);
		
		closure_result_area.setEditable(false);
		
		return result;
    }
	
    private JComponent BuildMetricsPanel() {
		return new JSplitPane(JSplitPane.VERTICAL_SPLIT, BuildMetricsResultPanel(), BuildMetricsChartPanel());
    }

    private JComponent BuildMetricsResultPanel() {
		JComponent result = new JScrollPane(metrics_result_area);

		metrics_result_area.setEditable(false);
		
		return result;
    }

    private JComponent BuildMetricsChartPanel() {
		JComponent result;

		JTable table = new JTable(metrics_chart_model);

		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		
		result = new JScrollPane(table);

		return result;
    }
	
    private JComponent BuildStatusPanel() {
		return StatusLine();
    }
	
    public void ResetQuery() {
		package_scope.setSelected(true);
		class_scope.setSelected(true);
		feature_scope.setSelected(true);
		scope_includes.setText("//");
		package_scope_includes.setText("");
		class_scope_includes.setText("");
		feature_scope_includes.setText("");
		scope_excludes.setText("");
		package_scope_excludes.setText("");
		class_scope_excludes.setText("");
		feature_scope_excludes.setText("");
	
		package_filter.setSelected(true);
		class_filter.setSelected(true);
		feature_filter.setSelected(true);
		filter_includes.setText("//");
		package_filter_includes.setText("");
		class_filter_includes.setText("");
		feature_filter_includes.setText("");
		filter_excludes.setText("");
		package_filter_excludes.setText("");
		class_filter_excludes.setText("");
		feature_filter_excludes.setText("");
    }
	
    void ClearDependencyResult() {
		dependencies_query = null;
		dependencies_result_area.setText("");
    }
	
    void DependencyQuery() {
		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		
		strategy.PackageScope(package_scope.isSelected());
		strategy.ClassScope(class_scope.isSelected());
		strategy.FeatureScope(feature_scope.isSelected());
		strategy.ScopeIncludes(scope_includes.getText());
		strategy.PackageScopeIncludes(package_scope_includes.getText());
		strategy.ClassScopeIncludes(class_scope_includes.getText());
		strategy.FeatureScopeIncludes(feature_scope_includes.getText());
		strategy.ScopeExcludes(scope_excludes.getText());
		strategy.PackageScopeExcludes(package_scope_excludes.getText());
		strategy.ClassScopeExcludes(class_scope_excludes.getText());
		strategy.FeatureScopeExcludes(feature_scope_excludes.getText());
	
		strategy.PackageFilter(package_filter.isSelected());
		strategy.ClassFilter(class_filter.isSelected());
		strategy.FeatureFilter(feature_filter.isSelected());
		strategy.FilterIncludes(filter_includes.getText());
		strategy.PackageFilterIncludes(package_filter_includes.getText());
		strategy.ClassFilterIncludes(class_filter_includes.getText());
		strategy.FeatureFilterIncludes(feature_filter_includes.getText());
		strategy.FilterExcludes(filter_excludes.getText());
		strategy.PackageFilterExcludes(package_filter_excludes.getText());
		strategy.ClassFilterExcludes(class_filter_excludes.getText());
		strategy.FeatureFilterExcludes(feature_filter_excludes.getText());

		if (Maximize()) {
			dependencies_query = new GraphCopier(strategy);
		} else {
			dependencies_query = new GraphSummarizer(strategy);
		}
		
		dependencies_query.TraverseNodes(Packages());

		RefreshDependenciesDisplay();
    }

    void RefreshDependenciesDisplay() {
		if (dependencies_query != null) {
			com.jeantessier.dependency.PrettyPrinter printer = new com.jeantessier.dependency.PrettyPrinter();

			printer.ShowInbounds(show_inbounds.isSelected());
			printer.ShowOutbounds(show_outbounds.isSelected());
			printer.ShowEmptyNodes(show_empty_nodes.isSelected());
			
			printer.TraverseNodes(dependencies_query.Scope());
			
			dependencies_result_area.setText(printer.toString());
		}
    }		
	
    void ClearClosureResult() {
		closure_result_area.setText("");
    }
	
    void ClosureQuery() {
		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		
		strategy.PackageScope(package_scope.isSelected());
		strategy.ClassScope(class_scope.isSelected());
		strategy.FeatureScope(feature_scope.isSelected());
		strategy.ScopeIncludes(scope_includes.getText());
		strategy.PackageScopeIncludes(package_scope_includes.getText());
		strategy.ClassScopeIncludes(class_scope_includes.getText());
		strategy.FeatureScopeIncludes(feature_scope_includes.getText());
		strategy.ScopeExcludes(scope_excludes.getText());
		strategy.PackageScopeExcludes(package_scope_excludes.getText());
		strategy.ClassScopeExcludes(class_scope_excludes.getText());
		strategy.FeatureScopeExcludes(feature_scope_excludes.getText());
	
		strategy.PackageFilter(package_filter.isSelected());
		strategy.ClassFilter(class_filter.isSelected());
		strategy.FeatureFilter(feature_filter.isSelected());
		strategy.FilterIncludes(filter_includes.getText());
		strategy.PackageFilterIncludes(package_filter_includes.getText());
		strategy.ClassFilterIncludes(class_filter_includes.getText());
		strategy.FeatureFilterIncludes(feature_filter_includes.getText());
		strategy.FilterExcludes(filter_excludes.getText());
		strategy.PackageFilterExcludes(package_filter_excludes.getText());
		strategy.ClassFilterExcludes(class_filter_excludes.getText());
		strategy.FeatureFilterExcludes(feature_filter_excludes.getText());
		
		GraphCopier copier = new TransitiveClosure(strategy);
		
		copier.TraverseNodes(Packages());
		
		com.jeantessier.dependency.Printer printer = new com.jeantessier.dependency.PrettyPrinter();
		printer.TraverseNodes(copier.Scope());
		closure_result_area.setText(printer.toString());
    }		
	
    void ClearMetricsResult() {
		metrics_result_area.setText("");
    }
	
    void MetricsQuery() {
		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		
		strategy.PackageScope(package_scope.isSelected());
		strategy.ClassScope(class_scope.isSelected());
		strategy.FeatureScope(feature_scope.isSelected());
		strategy.ScopeIncludes(scope_includes.getText());
		strategy.PackageScopeIncludes(package_scope_includes.getText());
		strategy.ClassScopeIncludes(class_scope_includes.getText());
		strategy.FeatureScopeIncludes(feature_scope_includes.getText());
		strategy.ScopeExcludes(scope_excludes.getText());
		strategy.PackageScopeExcludes(package_scope_excludes.getText());
		strategy.ClassScopeExcludes(class_scope_excludes.getText());
		strategy.FeatureScopeExcludes(feature_scope_excludes.getText());
	
		strategy.PackageFilter(package_filter.isSelected());
		strategy.ClassFilter(class_filter.isSelected());
		strategy.FeatureFilter(feature_filter.isSelected());
		strategy.FilterIncludes(filter_includes.getText());
		strategy.PackageFilterIncludes(package_filter_includes.getText());
		strategy.ClassFilterIncludes(class_filter_includes.getText());
		strategy.FeatureFilterIncludes(feature_filter_includes.getText());
		strategy.FilterExcludes(filter_excludes.getText());
		strategy.PackageFilterExcludes(package_filter_excludes.getText());
		strategy.ClassFilterExcludes(class_filter_excludes.getText());
		strategy.FeatureFilterExcludes(feature_filter_excludes.getText());
		
		com.jeantessier.dependency.MetricsGatherer metrics = new com.jeantessier.dependency.MetricsGatherer(strategy);
		
		metrics.TraverseNodes(Packages());

		MetricsReport report = new MetricsReport();
		// report.ClassesPerPackageChart(true);
		// report.FeaturesPerClassChart(true);
		// report.InboundsPerPackageChart(true);
		// report.OutboundsPerPackageChart(true);
		// report.InboundsPerClassChart(true);
		// report.OutboundsPerClassChart(true);
		// report.InboundsPerFeatureChart(true);
		// report.OutboundsPerFeatureChart(true);
		report.Process(metrics);
		
		metrics_result_area.setText(report.toString());
		metrics_chart_model.Metrics(metrics);
    }		

    void NewDependencyGraph() {
		Packages(Collections.EMPTY_SET);
		ResetQuery();
		NodeFactory(null);
		Collector(null);
    }

    public static void Error(CommandLineUsage clu, String msg) {
		System.err.println(msg);
		Error(clu);
    }

    public static void Error(CommandLineUsage clu) {
		System.err.println(clu);
    }

    public static void main(String[] args) throws Exception {
		// Parsing the command line
		CommandLine command_line = new CommandLine(new NullParameterStrategy());
		command_line.AddToggleSwitch("minimize");
		command_line.AddToggleSwitch("maximize");
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",   DEFAULT_TRACEFILE);

		CommandLineUsage usage = new CommandLineUsage("DependencyFinder");
		command_line.Accept(usage);

		try {
			command_line.Parse(args);
		} catch (IllegalArgumentException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		} catch (CommandLineException ex) {
			Error(usage, ex.toString());
			System.exit(1);
		}

		if (command_line.ToggleSwitch("help")) {
			Error(usage);
			System.exit(1);
		}

		if (command_line.ToggleSwitch("maximize") && command_line.ToggleSwitch("minimize")) {
			Error(usage, "Only one of -maximize or -minimize allowed");
		}

		if (command_line.IsPresent("verbose")) {
			Logger logger = Logger.getLogger("com.jeantessier.dependency");
			logger.setLevel(Level.DEBUG);
			
			if ("System.out".equals(command_line.OptionalSwitch("verbose"))) {
				logger.addAppender(new ConsoleAppender(DEFAULT_LOG_LAYOUT));
			} else {
				logger.addAppender(new WriterAppender(DEFAULT_LOG_LAYOUT, new FileWriter(command_line.OptionalSwitch("verbose"))));
			}
		}

		if (command_line.IsPresent("trace")) {
			Logger logger = Logger.getLogger("com.jeantessier.classreader");
			logger.setLevel(Level.DEBUG);
			
			if ("System.out".equals(command_line.OptionalSwitch("trace"))) {
				logger.addAppender(new ConsoleAppender(DEFAULT_LOG_LAYOUT));
			} else {
				logger.addAppender(new WriterAppender(DEFAULT_LOG_LAYOUT, new FileWriter(command_line.OptionalSwitch("trace"))));
			}
		}

		/*
		 *  Beginning of main processing
		 */

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Ignore
		}

		DependencyFinder model = new DependencyFinder();
		model.Maximize(command_line.ToggleSwitch("maximize"));
		model.Minimize(command_line.ToggleSwitch("minimize"));
		model.setVisible(true);
    }
}
