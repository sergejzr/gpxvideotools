package de.zerr.core.animator;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.BivariateGridInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.MultivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import de.zerr.core.videowriter.InterpolationType;
import io.jenetics.jpx.WayPoint;

public class GPXInterpolatorA {
	
	
	public static HiResUnivariateFunction interpolate(ZonedDateTime starttime, List<WayPoint> route, GPXTargetValue targetValue)
	{
		return interpolate(starttime, route, targetValue, InterpolationType.SPLINE);
	}

	public static HiResUnivariateFunction interpolate(ZonedDateTime starttime, List<WayPoint> route, GPXTargetValue targetValue,InterpolationType itype) {


		
		List<ZonedDateTime>  x=new ArrayList<ZonedDateTime>();
		List<Double> y = new ArrayList<Double>();
		
		int i = 0;
		for (WayPoint wp : route) {
			//double pos = Duration.between(starttime, wp.getTime().get()).getSeconds()*1000.;
		
			
			
			
			x.add(wp.getTime().get());
			
			switch (targetValue) {
			case SPEED: {
				y.add(wp.getSpeed().get().doubleValue());
			}
				break;
			case ELEVATION: {
				y.add(wp.getElevation().get().doubleValue());
			}
				break;
			case LAT: {
				y.add( wp.getLatitude().doubleValue());
			}
				break;
			case LON: {
				y.add(wp.getLongitude().doubleValue());
			}
				break;
			default:
				break;
			}

			i++;
		}

		HiResUnivariateFunction ret=new HiResUnivariateFunction(starttime, x, y, itype);
		
		return ret;

	}
	/*
private List<WayPoint> refine(List<WayPoint> intracksegment) {
		
		List<WayPoint> tracksegment=new ArrayList<WayPoint>();
		WayPoint prev=null;
		for (WayPoint wp : intracksegment) {
			if (prev == null) {
				prev = wp;
				tracksegment.add(wp);
				continue;
			}
			ZonedDateTime prevTime = prev.getTime().get();
			ZonedDateTime curTime = wp.getTime().get();
			
			//Duration d=Duration.between(prevTime, curTime);
			
			prevTime=prevTime.plusSeconds(1);
			while(prevTime.isBefore(curTime))
			{
				
				tracksegment.add(prev.toBuilder().time(prevTime).build());
				prevTime=prevTime.plusSeconds(1);
			}
			tracksegment.add(wp);
			prev = wp;
		}
		return tracksegment;
	}
*/

}
