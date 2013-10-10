package gui;

import java.util.Vector;

//Provides a base class that handles the calling RoverUpdateListeners

public class Rover {
	Vector<RoverUpdateListener> listeners = new Vector<RoverUpdateListener>();
	protected Position currentPosition, targetPosition;
	protected double temperature, pressure;
	
	public Rover() {
		currentPosition = new Position(0,0);
		targetPosition = new Position(0,0);
	}
	public void attachListener(RoverUpdateListener l) {
		listeners.add(l);
	};
	public void removeListener(RoverUpdateListener l) {
		listeners.remove(l);
	};
	
	
	protected void firePositionUpdate() {
		for(RoverUpdateListener l : listeners) {
			l.positionUpdated(currentPosition, targetPosition, this);
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
}
