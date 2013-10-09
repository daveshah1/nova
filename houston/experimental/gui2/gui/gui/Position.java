package gui;

public class Position {
	private double latitude, longitude;
	public Position(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
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
		dlon = p.getLon() - longitude;
		dlat = p.getLat() - latitude;
		a = Math.pow(Math.pow((Math.sin(dlat/2)),2) + Math.cos(p.getLon()) * Math.cos(p.getLat()) * (Math.sin(dlon/2)),2);
		c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
		d = 6373 * c;
		return d;
	}
}
