package gui;
/*
 * This class is designed to provide an interface to the Arduino on the base station
 * which is responsible for radio communications and antenna positioning.
 */
public class BaseStationCommunications {

	public BaseStationCommunications() {
		// TODO Auto-generated constructor stub
	}
	
	//True = OK
	//False = Error
	//Open the serial port
	public boolean startCommunications(String port) {
		return true;
	}
	
	//Close the serial port
	public void stopCommunications() {
		
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna pan (left/right) servo angle
	public boolean setAntennaPan(double theta) {
		return true;
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna tilt (up/down) servo angle
	public boolean setAntennaTilt(double theta) {
		return true;
	}
	
	public String getAvailableRadioData() {
		return "";
	}
	
	public boolean sendRadio(String data) {
		return false;
	}
}
