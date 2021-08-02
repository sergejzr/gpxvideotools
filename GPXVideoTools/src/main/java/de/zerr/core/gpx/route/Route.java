package de.zerr.core.gpx.route;

import java.time.ZonedDateTime;

public interface  Route {
	

	public void put(RoutePoint p);

	public RoutePoint at(ZonedDateTime zonedDateTime);
	
	

}
