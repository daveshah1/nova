package gui;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;


/*
 * This class is designed to provide an interface to the Arduino on the base station
 * which is responsible for radio communications and antenna positioning.
 */
public class BaseStationCommunications {
	private SerialPort serialPort;
	public BaseStationCommunications() {
		// TODO Auto-generated constructor stub
	}
	
	//True = OK
	//False = Error
	//Open the serial port
	public boolean startCommunications(String port) {
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(port);
			CommPort commPort = portIdentifier.open("novaController",500);
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
		} catch (NoSuchPortException e) {
			return false;
		} catch (PortInUseException e) {
			return false;
		} catch (UnsupportedCommOperationException e) {
			return false;
		}
		
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
