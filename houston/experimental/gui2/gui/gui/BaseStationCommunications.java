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
	private boolean busy = false;
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
			busy = false;
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
	
	private boolean waitForFreePort() {
		long startWaitTime = System.currentTimeMillis();
		while(busy) {
			if((System.currentTimeMillis() - startWaitTime) > 5000) {
				return false;
			}
		}
		return true;
	}
	
	//Close the serial port
	public void stopCommunications() {
		waitForFreePort();
		busy = false;
		serialPort.close();
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna target bearing
	public boolean setAntennaPan(int theta) {
		if(!waitForFreePort())
			return false;
		busy = true;
		serialPortWriter.println("A P " + theta);
		busy = false;
		return true;
	}
	
	//True = OK
	//False = Error
	//Sets WiFi antenna target tilt (up/down) angle
	public boolean setAntennaTilt(int theta) {
		if(!waitForFreePort())
			return false;
		busy = true;
		serialPortWriter.println("A T " + theta);
		busy = false;
		return true;
	}
	
	public String getAvailableRadioData() {
		if(!waitForFreePort())
			return "";
		busy = true;
		serialPortWriter.println("R R");
		String s;
		try {
			s = serialPortReader.readLine();
		} catch (IOException e) {
			s = "";
		}
		busy = false;
		return s;
	}
	
	public boolean sendRadio(String data) {
		if(!waitForFreePort())
			return false;
		busy = true;
		serialPortWriter.println("R W " + data);
		busy = false;
		return false;
	}
}
