package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextArea;

public class Console extends JTextArea implements RoverUpdateListener {
	private static final long serialVersionUID = 4989941948035851333L;
	public boolean paused = false;
	Console() {
		super();
		setLineWrap(true);
		setColumns(25);
		setFont(new Font("Lucida Console", Font.PLAIN, 12));
		setForeground(Color.GREEN);
		setBackground(Color.BLACK);
		setEditable(false);
	}
	
	public void startDebug() {
		paused = false;
	}
	
	public void pauseDebug() {
		paused = true;
	}
	
	@Override
	public void positionUpdated(Position newPosition, Position targetPosition, boolean atTargetPosition,
			Rover r) {
		if(!paused) {
			append("D1|Location: " + newPosition.toString() + "\n");
			setCaretPosition(getDocument().getLength());
		};
	}
	@Override
	public void dataUpdated(double temperature, double pressure, Rover r) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void messageRecieved(String message, Rover r) {
		// TODO Auto-generated method stub
		
	}
}
