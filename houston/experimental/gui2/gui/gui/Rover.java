package gui;

import java.util.Vector;

//Provides a base class that handles the calling RoverUpdateListeners

public class Rover {
	Vector<RoverUpdateListener> listeners = new Vector<RoverUpdateListener>();
	public Position currentPosition, targetPosition;
	public boolean atTargetPosition;
	protected double temperature, pressure;
	
	public Rover() {
		currentPosition = new Position(0,0);
		targetPosition = new Position(0,0);
		atTargetPosition = true;
	}
	public void attachListener(RoverUpdateListener l) {
		listeners.add(l);
	};
	public void removeListener(RoverUpdateListener l) {
		listeners.remove(l);
	};
	
	
	protected void firePositionUpdate() {
		for(RoverUpdateListener l : listeners) {
			l.positionUpdated(currentPosition, targetPosition, atTargetPosition, this);
		}
	}
	
	protected void fireDataUpdate() {
		for(RoverUpdateListener l : listeners) {
			l.dataUpdated(temperature,pressure, this);
		}
	}
	
	protected void forwardMessage(String message) {
		for(RoverUpdateListener l : listeners) {
			l.messageRecieved(message, this);
		}
	};
	
	protected void throwError(String message) {
		for(RoverUpdateListener l : listeners) {
			l.errorThrown(message, this);
		}
	}
}
