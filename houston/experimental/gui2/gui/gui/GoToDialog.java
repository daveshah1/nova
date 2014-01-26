package gui;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class GoToDialog {
	JFrame parent;
	public GoToDialog(JFrame dialogParent) {
		parent = dialogParent;
	}
	public GoToDialogResult show(double defaultLat, double defaultLon) {		
		JRadioButton toRoverPosition = new JRadioButton("Go to Rover");
		toRoverPosition.setSelected(true); //Default option
		JRadioButton toLandingModulePosition = new JRadioButton("Go to Landing Module");
		JRadioButton toLatLongPair = new JRadioButton("Go to Latitude/Longitude:");
		
		ButtonGroup gotoOptions = new ButtonGroup();
		gotoOptions.add(toRoverPosition);
		gotoOptions.add(toLandingModulePosition);
		gotoOptions.add(toLatLongPair);
		
		JTextField latitudeField = new JTextField(Double.toString(defaultLat));
		JTextField longitudeField = new JTextField(Double.toString(defaultLon));
		
		final JComponent[] inputs = new JComponent[] {
			toRoverPosition,
			toLandingModulePosition,
			toLatLongPair,
			new JLabel("Latitude"),
			latitudeField,
			new JLabel("Longitude"),
			longitudeField
		};
		GoToDialogResult goToWhere;
		int dialogResult = JOptionPane.showConfirmDialog(parent, inputs,"Go to where?",JOptionPane.OK_CANCEL_OPTION);
		if(dialogResult == JOptionPane.CANCEL_OPTION) {
			goToWhere = new GoToDialogResult(false, null, null);
		} else {
			if(toRoverPosition.isSelected()) {
				goToWhere = new GoToDialogResult(true, GoToDialogResult.Selection.GOTO_ROVER, null);
			} else if(toLandingModulePosition.isSelected()) {
				goToWhere = new GoToDialogResult(true, GoToDialogResult.Selection.GOTO_MODULE, null);
			} else if(toLatLongPair.isSelected()) {
				double latitude, longitude;
				try {
					latitude = Double.parseDouble(latitudeField.getText());
					longitude = Double.parseDouble(longitudeField.getText());
					goToWhere = new GoToDialogResult(true, 
							GoToDialogResult.Selection.GOTO_LATLONG, 
							new Position(latitude, longitude));
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(parent, "Invalid latitude/longitude","Error",JOptionPane.WARNING_MESSAGE);
					goToWhere = new GoToDialogResult(false, null, null);
				}
			} else {
				goToWhere = new GoToDialogResult(false, null, null);
			}
		}
		return goToWhere;
	}
}
