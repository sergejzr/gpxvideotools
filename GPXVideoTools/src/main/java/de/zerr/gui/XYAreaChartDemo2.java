package de.zerr.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
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
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;


/**
 * A simple demonstration application showing how to create an area chart with a date axis for
 * the domain values.
 *
 */
public class XYAreaChartDemo2 extends JFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public XYAreaChartDemo2(final String title) {

        super(title);

        final TimeSeries series1 = new TimeSeries("Random 1");
        double value = 0.0;
        Day day = new Day();
        for (int i = 0; i < 200; i++) {
            value +=1; //Math.abs(value + Math.random() - 0.5);
            series1.add(day, value);
            day = (Day) day.next();
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection(series1);

        final JFreeChart chart = createChart(dataset);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createXYAreaChart(
            "XY Area Chart Demo 2",
            "Time", "Value",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            false,  // tool tips
            false  // URLs
        );
       
        final XYPlot plot = chart.getXYPlot();
        
        XYShapeAnnotation a1 = new XYShapeAnnotation(
                new Rectangle2D.Double(1.0, 2.0, 300, 900), new BasicStroke(70.0f), Color.blue
            );
        //
        plot.addAnnotation(a1);

        final ValueAxis domainAxis = new DateAxis("Time");
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
        
        day = (Day) day.next();day = (Day) day.next();day = (Day) day.next();day = (Day) day.next();day = (Day) day.next();
       
       TimeSeries series2 = new TimeSeries("Random 1");
       series2.add(day,0);
       final Hour h = new Hour(2, new Day(1, 8, 2021));
       final Minute m = new Minute(10, h);
       long millis = m.getFirstMillisecond();
       
       final XYPointerAnnotation pointer = new XYPointerAnnotation("", millis, 1,
               3.0 * Math.PI / 4.0);
pointer.setBaseRadius(35.0);
pointer.setTipRadius(10.0);
pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
pointer.setPaint(Color.blue);
pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
//plot.addAnnotation(pointer);

plot.addAnnotation(new XYTextAnnotation("", millis, 60) {
	
	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
			
			int rendererIndex, PlotRenderingInfo info) {
		super.draw(g2, plot, dataArea, domainAxis, rangeAxis, rendererIndex, info);
		
		 PlotOrientation orientation = plot.getOrientation();
	        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
	                plot.getDomainAxisLocation(), orientation);
	        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
	                plot.getRangeAxisLocation(), orientation);
	        double j2DX = domainAxis.valueToJava2D(getX(), dataArea, domainEdge);
	        double j2DY = rangeAxis.valueToJava2D(getY(), dataArea, rangeEdge);
	        
	        g2.drawString("Zhipa", (int)j2DX, (int)j2DY);
		
	}
});

        
       // final XYItemRenderer renderer = plot.getRenderer();
        
      
       
        /*
        renderer.addAnnotation(new AbstractXYAnnotation() {
			
			@Override
			public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
					int rendererIndex, PlotRenderingInfo info) {
				g2.drawRect(plot.getDataset()., rendererIndex, rendererIndex, rendererIndex)
				
			}
		}
        );
        */

        return chart;      
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final XYAreaChartDemo2 demo = new XYAreaChartDemo2("XY Area Chart Demo 2");
        demo.pack();
      
        demo.setVisible(true);

    }

}
