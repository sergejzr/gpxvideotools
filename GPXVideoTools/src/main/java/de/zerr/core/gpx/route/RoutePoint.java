package de.zerr.core.gpx.route;

import java.time.ZonedDateTime;

import io.jenetics.jpx.WayPoint;



public class RoutePoint {
double  lattitude,lontgitude,speed,elevation,distance,slope,heading,acceleration;
ZonedDateTime time;


public double getLattitude() {
	return lattitude;
}
public RoutePoint setLattitude(double lattitude) {
	this.lattitude = lattitude;
	return this;
}
public double getLontgitude() {
	return lontgitude;
}
public RoutePoint setLontgitude(double lontgitude) {
	this.lontgitude = lontgitude;
	return this;
}
public double getSpeed() {
	return speed;
}
public RoutePoint setSpeed(double speed) {
	this.speed = speed;
	return this;
}
public double getElevation() {
	return elevation;
}
public RoutePoint setElevation(double elevation) {
	this.elevation = elevation;
	return this;
}
public double getDistance() {
	return distance;
}
public RoutePoint setDistance(double distance) {
	this.distance = distance;
	return this;
}
public double getSlope() {
	return slope;
}
public RoutePoint setSlope(double slope) {
	this.slope = slope;
	return this;
}
public double getHeading() {
	return heading;
}
public RoutePoint setHeading(double heading) {
	this.heading = heading;
	return this;
}
public double getAcceleration() {
	return acceleration;
}
public RoutePoint setAcceleration(double acceleration) {
	this.acceleration = acceleration;
	return this;
}
public ZonedDateTime getTime() {
	return time;
}
public RoutePoint setTime(ZonedDateTime time) {
	this.time = time;
	return this;
}

public static RoutePoint from(WayPoint wp) {
	
	RoutePoint ret=new  RoutePoint();
	ret.lattitude=wp.getLatitude().doubleValue();
	ret.lontgitude=wp.getLongitude().doubleValue();
	ret.time=wp.getTime().get();
	ret.speed=wp.getSpeed().get().doubleValue();
	ret.elevation=wp.getElevation().get().doubleValue();
	return ret;
}




}
