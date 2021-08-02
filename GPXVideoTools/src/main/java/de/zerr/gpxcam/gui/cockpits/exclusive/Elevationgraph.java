package de.zerr.gpxcam.gui.cockpits.exclusive;

import java.awt.Color;

import javax.swing.JPanel;

public class Elevationgraph extends StatGraph {

	public Elevationgraph(Color cline, Color cfill, double xtickres, double ytickres) {
		super(cline, cfill,xtickres,ytickres);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getYEinheit() {
		// TODO Auto-generated method stub
		return "m";
	}

	@Override
	public Double getFactor() {
		// TODO Auto-generated method stub
		return 1.;
	}

}
