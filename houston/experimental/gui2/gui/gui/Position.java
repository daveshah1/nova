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
	
	//Vincenty formula for calculating distance
	//Converted from a Javascript implementation
	public double getDistanceTo(Position p) {
		  double a = 6378137, b = 6356752.314245,  f = 1/298.257223563;  // WGS-84 ellipsoid params
		  double L = Math.toRadians((p.longitude-longitude));
		  double U1 = Math.atan((1-f) * Math.tan(Math.toRadians(latitude)));
		  double U2 = Math.atan((1-f) * Math.tan(Math.toRadians(p.latitude)));
		  double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		  double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
		  double cosSqAlpha, sinSigma, cos2SigmaM, cosSigma, sigma;
		  double lambda = L, lambdaP, iterLimit = 100;
		  do {
		    double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
		    sinSigma = Math.sqrt((cosU2*sinLambda) * (cosU2*sinLambda) + 
		      (cosU1*sinU2-sinU1*cosU2*cosLambda) * (cosU1*sinU2-sinU1*cosU2*cosLambda));
		    if (sinSigma==0) return 0;  // co-incident points
		    cosSigma = sinU1*sinU2 + cosU1*cosU2*cosLambda;
		    sigma = Math.atan2(sinSigma, cosSigma);
		    double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
		    cosSqAlpha = 1 - sinAlpha*sinAlpha;
		    cos2SigmaM = cosSigma - 2*sinU1*sinU2/cosSqAlpha;
		    if ((cos2SigmaM) == Double.NaN) cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0 (§6)
		    double C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
		    lambdaP = lambda;
		    lambda = L + (1-C) * f * sinAlpha *
		      (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
		  } while (Math.abs(lambda-lambdaP) > 1e-12 && --iterLimit>0);

		  if (iterLimit==0) return -1;  // formula failed to converge

		  double uSq = cosSqAlpha * (a*a - b*b) / (b*b);
		  double A = 1 + uSq/16384*(4096+uSq*(-768+uSq*(320-175*uSq)));
		  double B = uSq/1024 * (256+uSq*(-128+uSq*(74-47*uSq)));
		  double deltaSigma = B*sinSigma*(cos2SigmaM+B/4*(cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)-
		    B/6*cos2SigmaM*(-3+4*sinSigma*sinSigma)*(-3+4*cos2SigmaM*cos2SigmaM)));
		  double s = b*A*(sigma-deltaSigma);
		  return s;
	}
	
	public double getBearingTo(Position p) {
		  double a = 6378137, b = 6356752.314245,  f = 1/298.257223563;  // WGS-84 ellipsoid params
		  double L = Math.toRadians((p.longitude-longitude));
		  double U1 = Math.atan((1-f) * Math.tan(Math.toRadians(latitude)));
		  double U2 = Math.atan((1-f) * Math.tan(Math.toRadians(p.latitude)));
		  double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
		  double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
		  double cosSqAlpha, sinSigma, cos2SigmaM, cosSigma, sigma, sinLambda, cosLambda;
		  double lambda = L, lambdaP, iterLimit = 100;
		  do {
		    sinLambda = Math.sin(lambda);
		    cosLambda = Math.cos(lambda);
		    sinSigma = Math.sqrt((cosU2*sinLambda) * (cosU2*sinLambda) + 
		      (cosU1*sinU2-sinU1*cosU2*cosLambda) * (cosU1*sinU2-sinU1*cosU2*cosLambda));
		    if (sinSigma==0) return 0;  // co-incident points
		    cosSigma = sinU1*sinU2 + cosU1*cosU2*cosLambda;
		    sigma = Math.atan2(sinSigma, cosSigma);
		    double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
		    cosSqAlpha = 1 - sinAlpha*sinAlpha;
		    cos2SigmaM = cosSigma - 2*sinU1*sinU2/cosSqAlpha;
		    if ((cos2SigmaM) == Double.NaN) cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0 (§6)
		    double C = f/16*cosSqAlpha*(4+f*(4-3*cosSqAlpha));
		    lambdaP = lambda;
		    lambda = L + (1-C) * f * sinAlpha *
		      (sigma + C*sinSigma*(cos2SigmaM+C*cosSigma*(-1+2*cos2SigmaM*cos2SigmaM)));
		  } while (Math.abs(lambda-lambdaP) > 1e-12 && --iterLimit>0);

		  if (iterLimit==0) return -1;  // formula failed to converge

		  double fwdAz = Math.atan2(cosU2*sinLambda,  cosU1*sinU2-sinU1*cosU2*cosLambda);
		  return Math.toDegrees(fwdAz);
	}
	
	public String toString() {
		String s;
		s = String.format("%.5f, %.5f",latitude,longitude);
		return s;
	};
}
