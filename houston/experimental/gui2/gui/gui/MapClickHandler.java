package gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

public class MapClickHandler implements MouseListener {
    public MapClickHandler(VirtualRover rover) {
		super();
		this.rover = rover;
	}

	VirtualRover rover;
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JMapViewer eventOriginator;
		int xCoord, yCoord;
		int move;
		Coordinate realPosition;
		if(arg0.getComponent() instanceof JMapViewer) { //Basic sanity check
			//Obtain the original JMapViewer for position look-up
			eventOriginator = (JMapViewer) arg0.getComponent();
			//Get raw X and Y coordinates (relative to the JMapViewer object)
			xCoord = arg0.getX();
			yCoord = arg0.getY();
			realPosition = eventOriginator.getPosition(xCoord,yCoord);
			move = JOptionPane.showConfirmDialog(eventOriginator.getRootPane(), "Latitude: " + realPosition.getLat() + 
                    "\nLongitude: " + realPosition.getLon(), "Confirm Move", JOptionPane.YES_NO_OPTION);
			if (move == JOptionPane.YES_OPTION) {
			rover.targetLat = realPosition.getLat();
			rover.targetLon = realPosition.getLon();
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Not used at the time being

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Not used at the time being

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// Not used at the time being

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Not used at the time being

	}

}
