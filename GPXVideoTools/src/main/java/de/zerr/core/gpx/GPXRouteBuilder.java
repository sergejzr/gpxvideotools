package de.zerr.core.gpx;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import de.zerr.core.gpx.route.DiscreteRoute;
import de.zerr.core.gpx.route.Route;
import de.zerr.core.gpx.route.RoutePoint;
import de.zerr.core.math.HiResUnivariateFunction;
import de.zerr.core.math.InterpolationType;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.WayPoint.Builder;

public class GPXRouteBuilder {

	private GPX gpx;

	//Route route = new DiscreteRoute();

	private HiResUnivariateFunction latfunc;

	private HiResUnivariateFunction lonfunc;

	private HiResUnivariateFunction elevationfunc;

	private List<WayPoint> mytrack;

	private InterpolationType itype;

	public GPXRouteBuilder(String GPXFlename, InterpolationType itype) throws IOException {
		gpx = GPX.reader(Mode.LENIENT).read(GPXFlename);
		this.itype = itype;
		createRoute(0, 0);
	}

	public DiscreteRoute makeRoute(List<WayPoint> track, ZonedDateTime videoStartTime, ZonedDateTime videoEndTime) {
		

		List<WayPoint> speedenhanced = addSpeed(fillGaps(track));
		
		ZonedDateTime gpxstarttime = speedenhanced.get(0).getTime().get();
		DiscreteRoute ret = new DiscreteRoute(gpxstarttime);

		WayPoint prev = null;
		double prevdistance = 0;
		for (WayPoint wp : speedenhanced) {
			
			if(wp.getTime().get().isBefore(videoStartTime)) 
			{
				continue;
			}
			if(wp.getTime().get().isAfter(videoEndTime)) 
			{
				break;
			}
			
			
			RoutePoint rp = RoutePoint.from(wp);
			if (prev == null) {
				rp.setAcceleration(0).setDistance(0).setHeading(0).setSlope(0);
				prev = wp;
				ret.put(rp);
				continue;
			}

			RoutePoint prevRT = ret.at(wp.getTime().get());

			Duration dur = Duration.between(wp.getTime().get(), prevRT.getTime());
			double timediff = (dur.toMillis() / 1000.);
			double speeddiff = (wp.getSpeed().get().doubleValue() - prevRT.getSpeed()) / timediff;
			double distance = prev.distance(wp).doubleValue();

			double slope = distance < 1 ? prevRT.getSlope()
					: (wp.getElevation().get().doubleValue() - prev.getElevation().get().doubleValue()) / distance;

			double lat1 = prevRT.getLattitude();
			double lat2 = wp.getLatitude().doubleValue();

			double longitude1 = prevRT.getLontgitude();
			double longitude2 = wp.getLongitude().doubleValue();

			double latitude1 = Math.toRadians(lat1);
			double latitude2 = Math.toRadians(lat2);
			double longDiff = Math.toRadians(longitude2 - longitude1);
			double y = Math.sin(longDiff) * Math.cos(latitude2);
			double x = Math.cos(latitude1) * Math.sin(latitude2)
					- Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);
			double resultDegree = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

			rp = rp.setAcceleration(speeddiff / timediff).setDistance(distance + prevRT.getDistance()).setSlope(slope)
					.setHeading(resultDegree);
			ret.put(rp);
			prevdistance = distance;
			prev = wp;
		}

		return ret;
	}

	private void createRoute(int track, int segment) {

		mytrack = gpx.getTracks().get(track).getSegments().get(segment).getPoints();
		ZonedDateTime starttime = mytrack.get(0).getTime().get();

		// InterpolationType itype = InterpolationType.LINEAR;

		latfunc = GPXInterpolator.interpolate(starttime, mytrack, GPXTargetValue.LAT, itype);
		lonfunc = GPXInterpolator.interpolate(starttime, mytrack, GPXTargetValue.LON, itype);
		elevationfunc = GPXInterpolator.interpolate(starttime, mytrack, GPXTargetValue.ELEVATION, itype);

		List<WayPoint> uniformtrack = fillGaps(mytrack);

	}

	public List<WayPoint> addSpeed(List<WayPoint> in) {

		List<WayPoint> ret = new ArrayList<WayPoint>();
		WayPoint prev = null;
		for (WayPoint wp : in) {
			if (prev == null) {
				ret.add(wp.toBuilder().speed(0).build());
				prev = wp;
				continue;
			}

			double dist = prev.distance(wp).doubleValue();
			Duration d = Duration.between(prev.getTime().get(), wp.getTime().get());
			double speed_mps = (dist * 1000.) / (d.toNanos() / 1000000.);

			ret.add(wp.toBuilder().speed(speed_mps).build());
			prev = wp;
		}

		return ret;
	}

	public List<WayPoint> getMytrack() {
		return mytrack;
	}

	public List<WayPoint> fillGaps(List<WayPoint> in) {
		List<WayPoint> ret = new ArrayList<WayPoint>();

		WayPoint prev = in.get(0);

		ZonedDateTime curtime = prev.getTime().get();
		ZonedDateTime lasttime = in.get(in.size() - 1).getTime().get();

		while (!curtime.isAfter(lasttime)) {
			ret.add(WayPoint.builder().lat(latfunc.value(curtime)).lon(lonfunc.value(curtime))
					.ele(elevationfunc.value(curtime)).time(curtime).build());

			curtime = curtime.plusSeconds(1);
		}
		return ret;
	}

	public static Hashtable<GPXTargetValue, List<Double>> toDoubleArrays(List<WayPoint> in,
			GPXTargetValue... enhancements) throws Exception {
		// List<Double> y = new ArrayList<Double>();

		Hashtable<GPXTargetValue, List<Double>> ret = new Hashtable<GPXTargetValue, List<Double>>();

		for (GPXTargetValue en : enhancements) {
			List<Double> x = new ArrayList<Double>();
			ret.put(en, x);
			for (WayPoint wp : in) {
				switch (en) {
				case ELEVATION: {
					x.add(wp.getElevation().get().doubleValue());
				}
					break;
				case LAT: {
					x.add(wp.getLatitude().doubleValue());
				}
					break;
				case LON: {
					x.add(wp.getLongitude().doubleValue());
				}
					break;
				case SPEED: {
					x.add(wp.getSpeed().get().doubleValue());
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

	public static ArrayList<WayPoint> lowPass(List<WayPoint> in, double lowPass, double frequency,
			GPXTargetValue... enhancements) throws Exception {
		Hashtable<GPXTargetValue, List<Double>> dar = toDoubleArrays(in, enhancements);
		Hashtable<GPXTargetValue, double[]> darout = new Hashtable<GPXTargetValue, double[]>();
		ArrayList<WayPoint> ret = new ArrayList<WayPoint>();

		for (GPXTargetValue en : enhancements) {
			List<Double> dV = dar.get(en);
			double[] ind = new double[dV.size()];
			for (int i = 0; i < dV.size(); i++) {
				ind[i] = dV.get(i);
			}
			ind = fourierLowPassFilter(ind, lowPass, frequency);
			darout.put(en, ind);
		}

		for (int i = 0; i < in.size(); i++) {
			WayPoint wp = in.get(i);
			Builder wb = wp.toBuilder();

			for (GPXTargetValue en : enhancements) {
				double val = darout.get(en)[i];
				switch (en) {
				case ELEVATION: {
					wb.ele(val);
				}
					break;
				case LAT: {
					wb.lat(val);
				}
					break;
				case LON: {
					wb.lon(val);
				}
					break;
				case SPEED: {
					wb.speed(val);
				}
					break;
				default: {
					throw new Exception("'" + "' is  not  implemented!");
				}

				}
			}
			ret.add(wb.build());
		}

		return ret;
	}

	public static double[] fourierLowPassFilter(double[] data, double lowPass, double frequency) {
		// data: input data, must be spaced equally in time.
		// lowPass: The cutoff frequency at which
		// frequency: The frequency of the input data.

		// The apache Fft (Fast Fourier Transform) accepts arrays that are powers of 2.
		int minPowerOf2 = 1;
		while (minPowerOf2 < data.length)
			minPowerOf2 = 2 * minPowerOf2;

		// pad with zeros
		double[] padded = new double[minPowerOf2];
		for (int i = 0; i < data.length; i++)
			padded[i] = data[i];

		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] fourierTransform = transformer.transform(padded, TransformType.FORWARD);

		// build the frequency domain array
		double[] frequencyDomain = new double[fourierTransform.length];
		for (int i = 0; i < frequencyDomain.length; i++)
			frequencyDomain[i] = frequency * i / (double) fourierTransform.length;

		// build the classifier array, 2s are kept and 0s do not pass the filter
		double[] keepPoints = new double[frequencyDomain.length];
		keepPoints[0] = 1;
		for (int i = 1; i < frequencyDomain.length; i++) {
			if (frequencyDomain[i] < lowPass)
				keepPoints[i] = 2;
			else
				keepPoints[i] = 0;
		}

		// filter the fft
		for (int i = 0; i < fourierTransform.length; i++)
			fourierTransform[i] = fourierTransform[i].multiply((double) keepPoints[i]);

		// invert back to time domain
		Complex[] reverseFourier = transformer.transform(fourierTransform, TransformType.INVERSE);

		// get the real part of the reverse
		double[] result = new double[data.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = reverseFourier[i].getReal();
		}

		return result;
	}

	public static void main(String[] args) {
		try {
			GPXRouteBuilder rb = new GPXRouteBuilder("/home/szerr/Downloads/RK_gpx _2021-05-30_1045.gpx",
					InterpolationType.SPLINE);

			List<WayPoint> mt = rb.fillGaps(rb.getMytrack());

			List<WayPoint> enhanced = rb.addSpeed(mt);

			try {
				ArrayList<WayPoint> res = rb.lowPass(enhanced, 100., 100., GPXTargetValue.SPEED);
				rb.print(res);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// List<WayPoint> mt = rb.addSpeed(rb.fillGaps(rb.getMytrack()));

			// rb.print(mt); //ArrayList<WayPoint> smoothed = smooth(mt, 8,
			// GPXTargetValue.SPEED); rb.print(enhanced);

			/*
			 * List<WayPoint> mt = rb.getMytrack();
			 * 
			 * ArrayList<WayPoint> sm = rb.smooth(mt, 8, new GPXTargetValue[] {
			 * GPXTargetValue.LAT, GPXTargetValue.LON, GPXTargetValue.ELEVATION });
			 * 
			 * List<WayPoint> fb = rb.fillGaps(sm); List<WayPoint> enhanced =
			 * rb.addSpeed(fb); rb.print(rb.addSpeed(enhanced));
			 */
			/*
			 * List<WayPoint> mt = rb.fillGaps(rb.getMytrack());
			 * 
			 * ArrayList<WayPoint> sm = rb.smooth(mt, 8, new
			 * GPXTargetValue[]{GPXTargetValue.LAT,GPXTargetValue.LON,GPXTargetValue.
			 * ELEVATION});
			 * 
			 * List<WayPoint> enhanced = rb.addSpeed(sm);
			 * 
			 * //List<WayPoint> mt = rb.addSpeed(rb.fillGaps(rb.getMytrack()));
			 * 
			 * 
			 * //rb.print(mt); //ArrayList<WayPoint> smoothed = smooth(mt, 8,
			 * GPXTargetValue.SPEED); rb.print(enhanced);
			 */

			// rb.print(rb.addSpeed(rb.calculateDistances()));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void print(List<WayPoint> l) {
		System.out.println("Time\tLon\tLat\tEle\tSpeed");
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		for (WayPoint wp : l) {
			Double speed = getVal(wp.getSpeed());
			System.out.println(

					sdf.format(Date.from(wp.getTime().get().toInstant())) + "\t" + wp.getLongitude() + "\t"
							+ wp.getLatitude() + "\t"

							+ getVal(wp.getElevation()) + "\t" + (speed == null ? "" : GPXRouteBuilder.kmph(speed))

			);
		}
	}

	public static ArrayList<WayPoint> smooth(List<WayPoint> in, int windows, GPXTargetValue... enhancements) {
		ArrayList<WayPoint> out = new ArrayList<WayPoint>();
		// DescriptiveStatistics stats = new DescriptiveStatistics();

		// Read data from an input stream,
		// displaying the mean of the most recent 100 observations
		// after every 100 observations
		int head = windows / 2 + 1;

		for (int a = 0; a < head; a++) {
			out.add(in.get(a));
		}
		Hashtable<GPXTargetValue, DescriptiveStatistics> idx = new Hashtable<GPXTargetValue, DescriptiveStatistics>();

		for (GPXTargetValue en : enhancements) {
			DescriptiveStatistics stat = new DescriptiveStatistics();
			stat.setWindowSize(windows);
			idx.put(en, stat);
		}

		for (int i = head; i < in.size(); i++) {

			DescriptiveStatistics stat;
			for (int y = i - head; y < i + windows && y < in.size(); y++) {
				WayPoint wp = in.get(y);

				for (GPXTargetValue en : enhancements) {
					stat = idx.get(en);
					switch (en) {
					case ELEVATION: {
						stat.addValue(wp.getElevation().get().doubleValue());
					}
						break;
					case LAT: {
						stat.addValue(wp.getLatitude().doubleValue());
					}
						break;
					case LON: {
						stat.addValue(wp.getLongitude().doubleValue());
					}
						break;
					case SPEED: {
						stat.addValue(wp.getSpeed().get().doubleValue());
					}
						break;
					}

				}
			}

			Builder ib = in.get(i).toBuilder();

			for (GPXTargetValue en : enhancements) {
				stat = idx.get(en);
				switch (en) {
				case ELEVATION: {
					ib.ele(stat.getMean());
				}
					break;
				case LAT: {
					ib.lat(stat.getMean());
				}
					break;
				case LON: {
					ib.lon(stat.getMean());
				}
					break;
				case SPEED: {
					ib.speed(stat.getMean());
				}
					break;
				}
				stat.clear();
			}
			out.add(ib.build());
		}
		return out;

	}

	private Double getVal(Optional in) {
		if (in.isEmpty())
			return null;
		Number s = (Number) in.get();
		return s.doubleValue();
	}
	public static double kmph(double speed_mps) {
		double speed_kph = (speed_mps * 3600.0) / 1000.0;
		return speed_kph;
	}
}
