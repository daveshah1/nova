package gui;

public interface RoverUpdateListener {
	public void positionUpdated(Position newPosition, Position targetPosition, Rover r);
	public void dataUpdated(double temperature, double pressure, Rover r);
	public void messageRecieved(String message, Rover r);
}
