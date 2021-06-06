package de.zerr.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import com.coremedia.iso.IsoFile;

import de.zerr.core.animator.GPXIterator;
import de.zerr.core.animator.GPXRouteBuilder;
import io.jenetics.jpx.WayPoint;

public class AnimateController {
	
	private GPXIterator it;
	private FFmpegFrameGrabber frameGrabber;
	private ZonedDateTime videoStartTime;
	private SpeedometerView sv;
	private double framerate;

	public AnimateController(String videoname, ZonedDateTime videoStartTime, String gpxfile) {

		sv = new SpeedometerView();
		sv.setVisible(true);
		sv.setController(this);

		try {
	
			// Animator anim = new Animator("/home/szerr/Downloads/RK_gpx
			// _2021-05-30_1045.gpx");
			 frameGrabber = new FFmpegFrameGrabber(videoname);
			 frameGrabber.start();
			 framerate = frameGrabber.getFrameRate();
			// sv.videoLoaded(frameGrabber.getMetadata());
			// sv.videoLoaded(Integer.parseInt(frameGrabber.getMetadata().get("length")));
			 IsoFile isoFile = new IsoFile(videoname);
			 long lengthInSeconds =
		                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
		                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
			 
			 
			// int startseconds=1047;
			// int startseconds=1400;
			// int startseconds=1400;
			 //ZonedDateTime videoStartTime = (ZonedDateTime.parse("2021-05-30T08:49:50Z"));
			
			 
			 this.videoStartTime=videoStartTime;
			 
			 it = new GPXIterator(gpxfile, framerate, videoStartTime);
			sv.videoLoaded((int)lengthInSeconds);
			
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void seek(int offset) throws org.bytedeco.javacv.FFmpegFrameGrabber.Exception 
	{
		ZonedDateTime newStartTime = videoStartTime.plusSeconds(offset);
		it.seek(newStartTime);
		frameGrabber.setFrameNumber((int)(framerate*offset));
	}
	public void run(int startseconds) throws org.bytedeco.javacv.FrameGrabber.Exception
	{
		
	
		 ZonedDateTime newStartTime = videoStartTime.plusSeconds(startseconds);
		
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
	        it.seek(newStartTime);
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
			try {
				Thread.sleep((long)(delay-(diff)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Thread.sleep((long)(1000/framerate));
			  
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
