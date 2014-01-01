package gui;


import gui.NetworkRover.MoveOperation;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JPanel;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;


public class Main {

	private JFrame frame;
    private CustomMap map_2;
    
    private NetworkRover rover;
    private ScheduledExecutorService updater;
    @SuppressWarnings("unused")
	private LargeMap m;
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
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(0,0,1280,739);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Nova | Mission Control");
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		/*JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 300, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(panel);*/
		
		/*JTextArea txtrDebugArea = new JTextArea();
		txtrDebugArea.setFont(new Font("Lucida Console", Font.PLAIN, 12));
		txtrDebugArea.setForeground(Color.GREEN);
		txtrDebugArea.setBackground(Color.BLACK);
		txtrDebugArea.setEditable(false);
		txtrDebugArea.setText("Debug Area\nPlaceholder");*/
		//final Console console = new Console();
		
		//Current console data should be in raw, not debug - change null in line below back to as appropriate later.
		JScrollPane panel = new JScrollPane(null,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 300, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(panel);
		//panel.add(consoleContainer);

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
		//springLayout.putConstraint(SpringLayout.SOUTH, panel_2, 0, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, panel_2, 0, SpringLayout.EAST, panel_1);
		frame.getContentPane().add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnUp = new JButton("Forwards");
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rover.moveManually(MoveOperation.FORWARDS);
			}
		});
		panel_2.add(btnUp);
		
		JPanel panel_2b = new JPanel();
		panel_2b.setBackground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.NORTH, panel_2b, 0, SpringLayout.SOUTH, panel_2);
		springLayout.putConstraint(SpringLayout.WEST, panel_2b, 10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.EAST, panel_2b, 0, SpringLayout.EAST, panel_2);
		frame.getContentPane().add(panel_2b);
		panel_2b.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		/*JButton btnSelfDestruct = new JButton("Self Destruct");
		btnSelfDestruct.setToolTipText("Boom!");
		btnSelfDestruct.setBackground(Color.RED);
		btnSelfDestruct.setForeground(Color.WHITE);
		panel_2.add(btnSelfDestruct);*/
		JButton btnLeft = new JButton("Turn Left");
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rover.moveManually(MoveOperation.LEFT_TURN);
			}
		});	
		panel_2b.add(btnLeft);

		JButton btnStop = new JButton(" STOP ");
		btnStop.setBackground(Color.RED);
		btnStop.setForeground(Color.WHITE);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rover.targetPosition = new Position(rover.currentPosition);
				rover.atTargetPosition = true;
			}
		});		
		panel_2b.add(btnStop);
		
		JButton btnRight = new JButton("Turn Right");
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rover.moveManually(MoveOperation.RIGHT_TURN);
			}
		});
		panel_2b.add(btnRight);
		
		JPanel panel_2c = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel_2c, 0, SpringLayout.SOUTH, panel_2b);
		springLayout.putConstraint(SpringLayout.WEST, panel_2c, 10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_2c, 33, SpringLayout.SOUTH, panel_2b);
		springLayout.putConstraint(SpringLayout.EAST, panel_2c, 0, SpringLayout.EAST, panel_2);
		panel_2c.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_2c);
		panel_2c.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		 
		JButton btnDown = new JButton("Backwards");
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rover.moveManually(MoveOperation.BACKWARDS);
			}
		});
		panel_2c.add(btnDown);
		//Warn the user if downloaded maps cannot be found
		if(!Files.exists(Paths.get(System.getProperty("user.home")+"/mapcache/17"))) {
			JOptionPane.showMessageDialog(frame, "Could not find downloaded maps. Mapping will be unavailable\nPlease read 'README-maps.txt'.");
		}
		final Console console = new Console();
		rover = new NetworkRover();
		rover.attachListener(console);
		map_2 = new CustomMap(rover);
		springLayout.putConstraint(SpringLayout.NORTH, map_2, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, map_2, 10, SpringLayout.EAST, panel_1);
		springLayout.putConstraint(SpringLayout.SOUTH, map_2, 300, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, map_2, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add( map_2);
		
		Runnable roverUpdater = new Runnable() {
		    public void run() {
		        rover.update();
		    }
		};
		
	
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
		
		SpringLayout sl_panel_3 = new SpringLayout();
		panel_3.setLayout(sl_panel_3);
		JLabel lblRawDataHere = new JLabel("Raw Data Here");
		sl_panel_3.putConstraint(SpringLayout.NORTH, lblRawDataHere, 11, SpringLayout.NORTH, panel_3);
		sl_panel_3.putConstraint(SpringLayout.WEST, lblRawDataHere, 110, SpringLayout.WEST, panel_3);
		panel_3.add(lblRawDataHere);
		
		JPanel panel_4 = new JPanel();
		sl_panel_3.putConstraint(SpringLayout.NORTH, panel_4, 319, SpringLayout.NORTH, panel_3);
		sl_panel_3.putConstraint(SpringLayout.WEST, panel_4, 0, SpringLayout.WEST, panel_3);
		sl_panel_3.putConstraint(SpringLayout.SOUTH, panel_4, 381, SpringLayout.NORTH, panel_3);
		sl_panel_3.putConstraint(SpringLayout.EAST, panel_4, 294, SpringLayout.WEST, panel_3);
		panel_3.add(panel_4);
		
		JCheckBox chckbxData = new JCheckBox("Data 1");
		panel_4.add(chckbxData);
		
		JCheckBox chckbxData_1 = new JCheckBox("Data 2");
		panel_4.add(chckbxData_1);
		
		JCheckBox chckbxData_2 = new JCheckBox("Data 3");
		panel_4.add(chckbxData_2);
		
		JCheckBox chckbxData_3 = new JCheckBox("Data 4");
		panel_4.add(chckbxData_3);
		
		JButton btnConvertIntoDem = new JButton("Open Large Map");
		btnConvertIntoDem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				m = new LargeMap(rover);
				System.err.println("Click!");
			}
			
		});
		panel_4.add(btnConvertIntoDem);
		
		JButton btnCreateGraphs = new JButton("Create Graphs");
		panel_4.add(btnCreateGraphs);
		
		JPanel panel_2d = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel_2d, 0, SpringLayout.SOUTH, panel_2c);
		springLayout.putConstraint(SpringLayout.WEST, panel_2d, 10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_2d, 33, SpringLayout.SOUTH, panel_2c);

		JScrollPane scrollPane = new JScrollPane(console,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sl_panel_3.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, lblRawDataHere);
		sl_panel_3.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, panel_4);
		sl_panel_3.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, panel_4);
		sl_panel_3.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, panel_3);
		panel_3.add(scrollPane);
		panel_2d.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_2d);
		
		JLabel lblDebugControls = new JLabel("Debug Controls:");
		panel_2d.add(lblDebugControls);
		
		JButton btnStart = new JButton("Start");
		panel_2d.add(btnStart);
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				console.startDebug();
			}
		});
		
		JButton btnPause = new JButton("Pause");
		panel_2d.add(btnPause);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				console.pauseDebug();
			}
		});
		
		JLabel lblVideoControls = new JLabel("Video Controls:");
		panel_2d.add(lblVideoControls);
		
		JButton btnStartRecord = new JButton("Start Record");
		panel_2d.add(btnStartRecord);
		
		JButton btnStopRecord = new JButton("Stop Record");
		panel_2d.add(btnStopRecord);
		
		//final NetworkSender ns = new NetworkSender("127.0.0.1"); //For testing only, change to real IP later
		
		JPanel panel_2e = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, panel_2e, 0, SpringLayout.SOUTH, panel_2d);
		springLayout.putConstraint(SpringLayout.WEST, panel_2e, 10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel_2e, 33, SpringLayout.SOUTH, panel_2d);
		panel_2e.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_2e);
		JLabel lblNetwork = new JLabel("Network:");
		panel_2e.add(lblNetwork);
		
		JButton btnNetworkStart = new JButton("Start");
		panel_2e.add(btnNetworkStart);
		btnNetworkStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rover.begin("127.0.0.1");
			}
		});
		JButton btnNetworkTest = new JButton("Test");
		panel_2e.add(btnNetworkTest);
		btnNetworkTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NetworkStatus status = rover.testNetwork();
				if(status == NetworkStatus.OK) {
					JOptionPane.showMessageDialog(null,"Network OK!");
				} else {
					JOptionPane.showMessageDialog(null,"Network error: " + status.toString());
				}
			}
		});
		JButton btnNetworkStop = new JButton("Stop");
		panel_2e.add(btnNetworkStop);
		btnNetworkStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rover.disconnect();
			}
		});
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
}

//Pony
