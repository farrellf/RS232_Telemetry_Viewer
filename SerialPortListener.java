package com.farrellf.TelemetryGUI;

/**
 * Establishes RS232 communication and perpetually listens for incoming data.
 * 
 * @author  Farrell Farahbod
 * @version 1.0
 */

import gnu.io.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class SerialPortListener implements Runnable {
	private Database db;
	private SerialPort rs232;
	private InputStream rs232istream;
	private Scanner rs232scanner;
	private String line;
	private String value;
	private int number;
	
	public SerialPortListener(Database db) {
		this.db = db;
	}
	
	/**
	 * Get a list of available serial ports.
	 * 
	 * @return	An array representing the ports
	 */
	public Object[] getSerialPorts() {
		List<String> list = new ArrayList<String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
		
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = ports.nextElement();
			if(port.getPortType() == CommPortIdentifier.PORT_SERIAL)
				list.add(port.getName());
		}
		
		return list.toArray();
	}
	
	/**
	 * Attempt to create an RS232 link.
	 * 
	 * @param port		String name, for example: /dev/ttyUSB0
	 * @param baudRate	Baud rate
	 * @return			Returns true if link was created
	 */
	public Boolean establishConnection(String port, int baudRate) {
		try {
			rs232 = (SerialPort) CommPortIdentifier.getPortIdentifier(port).open("Robot Telemetry", 20000); // appname, timeout in ms
			rs232.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			rs232istream = rs232.getInputStream();
			rs232scanner = new Scanner(rs232istream);
			System.out.println("Connected to " + port + " at " + baudRate + " baud.");
			return true;
		} catch (Exception e) {
			System.err.println("Error setting up serial communications with port " + port + ".");
			System.err.println(e.getClass().toString());
			return false;
		}
	}
	
	/**
	 * A separate thread to constantly monitor the RS232 link.
	 */
	@Override
	public void run() {
		while(true) {
			try {
				// interpret incoming lines of text				
				line = rs232scanner.nextLine();
				
				if(line.length() == 65)
					line = line.substring(3); // trim \x1B[H ASCII escape sequence
				
				if(line.length() != 62)
					continue; // empty or corrupt line
				
				try {
					value = line.substring(12, 18);
					number = Integer.parseInt(value);
					
					// populate the db
					db.addValue(line.substring(0, line.indexOf(" ")), number);
					
					//System.out.println("line = \"" + line + "\",      value = " + value + ",      number = " + number);
				} catch(Exception e) {
					//System.err.println("Error processing line: \"" + line + "\"");
				}
			} catch(Exception e) {
				System.err.println("One or more errors occured.");
				System.err.println(e.getClass().toString());
			}
		}
	}
}
