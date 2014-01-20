package gui;

import java.util.Vector;
public class LandingModule {
	public enum DeploymentStatus {
		LOADED, DEPLOYED, UNKNOWN
	}

	Vector<LandingModuleListener> listeners = new Vector<LandingModuleListener>();
	public Position currentPosition;
	protected TPData currentData;
	public DeploymentStatus roverDeployed;
	public boolean connected;
	
	public void attachListener(LandingModuleListener l) {
		listeners.add(l);
	};

	public void removeListener(LandingModuleListener l) {
		listeners.remove(l);
	};

	public void deployRover() {

	}

	public void startCommunications(String serialPort) {

	}

	public void stopCommunications() {

	}

	protected void firePositionUpdate() {
		for (LandingModuleListener l : listeners) {
			l.positionUpdated(currentPosition, this);
		}
	}

	protected void fireStatusUpdate() {
		for (LandingModuleListener l : listeners) {
			l.statusUpdated(roverDeployed, connected, this);
		}
	}

	protected void fireDataUpdate() {
		for (LandingModuleListener l : listeners) {
			l.dataUpdated(currentData, this);
		}
	}
}
