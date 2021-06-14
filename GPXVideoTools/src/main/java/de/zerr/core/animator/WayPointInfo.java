package de.zerr.core.animator;

import java.time.ZonedDateTime;

import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Length.Unit;
import io.jenetics.jpx.WayPoint;

public class WayPointInfo{

	Length distanceInKM;
	Degrees slope;
	Degrees  heading;
	
	public WayPointInfo(WayPoint wp) {
		super();
		
	}
	
	WayPointInfo dist(double distanceInKM)
	{
		this.distanceInKM=Length.of(distanceInKM,Unit.KILOMETER);
		return this;
	}
	
	Length getDistance() 
	{
		return distanceInKM;
	}
	

}
