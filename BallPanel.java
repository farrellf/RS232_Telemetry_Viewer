package com.farrellf.TelemetryGUI;

import javax.swing.*;
import java.awt.*;

/**
 * Custom JPanel that displays a primitive 2D ball representation of the yaw and elevation angles of the robot.
 * 
 * @author Farrell Farahbod
 * @version 1.0
 */
public class BallPanel extends JPanel {
	
	private double angleX;
	private double angleY;
	
	private final int XOFF = 20;
	private final int YOFF = 25;
	private final int CIRCLE_WIDTH = 240;
	private final int CIRCLE_HEIGHT = 240;
	
	// calculate the size of the JPanel
	public BallPanel() {
		this.setSize(CIRCLE_WIDTH + (2 * XOFF), CIRCLE_HEIGHT + (2 * YOFF)); // w,h
		this.setMinimumSize(new Dimension(CIRCLE_WIDTH + (2 * XOFF), CIRCLE_HEIGHT + (2 * YOFF)));
		this.setMaximumSize(new Dimension(CIRCLE_WIDTH + (2 * XOFF), CIRCLE_HEIGHT + (2 * YOFF)));
		this.setPreferredSize(new Dimension(CIRCLE_WIDTH + (2 * XOFF), CIRCLE_HEIGHT + (2 * YOFF)));
		
		angleX = 0;
		angleY = 0;
	}
	
	// update the elevation angle
	public void setAngleX(double d) {
		angleX = d;
		repaint();
	}
	
	// update the yaw angle
	public void setAngleY(double d) {
		angleY = d;
		repaint();
	}
	
	// draw the ellipses
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// circle
		g2.setColor(Color.BLUE);
		g2.drawOval(XOFF, YOFF, CIRCLE_WIDTH, CIRCLE_HEIGHT);
		
		// x (horizontal) ellipse
		g2.setColor(Color.RED);
		int ellipseHeight = (int) (Math.sin(Math.toRadians(angleX)) * CIRCLE_HEIGHT);
		if(angleX >= 0 && angleX <= 90) {
			g2.drawArc(XOFF, YOFF + (CIRCLE_HEIGHT / 2) - (ellipseHeight / 2), CIRCLE_WIDTH, ellipseHeight, 0, 180);
			
		} else if(angleX < 0 && angleX >= -90){
			ellipseHeight *= -1;
			g2.drawArc(XOFF, YOFF + (CIRCLE_HEIGHT / 2) - (ellipseHeight / 2), CIRCLE_WIDTH, ellipseHeight, 180, 180);
		}
		
		// y (horizontal) ellipse
		g2.setColor(Color.RED);
		int ellipseWidth = (int) (Math.sin(Math.toRadians(angleY)) * CIRCLE_WIDTH);
		if(angleY >= 0 && angleY <= 90) {
			g2.drawArc(XOFF + (CIRCLE_WIDTH / 2) - (ellipseWidth / 2), YOFF, ellipseWidth, CIRCLE_HEIGHT, 90, 180);
			
		} else if(angleY < 0 && angleY >= -90){
			ellipseWidth *= -1;
			g2.drawArc(XOFF + (CIRCLE_WIDTH / 2) - (ellipseWidth / 2), YOFF, ellipseWidth, CIRCLE_HEIGHT, 270, 180);
		}
	}

}
