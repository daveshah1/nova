package gui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import gui.LandingModule.DeploymentStatus;

public class DataLogger implements RoverUpdateListener, LandingModuleListener {
	Position currentRoverPosition = new Position(0,0), currentLandingModulePosition = new Position(0,0);
	TPData currentRoverTP = new TPData(0,0), currentLandingModuleTP = new TPData(0,0);
	String filename;
	boolean logging = false;
	public DataLogger() {
		currentLandingModulePosition = new Position(0, 0);
		currentRoverPosition = new Position(0, 0);

	}
	
	public boolean start() {
		if(!Files.exists(Paths.get("./data/"))) {
			try {
				Files.createDirectory(Paths.get("./data/"));
			} catch (IOException e) {
				return false;
			}
			
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
		Date date = new Date();
		filename = "./data/log-" + dateFormat.format(date) + ".csv";
		logging = true;
		putLine("Time,Rover Latitude, Rover Longitude, Rover Temperature, Rover Pressure,"
				+ "Landing Module Latitude, Landing Module Longitude, Landing Module Temperature, Landing Module Pressure");
		return true;
	}
	
	private void putLine(String string) {
		FileOutputStream outputStream;
		PrintWriter fileWriter;
		try {
			outputStream = new FileOutputStream(filename,true); //Open file for appending
			fileWriter = new PrintWriter(outputStream);
			fileWriter.println(string);
			fileWriter.close();
			outputStream.close();
			outputStream = null;
			fileWriter = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addDataLine() {
		if(logging) {
			String dataLine = "";
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			Date date = new Date();
			dataLine += dateFormat.format(date);
			dataLine += ",";
			dataLine += currentRoverPosition.getLat();
			dataLine += ",";
			dataLine += currentRoverPosition.getLon();
			dataLine += ",";
			dataLine += currentRoverTP.temperature;
			dataLine += ",";
			dataLine += currentRoverTP.pressure;
			dataLine += ",";
			dataLine += currentLandingModulePosition.getLat();
			dataLine += ",";
			dataLine += currentLandingModulePosition.getLon();
			dataLine += ",";
			dataLine += currentLandingModuleTP.temperature;
			dataLine += ",";
			dataLine += currentLandingModuleTP.pressure;
			putLine(dataLine);
		};
	}
	
	@Override
	public void positionUpdated(Position newPosition, LandingModule m) {
		currentLandingModulePosition = newPosition;
		addDataLine();
	}

	@Override
	public void dataUpdated(TPData currentData, LandingModule m) {
		currentLandingModuleTP = currentData;
		addDataLine();
	}

	@Override
	public void statusUpdated(DeploymentStatus deployed, boolean connected,
			LandingModule m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void positionUpdated(Position newPosition, Position targetPosition,
			boolean atTargetPosition, Rover r) {
		currentRoverPosition = newPosition;
		addDataLine();
	}

	@Override
	public void dataUpdated(TPData data, Rover r) {
		currentRoverTP = data;		
		addDataLine();
	}

	@Override
	public void messageRecieved(String message, Rover r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorThrown(String message, Rover r) {
		// TODO Auto-generated method stub
		
	}

}
