package gui;

import java.net.URL;

import javax.swing.ImageIcon;

/*
 * 
 * NOW OBSELETE AND KEPT FOR REFERENCE ONLY
 * 
 * Documentation found here:
 * https://developers.google.com/maps/documentation/staticmaps/?csw=1
 * 
 * Usage: ImageIcon = google(String location, Boolean WantMarkers?, String OKSureMarkers, Boolean WantPaths?, String OKSurePaths, Int ZoomLevel, String TypeOfMap);
 * Null the strings if you don't want anything else
 * 
 * Add variables, etc and customise mapURL to suit our needs.
 * 
 */

public class getMap {
	public static ImageIcon google (String location, Boolean mks, String markers, Boolean pth, String path, int zoom, String type) throws Exception {
		String mapURL = null;
		if (mks == true && pth == true) {
			mapURL = "http://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=" + zoom + "&size=300x300&maptype=" + type + "&markers=" + markers + "%path=" + path + "&sensor=true";
		} else if (mks == true) {
			mapURL = "http://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=" + zoom + "&size=300x300&maptype=" + type + "&markers=" + markers + "&sensor=true";			
		} else if (pth == true) {
			mapURL = "http://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=" + zoom + "&size=300x300&maptype=" + type + "&path=" + path + "&sensor=true";
		} else { 
			mapURL = "http://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=" + zoom + "&size=300x300&maptype=" + type + "&sensor=true";
		}
		URL img = new URL(mapURL);
		ImageIcon image = new ImageIcon(img);
		return image;
	}
}
