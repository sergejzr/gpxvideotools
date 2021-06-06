package de.zerr.core.animator;

import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.BivariateGridInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.MultivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import io.jenetics.jpx.WayPoint;

public class GPXInterpolator {

	public static UnivariateFunction interpolate(List<WayPoint> route, GPXTargetValue targetValue) {
		UnivariateInterpolator linearInterpolator = new LinearInterpolator();

		double[] x = new double[route.size()];
		double[] y = new double[route.size()];

		int i = 0;
		for (WayPoint wp : route) {

			x[i] = wp.getTime().get().toEpochSecond();
			switch (targetValue) {
			case SPEED: {
				y[i] = wp.getSpeed().get().doubleValue();
			}
				break;
			case ELEVATION: {
				y[i] = wp.getElevation().get().doubleValue();
			}
				break;
			case LAT: {
				y[i] = wp.getLatitude().doubleValue();
			}
				break;
			case LON: {
				y[i] =wp.getLongitude().doubleValue();
			}
				break;
			default:
				break;
			}

			i++;
		}

		double[] dates = x;
		double[] deltas = y;
		UnivariateFunction psfSeaWater = linearInterpolator.interpolate(dates, deltas);

		return psfSeaWater;

	}

	MultivariateInterpolator interpolateGPS(List<WayPoint> route, GPXTargetValue targetValue) {
		BivariateGridInterpolator gip;
		return null;

	}

}
