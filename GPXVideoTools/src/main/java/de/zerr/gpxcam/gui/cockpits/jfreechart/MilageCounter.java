package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.HashUtils;
import org.jfree.chart.plot.dial.AbstractDialLayer;
import org.jfree.chart.plot.dial.DialLayer;
import org.jfree.chart.plot.dial.DialLayerChangeEvent;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.Size2D;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.SerialUtils;

public class MilageCounter extends AbstractDialLayer implements DialLayer{

    /** For serialization. */
    static final long serialVersionUID = 803094354130942585L;

    /** The dataset index. */
    private int datasetIndex;

    /** The angle that defines the anchor point. */
    private double angle;

    /** The radius that defines the anchor point. */
    private double radius;

    /** The frame anchor. */
    private RectangleAnchor frameAnchor;

    /** The template value. */
    private Number templateValue;

    /**
     * A data value that will be formatted to determine the maximum size of
     * the indicator bounds.  If this is null, the indicator bounds can grow
     * as large as necessary to contain the actual data value.
     *
     * @since 1.0.14
     */
    private Number maxTemplateValue;

    /** The formatter. */
    private NumberFormat formatter;

    /** The font. */
    private Font font;

    /** The paint. */
    private transient Paint paint;

    /** The background paint. */
    private transient Paint backgroundPaint;

    /** The outline stroke. */
    private transient Stroke outlineStroke;

    /** The outline paint. */
    private transient Paint outlinePaint;

    /** The insets. */
    private RectangleInsets insets;

    /** The value anchor. */
    private RectangleAnchor valueAnchor;

    /** The text anchor for displaying the value. */
    private TextAnchor textAnchor;

    /**
     * Creates a new instance of {@code DialValueIndicator}.
     */
    public MilageCounter() {
        this(0);
    }

    /**
     * Creates a new instance of {@code DialValueIndicator}.
     *
     * @param datasetIndex  the dataset index.
     */
    public MilageCounter(int datasetIndex) {
        this.datasetIndex = datasetIndex;
        this.angle = -90.0;
        this.radius = 0.3;
        this.frameAnchor = RectangleAnchor.CENTER;
        this.templateValue = 100.0;
        this.maxTemplateValue = null;
        this.formatter = new DecimalFormat("000.0");
        this.font = new Font("Dialog", Font.BOLD, 14);
        this.paint = Color.BLACK;
        this.backgroundPaint = Color.WHITE;
        this.outlineStroke = new BasicStroke(1.0f);
        this.outlinePaint = Color.BLUE;
        this.insets = new RectangleInsets(4, 4, 4, 4);
        this.valueAnchor = RectangleAnchor.RIGHT;
        this.textAnchor = TextAnchor.CENTER_RIGHT;
    }

    /**
     * Returns the index of the dataset from which this indicator fetches its
     * current value.
     *
     * @return The dataset index.
     *
     * @see #setDatasetIndex(int)
     */
    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    /**
     * Sets the dataset index and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param index  the index.
     *
     * @see #getDatasetIndex()
     */
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the angle for the anchor point.  The angle is specified in
     * degrees using the same orientation as Java's {@code Arc2D} class.
     *
     * @return The angle (in degrees).
     *
     * @see #setAngle(double)
     */
    public double getAngle() {
        return this.angle;
    }

    /**
     * Sets the angle for the anchor point and sends a
     * {@link DialLayerChangeEvent} to all registered listeners.
     *
     * @param angle  the angle (in degrees).
     *
     * @see #getAngle()
     */
    public void setAngle(double angle) {
        this.angle = angle;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the radius.
     *
     * @return The radius.
     *
     * @see #setRadius(double)
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Sets the radius and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param radius  the radius.
     *
     * @see #getRadius()
     */
    public void setRadius(double radius) {
        this.radius = radius;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the frame anchor.
     *
     * @return The frame anchor.
     *
     * @see #setFrameAnchor(RectangleAnchor)
     */
    public RectangleAnchor getFrameAnchor() {
        return this.frameAnchor;
    }

    /**
     * Sets the frame anchor and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getFrameAnchor()
     */
    public void setFrameAnchor(RectangleAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.frameAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the template value.
     *
     * @return The template value (never {@code null}).
     *
     * @see #setTemplateValue(Number)
     */
    public Number getTemplateValue() {
        return this.templateValue;
    }

    /**
     * Sets the template value and sends a {@link DialLayerChangeEvent} to
     * all registered listeners.
     *
     * @param value  the value ({@code null} not permitted).
     *
     * @see #setTemplateValue(Number)
     */
    public void setTemplateValue(Number value) {
        Args.nullNotPermitted(value, "value");
        this.templateValue = value;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the template value for the maximum size of the indicator
     * bounds.
     *
     * @return The template value (possibly {@code null}).
     *
     * @since 1.0.14
     *
     * @see #setMaxTemplateValue(java.lang.Number)
     */
    public Number getMaxTemplateValue() {
        return this.maxTemplateValue;
    }

    /**
     * Sets the template value for the maximum size of the indicator bounds
     * and sends a {@link DialLayerChangeEvent} to all registered listeners.
     *
     * @param value  the value ({@code null} permitted).
     *
     * @since 1.0.14
     *
     * @see #getMaxTemplateValue()
     */
    public void setMaxTemplateValue(Number value) {
        this.maxTemplateValue = value;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the formatter used to format the value.
     *
     * @return The formatter (never {@code null}).
     *
     * @see #setNumberFormat(NumberFormat)
     */
    public NumberFormat getNumberFormat() {
        return this.formatter;
    }

    /**
     * Sets the formatter used to format the value and sends a
     * {@link DialLayerChangeEvent} to all registered listeners.
     *
     * @param formatter  the formatter ({@code null} not permitted).
     *
     * @see #getNumberFormat()
     */
    public void setNumberFormat(NumberFormat formatter) {
        Args.nullNotPermitted(formatter, "formatter");
        this.formatter = formatter;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the font.
     *
     * @return The font (never {@code null}).
     *
     * @see #getFont()
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font and sends a {@link DialLayerChangeEvent} to all registered
     * listeners.
     *
     * @param font  the font ({@code null} not permitted).
     */
    public void setFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.font = font;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPaint(Paint)
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the background paint.
     *
     * @return The background paint.
     *
     * @see #setBackgroundPaint(Paint)
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background paint and sends a {@link DialLayerChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getBackgroundPaint()
     */
    public void setBackgroundPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.backgroundPaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never {@code null}).
     *
     * @see #setOutlineStroke(Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke and sends a {@link DialLayerChangeEvent} to
     * all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getOutlineStroke()
     */
    public void setOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never {@code null}).
     *
     * @see #setOutlinePaint(Paint)
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getOutlinePaint()
     */
    public void setOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the insets.
     *
     * @return The insets (never {@code null}).
     *
     * @see #setInsets(RectangleInsets)
     */
    public RectangleInsets getInsets() {
        return this.insets;
    }

    /**
     * Sets the insets and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param insets  the insets ({@code null} not permitted).
     *
     * @see #getInsets()
     */
    public void setInsets(RectangleInsets insets) {
        Args.nullNotPermitted(insets, "insets");
        this.insets = insets;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the value anchor.
     *
     * @return The value anchor (never {@code null}).
     *
     * @see #setValueAnchor(RectangleAnchor)
     */
    public RectangleAnchor getValueAnchor() {
        return this.valueAnchor;
    }

    /**
     * Sets the value anchor and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getValueAnchor()
     */
    public void setValueAnchor(RectangleAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.valueAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns the text anchor.
     *
     * @return The text anchor (never {@code null}).
     *
     * @see #setTextAnchor(TextAnchor)
     */
    public TextAnchor getTextAnchor() {
        return this.textAnchor;
    }

    /**
     * Sets the text anchor and sends a {@link DialLayerChangeEvent} to all
     * registered listeners.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getTextAnchor()
     */
    public void setTextAnchor(TextAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.textAnchor = anchor;
        notifyListeners(new DialLayerChangeEvent(this));
    }

    /**
     * Returns {@code true} to indicate that this layer should be
     * clipped within the dial window.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isClippedToWindow() {
        return true;
    }


    public void drawCaramba(Graphics2D g2, DialPlot plot, Rectangle2D frame,
            Rectangle2D view) {

        // work out the anchor point
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius,
                this.radius);
        Arc2D arc = new Arc2D.Double(f, this.angle, 0.0, Arc2D.OPEN);
        Point2D pt = arc.getStartPoint();
        
        

        // the indicator bounds is calculated from the templateValue (which
        // determines the minimum size), the maxTemplateValue (which, if
        // specified, provides a maximum size) and the actual value
        FontMetrics fm = g2.getFontMetrics(this.font);
        double value = plot.getValue(this.datasetIndex);
        String valueStr = this.formatter.format(value);
        Rectangle2D valueBounds = TextUtils.getTextBounds(valueStr, g2, fm);

        // calculate the bounds of the template value
        String s = this.formatter.format(this.templateValue);
        Rectangle2D tb = TextUtils.getTextBounds(s, g2, fm);
        double minW = tb.getWidth();
        double minH = tb.getHeight();

        double maxW = Double.MAX_VALUE;
        double maxH = Double.MAX_VALUE;
        if (this.maxTemplateValue != null) {
            s = this.formatter.format(this.maxTemplateValue);
            tb = TextUtils.getTextBounds(s, g2, fm);
            maxW = Math.max(tb.getWidth(), minW);
            maxH = Math.max(tb.getHeight(), minH);
        }
        double w = fixToRange(valueBounds.getWidth(), minW, maxW);
        double h = fixToRange(valueBounds.getHeight(), minH, maxH);

        // align this rectangle to the frameAnchor
        Rectangle2D bounds = RectangleAnchor.createRectangle(new Size2D(w, h),
                pt.getX(), pt.getY(), this.frameAnchor);

        // add the insets
        Rectangle2D fb = this.insets.createOutsetRectangle(bounds);
        
        FontMetrics fmc = g2.getFontMetrics();
        Rectangle2D mb = fmc.getStringBounds("0", g2);

        g2.setColor(Color.black);
        
        g2.fillRect((int)fb.getX(), (int)fb.getY(), (int)mb.getWidth(), (int)mb.getHeight());
        
        g2.setColor(Color.white);
        
        g2.drawString("0",(int)(fb.getX()), (int)(fb.getY()+mb.getHeight()/2));
        g2.drawString("1",(int)(fb.getX()), (int)(fb.getY()+mb.getHeight()));
        g2.drawString("2",(int)(fb.getX()), (int)(fb.getY()));
        
        if(true) return;
        
        
        
        
        g2.setPaint(this.backgroundPaint);
       
        int border = 5;
        int cx=(int) fb.getX();
        int	cy=(int) fb.getY();
        
        
        int oldcx=cx-border;
        int oldcy=cy-border;
        
        char[] letters = valueStr.toCharArray();
        
        paint=g2.getPaint();
        
        g2.setColor(Color.black);
        
        RoundRectangle2D  bg=new RoundRectangle2D.Double(oldcx,oldcy,(int)((tb.getWidth()+border*2)*letters.length+letters.length-1+border*3)+2, (int)(tb.getHeight()+border*4), 5, 10);
        g2.fill(bg);
        
        
        RoundRectangle2D  bg2=new RoundRectangle2D.Double(oldcx+border,oldcy+border,(int)((tb.getWidth()+border*2)*letters.length+letters.length-1+border)+2, (int)(tb.getHeight()+border*2), 5, 10);
        g2.setColor(Color.white);
        g2.fill(bg2);
        g2.setColor(Color.black);
        g2.setFont(font);
        for(int i=0;i<letters.length-1;i++)
        {
        	Character c=letters[i];
        
    	g2.setPaint(paint);
    	
    	RoundRectangle2D  wb=new RoundRectangle2D.Double(cx,cy,(int)(tb.getWidth()+border*2), (int)(tb.getHeight()+border*2), 5, 10);
    
    	g2.drawString(c+"", cx,cy);
    	cx+=(int)(tb.getWidth()+border*2);
    	
    	cx+=2;
    	
    	
    	
    	
    }
        
        cx=(int) fb.getX();
        cy=(int) fb.getY();
        
       g2.setPaint(paint);
        
        for(int i=0;i<letters.length-1;i++)
        {Character c=letters[i];
        
        	Stroke stroke = g2.getStroke();
        	paint=g2.getPaint();
        	
        	RoundRectangle2D  wb=new RoundRectangle2D.Double(cx,cy,(int)(tb.getWidth()+border*2), (int)(tb.getHeight()+border*2), 5, 10);
        
        	//g2.fill(wb);
        	
        	g2.setStroke(new  BasicStroke(2));
        	g2.setPaint(this.outlinePaint);
        	
        	g2.draw(wb);
        	
        	
        	g2.setStroke(new BasicStroke(1));
        	g2.setColor(Color.gray);
        	cx+=(int)(tb.getWidth()+border*2);
        	g2.drawLine(cx+1, cy+border, cx+1, cy+(int)(tb.getHeight()+border));
        	cx+=2;
        	
        	g2.setStroke(stroke);
        	g2.setPaint(paint);
        	
        	
        }
        
        cx+=3;
        Character c=letters[letters.length-1];
        
    	Stroke stroke = g2.getStroke();
    	paint=g2.getPaint();
    	
    	RoundRectangle2D  wb=new RoundRectangle2D.Double(cx,cy,(int)(tb.getWidth()+border*2), (int)(tb.getHeight()+border*2), 5, 10);
    
    	//g2.fill(wb);
    	
    	g2.setStroke(new  BasicStroke(2));
    	g2.setPaint(this.outlinePaint);
    	
    	g2.draw(wb);
    	
    	
    	
    	g2.setStroke(stroke);
    	g2.setPaint(paint);
        
        
      /*
        // draw the background
        g2.setPaint(this.backgroundPaint);
        g2.fill(fb);

        // draw the border
        g2.setStroke(this.outlineStroke);
        g2.setPaint(this.outlinePaint);
        g2.draw(fb);
*/
        // now find the text anchor point
        Shape savedClip = g2.getClip();
        g2.clip(fb);

        Point2D pt2 = this.valueAnchor.getAnchorPoint(bounds);
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        
        //TextUtils.drawAlignedString(valueStr, g2, (float) pt2.getX(),
         //       (float) pt2.getY(), this.textAnchor);
        
        g2.setClip(savedClip);

    }
    /**
     * Draws the background to the specified graphics device.  If the dial
     * frame specifies a window, the clipping region will already have been
     * set to this window before this method is called.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param plot  the plot (ignored here).
     * @param frame  the dial frame (ignored here).
     * @param view  the view rectangle ({@code null} not permitted).
     */
    @Override
    public void draw(Graphics2D g2, DialPlot plot, Rectangle2D frame,
            Rectangle2D view) {

        // work out the anchor point
        Rectangle2D f = DialPlot.rectangleByRadius(frame, this.radius,
                this.radius);
        Arc2D arc = new Arc2D.Double(f, this.angle, 0.0, Arc2D.OPEN);
        Point2D pt = arc.getStartPoint();

        
        font=new Font("Courier New",12,12);
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, 0.5);
        font= font.deriveFont(attributes);
        
        
        // the indicator bounds is calculated from the templateValue (which
        // determines the minimum size), the maxTemplateValue (which, if
        // specified, provides a maximum size) and the actual value
        FontMetrics fm = g2.getFontMetrics(this.font);
        double value = plot.getValue(this.datasetIndex);
        String valueStr = this.formatter.format(value);
        Rectangle2D valueBounds = TextUtils.getTextBounds(valueStr, g2, fm);

        // calculate the bounds of the template value
        String s = this.formatter.format(this.templateValue);
        Rectangle2D tb = TextUtils.getTextBounds(s, g2, fm);
        double minW = tb.getWidth();
        double minH = tb.getHeight();

        double maxW = Double.MAX_VALUE;
        double maxH = Double.MAX_VALUE;
        if (this.maxTemplateValue != null) {
            s = this.formatter.format(this.maxTemplateValue);
            tb = TextUtils.getTextBounds(s, g2, fm);
            maxW = Math.max(tb.getWidth(), minW);
            maxH = Math.max(tb.getHeight(), minH);
        }
        double w = fixToRange(valueBounds.getWidth(), minW, maxW);
        double h = fixToRange(valueBounds.getHeight(), minH, maxH);

        // align this rectangle to the frameAnchor
        Rectangle2D bounds = RectangleAnchor.createRectangle(new Size2D(w, h),
                pt.getX(), pt.getY(), this.frameAnchor);

        // add the insets
        Rectangle2D fb = this.insets.createOutsetRectangle(bounds);

        // draw the background
        g2.setPaint(this.backgroundPaint);
        g2.fill(fb);

        // draw the border
        g2.setStroke(this.outlineStroke);
        g2.setPaint(this.outlinePaint);
        g2.draw(fb);

        // now find the text anchor point
        Shape savedClip = g2.getClip();
        g2.clip(fb);

        Point2D pt2 = this.valueAnchor.getAnchorPoint(bounds);
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        
        TextUtils.drawAlignedString(valueStr, g2, (float) pt2.getX(),
                (float) pt2.getY(), this.textAnchor);
        
        g2.setClip(savedClip);

    }
    /**
     * A utility method that adjusts a value, if necessary, to be within a 
     * specified range.
     * 
     * @param x  the value.
     * @param minX  the minimum value in the range.
     * @param maxX  the maximum value in the range.
     * 
     * @return The adjusted value.
     */
    private double fixToRange(double x, double minX, double maxX) {
        if (minX > maxX) {
            throw new IllegalArgumentException("Requires 'minX' <= 'maxX'.");
        }
        if (x < minX) {
            return minX;
        }
        else if (x > maxX) {
            return maxX;
        }
        else {
            return x;
        }
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MilageCounter)) {
            return false;
        }
        MilageCounter that = (MilageCounter) obj;
        if (this.datasetIndex != that.datasetIndex) {
            return false;
        }
        if (this.angle != that.angle) {
            return false;
        }
        if (this.radius != that.radius) {
            return false;
        }
        if (!this.frameAnchor.equals(that.frameAnchor)) {
            return false;
        }
        if (!this.templateValue.equals(that.templateValue)) {
            return false;
        }
        if (!ObjectUtils.equal(this.maxTemplateValue,
                that.maxTemplateValue)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!PaintUtils.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.insets.equals(that.insets)) {
            return false;
        }
        if (!this.valueAnchor.equals(that.valueAnchor)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int result = 193;
        result = 37 * result + HashUtils.hashCodeForPaint(this.paint);
        result = 37 * result + HashUtils.hashCodeForPaint(
                this.backgroundPaint);
        result = 37 * result + HashUtils.hashCodeForPaint(
                this.outlinePaint);
        result = 37 * result + this.outlineStroke.hashCode();
        return result;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return The clone.
     *
     * @throws CloneNotSupportedException if some attribute of this instance
     *     cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writePaint(this.paint, stream);
        SerialUtils.writePaint(this.backgroundPaint, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writeStroke(this.outlineStroke, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.paint = SerialUtils.readPaint(stream);
        this.backgroundPaint = SerialUtils.readPaint(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.outlineStroke = SerialUtils.readStroke(stream);
    }

}
