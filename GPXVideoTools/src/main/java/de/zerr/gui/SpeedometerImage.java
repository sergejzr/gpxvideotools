package de.zerr.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;



public class SpeedometerImage {

	BufferedImage speedometerImage = null;

	private double speed, distance, attitude;

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getAttitude() {
		return attitude;
	}

	public void setAttitude(double attitude) {
		this.attitude = attitude;
	}

	public SpeedometerImage() {
		// TODO Auto-generated constructor stub
	}

	public void paint() {

		Graphics g;
		
		if (speedometerImage == null) {
			speedometerImage = new BufferedImage(500, 5000, BufferedImage.TYPE_INT_ARGB);
			//ImageIO.read(null)
		}
		g = speedometerImage.getGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, 500, 500);
		
		g.setColor(Color.black);
		g.drawOval(0, 0, 500, 500);
		
	}

	public BufferedImage getSpeedometerImage() {
		paint();
		return speedometerImage;
	}

}
