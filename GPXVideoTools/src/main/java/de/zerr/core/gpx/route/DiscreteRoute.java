package de.zerr.core.gpx.route;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.time.ZonedDateTime;
import java.util.TreeMap;

import io.jenetics.jpx.WayPoint;

public class DiscreteRoute extends TreeMap<ZonedDateTime, RoutePoint> implements Route {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5723867341114227904L;
	private ZonedDateTime gpxstarttime;

	public DiscreteRoute(ZonedDateTime gpxstarttime) {
		this.gpxstarttime = gpxstarttime;
	}

	@Override
	public void put(RoutePoint p) {
		this.put(p.getTime(), p);

	}

	public ZonedDateTime getGpxstarttime() {
		return gpxstarttime;
	}

	Rectangle2D bounds = null;

	public Rectangle2D getBounds(ZonedDateTime starttime, ZonedDateTime endime) {

		Double lat_min = null, lat_max = null, lon_min = null, lon_max = null;
		Double centerlat = 0., centerlon = 0.;

		if (bounds == null) {

			bounds = new Rectangle2D.Double();

			for (RoutePoint rp : values()) {
				if (rp.getTime().isBefore(starttime)) {
					continue;
				}
				if (rp.getTime().isAfter(endime)) {
					break;
				}
				double lat = rp.getLattitude();
				double lon = rp.getLontgitude();

				if (lat_min == null || lat < lat_min) {
					lat_min = lat;
				}
				if (lat_max == null || lat > lat_max) {
					lat_max = lat;
				}
				if (lon_min == null || lon < lon_min) {
					lon_min = lon;
				}
				if (lon_max == null || lon > lon_max) {
					lon_max = lon;
				}

				// centerlat+=lat;
				// centerlon+=lon;
			}

			WayPoint wpol = WayPoint.builder().lat(lat_min).lon(lon_max).build();
			WayPoint wpul = WayPoint.builder().lat(lat_min).lon(lon_min).build();

			WayPoint wpor = WayPoint.builder().lat(lat_max).lon(lon_max).build();
			WayPoint wpur = WayPoint.builder().lat(lat_max).lon(lon_min).build();

			bounds.setFrame((lat_min + lat_max) / 2, (lon_min + lon_max) / 2, wpul.distance(wpur).doubleValue(),
					wpol.distance(wpul).doubleValue());

		}
		return bounds;
	}

	@Override
	public RoutePoint at(ZonedDateTime zonedDateTime) {
		// TODO Auto-generated method stub
		return this.floorEntry(zonedDateTime).getValue();
	}

}
