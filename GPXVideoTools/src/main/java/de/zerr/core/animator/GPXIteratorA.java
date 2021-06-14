package de.zerr.core.animator;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.WayPoint;

public class GPXIteratorA {

	ZonedDateTime curpoint = null;

	private List<WayPoint> mytrack;

	private HiResUnivariateFunction elevationfunc;

	private HiResUnivariateFunction latfunc;

	private HiResUnivariateFunction lonfunc;

	private GPX gpx;

	private ZonedDateTime starttime;
	TreeMap<ZonedDateTime, WayPoint> original = new TreeMap<ZonedDateTime, WayPoint>();
	TreeMap<ZonedDateTime, Double> distances = new TreeMap<ZonedDateTime, Double>();

	public GPXIteratorA(String GPXFlename) throws Exception {
		this(GPXFlename, 0, 0);
	}

	public GPXIteratorA(String GPXFlename, int track, int segment) throws Exception {
		this(GPXFlename, track, segment, null);
	}

	public GPXIteratorA(String GPXFlename, int track, int segment, ZonedDateTime starttime) throws Exception {

		gpx = GPX.reader(Mode.LENIENT).read(GPXFlename);

		mytrack = gpx.getTracks().get(track).getSegments().get(segment).getPoints();

		if (starttime == null) {
			starttime = mytrack.get(0).getTime().get();
		}
		this.starttime = starttime;

		distances.put(starttime, 0.);
		for (WayPoint wp : mytrack) {
			original.put(wp.getTime().get(), wp);
		}

		mytrack = refine(mytrack);

		if (this.starttime == null) {
			this.starttime = mytrack.get(0).getTime().get();
		}

		latfunc = GPXInterpolatorA.interpolate(starttime, mytrack, GPXTargetValue.LAT);
		lonfunc = GPXInterpolatorA.interpolate(starttime, mytrack, GPXTargetValue.LON);
		elevationfunc = GPXInterpolatorA.interpolate(starttime, mytrack, GPXTargetValue.ELEVATION);

		calculateDistances();

	}

	private void calculateDistances() {
		double distance = 0;
		WayPoint prev = null;
		for (WayPoint wp : mytrack) {
			if (prev == null) {
				// distances.put(wp.getTime().get(), 0.);
				prev = wp;
			}
			double olddistance=distance;
			double addistance=prev.distance(wp).doubleValue();
			distance=olddistance+addistance;
			distances.put(wp.getTime().get(), distance);
			
			System.out.println("olddistance:"+olddistance+" + addistance:"+addistance+"="+distance);
			System.out.println(prev+" "+prev.getTime().get());
			System.out.println(wp+" "+wp.getTime().get());
			prev = wp;
		}
	}

	public double getDistance(ZonedDateTime resettime, WayPoint atpoint) {
		ZonedDateTime at = atpoint.getTime().get();

		WayPoint prev = null;
		Entry<ZonedDateTime, WayPoint> preventry = original.lowerEntry(at);// original.firstEntry()
		if (preventry == null) {
			return 0;
		}

		prev = preventry.getValue();
		

		double dist = prev.distance(atpoint).doubleValue();
dist=0;
		return dist + distances.get(preventry.getKey())-distances.floorEntry(resettime).getValue();
	}

	/*
	 * public double getDistance(WayPoint atpoint) { ZonedDateTime at =
	 * atpoint.getTime().get(); WayPoint prev = null;
	 * 
	 * 
	 * Entry<ZonedDateTime, Double> prevdistanceentry =
	 * distances.lowerEntry(at);//original.firstEntry()
	 * 
	 * ZonedDateTime lastknowndistancetp = prevdistanceentry.getKey();
	 * 
	 * 
	 * 
	 * Entry<ZonedDateTime, WayPoint> preventry =
	 * original.lowerEntry(lastknowndistancetp);//original.firstEntry() if
	 * (preventry == null) { return 0; }
	 * 
	 * original.h original.
	 * 
	 * }
	 */
	WayPoint oldprev = null;

	public double getSpeed(WayPoint atpoint) {
		ZonedDateTime at = atpoint.getTime().get();

		WayPoint prev = null;
		Entry<ZonedDateTime, WayPoint> preventry = original.lowerEntry(at);// original.firstEntry()
		if (preventry == null) {
			return 0;
		}

		prev = preventry.getValue();
		if (oldprev == null || preventry.getValue() != oldprev) {
			oldprev = preventry.getValue();
			printWP("prev", prev);
			// printWP("cur",atpoint);
			// System.out.println();
		}
		double dist = prev.distance(atpoint).doubleValue();
		Duration d = Duration.between(prev.getTime().get(), atpoint.getTime().get());
		double speed_mps = (dist * 1000.) / (d.toNanos() / 1000000.);
		return speed_mps;

	}

	public WayPoint next(ZonedDateTime at) {

		return WayPoint.builder().lat(latfunc.value(at)).lon(lonfunc.value(at)).ele(elevationfunc.value(at)).time(at)
				.build();
	}

	public static void printWP(String title, WayPoint wp) {
		System.out.println(title + "  " + wp + ", " + (wp.getSpeed().isPresent() ? wp.getSpeed().get() + ", " : "")
				+ wp.getTime().get());

	}

	private List<WayPoint> refine(List<WayPoint> intracksegment) {

		List<WayPoint> tracksegment = new ArrayList();
		WayPoint prev = null;
		for (WayPoint wp : intracksegment) {
			if (prev == null) {
				prev = wp;
				tracksegment.add(wp);
				continue;
			}
			ZonedDateTime prevTime = prev.getTime().get();
			ZonedDateTime curTime = wp.getTime().get();

			// Duration d=Duration.between(prevTime, curTime);

			prevTime = prevTime.plusSeconds(1);
			int max = 2;
			int cnt = 0;

			while (prevTime.isBefore(curTime)) {

				prevTime = prevTime.plusSeconds(1);
				cnt++;
			}
			if (cnt > 30) {
				System.out.println("zwischen " + prev.getTime().get() + " und " + wp.getTime().get() + " Pause von "
						+ cnt + " sekunden");
				System.out.println(prev);
				System.out.println(wp);

				prevTime = prev.getTime().get();
				prevTime = prevTime.plusSeconds(1);
				for (int i = 0; i < 3; i++) {
					tracksegment.add(prev.toBuilder().time(prevTime).build());
					prevTime = prevTime.plusSeconds(1);
				}

				prevTime = prev.getTime().get();

				prevTime = prevTime.plusSeconds(cnt - cnt / 5);

				System.out.println("Füge ein Zwischenframe am " + prevTime);

				tracksegment.add(prev.toBuilder().time(prevTime)
						.lat(Math.abs(prev.getLatitude().doubleValue() + wp.getLatitude().doubleValue()) / 2.)
						.lon(Math.abs(prev.getLongitude().doubleValue() + wp.getLongitude().doubleValue()) / 2.)
						.build());
				System.out.println(tracksegment.get(tracksegment.size() - 1));
				/*
				 * while(prevTime.isBefore(curTime)) {
				 * 
				 * tracksegment.add(prev.toBuilder().time(prevTime).build());
				 * prevTime=prevTime.plusSeconds(1); cnt++; }
				 */
			}

			/*
			 * while(prevTime.isBefore(curTime)) {
			 * 
			 * tracksegment.add(prev.toBuilder().time(prevTime).build());
			 * prevTime=prevTime.plusSeconds(1); if(cnt++>max) break; }
			 */
			tracksegment.add(wp);
			prev = wp;
		}
		return tracksegment;
	}

}