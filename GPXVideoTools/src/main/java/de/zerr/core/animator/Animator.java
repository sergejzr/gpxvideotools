package de.zerr.core.animator;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

import de.zerr.gui.SpeedometerPanel;
import io.jenetics.jpx.WayPoint;

public class Animator {
	private GPXIterator it;

	public Animator(String gpxfile) throws Exception {
		 it = new GPXIterator(gpxfile,25);
	}
	
	public void animate(SpeedometerPanel speedometer)
	{
		WayPoint wp=null;
		while((wp=it.next())!=null)
		{
			speedometer.setSpeed(ZonedDateTime.now(), GPXRouteBuilder.kmph(wp.getSpeed().get().doubleValue()));
			speedometer.repaint();
		}
	}

}
