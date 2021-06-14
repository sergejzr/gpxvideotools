package de.zerr.core.animator;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.WayPoint.Builder;

public class GPXIterator {

	ZonedDateTime curpoint = null;

	private List<WayPoint> mytrack;

	private UnivariateFunction speedfunc;

	private UnivariateFunction elevationfunc;

	private ZonedDateTime lastpoint;

	private UnivariateFunction latfunc;

	private UnivariateFunction lonfunc;

	private double framerate;

	public GPXIterator(String GPXFlename) throws Exception {
		this(GPXFlename,25,null);
	}
	
	public GPXIterator(String GPXFlename, int fps) throws Exception {
		this(GPXFlename,fps,null);
	}
	
	public GPXIterator(String GPXFlename, double framerate, ZonedDateTime starttime) throws Exception {

		GPXRouteBuilderX rbuilder = new GPXRouteBuilderX(GPXFlename);
		
		//mytrack=rbuilder.prepare()
		mytrack = rbuilder.getEnhanced(0, 0, GPXTargetValue.SPEED);
		
		this.framerate=framerate;
		
		latfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.LAT);
		lonfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.LON);
		
		if(speedfunc==null) {
		speedfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.SPEED);
		}
		if (elevationfunc == null) {
			elevationfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.ELEVATION);
		}
		List<WayPoint> newtrack = new ArrayList<WayPoint>();
		curpoint = mytrack.get(0).getTime().get();
		
		lastpoint = mytrack.get(mytrack.size() - 1).getTime().get();
		while(!curpoint.isAfter(lastpoint))
		{
			double speed = speedfunc.value(curpoint.toEpochSecond());
			
			newtrack.add(WayPoint.builder().lat(latfunc.value(curpoint.toEpochSecond()))
					.lon(lonfunc.value(curpoint.toEpochSecond()))
					.speed(speed)
					.ele(elevationfunc.value(curpoint.toEpochSecond())).time(curpoint).build()
					);
			curpoint=curpoint.plusSeconds(1);
		}

		
		
		mytrack=newtrack;
		
		//mytrack=GPXRouteBuilder.smooth(mytrack, 3, null);
		speedfunc=elevationfunc=null;
		
		
		
		for(WayPoint cp:mytrack) {
			double speed_mps = cp.getSpeed().get().doubleValue();
			System.out.println(cp.getTime().get()+" "+  speed_mps+" "+GPXRouteBuilderX.kmph(speed_mps));
			//System.out.println(cp.getTime().get()+ " speed:"+ (cp.getSpeed().get().doubleValue()));
		//System.out.println(cp.getTime().get()+ " speed:"+ GPXRouteBuilder.kmph(cp.getSpeed().get().doubleValue()));
		}
		
		if(starttime==null) {
			curpoint = mytrack.get(0).getTime().get();
			}else 
			{
				curpoint=starttime;
			}
		

	}

	public WayPoint next() {
		return next(GPXTargetValue.SPEED, GPXTargetValue.ELEVATION);
	}

	public WayPoint next(GPXTargetValue... target) {
		if (curpoint.isAfter(lastpoint)) {
			return null;
		}
		long curx = curpoint.toEpochSecond();
		long nexttime = (long)(1000/framerate*1000000);
		curpoint = curpoint.plusNanos(nexttime);
		Builder pointbuilder = WayPoint.builder();
		
		pointbuilder.lat(mytrack.get(0).getLatitude());
		pointbuilder.lon(mytrack.get(0).getLongitude());

		for (GPXTargetValue t : target) {
			switch (t) {
			case SPEED: {
				if (speedfunc == null) {
					speedfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.SPEED);
				}

				pointbuilder.speed(speedfunc.value(curx));
				break;
			}
			case ELEVATION: {
				if (elevationfunc == null) {
					elevationfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.ELEVATION);
				}
				pointbuilder.ele(elevationfunc.value(curx));
				
				break;
			}
			}
		}
		return pointbuilder.time(curpoint).build();

	}
	public WayPoint next(ZonedDateTime at) {
		return next(at,GPXTargetValue.SPEED, GPXTargetValue.ELEVATION);
	}
	public WayPoint next(ZonedDateTime at, GPXTargetValue... target) {
		if (curpoint.isAfter(lastpoint)) {
			return null;
		}
		long curx = curpoint.toEpochSecond();
		long nexttime = (long)(1000/framerate*1000000);
		curpoint = curpoint.plusNanos(nexttime);
		Builder pointbuilder = WayPoint.builder();
		
		pointbuilder.lat(mytrack.get(0).getLatitude());
		pointbuilder.lon(mytrack.get(0).getLongitude());

		for (GPXTargetValue t : target) {
			switch (t) {
			case SPEED: {
				if (speedfunc == null) {
					speedfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.SPEED);
				}

				pointbuilder.speed(speedfunc.value(curx));
				break;
			}
			case ELEVATION: {
				if (elevationfunc == null) {
					elevationfunc = GPXInterpolator.interpolate(mytrack, GPXTargetValue.ELEVATION);
				}
				pointbuilder.ele(elevationfunc.value(curx));
				break;
			}
			}
		}
		return pointbuilder.time(curpoint).build();

	}

	public void seek(ZonedDateTime newStartTime) {
		curpoint=newStartTime;
		
	}

}
