package gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class SettingsStore {
	private static Map<String, String> settings;
	public SettingsStore() {
		if (settings.isEmpty()) {
			InputStream inputStream;
			BufferedReader br;
			String line;
			try {
				inputStream = new FileInputStream("settings.txt");
				br = new BufferedReader(new InputStreamReader(inputStream));
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if(line.charAt(0) == '#') continue;
					String[] splitLine = line.split(" ",2);
					if(splitLine.length > 1) {
						settings.put(splitLine[0],splitLine[1]);
					};
				}
				br.close();
				br = null;
				inputStream = null;
			} catch(Exception e) {
				
			}
			boolean changed = false;
			if(!settings.containsKey("rover.ip")) {
				settings.put("rover.ip", "0.0.0.0");
				changed = true;
			}
			if(!settings.containsKey("serial.port")) {
				settings.put("serial.port", "COM0");
				changed = true;
			}
			if(!settings.containsKey("serial.baud")) {
				settings.put("serial.baud", "57600");
				changed = true;
			}
		}
		
	}

}
