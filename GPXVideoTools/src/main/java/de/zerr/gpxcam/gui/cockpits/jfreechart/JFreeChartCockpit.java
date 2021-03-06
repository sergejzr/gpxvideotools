package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Point;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialLayer;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.xy.XYSeries;

import de.zerr.core.gpx.GPXRouteBuilder;
import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;
import de.zerr.gpxcam.gui.AbstractCockpit;

//import org.jfree.ui.GradientPaintTransformType;
//import org.jfree.ui.StandardGradientPaintTransformer;

public class JFreeChartCockpit extends AbstractCockpit implements ChangeListener {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	DefaultValueDataset speedDataset;
	DefaultValueDataset elevationDataset;
	DefaultValueDataset distanceDataset;
	JSlider slider1;
	JSlider slider2;

	DialLayer d;

	private DefaultValueDataset accelerationDataset;

	private ContinuousRoute route;

	private Elemeter el;

	private Compass comp;

	private Slopemeter slope;

	private Speedprofile speedel;

	public void stateChanged(ChangeEvent changeevent) {

	}

	public JFreeChartCockpit(ContinuousRoute route) {
		super(route, route.getStartTime(), route.getEndtime());
		setLayout(new BorderLayout());

		speedDataset = new DefaultValueDataset(10D);
		elevationDataset = new DefaultValueDataset(50D);
		distanceDataset = new DefaultValueDataset(50D);
		accelerationDataset = new DefaultValueDataset(50D);

		DialPlot dialplot = new DialPlot();

		dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		dialplot.setDataset(0, speedDataset);
		dialplot.setDataset(1, elevationDataset);
		dialplot.setDataset(2, distanceDataset);
		dialplot.setDataset(3, accelerationDataset);

		StandardDialFrame standarddialframe = new StandardDialFrame();
		standarddialframe.setBackgroundPaint(Color.lightGray);
		standarddialframe.setForegroundPaint(Color.darkGray);
		dialplot.setDialFrame(standarddialframe);

		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(),
				new Color(170, 170, 220));
		DialBackground dialbackground = new DialBackground(gradientpaint);

		dialbackground
				.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));

		dialplot.setBackground(dialbackground);

		DialTextAnnotation dialtextannotation = new DialTextAnnotation("Speed/Elevation");
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.69999999999999996D);
		dialplot.addLayer(dialtextannotation);

		DialValueIndicator speedTextFeldIndicator = new DialValueIndicator(0);
		speedTextFeldIndicator.setFont(new Font("Dialog", 0, 10));
		speedTextFeldIndicator.setOutlinePaint(Color.darkGray);
		speedTextFeldIndicator.setRadius(0.59999999999999998D);
		speedTextFeldIndicator.setAngle(-103D);
		dialplot.addLayer(speedTextFeldIndicator);

		DialValueIndicator elevationTextFieldIndicator = new DialValueIndicator(1);
		elevationTextFieldIndicator.setFont(new Font("Dialog", 0, 10));
		elevationTextFieldIndicator.setOutlinePaint(Color.red);
		elevationTextFieldIndicator.setRadius(0.59999999999999998D);
		elevationTextFieldIndicator.setAngle(-77D);
		dialplot.addLayer(elevationTextFieldIndicator);

		MilageCounter distanceTextFieldIndicator = new MilageCounter(2);
		distanceTextFieldIndicator.setFont(new Font("Dialog", 0, 10));
		distanceTextFieldIndicator.setOutlinePaint(Color.green);
		distanceTextFieldIndicator.setRadius(0.59999999999999998D);
		distanceTextFieldIndicator.setAngle(77D);
		dialplot.addLayer(distanceTextFieldIndicator);

		DialValueIndicator accelerationTextFieldIndicator = new DialValueIndicator(3);
		accelerationTextFieldIndicator.setFont(new Font("Dialog", 0, 10));
		accelerationTextFieldIndicator.setOutlinePaint(Color.yellow);
		accelerationTextFieldIndicator.setRadius(0.59999999999999998D);
		accelerationTextFieldIndicator.setAngle(-50D);
		dialplot.addLayer(accelerationTextFieldIndicator);

		StandardDialScale speedScale = new StandardDialScale(0D, 60D, -120D, -300D, 10D, 4);
		speedScale.setTickRadius(0.88D);
		speedScale.setTickLabelOffset(0.14999999999999999D);
		speedScale.setTickLabelFont(new Font("Dialog", 0, 14));
		dialplot.addScale(0, speedScale);

		StandardDialScale elevationScale = new StandardDialScale(0D, 400D, -120D, -100D, 100D, 4);
		elevationScale.setTickRadius(0.5D);
		elevationScale.setTickLabelOffset(0.14999999999999999D);
		elevationScale.setTickLabelFont(new Font("Dialog", 0, 10));
		elevationScale.setMajorTickPaint(Color.CYAN);
		elevationScale.setMinorTickPaint(Color.CYAN);
		dialplot.addScale(1, elevationScale);

		dialplot.mapDatasetToScale(1, 1);

		StandardDialScale accelerationScale = new StandardDialScale(11D, -11D, -320D, -100D, .1D, 4);
		accelerationScale.setTickRadius(0.5D);
		accelerationScale.setTickLabelOffset(0.14999999999999999D);
		accelerationScale.setTickLabelFont(new Font("Dialog", 0, 10));
		accelerationScale.setMajorTickPaint(Color.red);
		accelerationScale.setMinorTickPaint(Color.red);
		dialplot.addScale(2, accelerationScale);

		dialplot.mapDatasetToScale(3, 2);

		StandardDialRange highVelocityRange = new StandardDialRange(35, 50D, Color.yellow);
		highVelocityRange.setScaleIndex(0);
		highVelocityRange.setInnerRadius(speedScale.getTickRadius() - speedScale.getMajorTickLength());
		highVelocityRange.setOuterRadius(speedScale.getTickRadius());
		dialplot.addLayer(highVelocityRange);

		StandardDialRange overdriveVelocityRange = new StandardDialRange(50D, 60D, Color.red);
		overdriveVelocityRange.setScaleIndex(0);
		overdriveVelocityRange.setInnerRadius(speedScale.getTickRadius() - speedScale.getMajorTickLength());
		overdriveVelocityRange.setOuterRadius(speedScale.getTickRadius());

		dialplot.addLayer(overdriveVelocityRange);

		org.jfree.chart.plot.dial.DialPointer.Pin pin = new org.jfree.chart.plot.dial.DialPointer.Pin(1);
		pin.setRadius(0.55000000000000004D);
		dialplot.addPointer(pin);

		org.jfree.chart.plot.dial.DialPointer.Pointer pointer = new org.jfree.chart.plot.dial.DialPointer.Pointer(0);
		dialplot.addPointer(pointer);

		org.jfree.chart.plot.dial.DialPointer.Pin pinacc = new org.jfree.chart.plot.dial.DialPointer.Pin(3);
		pinacc.setRadius(0.55000000000000004D);
		dialplot.addPointer(pinacc);

		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10000000000000001D);
		dialplot.setCap(dialcap);

		// ChartFactory.create

		JFreeChart jfreechart = new JFreeChart(dialplot);
		// jfreechart.setTitle("Dial Demo 2");
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(400, 400));
		chartpanel.setSize(new Dimension(400, 400));

		/*
		 * JPanel jpanel = new JPanel(new GridLayout(2, 2));
		 * 
		 * 
		 * jpanel.add(new JLabel("Outer Needle:")); jpanel.add(new
		 * JLabel("Inner Needle:")); slider1 = new JSlider(-40, 60);
		 * slider1.setMajorTickSpacing(20); slider1.setPaintTicks(true);
		 * slider1.setPaintLabels(true); slider1.addChangeListener(this);
		 * jpanel.add(slider1); jpanel.add(slider1); slider2 = new JSlider(0, 100);
		 * slider2.setMajorTickSpacing(20); slider2.setPaintTicks(true);
		 * slider2.setPaintLabels(true); slider2.addChangeListener(this);
		 * jpanel.add(slider2);
		 */
		add(chartpanel, BorderLayout.CENTER);
		JPanel east = new JPanel(new GridLayout(2, 1));

		XYSeries serieselevation = new XYSeries("Planned");
		XYSeries seriesspeed = new XYSeries("Planned");
		Duration d = Duration.ofNanos(250000000);
		Iterator<RoutePoint> it = iterateBy(d);

		Double oldx = null;
		RoutePoint startKm = getAt(getStarttime());
		Double startdist = startKm.getDistance();
		TreeMap<ZonedDateTime, Integer> theway = new TreeMap();
		while (it.hasNext()) {
			RoutePoint rp = it.next();

			double curkm = rp.getDistance();
			if (startKm == null) {
				startKm = rp;
				startdist = startKm.getDistance();
			}

			if (oldx != null && oldx == curkm) {
				continue;
			}
			oldx = curkm;

			// theway.put(startpoint,series1.getItemCount());
			serieselevation.add(curkm - startdist, rp.getElevation());
			seriesspeed.add(curkm - startdist, GPXRouteBuilder.kmph(rp.getSpeed()));

			theway.put(rp.getTime(), seriesspeed.getItemCount());
		}

		el = new Elemeter(serieselevation, theway);
		speedel = new Speedprofile(seriesspeed, theway);
		// el = Elemeter.fromRoute(route);

		// speedel = Speedprofile.fromRoute(route);
		east.add(el);
		east.add(speedel);

		// add(el,BorderLayout.EAST);
		add(east, BorderLayout.EAST);

		comp = new Compass();
		comp.setPreferredSize(new Dimension(100, 200));
		slope = new Slopemeter();
		slope.setPreferredSize(new Dimension(100, 200));

		JPanel compasslope = new JPanel(new GridLayout(2, 1));
		compasslope.add(comp);
		compasslope.add(slope);
		add(compasslope, BorderLayout.WEST);

		// add(jpanel, "South");
	}

	/*
	 * public void setRoute(ContinuousRoute route) {
	 * 
	 * ZonedDateTime start = route.getStartTime(); ZonedDateTime end =
	 * route.getEndtime();
	 * 
	 * final TimeSeries series1 = new TimeSeries("Elevation profile"); double value
	 * = 0.0; Day day = new Day(); for (int i = 0; i < 200; i++) { value += 1; //
	 * Math.abs(value + Math.random() - 0.5); series1.add(day, value); day = (Day)
	 * day.next(); } RegularTimePeriod b; final TimeSeriesCollection dataset = new
	 * TimeSeriesCollection(series1);
	 * 
	 * while (start.isBefore(end)) {
	 * 
	 * }
	 * 
	 * }
	 */
	@Override
	public void at(ZonedDateTime time) {

		RoutePoint rp = getAt(time);
		speedDataset.setValue(rp.getSpeed());
		distanceDataset.setValue(rp.getDistance() / 1000.);
		elevationDataset.setValue(rp.getElevation());
		accelerationDataset.setValue(rp.getAcceleration());
		el.setTime(time);
		speedel.setTime(time);
		comp.setBearing(rp.getHeading());
		slope.setSlope(rp.getSlope());
	}

	/*
	 * public JFreeChartCockpit(String s) { super(s); setDefaultCloseOperation(3);
	 * setContentPane(createDemoPanel()); }
	 * 
	 * public static JPanel createDemoPanel() { return new DemoPanel(); }
	 * 
	 * public static void main(String args[]) { JFreeChartCockpit dialdemo2 = new
	 * JFreeChartCockpit("JFreeChart - Dial Demo 2"); dialdemo2.pack();
	 * dialdemo2.setVisible(true); }
	 */
}
