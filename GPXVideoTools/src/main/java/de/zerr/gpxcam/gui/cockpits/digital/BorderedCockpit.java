package de.zerr.gpxcam.gui.cockpits.digital;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.ZonedDateTime;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import de.zerr.core.gpx.route.ContinuousRoute;
import de.zerr.core.gpx.route.RoutePoint;
import de.zerr.gpxcam.gui.AbstractCockpit;

public class BorderedCockpit extends AbstractCockpit {

	JLabel speedlabel = new JLabel("Speed");
	JLabel mileage = new JLabel("Mileage");
	JLabel compass = new JLabel("Compass");
	JLabel elevation = new JLabel("Elevation");
	JLabel acceleration = new JLabel("Acceleration");
	JLabel slope = new JLabel("Slope");
	private JLabel map;



	public BorderedCockpit(ContinuousRoute route, ZonedDateTime videoStartTime, ZonedDateTime videoEndTime) {
		super(route, videoStartTime, videoEndTime);
		
		Dimension psize = new Dimension(100,100);
		speedlabel.setBorder(new TitledBorder("Speed"));
		mileage.setBorder(new TitledBorder("Mileage"));
		compass.setBorder(new TitledBorder("Compass"));
		elevation.setBorder(new TitledBorder("Elevation"));
		acceleration.setBorder(new TitledBorder("Acceleration"));
		slope.setBorder(new TitledBorder("Slope"));
		
		speedlabel.setPreferredSize(psize);
		//mileage.setPreferredSize(psize);
		compass.setPreferredSize(psize);
		elevation.setPreferredSize(psize);
		acceleration.setPreferredSize(psize);
		slope.setPreferredSize(psize);
		
		JPanel  instrumentPanel=new JPanel();
		instrumentPanel.setLayout(new BorderLayout());
		this.setLayout(new BorderLayout());
		JPanel speedometer = new JPanel();
		speedometer.setLayout(new BorderLayout());
		speedometer.add(speedlabel,BorderLayout.CENTER);
		speedometer.add(mileage,BorderLayout.SOUTH);
		instrumentPanel.add(speedometer, BorderLayout.CENTER);
		
		JPanel west = new JPanel();
		west.setLayout(new GridLayout(2,1));
		west.add(compass);
		west.add(elevation);
		instrumentPanel.add(west, BorderLayout.WEST);
		
		JPanel east = new JPanel();
		east.setLayout(new GridLayout(2,1));
		east.add(acceleration);
		east.add(slope);
		instrumentPanel.add(east, BorderLayout.EAST);
		
		
		
		
		JPanel graphs=new  JPanel();
		graphs.setLayout(new  GridLayout(2,1));
		add(graphs, BorderLayout.EAST);
		 map=new JLabel();
		add(map, BorderLayout.WEST);
		
		add(instrumentPanel, BorderLayout.CENTER);
		setPreferredSize(new Dimension(300,100));
		
		
	}

	private void createMap() 
	{
		
		iterateBy(null);
	}
	
	@Override
	public void at(ZonedDateTime time) {

		RoutePoint rp = getAt(time);

		speedlabel.setText(Math.round(rp.getSpeed()*(60*60)/1000) + " kmH");
		mileage.setText(Math.round(rp.getDistance()/10)/100. + " km");
		compass.setText(formatBearing(rp.getHeading()));
		elevation.setText(Math.round(rp.getElevation()*10)/10. + "m");
		acceleration.setText(Math.round(rp.getAcceleration()*100)/100. + " m/s²");
		slope.setText(Math.round(rp.getSlope()*10000)/100. + "%");
		//map.setIcon(rp.getMap());
		

	}
	 private String formatBearing(double bearing) {
		    if (bearing < 0 && bearing > -180) {
		      // Normalize to [0,360]
		      bearing = 360.0 + bearing;
		    }
		    if (bearing > 360 || bearing < -180) {
		      return "Unknown";
		    }

		    String directions[] = {
		      "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
		      "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW",
		      "N"};
		    String cardinal = directions[(int) Math.floor(((bearing + 11.25) % 360) / 22.5)];
		    return cardinal;// + " (" + formatBearing.format(bearing) + " deg)";
		  }
}
