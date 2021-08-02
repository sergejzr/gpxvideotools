package de.zerr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Properties;

import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;

import de.zerr.core.animator.AnimateController;
import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.gpxcam.gui.AbstractCockpit;
import de.zerr.gpxcam.gui.cockpits.digital.BorderedCockpit;
import de.zerr.gpxcam.gui.cockpits.exclusive.ExclusiveCockpit;
import de.zerr.gpxcam.gui.cockpits.jfreechart.JFreeChartCockpit;

public class SpeedometerTestApp {

	public static void main(String[] args) {
		ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:49:50Z"));
		ZonedDateTime videoEndTime = videoStartTime.plusMinutes(50).plusSeconds(29);

		Properties prop = new Properties();
		Properties creds = new Properties();
		try (InputStream input = SpeedometerTestApp.class.getClassLoader().getResourceAsStream("config.properties")// new
																													// FileInputStream("path/to/config.properties")

		) {

			// load a properties file
			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try (InputStream input = new FileInputStream(prop.get("credentialspath").toString())

		) {

			// load a properties file
			creds.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
prop.setProperty("GPXAnimatedVideoPath", "/home/szerr/Videos/GPX-Animation.mp4");
prop.setProperty("mapquestlink", creds.getProperty("maplink"));

		AnimateController ac = new AnimateController("/home/szerr/Videos/bikeSCHNEKLK.mp4", videoStartTime,
				videoEndTime, "/home/szerr/Downloads/RK_gpx _2021-05-30_1045.gpx", prop) {
			@Override
			public AbstractCockpit generateCockpit(ContinuousRoute route, Properties props) throws Exception {
				// return new JFreeChartCockpit(route);
				return new ExclusiveCockpit(route, videoStartTime, videoEndTime,props);
			}

		};

		try {
			ac.run();
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
