package de.zerr.gpxcam.gui.cockpits.exclusive;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.commons.lang3.ArrayUtils;

import de.zerr.core.gpx.GPXTargetValue;
import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;

public abstract class StatGraph extends JPanel {

	int xs[] = new int[0];
	int ys[] = new int[0];

	int xsa[] = new int[0];
	int ysa[] = new int[0];
	private Color cfill;
	private Color cline;
	private Insets is;

	Point curpos = null;

	TreeMap<ZonedDateTime, Point> inversedistance = new TreeMap<ZonedDateTime, Point>();

	private BasicStroke dashed;
	private double resolution;
	private double scale;
	private double xtickres;
	private double ytickres;

	Vector<Integer> xticks;
	int[] yticks;
	private BasicStroke axixstroke;
	private TreeMap<Integer, Integer> xtickidx;
	private RoutePoint rstart;
	private RoutePoint rfinish;

	public StatGraph(Color cline, Color cfill, double xtickres, double ytickres) {
		this.cline = cline;
		this.cfill = cfill;
		dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
		axixstroke = new BasicStroke();

		this.xtickres = xtickres;
		this.ytickres = ytickres;
	}

	public abstract String getYEinheit();
	public abstract Double getFactor();

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		g.setColor(cfill);
		g.fillPolygon(xsa, ysa, xsa.length);
		g.setColor(cline);
		g.drawPolyline(xs, ys, xs.length);

		Graphics2D g2 = (Graphics2D) g;
		if (curpos != null) {

			g.setColor(Color.RED);
			g.drawOval((int) curpos.getX() - 2, (int) curpos.getY() - 2, 4, 4);
			g.setColor(Color.BLUE);
			g2.setStroke(dashed);
			g.drawLine(0, (int) curpos.getY(), (int) curpos.getX() + 20, (int) curpos.getY());
			g.drawLine((int) curpos.getX(), is.top, (int) curpos.getX(), (int) curpos.getY() + 20);
		}

		g2.setStroke(axixstroke);
		g2.setFont(new Font("TimesRoman", Font.PLAIN, 8));
		xticks = new Vector<Integer>();

		int y = ysa[ysa.length - 1];// getHeight()-(is.bottom+is.top);

		g.setColor(Color.black);
		for (int i = 0; i < rfinish.getDistance(); i += xtickres) {
			Entry<Integer, Integer> e = xtickidx.floorEntry(i);

			if (e == null)
				continue;

			g.drawLine(e.getValue(), y, e.getValue(), y + 3);
			String text = Math.round(i / 1000.) + "km";
			Rectangle2D b = g.getFontMetrics().getStringBounds(text, g2);
			int width = (int) b.getWidth();
			int hight = (int) b.getHeight();
			/*
			 * int width = g.getFontMetrics().stringWidth(text); int height =
			 * g.getFontMetrics().string(text);
			 */
			g.drawString(text, e.getValue() - width / 2, y + hight);

		}

		g.drawLine(xs[0], y, xsa[xsa.length - 1], y);

		

		double startval = 0;
		int i=y;
		for ( i = y; i > is.top+10;) {
			i = (int) (i - (scale * ytickres));
			g.drawLine(xs[0], i, xs[0] - 2, i);
			

			startval += ytickres;
			
			String text = Math.round(startval/getFactor()) + getYEinheit();// +"km";
			Rectangle2D b = g.getFontMetrics().getStringBounds(text, g2);
			int width = (int) b.getWidth();
			int hight = (int) b.getHeight();
			
			g.drawString(text, xs[0] - width-5, i + hight / 2);	

		}
		g.drawLine(xs[0], y, xs[0], i-1);

	}

	public void initRoute(int w, int h, GPXTargetValue type, ContinuousRoute route) {

		xtickidx = new TreeMap<Integer, Integer>();
		is = getBorder().getBorderInsets(this);

		double rahmen = 30.;

		Dimension dim = new Dimension(w, h);
		setSize(new Dimension(w, h));
		setPreferredSize(dim);
		// setMaximumSize(dim);
		setMinimumSize(dim);

		w = w - (is.left + is.right);
		h = h - (is.top + is.bottom);

		rstart = route.at(route.getStartTime());
		rfinish = route.at(route.getEndtime());

		double distance = rfinish.getDistance() - rstart.getDistance();

		resolution = distance / (w - rahmen);

		Iterator<RoutePoint> it = route.iterateBy(Duration.ofMillis(250));

		Vector<Integer> vxs = new Vector<Integer>();
		Vector<Integer> vys = new Vector<Integer>();

		xs = new int[w];
		ys = new int[w];

		Vector<Integer> vxsa = new Vector<Integer>();
		Vector<Integer> vysa = new Vector<Integer>();

		xsa = new int[w + 2];
		ysa = new int[w + 2];

		vxsa.add((int) (is.left + rahmen));
		vysa.add(h + is.top);

		int start = 0;

		double max = Double.MIN_VALUE;

		while (it.hasNext()) {
			RoutePoint rp = it.next();

			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}
			if (max < val) {
				max = val;
			}

		}

		scale = h / max;
		it = route.iterateBy(Duration.ofMillis(20));

		double olddistance = -1;

		while (it.hasNext()) {
			RoutePoint rp = it.next();
			if (rp.getDistance() < olddistance) {
				continue;
			}

			olddistance = rp.getDistance() + resolution;

			xtickidx.put((int) rp.getDistance(), (int) (start + is.left + rahmen));
			vxs.add((int) (start + is.left + rahmen));
			vxsa.add((int) (start + is.left + rahmen));

			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}

			vys.add(h - (int) (val * scale) + is.top);
			vysa.add(h - (int) (val * scale) + is.top);

			inversedistance.put(rp.getTime(),
					new Point((int) (start + is.left + rahmen), h - (int) (val * scale) + is.top));
			start++;
			// if(start>w-1) break;
		}

		vxsa.add(w + is.left);
		vysa.add(h + is.top);

		xs = ArrayUtils.toPrimitive(vxs.toArray(new Integer[vxs.size()]));
		ys = ArrayUtils.toPrimitive(vys.toArray(new Integer[vys.size()]));

		xsa = ArrayUtils.toPrimitive(vxsa.toArray(new Integer[vxsa.size()]));
		ysa = ArrayUtils.toPrimitive(vysa.toArray(new Integer[vysa.size()]));

	}

	public void initRoute3(int w, int h, GPXTargetValue type, ContinuousRoute route) {

		xtickidx = new TreeMap<Integer, Integer>();
		is = getBorder().getBorderInsets(this);

		double rahmen = 10.;

		Dimension dim = new Dimension(w, h);
		setSize(new Dimension(w, h));
		setPreferredSize(dim);
		// setMaximumSize(dim);
		setMinimumSize(dim);

		w = w - (is.left + is.right);
		h = h - (is.top + is.bottom);

		rstart = route.at(route.getStartTime());
		rfinish = route.at(route.getEndtime());

		double distance = rfinish.getDistance() - rstart.getDistance();

		resolution = distance / (w - rahmen);

		Iterator<RoutePoint> it = route.iterateBy(Duration.ofMillis(250));

		Vector<Integer> vxs = new Vector<Integer>();
		Vector<Integer> vys = new Vector<Integer>();

		xs = new int[w];
		ys = new int[w];

		Vector<Integer> vxsa = new Vector<Integer>();
		Vector<Integer> vysa = new Vector<Integer>();

		xsa = new int[w + 2];
		ysa = new int[w + 2];

		xsa[0] = (int) (is.left + rahmen);
		ysa[0] = h + is.top;
		int start = 0;

		double max = Double.MIN_VALUE;

		while (it.hasNext()) {
			RoutePoint rp = it.next();

			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}
			if (max < val) {
				max = val;
			}

		}

		scale = h / max;
		it = route.iterateBy(Duration.ofMillis(20));

		double olddistance = -1;

		while (it.hasNext()) {
			RoutePoint rp = it.next();
			if (rp.getDistance() < olddistance) {
				continue;
			}

			olddistance = rp.getDistance() + resolution;

			xtickidx.put((int) rp.getDistance(), start);
			xs[start] = (int) (start + is.left + rahmen);
			xsa[start + 1] = (int) (start + is.left + rahmen);
			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}
			ys[start] = h - (int) (val * scale) + is.top;
			ysa[start + 1] = ys[start];

			inversedistance.put(rp.getTime(), new Point(xs[start], ys[start]));
			start++;
			// if(start>w-1) break;
		}
		xsa[xsa.length - 1] = w + is.left;
		ysa[xsa.length - 1] = h + is.top;

	}

	public void initRoute2(int w, int h, GPXTargetValue type, ContinuousRoute route) {

		is = getBorder().getBorderInsets(this);

		Dimension dim = new Dimension(w, h);
		setSize(new Dimension(w, h));
		setPreferredSize(dim);
		// setMaximumSize(dim);
		setMinimumSize(dim);

		w = w - (is.left + is.right);
		h = h - (is.top + is.bottom);

		Duration routeduration = Duration.between(route.getStartTime(), route.getEndtime());
		Duration resolution = routeduration.dividedBy(w);

		Iterator<RoutePoint> it = route.iterateBy(resolution);
		xs = new int[w];
		ys = new int[w];

		xsa = new int[w + 2];
		ysa = new int[w + 2];
		xsa[0] = is.left;
		ysa[0] = h + is.top;
		int start = 0;

		double max = Double.MIN_VALUE;

		while (it.hasNext()) {
			RoutePoint rp = it.next();

			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}
			if (max < val) {
				max = val;
			}

		}

		double scale = h / max;
		it = route.iterateBy(resolution);

		while (it.hasNext()) {
			RoutePoint rp = it.next();
			xs[start] = start + is.left;
			xsa[start + 1] = start + is.left;
			double val = 0;
			switch (type) {
			case ELEVATION:
				val = rp.getElevation();
				break;
			case SPEED:
				val = rp.getSpeed();
				break;
			default:
			}
			ys[start] = h - (int) (val * scale) + is.top;
			ysa[start + 1] = ys[start];
			start++;
		}
		xsa[xsa.length - 1] = w + is.left;
		ysa[xsa.length - 1] = h + is.top;

	}

	public void at(ZonedDateTime d) {
		Point pos = inversedistance.ceilingEntry(d).getValue();
		curpos = pos;

		repaint();
	}

}
