package de.zerr.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import com.coremedia.iso.IsoFile;

import de.zerr.ContinuousRoute;
import de.zerr.DiscreteRoute;
import de.zerr.GPXRouteBuilder;
import de.zerr.core.animator.GPXIteratorA;
import de.zerr.core.animator.GPXRouteBuilderX;
import de.zerr.core.animator.GPXTargetValue;
import de.zerr.core.animator.Route;
import de.zerr.core.animator.RoutePoint;
import de.zerr.core.videowriter.InterpolationType;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.GPX.Reader.Mode;

public class AnimateControllerA implements IAnimateController {

	//private GPXIteratorA it;
	private FFmpegFrameGrabber frameGrabber;
	private ZonedDateTime videoStartTime;
	private SpeedometerView sv;
	private double framerate;

	private ZonedDateTime curTime;
    
	private ZonedDateTime resettime;
	private GPXRouteBuilder rb;
	private ContinuousRoute route;
	
	public AnimateControllerA(String videoname, ZonedDateTime videoStartTime, ZonedDateTime videoEndTime, String gpxfile) {

		
		
		try {

			// Animator anim = new Animator("/home/szerr/Downloads/RK_gpx
			// _2021-05-30_1045.gpx");
			frameGrabber = new FFmpegFrameGrabber(videoname);
			frameGrabber.start();
			framerate = frameGrabber.getFrameRate();
			// sv.videoLoaded(frameGrabber.getMetadata());
			// sv.videoLoaded(Integer.parseInt(frameGrabber.getMetadata().get("length")));
			IsoFile isoFile = new IsoFile(videoname);
			long lengthInSeconds = isoFile.getMovieBox().getMovieHeaderBox().getDuration()
					/ isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
			isoFile.close();
			// int startseconds=1047;
			// int startseconds=1400;
			// int startseconds=1400;
			// ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:49:50Z"));

			this.videoStartTime = videoStartTime;
			resettime=videoStartTime;
			curTime = videoStartTime;
			//it = new GPXIteratorA(gpxfile, 0, 0);
			
			rb=new GPXRouteBuilder(gpxfile, InterpolationType.LINEAR);
			
			List<WayPoint> mt = rb.fillGaps(rb.getMytrack());
			
			 DiscreteRoute r = rb.makeRoute(mt);
			
			route=ContinuousRoute.fromDiscrete(r, InterpolationType.SPLINE, GPXTargetValue.ALL_DEPENDENTSET);
			route.setBounds(videoStartTime,videoEndTime);
			//List<WayPoint> enhanced = rb.addSpeed(mt);
			
			sv = new SpeedometerView(route);
			sv.setVisible(true);
			sv.setController(this);
			
			sv.videoLoaded((int) lengthInSeconds);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void seek(int offset) throws org.bytedeco.javacv.FFmpegFrameGrabber.Exception {
		curTime = videoStartTime.plusSeconds(offset);
		frameGrabber.setFrameNumber((int) (framerate * offset));
	}

	public void run() throws org.bytedeco.javacv.FrameGrabber.Exception {
		ICockpit speedometer = sv.getSpeedometer();
		Frame f = null;
		Java2DFrameConverter c = new Java2DFrameConverter();

		BufferedImage bi = null;
		long delay = (long) (1000. / framerate);
		double ar = 600. / frameGrabber.getImageHeight();

		frameGrabber.setImageHeight(600);
		frameGrabber.setImageWidth((int) (frameGrabber.getImageWidth() * ar));

		try {
			f = frameGrabber.grabImage();
		} catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	//route.print(curTime,curTime.plusHours(2), 1000,GPXTargetValue.TIME,GPXTargetValue.DISTANCE);

		while (f != null) {
			Date d = new Date();
			
			//WayPoint wp = it.next(curTime);
			//double speed = it.getSpeed(wp);
			RoutePoint wp = route.at(curTime);
			//if(speed<0.5)speed=0;
			speedometer.setTime(curTime);
			speedometer.setSpeed(GPXRouteBuilderX.kmph(wp.getSpeed()));
			speedometer.setElevation(Math.round(wp.getElevation()));
			speedometer.setKm(Math.round(wp.getDistance()/10.)/100.);
			speedometer.setAcceleration(wp.getAcceleration());
			speedometer.setBearing(wp.getHeading());
			speedometer.setSlope(wp.getSlope());

			
			
			if (bi != null) {
				// bi=resize(bi, 300, 300);
				sv.setVideoFrame(bi);
			}
			sv.repaint();
			try {
				f = frameGrabber.grabImage();
				bi = c.convert(f);
				
			} catch (Exception e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				if(f!=null)
					f.close();
			}

			Date d1 = new Date();
			long diff = d1.getTime() - d.getTime();
			if (diff > delay) {
				diff = delay;
			} else {
				diff = delay - diff;
			}

			try {
				Thread.sleep((long) ((diff)));
				curTime = curTime.plusNanos(delay * 1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Thread.sleep((long)(1000/framerate));

		}
		frameGrabber.stop();
		frameGrabber.close();
	}

	public void fired() {
		try {
			seek(200);
		} catch (org.bytedeco.javacv.FFmpegFrameGrabber.Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
