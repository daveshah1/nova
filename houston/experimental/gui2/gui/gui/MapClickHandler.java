package gui;

/*
 * This class handles rover position updating, and is designed to be attached as
 * a mouse listener to a JMapViewer or CustomMap
 */

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

public class MapClickHandler implements MouseListener {
    public MapClickHandler(Rover rover) {
		super();
		this.rover = rover;
	}

	Rover rover;
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JMapViewer eventOriginator;
		int xCoord, yCoord;
		int move;
		Position realPosition;
		if(arg0.getComponent() instanceof JMapViewer) { //Basic sanity check
			//Obtain the original JMapViewer for position look-up
			eventOriginator = (JMapViewer) arg0.getComponent();
			//Get raw X and Y coordinates (relative to the JMapViewer object)
			xCoord = arg0.getX();
			yCoord = arg0.getY();
			realPosition = new Position(eventOriginator.getPosition(xCoord,yCoord));
			//Prompt the user whether they really want to move
			move = JOptionPane.showConfirmDialog(eventOriginator.getRootPane(),
					"Move to " + realPosition.toString() + "?\n" +
                    "Distance: " + 
					String.format("%.0f",realPosition.getDistanceTo(rover.currentPosition)*1000) +
					"m",
					"Confirm Move", 
					JOptionPane.YES_NO_OPTION);
			if (move == JOptionPane.YES_OPTION) {
				rover.targetPosition = realPosition;
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
