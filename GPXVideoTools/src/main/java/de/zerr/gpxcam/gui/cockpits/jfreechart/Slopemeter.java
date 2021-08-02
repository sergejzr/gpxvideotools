package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Slopemeter extends JPanel {

	private BufferedImage compassimage;
	private Point center;
	private double slope;
	private int w;
	private int oldw;
	private int h;
	private int oldh;
	private BufferedImage resized;
	private double ratio;
	private int xoff;

	public Slopemeter() {
		// TODO Auto-generated constructor stub
		try {
			compassimage = ImageIO.read(new File("/home/szerr/git/gpxvideotools/GPXVideoTools/img/icons/bike.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ratio=(1.*compassimage.getHeight())/compassimage.getWidth();
		setPreferredSize(new Dimension(compassimage.getWidth(), compassimage.getHeight()));
		center = new Point(compassimage.getWidth() / 2, compassimage.getHeight() / 2);
		setSize(new Dimension(compassimage.getWidth(), compassimage.getHeight()));
		setBackground(Color.gray);
		// double rotationRequired = Math.toRadians (45);
		// double locationX = image.getWidth() / 2;
		// double locationY = image.getHeight() / 2;

		// Drawing the rotated image at the required drawing locations

	}

	public void setSlope(double slope) {
		// double rotationRequired = Math.toRadians (45);
		this.slope = slope;

	}

	@Override
	protected void paintComponent(Graphics g2) {
		// TODO Auto-generated method stub

		super.paintComponent(g2);
		

		w = (int)(getWidth()*.7);
		h = (int)(w*ratio);
		//w=h=Math.min(w,h);
		if (oldw != w || oldh != h) {

			
			resized=resize(compassimage, w, h);
			center=new Point(w/2,h/2);
			
			oldw=w; oldh=h;
			xoff=(int)(getWidth()*.3/2);
		}
		
		// super.paint(g2);
		AffineTransform tx = AffineTransform.getRotateInstance(-Math.atan(slope), 0, h);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		g2.drawLine(xoff,h,w+xoff,h);
		g2.drawImage(op.filter(resized, null), xoff, 0, null);
		g2.drawString(Math.round(slope*1000)/10.+"%", xoff, h+10);
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}
	// @Override
	// public void paint(Graphics g2) {}

}
