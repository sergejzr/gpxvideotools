package de.zerr.core.math;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public class HiResUnivariateFunction implements UnivariateFunction {

	private UnivariateFunction intepoliert;
	private ZonedDateTime ref;

	public HiResUnivariateFunction(ZonedDateTime ref, List<ZonedDateTime> xs, List<Double> ys, InterpolationType itype) {
		double[] xd = new double[xs.size()];
		double[] yd = new double[xs.size()];
		this.ref = ref;

		for (int i = 0; i < xs.size(); i++) {
			double pos = Duration.between(ref, xs.get(i)).toNanos()/1000000.;

			xd[i] = pos;
			yd[i] = ys.get(i);

		}
		UnivariateInterpolator linearInterpolator = null;
		
		switch(itype)
		{
		case LINEAR:{linearInterpolator = new LinearInterpolator();}
		break;
		case SPLINE:
		{
			linearInterpolator  =new  SplineInterpolator();	
		}
		break;
		}

		intepoliert = linearInterpolator.interpolate(xd, yd);

	}

	@Override
	public double value(double x) {
		// TODO Auto-generated method stub
		return intepoliert.value(x);
	}

	public double value(ZonedDateTime x) {
		Duration d = Duration.between(ref, x);

		double pos = d.toNanos()/1000000;
		// TODO Auto-generated method stub
		return intepoliert.value(pos);
	}

}
