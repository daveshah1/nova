package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import gnu.io.CommPortIdentifier;

public class SettingsWindow extends JFrame {
	private static final long serialVersionUID = 8536169689704756762L;
	SettingsWindow() {
		super();
		setTitle("Nova - Settings");
		setSize(500, 250);
		SpringLayout springLayout = new SpringLayout();
		this.getContentPane().setLayout(springLayout);
		JLabel lblRoverIP = new JLabel("Rover IP address: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblRoverIP, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblRoverIP, 5, SpringLayout.WEST, this);
		this.add(lblRoverIP);
		JTextField txtRoverIP = new JTextField("0.0.0.0");
		springLayout.putConstraint(SpringLayout.NORTH, txtRoverIP, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, txtRoverIP, 0, SpringLayout.EAST, lblRoverIP);
		springLayout.putConstraint(SpringLayout.EAST, txtRoverIP, 200, SpringLayout.EAST, lblRoverIP);
		this.add(txtRoverIP);
		
		JSeparator separator1 = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator1, 10, SpringLayout.SOUTH, txtRoverIP);
		springLayout.putConstraint(SpringLayout.WEST, separator1, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, separator1, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator1, 12, SpringLayout.SOUTH, txtRoverIP);

		this.add(separator1);
		
		JLabel lblSerialPort = new JLabel("Arduino Serial Port: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblSerialPort, 10, SpringLayout.NORTH, separator1);
		springLayout.putConstraint(SpringLayout.WEST, lblSerialPort, 5, SpringLayout.WEST, this);
		this.add(lblSerialPort);
		
		final JComboBox<String> serialPortList = new JComboBox<String>();
		@SuppressWarnings("rawtypes")
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();

	    while (portList.hasMoreElements()) {
	        CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
	        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	            serialPortList.addItem(portId.getName());
	        }
	    }
	    
		springLayout.putConstraint(SpringLayout.NORTH, serialPortList, 10, SpringLayout.NORTH, separator1);
		springLayout.putConstraint(SpringLayout.WEST, serialPortList, 10, SpringLayout.EAST, lblSerialPort);
	    this.add(serialPortList);
	    
	    JButton btnRescan = new JButton("Rescan");
	    btnRescan.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				serialPortList.removeAllItems();
				@SuppressWarnings("rawtypes")
				Enumeration portList = CommPortIdentifier.getPortIdentifiers();

			    while (portList.hasMoreElements()) {
			        CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			        if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			            serialPortList.addItem(portId.getName());
			        }
			    }				
			}
		});
	    
		springLayout.putConstraint(SpringLayout.NORTH, btnRescan, 10, SpringLayout.NORTH, separator1);
		springLayout.putConstraint(SpringLayout.WEST, btnRescan, 10, SpringLayout.EAST, serialPortList);
	    this.add(btnRescan);
	    
	    JLabel lblBaudRate = new JLabel("Baud rate: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblBaudRate, 10, SpringLayout.SOUTH, serialPortList);
		springLayout.putConstraint(SpringLayout.WEST, lblBaudRate, 5, SpringLayout.WEST, this);
		this.add(lblBaudRate);
		
		JTextField txtBaudRate = new JTextField("115200");
		springLayout.putConstraint(SpringLayout.NORTH, txtBaudRate, 10, SpringLayout.SOUTH, serialPortList);
		springLayout.putConstraint(SpringLayout.WEST, txtBaudRate, 5, SpringLayout.EAST, lblBaudRate);
		springLayout.putConstraint(SpringLayout.EAST, txtBaudRate, 95, SpringLayout.EAST, lblBaudRate);

		this.add(txtBaudRate);		
		
		JSeparator separator2 = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator2, 10, SpringLayout.SOUTH, txtBaudRate);
		springLayout.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, separator2, 0, SpringLayout.EAST, this.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, separator2, 12, SpringLayout.SOUTH, txtBaudRate);

		this.add(separator2);
		
		JLabel lblHomeLocation = new JLabel("Home location");
		springLayout.putConstraint(SpringLayout.NORTH, lblHomeLocation, 10, SpringLayout.NORTH, separator2);
		springLayout.putConstraint(SpringLayout.WEST, lblHomeLocation, 5, SpringLayout.WEST, this);
		this.add(lblHomeLocation);
		
		JLabel lblHomeLat = new JLabel("Lat: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblHomeLat, 5, SpringLayout.SOUTH, lblHomeLocation);
		springLayout.putConstraint(SpringLayout.WEST, lblHomeLat, 5, SpringLayout.WEST, this);
		this.add(lblHomeLat);
		
		JTextField txtHomeLat = new JTextField("51.00000");
		springLayout.putConstraint(SpringLayout.NORTH, txtHomeLat, 5, SpringLayout.SOUTH, lblHomeLocation);
		springLayout.putConstraint(SpringLayout.WEST, txtHomeLat, 5, SpringLayout.EAST, lblHomeLat);
		springLayout.putConstraint(SpringLayout.EAST, txtHomeLat, 95, SpringLayout.EAST, lblHomeLat);

		this.add(txtHomeLat);
		
		JLabel lblHomeLong = new JLabel("Long: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblHomeLong, 5, SpringLayout.SOUTH, lblHomeLocation);
		springLayout.putConstraint(SpringLayout.WEST, lblHomeLong, 10, SpringLayout.EAST, txtHomeLat);
		this.add(lblHomeLong);
		
		JTextField txtHomeLong = new JTextField("0.00000");
		springLayout.putConstraint(SpringLayout.NORTH, txtHomeLong, 5, SpringLayout.SOUTH, lblHomeLocation);
		springLayout.putConstraint(SpringLayout.WEST, txtHomeLong, 5, SpringLayout.EAST, lblHomeLong);
		springLayout.putConstraint(SpringLayout.EAST, txtHomeLong, 95, SpringLayout.EAST, lblHomeLong);

		this.add(txtHomeLong);
		
		JButton btnSaveExit = new JButton("Save & Exit");
		springLayout.putConstraint(SpringLayout.SOUTH, btnSaveExit, -5, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnSaveExit, 5, SpringLayout.WEST, this);		
		setVisible(true);
		
		this.add(btnSaveExit);
		
		JButton btnCancel = new JButton("Cancel");
		springLayout.putConstraint(SpringLayout.SOUTH, btnCancel, -5, SpringLayout.SOUTH, this.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnCancel, 10, SpringLayout.EAST, btnSaveExit);	
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispatchEvent(new WindowEvent(SettingsWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		setVisible(true);
		
		this.add(btnCancel);
	}
}
