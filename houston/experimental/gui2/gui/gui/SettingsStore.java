package gui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SettingsStore {
	private static final Map<String, String> settings = new HashMap<String, String>();
	private static boolean initialised = false;
	public SettingsStore() {
		if (!initialised) { 
			initialised = true;
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
			
			if(!settings.containsKey("home.lat")) {
				settings.put("home.lat", "51.487556");
				changed = true;
			}
			
			if(!settings.containsKey("home.long")) {
				settings.put("home.long", "-0.2381855");
				changed = true;
			}
			
			if(!settings.containsKey("home.alt")) {
				settings.put("home.alt", "0");
				changed = true;
			}
			if(changed) {
				save();
			}
		}
		
	}
	public void save() {
		FileOutputStream outputStream;
		PrintWriter fileWriter;
		try {
			outputStream = new FileOutputStream("settings.txt");
			fileWriter = new PrintWriter(outputStream);
			for(Entry<String, String> entry : settings.entrySet()) {
				fileWriter.println(entry.getKey() + " " +entry.getValue());
			}
			fileWriter.close();
			outputStream.close();
			outputStream = null;
			fileWriter = null;
		} catch(Exception e) {
			
		}
	}
	public void set(String key, String value) {
		settings.put(key, value);
	}
	public String get(String key) {
		if(settings.containsKey(key)) {
			return settings.get(key);
		} else {
			return null;
		}
	}
	public double getDouble(String key) {
		try {
			return Double.parseDouble(settings.get(key));
		} catch(Exception e) {
			return 0;
		}
	}
	public int getInt(String key) {
		try {
			return Integer.parseInt(settings.get(key));
		} catch(Exception e) {
			return 0;
		}
	}
}
