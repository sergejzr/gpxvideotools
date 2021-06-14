package de.zerr;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import de.zerr.core.animator.GPXRouteBuilderX;
import de.zerr.core.animator.GPXTargetValue;
import de.zerr.core.animator.Route;
import de.zerr.core.animator.RoutePoint;
import de.zerr.core.videowriter.InterpolationType;
import de.zerr.gui.Elemeter;
import io.jenetics.jpx.WayPoint;

public class ContinuousRoute implements Route {



	Hashtable<GPXTargetValue, UnivariateFunction> interpolated = new Hashtable<GPXTargetValue, UnivariateFunction>();

	UnivariateFunction mileagetimeinterpolated;
	 ZonedDateTime referencetp = null;

	private ZonedDateTime starttime;

	private ZonedDateTime endtime;

	private ContinuousRoute() {

	}
public ZonedDateTime getEndtime() {
	return endtime;
}


	public ZonedDateTime getStartTime() 
	{
		return starttime;
	}
	
	public RoutePoint atKm(double mileage, GPXTargetValue... targets) throws Exception
	{
		double timediff = mileagetimeinterpolated.value(mileage);

		return at(starttime.plusNanos((long)timediff*1000000));
	}
	
	public RoutePoint atTime(ZonedDateTime zonedDateTime, GPXTargetValue... targets) throws Exception {

		RoutePoint ret=new RoutePoint();
		
		double xtime = getXTime(referencetp, zonedDateTime);
		ret.setTime(zonedDateTime);
		for(GPXTargetValue t:targets) 
		{
			
			
			double val = interpolated.get(t).value(xtime);
			
			switch (t) {
			case ELEVATION: {
				ret.setElevation(val);
			}
				break;
			case LAT: {
				ret.setLattitude(val);
			}
				break;
			case LON: {
				ret.setLontgitude(val);
			}
				break;
			case SPEED: {
				ret.setSpeed(val);
			}
				break;
			case ACCELERATION: {
				ret.setAcceleration(val);
			}
				break;
			case DISTANCE: {
				ret.setDistance(val);
			}
				break;

			case HEADING: {
				ret.setHeading(val);
			}
				break;
			case SLOPE: {
				ret.setSlope(val);;
			}
				break;
		
			default: {
				throw new Exception("'" +t+"' is  not  implemented!");
			}

			}
		}
		
		
		return ret;
	}
	
	@Override
	public void put(RoutePoint p) {
		// TODO Auto-generated method stub

	}

	public static ContinuousRoute fromDiscrete(DiscreteRoute from, InterpolationType itype, GPXTargetValue... targets)
			throws Exception {
		Hashtable<GPXTargetValue, UnivariateFunction> interpolated = new Hashtable<GPXTargetValue, UnivariateFunction>();
		ContinuousRoute ret = new ContinuousRoute();
ret.setStartTime(from.firstEntry().getValue().getTime());
ret.setEndTime(from.lastEntry().getValue().getTime());
		RoutePoint fp = from.firstEntry().getValue();
		ret.referencetp = fp.getTime();

		Collection<RoutePoint> list = from.values();

		Hashtable<GPXTargetValue, List<Double>> dar = toDoubleArrays(list, GPXTargetValue.ALL_SET);

		double x[] = ArrayUtils.toPrimitive(dar.get(GPXTargetValue.TIME).toArray(new Double[list.size()]));
		double mileage[] = ArrayUtils.toPrimitive(dar.get(GPXTargetValue.DISTANCE).toArray(new Double[list.size()]));
		
		UnivariateInterpolator linearInterpolator = null;

		switch (itype) {
		case LINEAR: {
			linearInterpolator = new LinearInterpolator();
		}
			break;
		case SPLINE: {
			linearInterpolator = new SplineInterpolator();
		}
			break;
		}
		
		for (GPXTargetValue en : targets) {
			double[] y = ArrayUtils.toPrimitive(dar.get(en).toArray(new Double[list.size()]));

			
try {
			ret.interpolated.put(en, linearInterpolator.interpolate(x, y));
}catch(Exception e) 
{
e.printStackTrace();	
}

		}
		ret.mileagetimeinterpolated=linearInterpolator.interpolate(mileage, x);
		
		
		return ret;

	}

	private void setEndTime(ZonedDateTime time2) {
		this.endtime=time2;
		
	}

	private void setStartTime(ZonedDateTime time2) {
this.starttime=time2;
	}

	public static Hashtable<GPXTargetValue, List<Double>> toDoubleArrays(Collection<RoutePoint> in, GPXTargetValue... targets)
			throws Exception {
		// List<Double> y = new ArrayList<Double>();

		Hashtable<GPXTargetValue, List<Double>> ret = new Hashtable<GPXTargetValue, List<Double>>();
		ZonedDateTime firsttp = in.iterator().next().getTime();
		for (GPXTargetValue en : targets) {
			List<Double> y = new ArrayList<Double>();
			ret.put(en, y);
			for (RoutePoint wp : in) {
				switch (en) {
				case ELEVATION: {
					y.add(wp.getElevation());
				}
					break;
				case LAT: {
					y.add(wp.getLattitude());
				}
					break;
				case LON: {
					y.add(wp.getLontgitude());
				}
					break;
				case SPEED: {
					y.add(wp.getSpeed());
				}
					break;
				case ACCELERATION: {
					y.add(wp.getAcceleration());
				}
					break;
				case DISTANCE: {
					y.add(wp.getDistance());
				}
					break;

				case HEADING: {
					y.add(wp.getHeading());
				}
					break;
				case SLOPE: {
					y.add(wp.getSlope());
				}
					break;
				case TIME: {
					double pos = getXTime(firsttp,wp.getTime());
					y.add(pos);
				}
					break;
				default: {
					throw new Exception("'" + "' is  not  implemented!");
				}

				}
			}
		}

		return ret;
	}

	private static double getXTime(ZonedDateTime tp1, ZonedDateTime tp2) {
		double pos = Duration.between(tp1, tp2).toNanos() / 1000000.;
		return pos;
	}

	@Override
	public RoutePoint at(ZonedDateTime zonedDateTime) {
		
		try {
			return atTime(zonedDateTime,GPXTargetValue.ALL_DEPENDENTSET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
public void print(ZonedDateTime from,ZonedDateTime to, double resolution_millisec,GPXTargetValue... todisplay)
{
	ZonedDateTime curp = from;
	
	for(GPXTargetValue t:todisplay)
	{
		System.out.print(t+"\t");
	}
	System.out.println();
	
	while(!curp.isAfter(to))
	{
		RoutePoint rb = this.at(curp);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.print(sdf.format(Date.from(rb.getTime().toInstant()))+"\t");
		
		for(GPXTargetValue t:todisplay)
		{

		
		
		switch (t) {
		case ELEVATION: {
			System.out.print(rb.getElevation()+"\t");
		}
			break;
		case LAT: {
			System.out.print(rb.getLattitude()+"\t");
		}
			break;
		case LON: {
			System.out.print(rb.getLontgitude()+"\t");
		}
			break;
		case SPEED: {
			System.out.print(rb.getSpeed()+"\t");
		}
			break;
		case ACCELERATION: {
			System.out.print(rb.getAcceleration()+"\t");
		}
			break;
		case DISTANCE: {
			System.out.print(rb.getDistance()+"\t");
		}
			break;

		case HEADING: {
			System.out.print(rb.getHeading()+"\t");
		}
			break;
		case SLOPE: {
			System.out.print(rb.getSlope()+"\t");
		}
			break;
	
	

		}
		
		}
		
		System.out.println();
		
		curp=curp.plusNanos((long)(resolution_millisec*1000000.));
	}
	
}
public void setBounds(ZonedDateTime videoStartTime, ZonedDateTime videoEndTime) {
	starttime=videoStartTime;  endtime=videoEndTime;
	
}
}
