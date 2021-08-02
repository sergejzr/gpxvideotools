package de.zerr.core.gpx;

import java.util.Map.Entry;
import java.util.TreeMap;

public class GPXTool {
	static final TreeMap<Double, Integer> zoomlevels = new TreeMap<Double, Integer>();
	static final TreeMap<Integer, Double> resolutions = new TreeMap<Integer, Double>();
	static 
	{
		for(String str:("0,156.412;"
		+ "1,78206;"
		+ "2,39103;"
		+ "3,19551;"
		+ "4,9776;"
		+ "5,4888;"
		+ "6,2444;"
		+ "7,1222;"
		+ "8,611;"
		+ "9,305;"
		+ "10,153;"
		+ "11,76;"
		+ "12,38;"
		+ "13,19;"
		+ "14,10;"
		+ "15,5;"
		+ "16,2;"
		+ "17,1;"
		+ "18,0,6;"
		+ "19,0,3;").split(";")) 
		{
			String[] pair = str.split(",");
			if(pair.length==2) {
				zoomlevels.put(Double.parseDouble(pair[1].trim()),Integer.parseInt(pair[0].trim()));
				resolutions.put(Integer.parseInt(pair[0].trim()), Double.parseDouble(pair[1].trim()));
			}
			
		}
	}
	public static double getResolution(int zoomlevel) 
	{
		return resolutions.get(zoomlevel);
	}
	public static int getZoomlevel(double... fds) {
		double max=0;
		for(double d:fds) 
		{
			if(d>max)max=d;
		}
		Entry<Double, Integer> ret = zoomlevels.ceilingEntry(max);
		return ret.getValue();
	}
}
