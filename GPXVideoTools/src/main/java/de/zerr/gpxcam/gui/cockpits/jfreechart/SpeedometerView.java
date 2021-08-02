package de.zerr.gpxcam.gui.cockpits.jfreechart;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;

import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.gpxcam.gui.AbstractCockpit;
import de.zerr.gpxcam.gui.ICockpit;
import de.zerr.gui.IAnimateController;

public class SpeedometerView extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2223627618972535213L;

JLabel video=new JLabel("video");
JButton seek=new JButton("Seek");
private IAnimateController animateController;
private JSlider slider;
	public SpeedometerView(AbstractCockpit  speedometer) {
		this.setBounds(100, 100, 1000, 500);
		/*
		SpeedometerImage si=new SpeedometerImage();
		si.setAttitude(253);
		si.setDistance(23345);
		si.setSpeed(32.6);
		this.getContentPane().add(new JLabel(new ImageIcon(si.getSpeedometerImage())));
		*/
		//speedometer=new ElemeterPanel(100,50);

		//speedometer.setSpeed(0);
		//seek.setBounds(100, 100, 1000, 500);
	
	this.setLayout(new BorderLayout());
	
		 slider=new JSlider();
		 slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int value = slider.getValue();
				try {
					animateController.seek(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	this.add(slider, BorderLayout.NORTH);
	this.add(speedometer, BorderLayout.SOUTH);
	this.add(video, BorderLayout.CENTER);
	//this.pack();
	this.repaint();
		seek.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				animateController.fired();
				
			}
		});
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        System.out.println("JFrame Closed!");
		    }
		});
	}

	

	public void setVideoFrame(BufferedImage bi) {
		video.setIcon(new  ImageIcon(bi));
		
	}

	public void setController(IAnimateController animateController) {
		this.animateController=animateController;
		
	}

public void videoLoaded(int maxSeconds)
{
	slider.setMaximum(maxSeconds);
}

	



}
