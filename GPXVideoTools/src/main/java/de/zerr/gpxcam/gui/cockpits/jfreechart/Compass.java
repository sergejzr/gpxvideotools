package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class Compass extends JPanel {

	private BufferedImage compassimage;
	private Point center;
	private double bearing;
	private int w;
	private int oldw;
	private int h;
	private int oldh;
	private BufferedImage resized;

	public Compass() {
		// TODO Auto-generated constructor stub
		try {
			compassimage = ImageIO.read(new File("/home/szerr/git/gpxvideotools/GPXVideoTools/img/icons/compass.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(compassimage.getWidth(), compassimage.getHeight()));
		center = new Point(compassimage.getWidth() / 2, compassimage.getHeight() / 2);
		setSize(new Dimension(compassimage.getWidth(), compassimage.getHeight()));
		setBackground(Color.gray);
		// double rotationRequired = Math.toRadians (45);
		// double locationX = image.getWidth() / 2;
		// double locationY = image.getHeight() / 2;

		// Drawing the rotated image at the required drawing locations

	}

	public void setBearing(double bearing) {
		// double rotationRequired = Math.toRadians (45);
		this.bearing = bearing;

	}

	@Override
	protected void paintComponent(Graphics g2) {
		// TODO Auto-generated method stub
		Border border = getBorder();
		Insets is = border.getBorderInsets(this);
		
		super.paintComponent(g2);
		g2.drawString("test", 10, 10);

		w = getWidth()-is.left-is.right;
		h = getHeight()-is.top-is.bottom;
		w=h=Math.min(w,h);
		if (oldw != w || oldh != h) {

			
			resized=resize(compassimage, w, h);
			center=new Point(w/2,h/2);
			oldw=w; oldh=h;
		}
		// super.paint(g2);
		AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians (bearing), center.getX(), center.getY());
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		
		g2.drawImage(op.filter(resized, null), is.left, is.top, null);

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
