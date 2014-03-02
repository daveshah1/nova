package gui;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Radio sentence format
 * STARTdataEND
 * data: temp,pressure,gpsAvailable,lat,long,alt,deployed (comma separated)
 * e.g. sentence: B175,100000,1,51.487556,-0.2381855,100,1E
 * temp: temperature in degrees C * 10
 * pressure: pressure in Pa
 * gpsAvailable: 1 if GPS fix available, 0 otherwise
 * lat: GPS latitude
 * long: GPS longitude
 * alt: GPS (not barometric) altitude
 * state: 7 if rover deployed, ? if not
 */

public class LandingModule {
	public enum DeploymentStatus {
		LOADED, DEPLOYED, UNKNOWN
	}

	private ScheduledExecutorService updater;
	Vector<LandingModuleListener> listeners = new Vector<LandingModuleListener>();
	public Position currentPosition = new Position(0, 0);
	protected TPData currentData = new TPData(0, 0);
	public DeploymentStatus roverDeployed = DeploymentStatus.LOADED;
	public boolean connected = false, running = false, gpsAvailable = false;
	String radioBuffer = "";
	private BaseStationCommunications baseStation;
	public double gpsAltitude = 0;
	public int status = -1;

	public void attachListener(LandingModuleListener l) {
		listeners.add(l);
	};

	public void removeListener(LandingModuleListener l) {
		if (listeners.contains(l)) {
			listeners.remove(l);
		}
		;
	};

	public void deployRover() {
		Runnable radioSender = new Runnable() {
			public void run() {
				if ((status == 6) || (status == 7)) {
					updater.shutdown();
				}
				baseStation.sendRadio("DEP");
			}
		};

		// Keep going until accepted
		updater = Executors.newScheduledThreadPool(1);
		updater.scheduleAtFixedRate(radioSender, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public void terminateDeployment() {
		Runnable radioSender = new Runnable() {
			public void run() {
				if (status == 8) {
					updater.shutdown();
				}
				baseStation.sendRadio("END");
			}
		};

		// Keep going until accepted
		updater = Executors.newScheduledThreadPool(1);
		updater.scheduleAtFixedRate(radioSender, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public void startCommunications(BaseStationCommunications baseArduinoComms) {
		baseStation = baseArduinoComms;
		running = true;
	}

	public void stopCommunications() {
		running = false;
	}

	public void update() {

		if (running) {
			boolean packetRecieved = false;
			// System.err.println("...");
			String newData = baseStation.getAvailableRadioData();
			radioBuffer += newData;
			System.err.println(radioBuffer);
			try {
				while (true) {
					int startOfSentence = radioBuffer.indexOf("START");
					int endOfSentence = radioBuffer.indexOf("END", startOfSentence + 5);
					if (endOfSentence < 0)
						break;
					if (startOfSentence < 0)
						break; // No full packet remaining, stop

					String sentence = radioBuffer.substring(startOfSentence + 5, endOfSentence);
					System.out.println(sentence);
					radioBuffer = radioBuffer.substring(endOfSentence + 3); 
					String splitSentence[] = sentence.split(",");
					// if(splitSentence.length < 7) continue;
					currentData.temperature = Double.parseDouble(splitSentence[0]) / 100;
					currentData.pressure = Double.parseDouble(splitSentence[1]);
					System.out.println(currentData.temperature);
					gpsAvailable = (Integer.parseInt(splitSentence[2]))>0;
					if (gpsAvailable) {
						currentPosition = new Position(Double.parseDouble(splitSentence[3]),
								Double.parseDouble(splitSentence[4]));
						gpsAltitude = Double.parseDouble(splitSentence[5]);
					}

					packetRecieved = true;
					status = Integer.parseInt(splitSentence[6]);
					System.out.println(currentData.temperature);
					if (status == 7) {
						roverDeployed = DeploymentStatus.DEPLOYED;
					} else {
						roverDeployed = DeploymentStatus.LOADED;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			;
			if (packetRecieved) {
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
