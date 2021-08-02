package de.zerr.gpxcam.gui.cockpits.exclusive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;

import javax.swing.JPanel;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import de.zerr.core.gpx.GPXTool;
import de.zerr.core.gpx.MapProducer;
import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.WayPoint;

public class EMap extends JPanel {

	private BufferedImage res;
	private int w;
	private int h;
	private Rectangle2D bounds;
	private ContinuousRoute route;
	ZonedDateTime stoptime;
	private FFmpegFrameGrabber frameGrabber;
	private Java2DFrameConverter c;
	private double framerate;
	private int oldframnr=-1;
	private ZonedDateTime videoStartTime;
	private Insets is;

	public EMap(int w, int h, ContinuousRoute route, String routempeg, ZonedDateTime videoStartTime) throws Exception {
		super();
		this.w = w;
		this.h = h;
		Dimension dim = new Dimension(w, h);
		setSize(new Dimension(w, h));
		setPreferredSize(dim);
		setMaximumSize(dim);
		setMinimumSize(dim);
		this.route = route;

		frameGrabber = new FFmpegFrameGrabber(routempeg);
		frameGrabber.start();

		double ar = (h * 1.) / frameGrabber.getImageHeight();

		frameGrabber.setImageHeight(h);
		frameGrabber.setImageWidth((int) (frameGrabber.getImageWidth() * ar));
		 c = new Java2DFrameConverter();
		 framerate = frameGrabber.getFrameRate();
		 this.videoStartTime=videoStartTime;
		
	}

	@Override
	public void paint(Graphics g) {
		
		

		super.paint(g);

		
		  if (res != null) { g.drawImage(res, is.left, is.top, null); }
		 
	}

	public void at(ZonedDateTime t) 
	{
		int framenr = (int)(framerate*Duration.between(route.getGpxstarttime(), t).toSeconds());
		if(oldframnr!=framenr) {
		try {
			//frameGrabber.setVideoTimestamp(Duration.between(route.getStartTime(), t).toMillis());
			
			
			frameGrabber.setFrameNumber(framenr);
			oldframnr=framenr;
			
			Frame f = frameGrabber.grabImage();
			res=c.convert(f);
			f.close();
		} catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		repaint();
		}
	}

	public void initMap(Rectangle2D bounds, MapProducer producer) {
		this.bounds = bounds;
		 is = getBorder().getBorderInsets(this);
		/*
		 * int zoomlevel = GPXTool.getZoomlevel(bounds.getWidth()/(w*.7),
		 * bounds.getHeight()/(h*.7)); double
		 * resolution=GPXTool.getResolution(zoomlevel);
		 * 
		 * 
		 * 
		 * res = producer.produce(bounds, w, h, zoomlevel);
		 * 
		 * setPreferredSize(new Dimension(300, 250)); setSize(300, 250);
		 * setMinimumSize(new Dimension(300, 250));
		 * 
		 * 
		 * Duration d = Duration.ofMillis(250); Iterator<RoutePoint> it =
		 * route.iterateBy(route.getStartTime(), stoptime, d);
		 * 
		 * WayPoint
		 * center=WayPoint.builder().lat(bounds.getCenterX()).lon(bounds.getY()).build()
		 * ;
		 * 
		 * while (it.hasNext()) { RoutePoint rp = it.next(); WayPoint
		 * xp=WayPoint.builder().lat(rp.getLattitude()).lon(center.getLongitude()).build
		 * (); WayPoint
		 * yp=WayPoint.builder().lat(center.getLatitude()).lon(rp.getLontgitude()).build
		 * ();
		 * 
		 * double dx = xp.distance(center).doubleValue(); double dy =
		 * yp.distance(center).doubleValue();
		 * 
		 * 
		 * 
		 * }
		 */

	}

}
