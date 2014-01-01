package gui;

public interface RoverUpdateListener {
	public void positionUpdated(Position newPosition, Position targetPosition, boolean atTargetPosition, Rover r);
	public void dataUpdated(TPData data, Rover r);
	public void messageRecieved(String message, Rover r);
	public void errorThrown(String message, Rover r);

}
