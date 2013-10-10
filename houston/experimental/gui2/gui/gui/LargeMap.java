package gui;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;

/*
 * This class is designed to provide a window showing an enlarged map.
 */

public class LargeMap extends JFrame implements WindowStateListener {
	RoverUpdateListener l;
	private static final long serialVersionUID = -1257523731129487909L;
	CustomMap map_2;
    Rover rover;
	LargeMap(Rover r) {
		super();
		setTitle("Map");
		setSize(500,500);
		map_2 = new CustomMap(r);
		getContentPane().add( map_2);
        setVisible(true);
		this.rover = r;
	}
	@Override
	public void windowStateChanged(WindowEvent arg0) {
		
	};
    public void windowClosed(WindowEvent e) {
    	rover.removeListener(l);
    }
}
