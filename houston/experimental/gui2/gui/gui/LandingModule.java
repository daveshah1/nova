package gui;

import java.util.Vector;

/*
 * Radio sentence format
 * BdataE
 * data: temp,pressure,gpsAvailable,lat,long,alt,deployed (comma separated)
 * e.g. sentence: B175,100000,1,51.487556,-0.2381855,100,1E
 * temp: temperature in degrees C * 10
 * pressure: pressure in Pa
 * gpsAvailable: 1 if GPS fix available, 0 otherwise
 * lat: GPS latitude
 * long: GPS longitude
 * alt: GPS (not barometric) altitude
 * deployed: 1 if rover deployed, 0 if not
 */

public class LandingModule {
	public enum DeploymentStatus {
		LOADED, DEPLOYED, UNKNOWN
	}

	Vector<LandingModuleListener> listeners = new Vector<LandingModuleListener>();
	public Position currentPosition;
	protected TPData currentData;
	public DeploymentStatus roverDeployed;
	public boolean connected = false, running = false, gpsAvailable = false;
	String radioBuffer = "";
	private BaseStationCommunications baseStation;
	public void attachListener(LandingModuleListener l) {
		listeners.add(l);
	};

	public void removeListener(LandingModuleListener l) {
		if(listeners.contains(l)) {
			listeners.remove(l);
		};
	};

	public void deployRover() {

	}

	public void startCommunications(BaseStationCommunications baseArduinoComms) {
		baseStation = baseArduinoComms;
		running = false;
	}

	public void stopCommunications() {
		running = false;
	}
	
	public void update() {
		if(running) {
			 boolean packetRecieved = false;
			 String newData = baseStation.getAvailableRadioData();
			 radioBuffer += newData;
			 while(true) {
				 int endOfSentence = radioBuffer.indexOf("E");
				 if(endOfSentence == -1) break; //No full packet remaining, stop
				 String sentence = radioBuffer.substring(0,endOfSentence); //Split at ending delimiter
				 radioBuffer = radioBuffer.substring(endOfSentence+1); //Put remainder back into buffer
				 if(!sentence.startsWith("B")) continue; //Not a full sentence, discard
				 //Trim starting letter
				 sentence = sentence.substring(1);
				 String splitSentence[] = sentence.split(",");
				 if(splitSentence.length < 7) continue;
				 currentData.temperature = Double.parseDouble(splitSentence[0]) / 10;
				 currentData.pressure = Double.parseDouble(splitSentence[1]);
				 gpsAvailable = Boolean.parseBoolean(splitSentence[2]);
				 if(gpsAvailable) {
					 currentPosition = new Position( Double.parseDouble(splitSentence[3]), 
							 Double.parseDouble(splitSentence[4]));
				 }
				 packetRecieved = true;

			 }
			 if(packetRecieved) {
				 firePositionUpdate();
				 fireDataUpdate();
				 fireStatusUpdate();
			 }
		}
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
