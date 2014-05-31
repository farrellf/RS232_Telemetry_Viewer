package com.farrellf.TelemetryGUI;

import java.io.File;
import java.util.Scanner;

/**
 * Reads the configuration file and creates TelemetryGroups and TelemetryGroupItems as needed.
 * 
 * @author Farrell Farahbod
 * @version 1.0
 */
public class ConfigurationLoader {
	
	File f;
	Scanner s;
	TelemetryGUI gui;

	public ConfigurationLoader(TelemetryGUI gui) {
		
		this.gui = gui;

		// open the config file
		try {
			f = new File("config.txt");
			s = new Scanner(f);
		} catch(Exception e) {
			System.err.println("Unable to open the configuration file at " + f.getAbsolutePath());
		}
		
		// parse the config file
		while(s.hasNext()) {
			String line = s.nextLine();
			try{
				if(line.length() == 0) {
					// ignore empty lines
					continue;
				} else if(line.startsWith("#")) {
					// ignore comment lines
					continue;
				} else if(line.startsWith("Group:")) {
					// process groups
					line = line.substring(6);
					String[] chunks = line.split(",");
					String groupName = chunks[0].trim();
					int x = Integer.parseInt(chunks[1].trim());
					int y = Integer.parseInt(chunks[2].trim());
					gui.addGroup(groupName, x, y);
					System.out.println("Adding Group: " + groupName + ", " + x + ", " + y);
				} else if(line.startsWith("Item:")) {
					// process items
					line = line.substring(5);
					String[] chunks = line.split(",");
					String groupName = chunks[0].trim();
					String itemName = chunks[1].trim();
					String dbName = chunks[2].trim();
					int min = Integer.parseInt(chunks[3].trim());
					int max = Integer.parseInt(chunks[4].trim());
					double factor = Double.parseDouble(chunks[5].trim());
					String df = chunks[6].trim();
					String suffix = chunks[7].trim();
					int defaultValue = Integer.parseInt(chunks[8].trim());
					gui.addGroupItem(groupName, itemName, dbName, min, max, factor, df, suffix, defaultValue);
					System.out.println("Adding Item: " + groupName + ", " + itemName + ", " + dbName + ", " + min + ", " + max + ", " + factor + ", " + df + ", " + suffix + ", " + defaultValue);
				} else {
					// invalid line
				}			
			} catch(Exception e) {
				System.err.println("Error parsing configuration file line: " + line);
			}
		}
	}
}
