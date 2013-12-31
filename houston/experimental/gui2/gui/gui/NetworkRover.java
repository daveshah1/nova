package gui;

import javax.swing.JOptionPane;

public class NetworkRover extends Rover {
	
	private NetworkSender network;
	
	public NetworkRover() {

	}
	
	boolean begin(String hostname) {
		network = new NetworkSender(hostname);
		if(testNetwork() == NetworkStatus.OK) {
			//Initialise video stream
			return true;
		} else {
			disconnect();
			return false;
		}
	}
	
	NetworkStatus testNetwork() {
		return network.sendCommand(new NetworkCommand("PI")).getStatus();
	}
	
	void disconnect() {
		//Stop video stream
		//Any other closing down stuff
	}
	
	void moveToPosition(Position p) {
		targetPosition = p;
		atTargetPosition = false;
		NetworkCommand cmd = new NetworkCommand("GT",p.getLat() + " "  + p.getLon());
		NetworkStatus status = network.sendCommand(cmd).getStatus();
		switch(status) {
		case OK:
			break;
		case COMMUNICATION_ERROR:
			//Try once more
			NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
			if(newStatus == NetworkStatus.OK) break;
			//Otherwise drop into error handling
		default:
			targetPosition = currentPosition;
			atTargetPosition = true;
			JOptionPane.showMessageDialog(null,"Network error: " + status.toString());
			break;
		}
	}
	
	void stopRover() {
		
	}
}
