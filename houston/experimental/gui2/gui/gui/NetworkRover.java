package gui;

import javax.swing.JOptionPane;

public class NetworkRover extends Rover {
	private NetworkSender network;
	private boolean online = false;
	private boolean busy = false;
	enum MoveOperation {
		FORWARDS,
		BACKWARDS,
		LEFT_TURN,
		RIGHT_TURN
	}
	
	public NetworkRover() {

	}
	
	boolean begin(String hostname) {
		busy = false;
		network = new NetworkSender(hostname);
		online = true;
		System.err.println(testNetwork().toString());
		if(testNetwork() == NetworkStatus.OK) {
			//Initialise video stream
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
		busy = false;
		network = null;
		//Stop video stream
		//Any other closing down stuff
	}
	
	private void waitForFreeNetwork() throws NetworkException {
		long startWaitTime = System.currentTimeMillis();
		while(busy) {
			if((System.currentTimeMillis() - startWaitTime) > 5000) {
				System.err.println("---");

				throw new NetworkException(NetworkStatus.TIMEOUT);
			}
		}
	}
	
	public void moveToPosition(Position p) {
		if(online) {
			try {
				waitForFreeNetwork();
			} catch (NetworkException e) {
				return;
			}
			busy = true;
			targetPosition = p;
			atTargetPosition = false;
			NetworkCommand cmd = new NetworkCommand("GT",p.getLat() + " "  + p.getLon());
			NetworkStatus status = network.sendCommand(cmd).getStatus();
			busy = false;
			switch(status) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				try {
					waitForFreeNetwork();
				} catch (NetworkException e) {
					break;
				}
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
				if(newStatus == NetworkStatus.OK) break;
				//Otherwise drop into error handling
			default:
				targetPosition = currentPosition;
				atTargetPosition = true;
				JOptionPane.showMessageDialog(null,"Network error: " + status.toString());
				break;
			}
			busy = false;
		};
	}
	
	public Position getPosition() throws NetworkException {
		if(online) {
			waitForFreeNetwork();
			busy = true;
			NetworkCommand cmd = new NetworkCommand("CL");
			NetworkResponse response = network.sendCommand(cmd);
			busy = false;
			switch(response.getStatus()) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				waitForFreeNetwork();
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
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
			try {
				waitForFreeNetwork();
			} catch(NetworkException e) {
				throwError("Couldn't move - error: timeout");
				return;
			}
			busy = true;
			NetworkCommand cmd = new NetworkCommand("MV",opcode);
			NetworkStatus status = network.sendCommand(cmd).getStatus();
			busy = false;
			switch(status) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				try {
					waitForFreeNetwork();
				} catch(NetworkException e) {
					throwError("Couldn't move - error: timeout");
					return;
				}
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
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
			try {
				waitForFreeNetwork();
			} catch(NetworkException e) {
				throwError("Couldn't stop - error: timeout");
				return;
			}
			busy = true;
			NetworkCommand cmd = new NetworkCommand("ST");
			NetworkStatus status = network.sendCommand(cmd).getStatus();
			busy = false;
			switch(status) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				try {
					waitForFreeNetwork();
				} catch(NetworkException e) {
					throwError("Couldn't stop - error: timeout");
					return;
				}
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
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
	
	public void formatEEPROM() {
		if(online) {
			try {
				waitForFreeNetwork();
			} catch(NetworkException e) {
				throwError("Couldn't format EEPROM - error: timeout");
				return;
			}
			busy = true;
			NetworkCommand cmd = new NetworkCommand("FT");
			NetworkStatus status = network.sendCommand(cmd).getStatus();
			busy = false;
			switch(status) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				try {
					waitForFreeNetwork();
				} catch(NetworkException e) {
					throwError("Couldn't format EEPROM - error: timeout");
					return;
				}
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
				if(newStatus == NetworkStatus.OK) break;
				//Otherwise drop into error handling
			default:
				throwError("Couldn't format EEPROM - error: " + status.toString());
				return;
			}
		};
	}
	
	public TPData getTP() throws NetworkException {
		if(online) {
			waitForFreeNetwork();
			busy = true;
			NetworkCommand cmd = new NetworkCommand("TP");
			NetworkResponse response = network.sendCommand(cmd);
			busy = false;
			switch(response.getStatus()) {
			case OK:
				break;
			case COMMUNICATION_ERROR:
				//Try once more
				waitForFreeNetwork();
				busy = true;
				NetworkStatus newStatus = network.sendCommand(cmd).getStatus();
				busy = false;
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
