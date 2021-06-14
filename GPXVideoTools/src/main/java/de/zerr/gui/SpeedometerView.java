package de.zerr.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;

import de.zerr.ContinuousRoute;

public class SpeedometerView extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2223627618972535213L;
	private JFreeChartCockpit speedometer;
JLabel video=new JLabel("video");
JButton seek=new JButton("Seek");
private IAnimateController animateController;
private JSlider slider;
	public SpeedometerView(ContinuousRoute route) {
		this.setBounds(100, 100, 700, 700);
		/*
		SpeedometerImage si=new SpeedometerImage();
		si.setAttitude(253);
		si.setDistance(23345);
		si.setSpeed(32.6);
		this.getContentPane().add(new JLabel(new ImageIcon(si.getSpeedometerImage())));
		*/
		//speedometer=new ElemeterPanel(100,50);
		speedometer=new JFreeChartCockpit(route);
		speedometer.setSpeed(0);
		seek.setBounds(100, 100, 100, 100);
		speedometer.setMinimumSize(new Dimension(300,300));
		speedometer.setPreferredSize(new Dimension(300,500));
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

	public ICockpit getSpeedometer() {
		return speedometer;
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
