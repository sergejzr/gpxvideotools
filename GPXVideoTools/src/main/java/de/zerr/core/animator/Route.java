package de.zerr.core.animator;

import java.time.ZonedDateTime;
import java.util.TreeMap;

public interface  Route {
	

	public void put(RoutePoint p);

	public RoutePoint at(ZonedDateTime zonedDateTime);
	
	

}
