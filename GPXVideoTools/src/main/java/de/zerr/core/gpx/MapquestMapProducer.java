package de.zerr.core.gpx;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;

public class MapquestMapProducer implements MapProducer {

	private String maskedURL;

	public MapquestMapProducer(String maskedURL) {
		this.maskedURL = maskedURL;
	}

	@Override
	public BufferedImage produce(Rectangle2D bounds, int w, int h, int zoomlevel) {

		

		String filledurl = maskedURL.replaceFirst("\\{w\\}", w+"").replaceFirst("\\{h\\}", h+"")
				.replaceFirst("\\{z\\}", "" + zoomlevel).replaceFirst("\\{lat\\}", bounds.getX() + "")
				.replaceFirst("\\{lon\\}", bounds.getY() + "");
		BufferedImage ret = null;

		String hashid = DigestUtils.md5Hex(filledurl).toUpperCase();

		File tmpimg = new File("tmp/maptiles/" + hashid + ".png");

		if (tmpimg.exists()) {
			try {
				ret = ImageIO.read(tmpimg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {

			try {
				ret = ImageIO.read(new URL(filledurl));
				ImageIO.write(ret, "PNG", tmpimg);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

}
