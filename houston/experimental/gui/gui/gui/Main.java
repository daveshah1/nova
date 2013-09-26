package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.JTextPane;
import java.awt.TextArea;
import javax.swing.JLabel;
import java.awt.Font;

public class Main {

	private JFrame frmNovaMission;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmNovaMission.setVisible(true);
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
		frmNovaMission = new JFrame();
		frmNovaMission.setTitle("Nova | Mission Control");
		frmNovaMission.setBounds(100, 100, 1024, 600);
		//frmNovaMission.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frmNovaMission.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmNovaMission.getContentPane().setLayout(springLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		springLayout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, frmNovaMission.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, frmNovaMission.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, frmNovaMission.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, panel, 300, SpringLayout.WEST, frmNovaMission.getContentPane());
		frmNovaMission.getContentPane().add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		JLabel lblDebug = new JLabel("Debug");
		sl_panel.putConstraint(SpringLayout.NORTH, lblDebug, 5, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.EAST, lblDebug, -118, SpringLayout.EAST, panel);
		lblDebug.setFont(new Font("Gill Sans MT", Font.PLAIN, 15));
		panel.add(lblDebug);
	}
}
