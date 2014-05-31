package com.farrellf.TelemetryGUI;

import java.awt.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

/**
 * A GUI to display robot telemetry
 * 
 * @author Farrell Farahbod
 * @version 1.0
 *
 */
public class TelemetryGUI extends JFrame implements ActionListener {
	
	Map<String, TelemetryGroup> groups;
	JPanel mainPanel;
	BallPanel ball;
	Database db;

	/**
	 * Initialize the GUI.
	 * Items are grouped into "TelemetryGroups", which contain the "TelemetryGroupItems"
	 * 
	 * @param database     Database object
	 */
	public TelemetryGUI(Database database) {
		
		db = database;
		groups = new HashMap<String, TelemetryGroup>();
		
		try {UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");} catch (Exception e) {}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Robot Telemetry");
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		add(mainPanel);
		
		ball = new BallPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 0;
		mainPanel.add(ball, gbc);

		SerialConfigPanel serialConfig = new SerialConfigPanel(db);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		mainPanel.add(serialConfig, gbc);
		
		// all other panels are inserted by the config file parser
		new ConfigurationLoader(this);
		
		pack();
		setResizable(false);
		setVisible(true);
		
		// use a timer to update the GUI every 20ms (50Hz)
		Timer timer = new Timer(20, this);
		timer.start();
	}
	
	/**
	 * Create a new group and add it to the JFrame
	 * 
	 * @param groupName		Name of the group
	 * @param x				x coordinate for the JPanel
	 * @param y				y coordinate for the JPanel
	 */
	public void addGroup(String groupName, int x, int y) {
		if(!groups.containsKey(groupName)) {
			TelemetryGroup tg = new TelemetryGroup(groupName);
			groups.put(groupName, tg);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = x;
			gbc.gridy = y;
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL; // stretch children to fill width
			gbc.anchor = GridBagConstraints.NORTH; // position children at the top-center of their cell
			mainPanel.add(tg, gbc);
		}
	}
	
	/**
	 * Add an item to an existing group.
	 * 
	 * @param groupName		Group name
	 * @param itemName		Item name
	 * @param dbName		Identifier used in the database
	 * @param min			Minimum value
	 * @param max			Maximum value
	 * @param factor		Scaling factor. The raw value is divided by this number to get the formatted value.
	 * @param df			Format string for DecimalFormat
	 * @param suffix		Text to append to the formatted value
	 * @param value			Default raw value
	 */
	public void addGroupItem(String groupName, String itemName, String dbName, int min, int max, double factor, String df, String suffix, int value) {
		groups.get(groupName).addItem(itemName, dbName, min, max, factor, df, suffix, value);
	}
	
	/**
	 * Change the value of an existing TelemetryGroupItem
	 * 
	 * @param groupName		Group name
	 * @param itemName		Item name
	 * @param value			New value
	 */
	public void setGroupItemValue(String groupName, String itemName, int value) {
		groups.get(groupName).getTelemetryItem(itemName).setValue(value);
	}
	
	/**
	 * Timer event occurred, update all values by iterating through each group
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// For each TelemetryGroupItem in each TelemetryGroup, update its value
		for(String groupName: groups.keySet()) {
			for(String groupItemName: groups.get(groupName).items.keySet()) {
				String dbName = groups.get(groupName).getTelemetryItem(groupItemName).dbName;
				setGroupItemValue(groupName, groupItemName, db.getLastValue(dbName));
			}
		}

		ball.setAngleX(db.getLastValue("AngleY") / 114);
		ball.setAngleY(db.getLastValue("AngleX") / -114);
	}
	
	/**
	 * A custom JPanel with a Titled/EtchedBorder that contains TelemetryItems.
	 * The contained TelemetryItems are managed with a Map that maps string names to dynamically allocated TelemetryItems.
	 */
	private class TelemetryGroup extends JPanel {
		
		Map<String, TelemetryItem> items;
		GridBagConstraints gbc;
		int y;
		
		/**
		 * Initialize the TelemetryGroup.
		 * A HashMap is used to store the TelemetryGroupItems.
		 * 
		 * @param groupName		Group name
		 */
		public TelemetryGroup(String groupName) {
			y = 0;
			
			setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(10,5,10,5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 0;
			gbc.gridy = y;
			
			items = new HashMap<String, TelemetryItem>();
			setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), groupName, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		}
		
		/**
		 * Add an item to the TelemetryGroup
		 * 
		 * @param itemName		Item name
		 * @param dbName		Identifier used in the database
		 * @param min			Minimum value
		 * @param max			Maximum value
		 * @param factor		Scaling factor. The raw value is divided by this number to get the formatted value.
		 * @param df			Format string for DecimalFormat
		 * @param suffix		Text to append to the formatted value
		 * @param value			Default raw value
		 */
		public void addItem(String itemName, String dbName, int min, int max, double factor, String df, String suffix, int value) {
			if(!items.containsKey(itemName)) {
				TelemetryItem ti = new TelemetryItem(itemName, dbName, min, max, factor, df, suffix, value);
				add(ti, gbc);
				gbc.gridy++;
				items.put(itemName, ti);
			}
		}
		
		/**
		 * Returns the requested TelemetryItem
		 * 
		 * @param itemName		Name of the TelemetryItem
		 * @return				The TelemetryItem
		 */
		public TelemetryItem getTelemetryItem(String itemName) {
			return items.get(itemName);
		}
		
	}
	
	/**
	 * A custom JPanel that represents one telemetry item.
	 * A label, the raw integer, a formatted number and a slider are used to display data.
	 */
	private class TelemetryItem extends JPanel implements MouseListener {

		DecimalFormat formattedDF;
		DecimalFormat rawDF;
		String suffix;
		String name;
		String dbName;
		int min;
		int max;
		double factor;
		
		JLabel title;
		JLabel rawValue;
		JLabel formattedValue;
		JSlider slider;
		GridBagConstraints gbc;
		
		public TelemetryItem(String name, String dbName, int min, int max, double factor, String df, String suffix, int value) {
			this.formattedDF = new DecimalFormat(df);
			this.rawDF = new DecimalFormat("+00000;-#");
			this.suffix = suffix;
			this.name = name;
			this.dbName = dbName;
			this.min = min;
			this.max = max;
			this.factor = factor;
			
			title = new JLabel(name);
			rawValue = new JLabel(rawDF.format(value));
			formattedValue = new JLabel(formattedDF.format(value / factor) + " " + suffix);
			slider = new JSlider();
			slider.setValue((int) (value / factor));
			slider.setMinimum(min);
			slider.setMaximum(max);			
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setMinorTickSpacing((max - min) / 10);
			slider.setMajorTickSpacing((max - min) / 2);
			
			setLayout(new GridBagLayout());
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(3,3,3,3);
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0;
			title.setFont(new Font("Dialog", Font.BOLD, 12));
			add(title, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.weightx = 1.0;
			rawValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
			add(rawValue, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.weightx = 1.0;
			formattedValue.setFont(new Font("Monospaced", Font.PLAIN, 12));
			add(formattedValue, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridheight = 3;
			gbc.weightx = 0.0;
			add(slider, gbc);
			
			this.addMouseListener(this);
		}
		
		public void setValue(int value) {
			rawValue.setText(rawDF.format(value));
			formattedValue.setText(formattedDF.format(value / factor) + " " + suffix);
			slider.setValue((int) (value / factor));
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			new LineGraph(name, db.getList(dbName), min, max, factor, 500);
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
	}

}

