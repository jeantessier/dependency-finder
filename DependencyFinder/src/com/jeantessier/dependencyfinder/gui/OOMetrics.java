/*
 *  Copyright (c) 2001-2003, Jean Tessier
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
 *  	* Neither the name of Jean Tessier nor the names of his contributors
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
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;

import org.apache.log4j.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.metrics.*;

public class OOMetrics extends JFrame {
	private static final TableCellRenderer RENDERER = new MeasurementTableCellRenderer();

	private MetricsFactory factory;
	
	private JMenuBar     menu_bar      = new JMenuBar();
	private JMenu        file_menu     = new JMenu();
	private JMenu        help_menu     = new JMenu();
	private JToolBar     toolbar       = new JToolBar();
	private JTextArea    project_area  = new JTextArea();
	private JButton      filter_button = new JButton("Filter:");
	private JTextField   filter_field  = new JTextField("//");
	private StatusLine   status_line   = new StatusLine(420);
	private JProgressBar progress_bar  = new JProgressBar();

	private OOMetricsTableModel groups_model;
	private OOMetricsTableModel classes_model;
	private OOMetricsTableModel methods_model;
	
	private File input_file = new File(".");

	public OOMetrics(CommandLine command_line, MetricsFactory factory) {
		this.factory = factory;
		
		this.setSize(new Dimension(800, 600));
		this.setTitle("OO Metrics");
		this.setIconImage(new ImageIcon(getClass().getResource("icons/logoicon.gif")).getImage());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowKiller());

		groups_model  = new OOMetricsTableModel(factory.Configuration().GroupMeasurements());
		classes_model = new OOMetricsTableModel(factory.Configuration().ClassMeasurements());
		methods_model = new OOMetricsTableModel(factory.Configuration().MethodMeasurements());
		
		BuildMenus(command_line);
		BuildUI();

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception ex) {
			System.err.println("Trying to set look and feel: " + ex);
		}
		
		status_line.ShowInfo("Ready.");
	}

	MetricsFactory MetricsFactory() {
		return factory;
	}

	void MetricsFactory(MetricsFactory factory) {
		this.factory = factory;
	}
	
	JTextArea ProjectArea() {
		return project_area;
	}

	OOMetricsTableModel GroupsModel() {
		return groups_model;
	}

	OOMetricsTableModel ClassesModel() {
		return classes_model;
	}

	OOMetricsTableModel MethodsModel() {
		return methods_model;
	}
	
	File InputFile() {
		return input_file;
	}

	void InputFile(File input_file) {
		this.input_file = input_file;
	}

	JTextComponent FilterField() {
		return filter_field;
	}
	
	StatusLine StatusLine() {
		return status_line;
	}
		
	JProgressBar ProgressBar() {
		return progress_bar;
	}
	
	private void BuildMenus(CommandLine command_line) {
		BuildFileMenu(command_line);
		BuildHelpMenu(command_line);

		this.setJMenuBar(menu_bar);
	}
	
	private void BuildFileMenu(CommandLine command_line) {
		menu_bar.add(file_menu);

		file_menu.setText("File");

		Action action;
		JMenuItem menu_item;
		JButton button;
		
		action = new MetricsExtractAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
		menu_item.setMnemonic('e');
		button = toolbar.add(action);
		button.setToolTipText((String) action.getValue(Action.LONG_DESCRIPTION));

		toolbar.addSeparator();
		file_menu.addSeparator();
		
		action = new NewMetricsAction(this);
		menu_item = file_menu.add(action);
		menu_item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		menu_item.setMnemonic('n');
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

	private void BuildHelpMenu(CommandLine command_line) {
		menu_bar.add(help_menu);

		help_menu.setText("Help");

		Action action;
		JMenuItem menu_item;

		action = new AboutAction(this);
		menu_item = help_menu.add(action);
		menu_item.setMnemonic('a');
	}
	
	private void BuildUI() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(BuildControlPanel(), BorderLayout.NORTH);
		this.getContentPane().add(BuildResultPanel(), BorderLayout.CENTER);
		this.getContentPane().add(BuildStatusPanel(), BorderLayout.SOUTH);
	}

	private JComponent BuildControlPanel() {
		return toolbar;
	}
	
	private JComponent BuildResultPanel() {
		JPanel result = new JPanel();

		result.setLayout(new BorderLayout());
		result.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, BuildProjectPanel(), BuildChartsPanel()), BorderLayout.CENTER);
		result.add(BuildFilterPanel(), BorderLayout.SOUTH);
		
		return result;
	}

	private JComponent BuildProjectPanel() {
		JComponent result = new JScrollPane(project_area);
		
		project_area.setEditable(false);
		
		return result;
	}

	private JComponent BuildChartsPanel() {
		JTabbedPane result = new JTabbedPane();

		// result.setBorder(BorderFactory.createTitledBorder("Data"));
		result.addTab("Groups",  BuildGroupsChartPanel());
		result.addTab("Classes", BuildClassesChartPanel());
		result.addTab("Methods", BuildMethodsChartPanel());
		
		return result;
	}

	private JComponent BuildGroupsChartPanel() {
		return BuildChartPanel(GroupsModel());
	}

	private JComponent BuildClassesChartPanel() {
		return BuildChartPanel(ClassesModel());
	}
	
	private JComponent BuildMethodsChartPanel() {
		return BuildChartPanel(MethodsModel());
	}
	
	private JComponent BuildChartPanel(OOMetricsTableModel model) {
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

	private JComponent BuildFilterPanel() {
		JPanel result = new JPanel();

		result.setLayout(new BorderLayout());
		result.add(filter_button, BorderLayout.WEST);
		result.add(filter_field,  BorderLayout.CENTER);

		filter_button.addActionListener(new FilterActionListener(this));
		
		return result;
	}

	private JComponent BuildStatusPanel() {
		JPanel result = new JPanel();

		Dimension size = ProgressBar().getPreferredSize();
		size.width = 100;
		ProgressBar().setPreferredSize(size);
		ProgressBar().setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		result.setLayout(new BorderLayout());
		result.add(StatusLine(),  BorderLayout.CENTER);
		result.add(ProgressBar(), BorderLayout.EAST);
		
		return result;
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
		command_line.AddSingleValueSwitch("default-configuration", true);
		command_line.AddSingleValueSwitch("configuration");
		command_line.AddToggleSwitch("validate");
		command_line.AddToggleSwitch("help");

		CommandLineUsage usage = new CommandLineUsage("OOMetrics");
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

		MetricsFactory factory;
		
		if (command_line.IsPresent("configuration")) {
			factory = new MetricsFactory("Project", new MetricsConfigurationLoader(command_line.ToggleSwitch("validate")).Load(command_line.SingleSwitch("configuration")));
		} else {
			factory = new MetricsFactory("Project", new MetricsConfigurationLoader(command_line.ToggleSwitch("validate")).Load(command_line.SingleSwitch("default-configuration")));
		}
			
		/*
		 *  Beginning of main processing
		 */

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Ignore
		}

		OOMetrics model = new OOMetrics(command_line, factory);
		model.setVisible(true);
	}
}
