package de.zerr.gpxcam.gui.cockpits.exclusive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;

import javax.swing.JPanel;

import de.zerr.core.gpx.GPXTool;
import de.zerr.core.gpx.MapProducer;
import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.WayPoint;

public class EMapGPXViz extends JPanel {

	private BufferedImage res;
	private int w;
	private int h;
	private Rectangle2D bounds;
	private ContinuousRoute route;
	ZonedDateTime stoptime;

	public EMapGPXViz(int w, int h, ContinuousRoute route) {
		super();
		this.w = w;
		this.h = h;
		Dimension dim = new Dimension(w, h);
		setSize(new Dimension(w, h));
		setPreferredSize(dim);
		setMaximumSize(dim);
		setMinimumSize(dim);
		this.route = route;

	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		if (res != null) {
			g.drawImage(res, 0, 0, null);
		}

	}

	public void initMap(Rectangle2D bounds, MapProducer producer) {
		this.bounds = bounds;
		int zoomlevel = GPXTool.getZoomlevel(bounds.getWidth()/(w*.7), bounds.getHeight()/(h*.7));
		double resolution=GPXTool.getResolution(zoomlevel);
		
		
		
		res = producer.produce(bounds, w, h, zoomlevel);

		setPreferredSize(new Dimension(300, 250));
		setSize(300, 250);
		setMinimumSize(new Dimension(300, 250));
		

		Duration d = Duration.ofMillis(250);
		Iterator<RoutePoint> it = route.iterateBy(route.getStartTime(), stoptime, d);

		WayPoint center=WayPoint.builder().lat(bounds.getCenterX()).lon(bounds.getY()).build();
		
		while (it.hasNext()) {
			RoutePoint rp = it.next();
			WayPoint xp=WayPoint.builder().lat(rp.getLattitude()).lon(center.getLongitude()).build();
			WayPoint yp=WayPoint.builder().lat(center.getLatitude()).lon(rp.getLontgitude()).build();
			
			 double dx = xp.distance(center).doubleValue();
			 double dy = yp.distance(center).doubleValue();
			 
			 
			
		}
		
	}

}
