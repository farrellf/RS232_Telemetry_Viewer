package com.farrellf.TelemetryGUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 * A window for showing a live line graph of a telemetry item.
 * The line graph shows the recent history of the item, similar to the "roll mode" of an oscilloscope.
 * 
 * @author  Farrell Farahbod
 * @version 1.0
 *
 */
public class LineGraph extends JFrame implements ActionListener {
	
	Timer timer;

	/**
	 * Initialize the window.
	 * 
	 * @param windowName	Name to show in the title bar
	 * @param list			List of values
	 * @param min			Minimum value (scales the graph)
	 * @param max			Maximum value (scales the graph)
	 * @param factor		Factor to divide the raw integers by, to get the formatted values
	 * @param maxLength		How much history to show (scales the graph)
	 */
	public LineGraph(String windowName, final List<Integer> list, int min, int max, double factor, int maxLength) {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Line Graph: " + windowName);
		setSize(800, 335);
		setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width - this.getWidth(),
					GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - this.getHeight() + 40);
		setAlwaysOnTop(true);
		try {UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");} catch (Exception e) {}
		
		Chart c = new Chart(list, min, max, factor, maxLength);
		add(c);
		
		setVisible(true);
		
		// Redraw the window every 20ms (50Hz)
		timer = new Timer(20, this);
		timer.start();
	}
	
	/**
	 * Graph is drawn on this JPanel
	 */
	private class Chart extends JPanel {
		
		List<Integer> list;
		double min;
		double max;
		double factor;
		int maxLength;
		
		/**
		 * Configure the line graph
		 * 
		 * @param list			List of values
		 * @param min			Minimum value (scales the graph)
		 * @param max			Maximum value (scales the graph)
		 * @param factor		Factor to divide the raw integers by, to get the formatted values
		 * @param maxLength		How much history to show (scales the graph)
		 */
		public Chart(List<Integer> list, int min, int max, double factor, int maxLength) {
			this.list = list;
			this.min = min;
			this.max = max;
			this.factor = factor;
			if(maxLength != 0)
				this.maxLength = maxLength;
			else
				this.maxLength = Integer.MAX_VALUE;
		}
		
		/**
		 * Draw the line graph
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			
			double panelWidth = this.getWidth();
			double panelHeight = this.getHeight();
			double listSize = list.size();
			double x1, x2, y1, y2;
			int i;
			
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(0, (int) panelHeight/2, (int) panelWidth, (int) panelHeight/2);
			
			
			g.setColor(Color.BLACK);
			if(listSize > maxLength)
				i = (int) listSize - maxLength;
			else
				i = 1;

			for(; i < listSize; i++) {
				if(listSize > maxLength) {
					x1 = (i - 1 - listSize + maxLength) / maxLength * panelWidth;
					x2 = (i - listSize + maxLength) / maxLength * panelWidth;
				} else {
					x1 = (i - 1) / listSize * panelWidth;
					x2 = i / listSize * panelWidth;
				}
				y1 = panelHeight - (list.get(i-1) / factor - min) * (panelHeight / (max - min));
				y2 = panelHeight - (list.get(i) / factor - min) * (panelHeight / (max - min));
				g.drawLine( (int) x1, (int) y1, (int) x2, (int) y2);
			}
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.repaint();
	}
	
}
