package gui;

public interface RoverUpdateListener {
	public void positionUpdated(Position newPosition, Position targetPosition, boolean atTargetPosition, Rover r);
	public void dataUpdated(double temperature, double pressure, Rover r);
	public void messageRecieved(String message, Rover r);
}
