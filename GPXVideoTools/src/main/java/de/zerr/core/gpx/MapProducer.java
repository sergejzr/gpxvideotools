package de.zerr.core.gpx;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public interface MapProducer {
public BufferedImage produce(Rectangle2D bounds,int w, int h, int zoomlevel);

}
