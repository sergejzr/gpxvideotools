package de.zerr;

import java.time.ZonedDateTime;
import java.util.TreeMap;

import de.zerr.core.animator.Route;
import de.zerr.core.animator.RoutePoint;
import de.zerr.core.videowriter.InterpolationType;

public class DiscreteRoute extends TreeMap<ZonedDateTime, RoutePoint> implements Route {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5723867341114227904L;

	

	@Override
	public void put(RoutePoint p) {
		 this.put(p.getTime(),p);
		
	}


	@Override
	public RoutePoint at(ZonedDateTime zonedDateTime) {
		// TODO Auto-generated method stub
		return this.floorEntry(zonedDateTime).getValue();
	}

}
