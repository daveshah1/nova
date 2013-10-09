package gui;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class Position {
	private double latitude, longitude;
	public Position(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}
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
	public double getDistanceTo(Position p) {
		double dlon, dlat, a, c ,d;
		dlon = Math.toRadians(p.getLon() - longitude);
		dlat = Math.toRadians(p.getLat() - latitude);
		a =Math.pow((Math.sin(dlat/2)),2) + Math.cos(Math.toRadians(p.getLon())) * Math.cos(Math.toRadians(p.getLat())) * Math.pow(Math.sin(dlon/2),2);
		c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
		d = 6373 * c;
		return d;
	}
	public String toString() {
		String s;
		s = String.format("%.5f, %.5f",latitude,longitude);
		return s;
	};
}
