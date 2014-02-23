package gui;

import gui.LandingModule.DeploymentStatus;

public class AntennaManager implements RoverUpdateListener,
		LandingModuleListener {
	boolean havePositionFix = false;
	Position currentTrackingPosition = null;
	boolean trackingRover = false;
	public AntennaManager() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void positionUpdated(Position newPosition, LandingModule m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataUpdated(TPData currentData, LandingModule m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void statusUpdated(DeploymentStatus deployed, boolean connected,
			LandingModule m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void positionUpdated(Position newPosition, Position targetPosition,
			boolean atTargetPosition, Rover r) {
		// TODO Auto-generated method stub

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

}
