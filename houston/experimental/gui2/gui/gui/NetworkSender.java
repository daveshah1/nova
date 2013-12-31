package gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class NetworkSender {
	private Socket roverSocket;
	private String roverHostname;
	
	private PrintWriter out;
	private BufferedReader in;
	public NetworkSender(String hostname) {
		roverHostname = hostname;
	}
	private boolean connect() {
		try {
			disconnect();
			roverSocket = new Socket(roverHostname,3141);
			roverSocket.setSoTimeout(2500);
			out = new PrintWriter(roverSocket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(roverSocket.getInputStream()));
			
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	private void disconnect() {
		try {
			roverSocket.close();
		} catch (Exception e) {
			
		}
	}
	public NetworkResponse sendCommand(NetworkCommand command) {
		if(connect()) {
			try {
				out.println(command.toString());
				String responseText = in.readLine();
				return new NetworkResponse(responseText);
			} catch (SocketTimeoutException e) {
				return new NetworkResponse(NetworkStatus.TIMEOUT);
			} catch (Exception e) {
				return new NetworkResponse(NetworkStatus.COMMUNICATION_ERROR);
			}
		} else {
			return new NetworkResponse(NetworkStatus.ROVER_OFFLINE);
		}
	}
	
	
	

}
