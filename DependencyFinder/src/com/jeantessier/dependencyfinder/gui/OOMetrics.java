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
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import org.xml.sax.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.metrics.*;

public class OOMetrics extends JFrame {
    public static final String DEFAULT_LOGFILE   = "System.out";
    public static final String DEFAULT_TRACEFILE = "System.out";

	private MetricsFactory    factory       = new MetricsFactory("Project");
	
	private JMenuBar            menu_bar      = new JMenuBar();
	private JMenu               file_menu     = new JMenu();
	private JToolBar            toolbar       = new JToolBar();
	private JTextArea           project_area  = new JTextArea();
	private OOMetricsTableModel groups_model  = new OOMetricsTableModel();
	private OOMetricsTableModel classes_model = new OOMetricsTableModel();
	private OOMetricsTableModel methods_model = new OOMetricsTableModel();
	private JButton             filter_button = new JButton("Filter:");
	private JTextField          filter_field  = new JTextField("//");
	private StatusLine          status_line   = new StatusLine(420);

	private File input_file = new File(".");

	public OOMetrics() {
		this.setSize(new Dimension(800, 600));
		this.setTitle("OO Metrics");
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
	
	private void BuildMenus() {
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
		table.getTableHeader().addMouseListener(new TableHeaderListener(table, model));
			
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
		return StatusLine();
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
		command_line.AddToggleSwitch("help");
		command_line.AddOptionalValueSwitch("verbose", DEFAULT_LOGFILE);
		command_line.AddOptionalValueSwitch("trace",   DEFAULT_TRACEFILE);

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

		if (command_line.IsPresent("verbose")) {
			if ("System.out".equals(command_line.OptionalSwitch("verbose"))) {

			} else {

			}
		}

		if (command_line.IsPresent("trace")) {
			if ("System.out".equals(command_line.OptionalSwitch("trace"))) {

			} else {

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

		OOMetrics model = new OOMetrics();
		model.setVisible(true);
	}
}
