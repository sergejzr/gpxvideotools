package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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

	public Speedprofile(XYSeries series12, TreeMap<ZonedDateTime, Integer> theway) {
		series1=series12;
		this.theway=theway;
	
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
