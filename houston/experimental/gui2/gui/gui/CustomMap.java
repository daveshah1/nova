package gui;

import gui.LandingModule.DeploymentStatus;

import java.awt.Color;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;

/*
 * This is a customised version of JMapViewer that has built-in handling of
 * rover position display and updating, as well as registration of the click handler.
 * 
 */

public class CustomMap extends JMapViewer implements RoverUpdateListener, LandingModuleListener {
	private static final long serialVersionUID = 4892531410795284424L;
	Rover rover;
	MapMarkerDot actualPos, targetPos, modulePos;
	public CustomMap(Rover r) {
		super();
		this.rover = r;
		rover.attachListener(this);
		addMouseListener(new MapClickHandler(rover));
		setTileSource(new OfflineOsmTileSource("file:///" + System.getProperty("user.home").replace("\\","/") + "/mapcache/",14,18));
		setDisplayPositionByLatLon(51.487556,-0.2381855, 16);
	}
	@Override
	public void positionUpdated(Position newPosition, Position targetPosition, boolean atTargetPosition,
			Rover r) {
		try {
			removeMapMarker(actualPos);
			removeMapMarker(targetPos);
		} finally {
    	
		};
        actualPos = new MapMarkerDot(Color.green
        		, newPosition.getLat()
        		, newPosition.getLon());
        addMapMarker(actualPos);
        if(!atTargetPosition) {
	        targetPos = new MapMarkerDot(Color.red
	        		, targetPosition.getLat()
	        		, targetPosition.getLon());
	        addMapMarker(targetPos);
        };
		
	}
	
	// --- ROVER ---
	
	@Override
	public void dataUpdated(TPData data, Rover r) {
		// Not used at present
		
	}
	@Override
	public void messageRecieved(String message, Rover r) {
		// Not used at present
		
	}
	@Override
	public void errorThrown(String message, Rover r) {
		// TODO Auto-generated method stub
		
	}
	public void shutDown() {
		rover.removeListener(this);
	}
	// --- LANDING MODULE ---
	@Override
	public void positionUpdated(Position newPosition, LandingModule m) {
		try {
			removeMapMarker(modulePos);
		} finally {
	        modulePos = new MapMarkerDot(Color.yellow
	        		, newPosition.getLat()
	        		, newPosition.getLon());
	        addMapMarker(modulePos);
		};
		
		
	}
	@Override
	public void dataUpdated(TPData currentData, LandingModule m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void statusUpdated(DeploymentStatus deployed, boolean connected,
			LandingModule m) {
		// TODO Auto-generated method stub
		
	}
}
