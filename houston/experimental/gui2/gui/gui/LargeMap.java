package gui;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
/*
 * This class is designed to provide a window showing an enlarged map.
 */
import javax.swing.SpringLayout;

public class LargeMap extends JFrame implements WindowStateListener {
	RoverUpdateListener l;
	private static final long serialVersionUID = -1257523731129487909L;
	CustomMap map_2;
	Rover rover;
	JLabel lblPosition;
	Position relativeDatumPoint = null;
	Position lastMousePosition = null;
	LargeMap(Rover r, Position centre, int zoomLevel) {
		super();
		setTitle("Map");
		setSize(500, 500);
		SpringLayout springLayout = new SpringLayout();
		this.getContentPane().setLayout(springLayout);
		JButton btnGoto = new JButton("Go To");

		btnGoto.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GoToDialog dialog = new GoToDialog(null);
				GoToDialogResult result = dialog.show(map_2.getPosition()
						.getLat(), map_2.getPosition().getLon());
				if (result.success) {
					switch (result.selectedOption) {
					case GOTO_ROVER:
						map_2.setDisplayPositionByLatLon(
								rover.currentPosition.getLat(),
								rover.currentPosition.getLon(), map_2.getZoom());
						break;
					case GOTO_MODULE:
						// TODO
						break;
					case GOTO_LATLONG:
						map_2.setDisplayPositionByLatLon(
								result.enteredPosition.getLat(),
								result.enteredPosition.getLon(),
								map_2.getZoom());
						break;
					}
				}
			}
		});
		map_2 = new CustomMap(r);
		lblPosition = new JLabel("");
		
		map_2.setDisplayPositionByLatLon(centre.getLat(), centre.getLon(), zoomLevel);
		map_2.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int xCoord = arg0.getX();
				int yCoord = arg0.getY();
				lastMousePosition = new Position(map_2.getPosition(xCoord,
						yCoord));
				if(relativeDatumPoint == null) {
					lblPosition.setText(String.format(
							"Lat: %.5f, Long: %.5f",
							lastMousePosition.getLat(), lastMousePosition.getLon()));
				} else {
					lblPosition.setText(String.format(
							"Lat: %.5f, Long: %.5f, Rel: %.1fm, %.1f",
							lastMousePosition.getLat(), lastMousePosition.getLon(),
							relativeDatumPoint.getDistanceTo(lastMousePosition),
							relativeDatumPoint.getBearingTo(lastMousePosition)));
				}
				
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// Not used

			}
		});
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
		      @Override
		      public boolean dispatchKeyEvent(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_S) {
					if(lastMousePosition != null) {
						relativeDatumPoint = lastMousePosition;
						lblPosition.setText(String.format(
								"Lat: %.5f, Long: %.5f, Rel: %.1fm, %.1f",
								lastMousePosition.getLat(), lastMousePosition.getLon(),
								relativeDatumPoint.getDistanceTo(lastMousePosition),
								relativeDatumPoint.getBearingTo(lastMousePosition)));
					}
				}
				return true;
				
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnGoto, 10,
				SpringLayout.NORTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnGoto, 10,
				SpringLayout.WEST, this.getContentPane());
		getContentPane().add(btnGoto);
		springLayout.putConstraint(SpringLayout.NORTH, lblPosition, 0,
				SpringLayout.NORTH, btnGoto);
		springLayout.putConstraint(SpringLayout.WEST, lblPosition, 5,
				SpringLayout.EAST, btnGoto);
		getContentPane().add(lblPosition);
		springLayout.putConstraint(SpringLayout.NORTH, map_2, 10,
				SpringLayout.SOUTH, btnGoto);
		springLayout.putConstraint(SpringLayout.WEST, map_2, 0,
				SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, map_2, 0,
				SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, map_2, 0,
				SpringLayout.SOUTH, this.getContentPane());
		getContentPane().add(map_2);

		setVisible(true);
		this.rover =r;
	}

	@Override
	public void windowStateChanged(WindowEvent arg0) {

	};

	public void windowClosed(WindowEvent e) {
		rover.removeListener(l);
	}
}
