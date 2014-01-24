package gui;

import javax.swing.JOptionPane;

public class NetworkRover extends Rover {
	private NetworkSender network;
	private boolean online = false;
	
	enum MoveOperation {
		FORWARDS,
		BACKWARDS,
		LEFT_TURN,
		RIGHT_TURN
	}
	
	public NetworkRover() {

	}
	
	boolean begin(String hostname) {
		network = new NetworkSender(hostname);
		if(testNetwork() == NetworkStatus.OK) {
			//Initialise video stream
			online = true;
			return true;
		} else {
			disconnect();
			return false;
		}
	}
	
	NetworkStatus testNetwork() {
		if(online) {
			return network.sendCommand(new NetworkCommand("PI")).getStatus();
		} else {
			return NetworkStatus.ROVER_OFFLINE;
		}
	}
	
	void disconnect() {
		online = false;
		//Stop video stream
		//Any other closing down stuff
	}
	
	public void moveToPosition(Position p) {
		if(online) {
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
		};
	}
	
	public Position getPosition() throws NetworkException {
		if(online) {
			NetworkCommand cmd = new NetworkCommand("CL");
			NetworkResponse response = network.sendCommand(cmd);
			switch(response.getStatus()) {
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
				throw new NetworkException(response.getStatus());
			}
			String splitStr[] = response.getPayload().split("\\s+");
			if(splitStr.length < 2) throw new NetworkException(NetworkStatus.RESPONSE_INVALID,
					"Position string returned: " + response.getPayload());
			try {
				double lat = Double.parseDouble(splitStr[0]);
				double lon = Double.parseDouble(splitStr[1]);
				return new Position(lat,lon);
			} catch(Exception e) {
				throw new NetworkException(NetworkStatus.RESPONSE_INVALID,"Position string returned: " + response.getPayload());
			}
		} else {
			throw new NetworkException(NetworkStatus.ROVER_OFFLINE);
		}
	}
	
	public void update() {
		if(online) {
			//Test connectivity?
			try {
				currentPosition = getPosition();
				firePositionUpdate();
			} catch (NetworkException e) {
				throwError("Couldn't update position - error: " + e.getErrorCode().toString());
			} 
			
			try {
				currentData = getTP();
				fireDataUpdate();
			} catch (NetworkException e) {
				throwError("Couldn't update TP - error: " + e.getErrorCode().toString());
			} 
		}
	}
	
	public void moveManually(MoveOperation how) {
		if(online) {
			String opcode = "";
			switch(how) {
			case FORWARDS:
				opcode = "F";
				break;
			case BACKWARDS:
				opcode = "B";
				break;
			case LEFT_TURN:
				opcode = "L";
				break;
			case RIGHT_TURN:
				opcode = "R";
				break;
			}
	
			NetworkCommand cmd = new NetworkCommand("MV",opcode);
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
				throwError("Couldn't move - error: " + status.toString());
				return;
			}
			targetPosition = currentPosition;
			atTargetPosition = true;
		};
	}
	
	public void stopRover() {
		if(online) {
			NetworkCommand cmd = new NetworkCommand("ST");
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
				throwError("Couldn't stop - error: " + status.toString());
				return;
			}
			targetPosition = currentPosition;
			atTargetPosition = true;
		};
	}
	
	public TPData getTP() throws NetworkException {
		if(online) {
			NetworkCommand cmd = new NetworkCommand("TP");
			NetworkResponse response = network.sendCommand(cmd);
			switch(response.getStatus()) {
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
				throw new NetworkException(response.getStatus());
			}
			String splitStr[] = response.getPayload().split("\\s+");
			if(splitStr.length < 2) throw new NetworkException(NetworkStatus.RESPONSE_INVALID,
					"TP string returned: " + response.getPayload());
			try {
				double t = Double.parseDouble(splitStr[0]);
				double p = Double.parseDouble(splitStr[1]);
				return new TPData(t,p);
			} catch(Exception e) {
				throw new NetworkException(NetworkStatus.RESPONSE_INVALID,"TP string returned: " + response.getPayload());
			}
		} else {
			throw new NetworkException(NetworkStatus.ROVER_OFFLINE);
		}
	}
}
