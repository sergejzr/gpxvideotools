package de.zerr.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * ---------------------
 * XYAreaChartDemo2.java
 * ---------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYAreaChartDemo2.java,v 1.11 2004/05/11 14:56:17 mungady Exp $
 *
 * Changes
 * -------
 * 26-Nov-2002 : Version 1 (DG);
 *
 */

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.zerr.ContinuousRoute;
import de.zerr.core.animator.GPXRouteBuilderX;
import de.zerr.core.animator.GPXTargetValue;
import de.zerr.core.animator.RoutePoint;

/**
 * A simple demonstration application showing how to create an area chart with a
 * date axis for the domain values.
 *
 */
public class Speedprofile extends JPanel {

	private static XYSeries series1;
	private static double startdist;
	private ChartPanel chartPanel;
	private XYPlot plot;
	private XYPointerAnnotation pointer;
	private XYCrossAnnotation pointer2;

	static BufferedImage bikeimage;

	TreeMap<ZonedDateTime, Integer> theway;

	public static Speedprofile fromRoute(ContinuousRoute route) {
		TreeMap<ZonedDateTime, Integer> theway = new TreeMap<ZonedDateTime, Integer>();
		series1 = new XYSeries("Planned");
		ZonedDateTime startpoint = route.getStartTime();
		ZonedDateTime endpoint = route.getEndtime();

		RoutePoint startKm = route.at(startpoint);
		startdist = startKm.getDistance();
		// RoutePoint endKm = route.at(endpoint);
		Double oldx = null;
		while (!startpoint.isAfter(endpoint)) {
			RoutePoint rp = route.at(startpoint);
			double curkm = rp.getDistance();
			if (oldx != null && oldx == curkm) {
				continue;
			}
			oldx = curkm;

			double el = rp.getSpeed();
			theway.put(startpoint, series1.getItemCount());
			series1.add(curkm - startdist, GPXRouteBuilderX.kmph(el));

			startpoint = startpoint.plusNanos(250000000);
		}

		/*
		 * 
		 * 
		 * try { for (double i = startKm.getDistance(); i < endKm.getDistance();i+=1) {
		 * 
		 * RoutePoint p = route.atKm(i, GPXTargetValue.ALL_SET);
		 * 
		 * series1.add(i, p.getElevation());
		 * 
		 * 
		 * }
		 * 
		 * } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 */

		Speedprofile ret = new Speedprofile(series1);
		ret.theway = theway;
		return ret;
	}

	public Speedprofile(XYSeries series12) {
		XYSeriesCollection dataset = new XYSeriesCollection(series12);

		JFreeChart chart = createChart(dataset);

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		add(chartPanel);
	}

	/**
	 * Creates a new demo.
	 *
	 * @param title the frame title.
	 */
	public Speedprofile() {
		try {
			bikeimage = ImageIO.read(new File("/home/szerr/git/gpxvideotools/GPXVideoTools/img/icons/bike.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TimeSeries series1 = new TimeSeries("Random 1");
		double value = 0.0;

		Day day = new Day();
		for (int i = 0; i < 200; i++) {
			value += 1; // Math.abs(value + Math.random() - 0.5);
			series1.add(day, value);
			day = (Day) day.next();
		}

		TimeSeriesCollection dataset = new TimeSeriesCollection(series1);

		JFreeChart chart = createChart(dataset);

		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		add(chartPanel);
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYAreaChart("XY Area Chart Demo 2", "Time", "Value", dataset,
				PlotOrientation.VERTICAL, false, // legend
				false, // tool tips
				false // URLs
		);

		plot = chart.getXYPlot();

		final ValueAxis domainAxis = new NumberAxis("Km");
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);

		plot.setDomainAxis(domainAxis);
		plot.setForegroundAlpha(0.5f);

		DialValueIndicator elevationTextFieldIndicator = new DialValueIndicator(1);
		elevationTextFieldIndicator.setFont(new Font("Dialog", 0, 10));
		elevationTextFieldIndicator.setOutlinePaint(Color.red);
		elevationTextFieldIndicator.setRadius(0.59999999999999998D);
		elevationTextFieldIndicator.setAngle(-77D);

		Day day = new Day();

		day = (Day) day.next();
		day = (Day) day.next();
		day = (Day) day.next();
		day = (Day) day.next();
		day = (Day) day.next();

		TimeSeries series2 = new TimeSeries("Random 1");
		series2.add(day, 0);

		return chart;
	}

	public void setTime(ZonedDateTime curTime) {
		int idx = theway.floorEntry(curTime).getValue();
		XYDataItem pos = series1.getDataItem(idx);

		if (pointer2 == null) {

			plot.addAnnotation(pointer2 = new XYCrossAnnotation());
		}

		// pointer2.setX(pos.getXValue());
		// pointer2.setY(pos.getYValue());

		pointer2.setPos(pos.getXValue(), pos.getYValue());

		/*
		 * if (bikeimage==null) {
		 * 
		 * try { bikeimage=ImageIO.read(new
		 * File("/home/szerr/git/gpxvideotools/GPXVideoTools/img/icons/bike.png"));
		 * //imgwidth=bikeimage.getWidth(null); //imgheight=bikeimage.getHeight(null); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }else{
		 * 
		 * plot.addAnnotation(pointer2 = new
		 * XYImageAnnotation(series1.getDataItem(idx).getX().doubleValue(),
		 * series1.getDataItem(idx).getY().doubleValue(),bikeimage)); }
		 * 
		 * int idx = theway.floorEntry(curTime).getValue(); XYDataItem pos =
		 * series1.getDataItem(idx);
		 * 
		 * if (pointer2 != null) { //plot.removeAnnotation(pointer);
		 * //plot.removeAnnotation(pointer2); pointer2.setX(pos.getXValue());
		 * pointer2.setY(pos.getYValue());
		 * 
		 * }else{
		 * 
		 * plot.addAnnotation(pointer2 = new
		 * XYImageAnnotation(series1.getDataItem(idx).getX().doubleValue(),
		 * series1.getDataItem(idx).getY().doubleValue(),bikeimage)); }
		 */
	}

	public void setKm(double distance) {
	}

}
