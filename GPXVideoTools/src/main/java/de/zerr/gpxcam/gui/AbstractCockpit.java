package de.zerr.gpxcam.gui;

import java.awt.Rectangle;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;

import javax.swing.JPanel;

import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;

public abstract class AbstractCockpit extends JPanel implements ICockpit {

	private ContinuousRoute route;
	private ZonedDateTime endtime;
	private ZonedDateTime starttime;
	

	public AbstractCockpit() {
		super();
	}
	
	/*
	public AbstractCockpit(LayoutManager layout) {
		super(layout);
	}
*/
	public AbstractCockpit(ContinuousRoute route, ZonedDateTime starttime, ZonedDateTime endtime) {
		this.route = route;
		this.starttime = starttime;
		this.endtime = endtime;

	}
	
	

	public RoutePoint getAt(ZonedDateTime timepoint) {
		return route.at(timepoint);
	}

	public ZonedDateTime getStarttime() {
		return starttime;
	}

	public ZonedDateTime getEndtime() {
		return endtime;
	}

	public Iterator<RoutePoint> iterateBy(Duration resolution)
	{
		return new Iterator<RoutePoint>() {
			
			ZonedDateTime curTime=null;
			@Override
			public RoutePoint next() {
				setup();
				RoutePoint ret = route.at(curTime);
				curTime=curTime.plus(resolution);
				return ret;
			}
			
			@Override
			public boolean hasNext() {
				setup();
				return !curTime.plus(resolution).isAfter(endtime);
			}
			
			void setup() 
			{
				if(curTime==null) {curTime=starttime;}
			}
		};
	}
}
