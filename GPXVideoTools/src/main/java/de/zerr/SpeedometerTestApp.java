package de.zerr;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import de.zerr.core.animator.GPXIterator;
import de.zerr.core.animator.GPXRouteBuilder;
import de.zerr.gui.AnimateController;
import de.zerr.gui.SpeedometerPanel;
import de.zerr.gui.SpeedometerView;
import io.jenetics.jpx.WayPoint;

public class SpeedometerTestApp {

	public static void main(String[] args) {
		ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:49:50Z"));
		AnimateController ac=new AnimateController("/home/szerr/Videos/bikeSCHNEKLK.mp4", videoStartTime, "/home/szerr/Downloads/RK_gpx _2021-05-30_1045.gpx");
	try {
		ac.run(1000);
	} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}

	private static void test1() {

		SpeedometerView sv = new SpeedometerView();
		sv.setVisible(true);

		try {
	
			// Animator anim = new Animator("/home/szerr/Downloads/RK_gpx
			// _2021-05-30_1045.gpx");
			FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber("/home/szerr/Videos/bikeSCHNEKLK.mp4");
			 frameGrabber.start();
			 double framerate = frameGrabber.getFrameRate();
			 
			// int startseconds=1047;
			// int startseconds=1400;
			 int startseconds=1400;
			 //ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:49:50Z"));
			 ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:50:00Z"));
			 
			 videoStartTime=videoStartTime.plusSeconds(startseconds);
			 
			GPXIterator it = new GPXIterator("/home/szerr/Downloads/RK_gpx _2021-05-30_1045.gpx", framerate, videoStartTime);
			System.out.println("Framerate: "+frameGrabber.getFrameRate());
			
			
			SpeedometerPanel speedometer = sv.getSpeedometer();
			WayPoint wp = null;
			 
		       
		        Frame f;
		        Java2DFrameConverter c = new Java2DFrameConverter();
		      
		        BufferedImage  bi=null;
		        long delay=(long)(1000./framerate);
		        double ar = 600./frameGrabber.getImageHeight();
		        frameGrabber.setImageHeight(600);
		        frameGrabber.setImageWidth((int)(frameGrabber.getImageWidth()*ar));
		        
		        frameGrabber.setFrameNumber((int)(framerate*startseconds));
			while ((wp = it.next()) != null) {
				Date d=new Date();
				speedometer.setSpeed(wp.getTime().get(), GPXRouteBuilder.kmph(wp.getSpeed().get().doubleValue()));
				  try {
			            f = frameGrabber.grabImage();
			              bi = c.convert(f);
				  } catch (Exception e) {
			            // TODO Auto-generated catch block
			            e.printStackTrace();
			        }
			           
			       
				if(bi!=null) {
					//bi=resize(bi, 300, 300);
				sv.setVideoFrame(bi);
				}
				sv.repaint();
				Date d1=new Date();
				long diff = d1.getTime()-d.getTime();
				if(diff>delay)diff=delay;
				Thread.sleep((long)(delay-(diff)));
				//Thread.sleep((long)(1000/framerate));
				  
			}
			frameGrabber.stop();
			frameGrabber.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
