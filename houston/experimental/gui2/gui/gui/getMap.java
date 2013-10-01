package gui;

import java.net.URL;

import javax.swing.ImageIcon;

/*
 * Documentation found here:
 * https://developers.google.com/maps/documentation/staticmaps/?csw=1
 * 
 * Add variables, etc and customise mapURL to suit our needs.
 * 
 * TODO: OBJECT ORIENTATED WAYPOINT ADDING SHIZZ
 */

public class getMap {
	public static ImageIcon google (String location) throws Exception {
		String mapURL = "http://maps.googleapis.com/maps/api/staticmap?center=" + location + "&zoom=14&size=300x300&sensor=true";
		URL img = new URL(mapURL);
		ImageIcon image = new ImageIcon(img);
		return image;
	}
}
