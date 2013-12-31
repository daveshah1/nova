package gui;

public class VirtualRover extends Rover {
	public VirtualRover(Position p) {
		super();
		this.currentPosition = p;
		this.targetPosition = new Position(p);
		
	}
	public void updatePosition() {
		double diff;
		double currentLon, currentLat, targetLon, targetLat;
		if(!atTargetPosition) {
			currentLon = currentPosition.getLon();
			currentLat = currentPosition.getLat();
			targetLon = targetPosition.getLon();
			targetLat = targetPosition.getLat();
			if(currentLon > targetLon) {
				diff = Math.min(currentLon - targetLon,0.000015);
				currentLon -= diff;
			}
			if(currentLon < targetLon) {
				diff = Math.min(targetLon - currentLon,0.000015);
				currentLon += diff;
			}
			if(currentLat > targetLat) {
				diff = Math.min(currentLat - targetLat,0.000015);
				currentLat -= diff;
			}
			if(currentLat < targetLat) {
				diff = Math.min(targetLat - currentLat,0.000015);
				currentLat += diff;
			}
			currentPosition.set(currentLat,currentLon);
			if(currentPosition.getDistanceTo(targetPosition) < 0.0002) {
				atTargetPosition = true;
			}
		};
		firePositionUpdate();

	}
	
}
