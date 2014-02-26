package gui;

import gui.LandingModule.DeploymentStatus;

public class AntennaManager implements RoverUpdateListener,
		LandingModuleListener {
	private boolean havePositionFix = false;
	private Position currentTrackingPosition = null;
	private boolean trackingRover = false;
	private double currentTrackingAltitude = 0;
	private SettingsStore settings;
	public AntennaManager() {
		settings = new SettingsStore();
	}

	@Override
	public void positionUpdated(Position newPosition, LandingModule m) {
		if(!trackingRover) {
			if(m.gpsAvailable) {
				currentTrackingPosition = newPosition;
				currentTrackingAltitude = (int) m.gpsAltitude;
				havePositionFix = true;
			}
		}

	}

	@Override
	public void dataUpdated(TPData currentData, LandingModule m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void statusUpdated(DeploymentStatus deployed, boolean connected,
			LandingModule m) {
		if(deployed == DeploymentStatus.DEPLOYED ) {
			trackingRover = true;
		}

	}

	@Override
	public void positionUpdated(Position newPosition, Position targetPosition,
			boolean atTargetPosition, Rover r) {
		if(trackingRover) {
			if(newPosition.getLat() != 0) {
				currentTrackingPosition = newPosition;
				havePositionFix = true;
			}
		}

	}

	@Override
	public void dataUpdated(TPData data, Rover r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageRecieved(String message, Rover r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void errorThrown(String message, Rover r) {
		// TODO Auto-generated method stub

	}
	
	public void updateAntennaPosition(BaseStationCommunications b) {
		if(havePositionFix) {
			Position home = new Position(settings.getDouble("home.lat"),
					settings.getDouble("home.long"));
			double homeAlt = settings.getDouble("home.alt");
			double horizontalDistance = home.getDistanceTo(currentTrackingPosition);
			double horizontalAngle = home.getBearingTo(currentTrackingPosition);
			double verticalAngle = Math.toDegrees(Math.atan((currentTrackingAltitude - homeAlt) / horizontalDistance));
			b.setAntennaTilt((int)verticalAngle, (int)horizontalAngle);
		}
	}
}
