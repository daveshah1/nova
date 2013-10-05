package gui;

public class VirtualRover {
	public VirtualRover(double currentLat, double currentLon) {
		super();
		this.currentLat = currentLat;
		this.currentLon = currentLon;
		this.targetLat = this.currentLat;
		this.targetLon = this.currentLon;
	}
	double currentLat, currentLon, targetLat, targetLon;
	public void updatePosition() {
		double diff;
		if(currentLon > targetLon) {
			diff = Math.min(currentLon - targetLon,0.000005);
			currentLon -= diff;
		}
		if(currentLon < targetLon) {
			diff = Math.min(targetLon - currentLon,0.000005);
			currentLon += diff;
		}
		if(currentLat > targetLat) {
			diff = Math.min(currentLat - targetLat,0.000005);
			currentLat -= diff;
		}
		if(currentLat < targetLat) {
			diff = Math.min(targetLat - currentLat,0.000005);
			currentLat += diff;
		}
	}
}
