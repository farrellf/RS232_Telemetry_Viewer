package com.farrellf.TelemetryGUI;

/**
 * A tool to display telemetry data received over an RS232 link.
 * 
 * A database is used to store the history of each telemetry item and feed the GUI.
 * A GUI is created and it manages the RS232 connection and queries the database.
 * 
 * The GUI shows representations of the current telemetry data points,
 *   and allows the user to spawn new windows showing the history of
 *   an item in the form of a line graph.
 * 
 * @author  Farrell Farahbod
 * @version 1.0
 */
public class Main {
	public static void main(String[] args) {
		Database db = new Database();	
		TelemetryGUI gui = new TelemetryGUI(db);
	}
}