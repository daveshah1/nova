package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class Main {

	private JFrame frmNovaControlPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmNovaControlPanel.setVisible(true);
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
		frmNovaControlPanel = new JFrame();
		frmNovaControlPanel.setTitle("Nova Control Panel");
		frmNovaControlPanel.setBounds(100, 100, 1280, 800);
		frmNovaControlPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new BorderLayout() );
		frmNovaControlPanel.getContentPane().add( topPanel );		
		
		createPage1();
		createPage2();
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmNovaControlPanel.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab( "Main", panel1 );
		tabbedPane.addTab( "Debug", panel2 );
		topPanel.add( tabbedPane, BorderLayout.CENTER );		
	}
	
	public void createPage1() {
	}
	
	public void createPage2(){
	}
}
