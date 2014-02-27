package gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/*
 * This class is designed to provide an interface to the Arduino on the base station
 * which is responsible for radio communications and antenna positioning.
 */
public class BaseStationCommunications {

	private boolean busy = false;
	Process p;
	BufferedWriter out;
	BufferedReader in;
	public BaseStationCommunications() {
		// TODO Auto-generated constructor stub
	}
	
	//True = OK
	//False = Error
	//Open the serial port
	public boolean startCommunications(String port) {
		try {
			SettingsStore settings = new SettingsStore();
			p = Runtime.getRuntime().exec("SerialWrapper.exe");
			in = new BufferedReader( new InputStreamReader(p.getInputStream()) );
	        out = new BufferedWriter( new OutputStreamWriter(p.getOutputStream()) );
	        out.write(settings.get("serial.port") + "\n");
	        out.flush();
			busy = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
		//serialPortWriter.println("C S");
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
		//serialPortWriter.println("C E");
		busy = false;
		try {
			out.write("Q\n");
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try { Thread.sleep(100); } catch (InterruptedException e) { }
		p.destroy();
		p = null;
		
	}
	//True = OK
	//False = Error
	//Sets WiFi antenna target tilt (up/down) angle and pan (left/right) angle
	public boolean setAntennaTilt(int tilt, int pan) {
		if(!waitForFreePort())
			return false;
		busy = true;
		try {
			out.write("A " + pan + " " + tilt  + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		busy = false;
		return true;
	}

	public String getAvailableRadioData() {
		if(!waitForFreePort())
			return "";
		busy = true;
		try {
		out.write("R\n");
		out.flush();
		} catch (IOException e) {
			
		}
		String s = "", total = "";
		while(true) {
			try {
				s = in.readLine();
				if(s.contains("-----END-----")) break;
				total += s;
				//System.err.println(s);
			} catch (IOException e) {
				s = "";
				System.err.println("--ERR IN gARD---");
				e.printStackTrace();
			}
		}
		busy = false;
		return total;
	}
	
	public boolean sendRadio(String data) {
		if(!waitForFreePort())
			return false;
		busy = true;
		try {
			out.write("W " + data + "\n");
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		busy = false;
		return true;
	}
	
	
}
