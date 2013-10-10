package gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;

public class LargeMap extends JFrame implements WindowStateListener {
	/**
	 * 
	 */
	RoverUpdateListener l;
	private static final long serialVersionUID = -1257523731129487909L;
	JMapViewer map_2;
    private MapMarkerDot targetPos;
    private MapMarkerDot actualPos;
    Rover rover;
	LargeMap(Rover r) {
		super();
		setTitle("Map");
		setSize(500,500);
		map_2 = new JMapViewer();
		map_2.setTileSource(new OfflineOsmTileSource("file:///" + System.getProperty("user.home").replace("\\","/") + "/mapcache/",14,18));
		map_2.setDisplayPositionByLatLon(51.487556,-0.2381855, 16);
		map_2.addMouseListener(new MapClickHandler(r));
		getContentPane().add( map_2);
		l = new RoverUpdateListener() {
			@Override
			public void positionUpdated(Position newPosition,
				Position targetPosition, Rover r) {
					try {
						map_2.removeMapMarker(actualPos);
						map_2.removeMapMarker(targetPos);
					} finally {
		        	
					};
			        actualPos = new MapMarkerDot(Color.green
			        		, newPosition.getLat()
			        		, newPosition.getLon());
			        map_2.addMapMarker(actualPos);
			        targetPos = new MapMarkerDot(Color.red
			        		, targetPosition.getLat()
			        		, targetPosition.getLon());
			        map_2.addMapMarker(targetPos);
				
			}

			@Override
			public void dataUpdated(double temperature, double pressure, Rover r) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void messageRecieved(String message, Rover r) {
				// TODO Auto-generated method stub
				
			}; 
		};
		r.attachListener(l);
        setVisible(true);
		rover = r;
	}
	@Override
	public void windowStateChanged(WindowEvent arg0) {
		
	};
    public void windowClosed(WindowEvent e) {
    	rover.removeListener(l);
    }
}
