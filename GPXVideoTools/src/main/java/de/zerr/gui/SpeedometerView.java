package de.zerr.gui;

import java.util.Date;

import javax.swing.JFrame;

public class SpeedometerView extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2223627618972535213L;

	public SpeedometerView() {
		this.setBounds(100, 100, 600, 600);
		/*
		SpeedometerImage si=new SpeedometerImage();
		si.setAttitude(253);
		si.setDistance(23345);
		si.setSpeed(32.6);
		this.getContentPane().add(new JLabel(new ImageIcon(si.getSpeedometerImage())));
		*/
		Speedometer sp=new Speedometer();
		sp.setSpeed(new Date(), 4);
		this.add(sp);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        System.out.println("JFrame Closed!");
		    }
		});
	}

}
