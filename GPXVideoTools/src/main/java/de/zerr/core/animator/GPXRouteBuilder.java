package de.zerr.core.animator;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.WayPoint;

public class GPXRouteBuilder {

	private GPX gpx;

	public GPXRouteBuilder(String GPXFlename) throws IOException {
		gpx = GPX.reader(Mode.LENIENT).read(GPXFlename);
	}

	public static ArrayList<WayPoint> smooth(List<WayPoint> in, int windows, GPXTargetValue... enhancements) {
		ArrayList<WayPoint> out = new ArrayList<WayPoint>();
		//DescriptiveStatistics stats = new DescriptiveStatistics();
		

		// Read data from an input stream,
		// displaying the mean of the most recent 100 observations
		// after every 100 observations
		int head = windows / 2 + 1;

		for (int a = 0; a < head; a++) {
			out.add(in.get(a));
		}
		DescriptiveStatistics stats = new DescriptiveStatistics();
		stats.setWindowSize(windows);
		for (int i = head; i < in.size(); i++) {
			
			for (int y = i-head; y < i+windows && y< in.size(); y++) {
				WayPoint wp = in.get(y);
				stats.addValue(wp.getSpeed().get().doubleValue());
			}
			out.add(in.get(i).toBuilder().speed(stats.getMean()).build());
			stats.clear();
		}
		return out;

	}

	public List<WayPoint> getEnhanced(int track, int segment, GPXTargetValue... enhancements) {
		List<WayPoint> theroute = new ArrayList<WayPoint>();

		WayPoint prev = null;

		for (WayPoint wp : gpx.getTracks().get(track).getSegments().get(segment).getPoints()) {
			if (prev == null) {
				prev = wp;
				theroute.add(wp.toBuilder().speed(0).build());
				continue;

			}
			io.jenetics.jpx.WayPoint.Builder wpbuider = wp.toBuilder();

			for (GPXTargetValue en : enhancements) {
				switch (en) {
				case SPEED: {
					double dist = distance_on_geoid(prev.getLatitude().doubleValue(), prev.getLongitude().doubleValue(),
							wp.getLatitude().doubleValue(), wp.getLongitude().doubleValue());
					Duration d = Duration.between(prev.getTime().get(), wp.getTime().get());
					double speed_mps = dist / d.getSeconds();

					double speedlmph = GPXRouteBuilder.kmph(speed_mps);
					
					if (false&&speedlmph < 1) {
						speed_mps = 0;
					}

					wpbuider.speed(speed_mps);

					// System.out.println(wp.getTime().get()+" "+ speed_mps+"
					// "+GPXRouteBuilder.kmph(speed_mps));
				}
					break;
				default:
					break;
				}
			}

			theroute.add(wpbuider.build());
			prev = wp;
		}

		return theroute;
	}

	public static double kmph(double speed_mps) {
		double speed_kph = (speed_mps * 3600.0) / 1000.0;
		return speed_kph;
	}

	private double distance_on_geoid(double lat1, double lon1, double lat2, double lon2) {

		// Convert degrees to radians
		lat1 = lat1 * Math.PI / 180.0;
		lon1 = lon1 * Math.PI / 180.0;

		lat2 = lat2 * Math.PI / 180.0;
		lon2 = lon2 * Math.PI / 180.0;

		// radius of earth in metres
		double r = 6378100;

		// P
		double rho1 = r * Math.cos(lat1);
		double z1 = r * Math.sin(lat1);
		double x1 = rho1 * Math.cos(lon1);
		double y1 = rho1 * Math.sin(lon1);

		// Q
		double rho2 = r * Math.cos(lat2);
		double z2 = r * Math.sin(lat2);
		double x2 = rho2 * Math.cos(lon2);
		double y2 = rho2 * Math.sin(lon2);

		// Dot product
		double dot = (x1 * x2 + y1 * y2 + z1 * z2);
		double cos_theta = dot / (r * r);

		double theta = Math.acos(cos_theta);

		// Distance in Metres
		return r * theta;
	}

}
