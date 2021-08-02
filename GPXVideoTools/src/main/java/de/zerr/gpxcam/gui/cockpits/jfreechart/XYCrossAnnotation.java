package de.zerr.gpxcam.gui.cockpits.jfreechart;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class XYCrossAnnotation extends AbstractXYAnnotation {

	private BasicStroke dashed;
	Integer oldx = null, oldy = null;

	public XYCrossAnnotation() {
		dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
	}

	double xValue, yValue;

	public void setPos(double xValue, double yValue) {

		if (oldx == null || oldy == null) {
		} else {
			if (oldx == (int)xValue && oldy == (int)yValue)
				return;
		}

		this.xValue = oldx = (int)xValue;
		this.yValue = oldy = (int)yValue;
		fireAnnotationChanged();
	}

	public void setX(double xValue) {

		if (oldx == xValue)
			return;

		this.xValue = oldx = (int)xValue;
		fireAnnotationChanged();
	}

	public void setY(double yValue) {
		if (oldy == yValue)
			return;

		this.yValue = oldy = (int)yValue;

		// fireAnnotationChanged();
	}

	@Override
	public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis,
			int rendererIndex, PlotRenderingInfo info) {
		g2.setColor(Color.BLUE);
	
		
		PlotOrientation orientation = plot.getOrientation();
		RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
		RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);

		double anchorX = domainAxis.valueToJava2D(this.xValue, dataArea, domainEdge);
		double anchorY = rangeAxis.valueToJava2D(this.yValue, dataArea, rangeEdge);
		g2.setColor(Color.RED);
		g2.drawOval((int) anchorX-2, (int) anchorY-2, 4, 4);
		g2.setColor(Color.BLUE);
		g2.setStroke(dashed);
		g2.drawLine(0,(int) anchorY,(int) anchorX+20, (int) anchorY);
		g2.drawLine((int) anchorX,0,(int) anchorX, (int) anchorY+20);
		//g2.drawLine((int) anchorX,0,(int) anchorX, (int) anchorY);

	}

}
