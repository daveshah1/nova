package gui;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.JTextArea;

import java.awt.TextArea;

import javax.swing.JTextPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;

import javax.swing.Box;

import java.awt.FlowLayout;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;


public class Main {

	private JFrame frame;
    private JMapViewer map_2;
    
    private VirtualRover rover;
    private MapMarkerDot targetPos;
    private MapMarkerDot actualPos;
    private ScheduledExecutorService updater;
	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	

	
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100,100,1280,739);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Nova | Mission Control");
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 300, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(panel);
		
		JTextArea txtrDebugArea = new JTextArea();
		txtrDebugArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		txtrDebugArea.setForeground(Color.GREEN);
		txtrDebugArea.setBackground(Color.BLACK);
		txtrDebugArea.setEditable(false);
		txtrDebugArea.setText("Debug Area\nPlaceholder");
		panel.add(txtrDebugArea);
		
		Component horizontalStrut = Box.createHorizontalStrut(10);
		springLayout.putConstraint(SpringLayout.NORTH, horizontalStrut, 0, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, horizontalStrut, 0, SpringLayout.EAST, panel);
		frame.getContentPane().add(horizontalStrut);
		
		JPanel panel_1 = new JPanel();
		panel_1.setToolTipText("Video Here");
		panel_1.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.NORTH, panel_1, 0, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, panel_1, 0, SpringLayout.EAST, horizontalStrut);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_1, 480, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, panel_1, 640, SpringLayout.EAST, horizontalStrut);
		frame.getContentPane().add(panel_1);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		springLayout.putConstraint(SpringLayout.NORTH, verticalStrut, 0, SpringLayout.SOUTH, panel_1);
		springLayout.putConstraint(SpringLayout.WEST, verticalStrut, 0, SpringLayout.WEST, panel_1);
		springLayout.putConstraint(SpringLayout.SOUTH, verticalStrut, 10, SpringLayout.SOUTH, panel_1);
		frame.getContentPane().add(verticalStrut);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.NORTH, panel_2, 0, SpringLayout.SOUTH, verticalStrut);
		springLayout.putConstraint(SpringLayout.WEST, panel_2, 10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_2, 0, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, panel_2, 0, SpringLayout.EAST, panel_1);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnControl = new JButton("Control 1");
		panel_2.add(btnControl);
		
		JButton btnSelfDestruct = new JButton("Self Destruct");
		btnSelfDestruct.setToolTipText("Boom!");
		btnSelfDestruct.setBackground(Color.RED);
		btnSelfDestruct.setForeground(Color.WHITE);
		panel_2.add(btnSelfDestruct);
		
		JButton btnControl_1 = new JButton("Control 2");
		panel_2.add(btnControl_1);
		
		map_2 = new JMapViewer();
		//System.err.println(System.getProperty("user.home").replace("\\","/") + "/mapcache/");
		if(!Files.exists(Paths.get(System.getProperty("user.home")+"/mapcache/17"))) {
			JOptionPane.showMessageDialog(frame, "Could not find downloaded maps. Mapping will be unavailable\nPlease read 'README-maps.txt'.");
		}
		rover = new VirtualRover(new Position(51.487556,-0.2381855));
		map_2.setTileSource(new OfflineOsmTileSource("file:///" + System.getProperty("user.home").replace("\\","/") + "/mapcache/",14,18));
		map_2.setDisplayPositionByLatLon(51.487556,-0.2381855, 16);
		map_2.addMouseListener(new MapClickHandler(rover));
		springLayout.putConstraint(SpringLayout.NORTH, map_2, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, map_2, 10, SpringLayout.EAST, panel_1);
		springLayout.putConstraint(SpringLayout.SOUTH, map_2, 300, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, map_2, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add( map_2);
		
		Runnable roverUpdater = new Runnable() {
		    public void run() {
		        rover.updatePosition();
		       /* try {
		        	map_2.removeMapMarker(actualPos);
		        	map_2.removeMapMarker(targetPos);
		        } finally {
		        	
		        };
		        actualPos = new MapMarkerDot(Color.green
		        		, rover.currentPosition.getLat()
		        		, rover.currentPosition.getLon());
		        map_2.addMapMarker(actualPos);
		        targetPos = new MapMarkerDot(Color.red
		        		, rover.targetPosition.getLat()
		        		, rover.targetPosition.getLon());
		        map_2.addMapMarker(targetPos); */
		        System.err.println( rover.targetPosition.getLat() + "," + rover.currentPosition.getLon());
		    }
		};
		
		
		
		rover.attachListener(new RoverUpdateListener() {
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
				
			}
			
		});
		
		//Schedule automated updating of the simulated rover.
		updater = Executors.newScheduledThreadPool(1);
		updater.scheduleAtFixedRate(roverUpdater, 0, 100, TimeUnit.MILLISECONDS);
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.NORTH, panel_3, 10, SpringLayout.SOUTH, map_2);
		springLayout.putConstraint(SpringLayout.WEST, panel_3, 10, SpringLayout.EAST, panel_1);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_3, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel_3, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lblRawDataHere = new JLabel("Raw Data Here");
		lblRawDataHere.setBounds(110, 11, 73, 14);
		panel_3.add(lblRawDataHere);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 319, 294, 62);
		panel_3.add(panel_4);
		
		JCheckBox chckbxData = new JCheckBox("Data 1");
		panel_4.add(chckbxData);
		
		JCheckBox chckbxData_1 = new JCheckBox("Data 2");
		panel_4.add(chckbxData_1);
		
		JCheckBox chckbxData_2 = new JCheckBox("Data 3");
		panel_4.add(chckbxData_2);
		
		JCheckBox chckbxData_3 = new JCheckBox("Data 4");
		panel_4.add(chckbxData_3);
		
		JButton btnConvertIntoDem = new JButton("Generate graphs!");
		panel_4.add(btnConvertIntoDem);
		


		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
}

//Pony
