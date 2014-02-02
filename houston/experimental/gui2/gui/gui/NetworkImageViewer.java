package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class NetworkImageViewer extends JPanel {
	private static final long serialVersionUID = -5555045450327984237L;
	private SettingsStore settings = new SettingsStore();
	private BufferedImage image = null;
	private Timer t;
	//private boolean running = false;
	public NetworkImageViewer() {
		super();
	}

	public NetworkImageViewer(LayoutManager arg0) {
		super(arg0);
	}

	public NetworkImageViewer(boolean arg0) {
		super(arg0);
	}

	public NetworkImageViewer(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
	}

	public void startDisplay() {
		t = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				updateImage();
				
			}
		};
		t.scheduleAtFixedRate(task, 0, 2000);
	}
	
	public void stopDisplay() {
		t.cancel();
	}
	
	public void updateImage() {
		BufferedImage downloadedImage;
		try {
			downloadedImage = ImageIO.read(new URL("http://" + settings.get("rover.ip") + "/image.png"));
				image = downloadedImage;
				//Redraw new image
				super.repaint(); 
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, 640, 480, null);
		}
		g.setColor(new Color(255,0,0,100));
		g.fillRect(295, 239, 15, 2);
		g.fillRect(330, 239, 15, 2);
		g.fillRect(319, 215, 2, 15);
		g.fillRect(319, 250, 2, 15);
	}
}
