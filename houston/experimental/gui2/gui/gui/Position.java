package gui;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/*
 * Provides a class for storing a latitude/longitude pair, as well
 * as distance calculations to another position
 */

public class Position {
	private double latitude, longitude;
	public Position(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	//A position class can be initialised with a JMapViewer Coordinate.
	public Position(Coordinate c) {
		super();
		this.latitude = c.getLat();
		this.longitude = c.getLon();
	}
	public Position(Position p) {
		super();
		this.latitude = p.getLat();
		this.longitude = p.getLon();
	}
	public void set(double lat, double lon ) {
		latitude = lat;
		longitude = lon;
	}
	public double getLat() {
		return latitude;
	}
	public double getLon() {
		return longitude;
	}
	
	public void addOffset(double deltaLat, double deltaLon) {
		latitude += deltaLat;
		longitude += deltaLon;
	}
	
	/*
	 * This is based on an algorithm that relies on the Earth being a perfect sphere.
	 * As it is not, there may be very minor inaccuracies of the order of +/-10m.
	 * This does not present a major issue in itself, however, as the distances are only
	 * used as an approximate indicator and not used for any pathfinding algorithms.
	 */
	public double getDistanceTo(Position p) {
		double dlon, dlat, a, c ,d;
		dlon = Math.toRadians(p.getLon() - longitude);
		dlat = Math.toRadians(p.getLat() - latitude);
		a = Math.pow((Math.sin(dlat/2)),2) + Math.cos(Math.toRadians(p.getLon())) * Math.cos(Math.toRadians(p.getLat())) * Math.pow(Math.sin(dlon/2),2);
		c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
		d = 6373 * c; //6373 could be adjusted to give more accurate results at the UK's latitude.
		return d;
	}
	public String toString() {
		String s;
		s = String.format("%.5f, %.5f",latitude,longitude);
		return s;
	};
}
