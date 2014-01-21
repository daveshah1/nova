package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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
	private BufferedReader serialPortReader;
	private PrintWriter serialPortWriter;
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
			serialPortReader = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			serialPortWriter = new PrintWriter(new OutputStreamWriter(serialPort.getOutputStream()));
		} catch (NoSuchPortException e) {
			return false;
		} catch (PortInUseException e) {
			return false;
		} catch (UnsupportedCommOperationException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	//Close the serial port
	public void stopCommunications() {
		serialPort.close();
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna pan (left/right) servo angle
	public boolean setAntennaPan(int theta) {
		serialPortWriter.println("A P " + theta);
		return true;
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna tilt (up/down) servo angle
	public boolean setAntennaTilt(double theta) {
		serialPortWriter.println("A T " + theta);
		return true;
	}
	
	public String getAvailableRadioData() {
		serialPortWriter.println("R R");
		try {
			return serialPortReader.readLine();
		} catch (IOException e) {
			return "";
		}
	}
	
	public boolean sendRadio(String data) {
		serialPortWriter.println("R W " + data);
		return false;
	}
}
