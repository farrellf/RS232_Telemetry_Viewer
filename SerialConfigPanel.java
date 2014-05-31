package com.farrellf.TelemetryGUI;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

/**
 * A JPanel to allow the user to configure the RS232 link.
 * 
 * @author  Farrell Farahbod
 * @version 1.0
 */
public class SerialConfigPanel extends JPanel implements MouseListener {
	
	JLabel portLabel;
	JComboBox portString;
	JLabel baudRateLabel;
	JComboBox baudRateSelection;
	JButton applyButton;
	String[] baudRates;
	SerialPortListener rs232;
	Thread rs232thread;
	
	public SerialConfigPanel(Database db) {
		rs232 = new SerialPortListener(db);
		rs232thread = new Thread(rs232);
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		portLabel = new JLabel("Serial Port:");
		portLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 0, 10, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(portLabel, gbc);
		
		portString = new JComboBox(rs232.getSerialPorts());
		portString.setEditable(true);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 10, 5);
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(portString, gbc);
		
		baudRateLabel = new JLabel("Baud Rate:");
		baudRateLabel.setFont(new Font("Dialog", Font.BOLD, 12));
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 30, 10, 5);
		gbc.gridx = 2;
		gbc.gridy = 0;
		add(baudRateLabel, gbc);
		
		baudRates = new String[] {"1382400", "921600", "460800", "230400", "115200", "57600", "38400", "19200", "9600"};
		baudRateSelection = new JComboBox<String>(baudRates);
		baudRateSelection.setSelectedIndex(1); // default to 921600 baud
		baudRateSelection.setEditable(true);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 10, 5);
		gbc.gridx = 3;
		gbc.gridy = 0;
		add(baudRateSelection, gbc);
		
		applyButton = new JButton("Apply");
		applyButton.setFont(new Font("Dialog", Font.BOLD, 12));
		applyButton.addMouseListener(this);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 30, 10, 5);
		gbc.gridx = 4;
		gbc.gridy = 0;
		add(applyButton, gbc);
		
		// auto connect if only one serial port exists
		if(rs232.getSerialPorts().length == 1) {
			if(rs232.establishConnection((String) rs232.getSerialPorts()[0], Integer.parseInt((String) baudRateSelection.getSelectedItem()))) {
				rs232thread.start();
				applyButton.setEnabled(false);
			}
		}
	}

	/**
	 * Attempt to create an RS232 link if a link is not currently active.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(applyButton.isEnabled() == false)
			return;
		
		if(rs232.establishConnection((String) portString.getSelectedItem(), Integer.parseInt((String) baudRateSelection.getSelectedItem()))) {
			rs232thread.start();
			applyButton.setEnabled(false);
		}
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
