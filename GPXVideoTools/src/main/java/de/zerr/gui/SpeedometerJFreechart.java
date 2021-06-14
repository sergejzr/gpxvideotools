package de.zerr.gui;

/*
 * @(#) Speedometer.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import javax.swing.JPanel;

/**
 * This class implements a <code>Speedometer</code> view by implementing the
 * <code>ISpeedView</code> interface.
 * 
 * @author Clemens Krainer
 */
public class SpeedometerJFreechart extends JPanel implements ICockpit
{
	private static final long serialVersionUID = -4076648741571762140L;

	/**
	 * Some geometric constants.
	 */
	int size = 180;
	double f1 = 0.2777777;
	double f2 = 0.3888888;
	double f3 = 0.05;
	double f4 = 0.0888888;
	int r1 = (int)(size * f1);
	int r2 = (int)(size * f2);
	int l3 = (int)(size * f3);
	int l4 = (int)(size * f4);
	int x_origin = size/2;
	int y_origin = size/2;
	double startAngle = 225;
	double endAngle = -45;
	double max_speed =50;
	double main_interval = (startAngle-endAngle) / 5;
	double sub_interval = (startAngle-endAngle) / 25;
	int needleDiameter = (int)(size * 0.1);
	int needleLength = r1 + l3;
	double km=0;
	public double getKm() {
		return km;
	}
	public void setKm(double km) {
		this.km = km;
	}
	/**
	 * This <code>Ellipse2D</code> represents the center of the speedometer needle.
	 */
	private Ellipse2D needleCenter = new Ellipse2D.Double (
			x_origin-needleDiameter/2, y_origin-needleDiameter/2, needleDiameter, needleDiameter);
	
	/**
	 * This <code>Polygon</code> represents the speedometer needle.
	 */
	private Polygon needlePolygon = new Polygon (
			new int[] {x_origin+needleLength,x_origin,x_origin,x_origin+needleLength,x_origin+needleLength},
			new int[] {y_origin-2, y_origin-needleDiameter/4, y_origin+needleDiameter/4, y_origin+1, y_origin-2},
			5);
	
 
	public SpeedometerJFreechart(int size,
			double max_speed)
	{
		this(size,225,-45,max_speed);
		
		setSize (size, size);

		shapes = new Shape[26];
		
		int i=0;
		for (double k=startAngle; k > endAngle; k -= main_interval)
		{
			if (k == startAngle)
				shapes[i++] = createLine (r1, l4, k);
			
			for (double j=sub_interval; j <= main_interval-sub_interval; j += sub_interval)
			{
				shapes[i++] = createLine (r1,l3,k-j);
			}
			shapes[i++] = createLine (r1, l4, k-main_interval);
		}
//		System.out.println ("I="+i);
		
		locale = new Locale ("de","AT");
		
	}
	public SpeedometerJFreechart(int size,  double startAngle, double endAngle,
			double max_speed) {
		
		this(size, 
0.2777777,
0.3888888,
0.05,
0.0888888
		
				, startAngle, endAngle, max_speed);
		
	}
	
	public SpeedometerJFreechart(int size, double f1, double f2, double f3, double f4, double startAngle, double endAngle,
			double max_speed) {
		super();
		this.size = size;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
		this.startAngle = startAngle;
		this.endAngle = endAngle;
		this.max_speed = max_speed;
		
		
		this.r1 = (int)(size * f1);
		this.r2 = (int)(size * f2);
		this.l3 = (int)(size * f3);
		this.l4 = (int)(size * f4);
		this.x_origin = size/2;
		this.y_origin = size/2;
		
		this.main_interval = (startAngle-endAngle) / 5;
		this.sub_interval = (startAngle-endAngle) / 25;
		this.needleDiameter = (int)(size * 0.1);
		this.needleLength = r1 + l3;
		
		
		needleCenter = new Ellipse2D.Double (
				x_origin-needleDiameter/2, y_origin-needleDiameter/2, needleDiameter, needleDiameter);
		
		/**
		 * This <code>Polygon</code> represents the speedometer needle.
		 */
		needlePolygon = new Polygon (
				new int[] {x_origin+needleLength,x_origin,x_origin,x_origin+needleLength,x_origin+needleLength},
				new int[] {y_origin-2, y_origin-needleDiameter/4, y_origin+needleDiameter/4, y_origin+1, y_origin-2},
				5);
		
	}


	
	/**
	 * The <code>AffineTransform</code> matrix that rotates the speedometer needle.
	 */
	AffineTransform	at = new AffineTransform ();
	
	/**
	 * The current speed as a double value.
	 */
	private double speed = 0;
	
	/**
	 * The current speed as a string. 
	 */
	private String speedString = null;
	
	/**
	 * This variable contains all scale gradations of the speedometer.
	 */
	private Shape[] shapes;
	
	
	
    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;

	private ZonedDateTime curTime;

	/**
	 * Construct a <code>Speedometer</code>.
	 */

	
public void setup() {
	int size = 180;
	double f1 = 0.2777777;
	double f2 = 0.3888888;
	double f3 = 0.05;
	double f4 = 0.0888888;
	int r1 = (int)(size * f1);
	int r2 = (int)(size * f2);
	int l3 = (int)(size * f3);
	int l4 = (int)(size * f4);
	int x_origin = size/2;
	int y_origin = size/2;
	double startAngle = 225;
	double endAngle = -45;
	double max_speed =50;
	double main_interval = (startAngle-endAngle) / 5;
	double sub_interval = (startAngle-endAngle) / 25;
	int needleDiameter = (int)(size * 0.1);
	int needleLength = r1 + l3;
	
}
	/**
	 * Create a line of the scale gradations.
	 * 
	 * @param radius the inner radius of the scale
	 * @param length the length of the line
	 * @param alpha the angle of the line
	 * @return the corresponding line as a <class>Shape</class> instance.
	 */
	private Shape createLine (double radius, double length, double alpha)
	{		
		double sinAlpha = Math.sin (Math.toRadians (alpha));
		double cosAlpha = Math.cos (Math.toRadians (alpha));
		return new Line2D.Double (
				x_origin+radius*cosAlpha, y_origin-radius*sinAlpha,
				x_origin+(radius+length)*cosAlpha, y_origin-(radius+length)*sinAlpha
			);
	}
		
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.ui.ISpeedView#setSpeed(java.util.Date, double)
	 */
	public void setSpeed (double currentSpeed)
	{
		this.speed = currentSpeed/3.6;
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        String h1 = this.speed < 10 ? " " : "";
        String h2 = currentSpeed < 10 ? " " : "";
       // this.speedString = h1 + nf.format(this.speed) + "m/s = "+ h2 + nf.format(currentSpeed) +"km/h";
        this.speedString = h2 + nf.format(currentSpeed) +"km/h \t\t";
//		this.speedString = String.format ("%5.2fm/s = %5.2fkm/h", new Object[] {new Double (speed),new Double (currentSpeed)});
		repaint ();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g)
	{
		int center=size/2;
		FontMetrics fm = g.getFontMetrics();
		
		
		super.paintComponent (g);
		Graphics2D ga = (Graphics2D)g;

		ga.setColor (Color.white);
		ga.fillRect (0, 0, size-1, size-1);
		ga.setColor (Color.black);
		ga.drawRect (0, 0, size-1, size-1); // draw border 
		ga.drawString ("Speed", 5, 15);
		
		if (speedString != null)
			ga.drawString(speedString, 5, size-5);
		
		String kmstring=""+String.format("%.2f", Math.round(km*100)/100.);
		Rectangle2D kmstringbounds = fm.getStringBounds(kmstring, ga);
		
		int kmstringx=(int)(center-kmstringbounds.getWidth()/2.);
		int kmstringy=(int)(center-kmstringbounds.getHeight());
		ga.drawString (kmstring,kmstringx, kmstringy);
		
		for (int k=0; k < shapes.length; k++)
			ga.draw (shapes[k]);
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);
		for (double alpha=startAngle; alpha >= endAngle; alpha -= main_interval)
		{
			double sinAlpha = Math.sin (Math.toRadians (alpha));
			double cosAlpha = Math.cos (Math.toRadians (alpha));
			int speed = (int)(max_speed*(startAngle - alpha)/(startAngle-endAngle));
			String text = nf.format (speed);
//			String text = String.format ("%d", new Object[] {new Integer (speed)});
			ga.drawString (text, (int)(x_origin-7+(r2+7)*cosAlpha), (int)(y_origin+3-(r2+5)*sinAlpha));
		}
		
		at.setToIdentity ();
		at.translate (x_origin, y_origin);
		
		double angle = (3.6*speed*(startAngle - endAngle)/max_speed) - startAngle;
		at.rotate (Math.toRadians (angle));
		AffineTransform saveXform = ga.getTransform ();
		AffineTransform toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (at);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		
		ga.fill (needleCenter);
		
		if (speedString != null)
			ga.fillPolygon (needlePolygon);
		
		ga.setTransform (saveXform);
	}
	
	 public BufferedImage getScreenShot(){
	        BufferedImage bi = new BufferedImage(
	            getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	        paint(bi.getGraphics());
	        return bi;
	    }
	@Override
	public void setElevation(double d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setAcceleration(double d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTime(ZonedDateTime curTime) {
		this.curTime=curTime;
		
	}
	@Override
	public void setBearing(double heading) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setSlope(double slope) {
		// TODO Auto-generated method stub
		
	}
}