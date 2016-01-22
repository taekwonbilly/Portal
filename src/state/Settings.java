package state;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import backbone.*;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ReadOnlyTimer;

/**
 * The Settings Menu
 */
@SuppressWarnings("serial")
public class Settings extends JComponent implements State{
	public void update() {
	}
	public void update(ReadOnlyTimer timer) {
	}
	public void render(Renderer renderer) {
	}
	
	/** The inited. */
	boolean inited = false;
	
	public boolean inited(){
		return inited;
	}
	
	public void init() {
		Main.mainPanel.add(this, BorderLayout.CENTER);
		Main.mainPanel.repaint();
		requestFocusInWindow();
		inited = true;
	}
	
	/**
	 * paints the settings image to the window
	 */
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(Imaging.settings, 0, 0, getWidth(), getHeight(), null);
	}
	
	public void close() {
		inited = false;
		Main.mainPanel.remove(this);
	}
	
	/**
	 * Instantiates a new settings.
	 * adds key controls
	 */
	public Settings(){
		setFocusable(true);

		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				switch(e.getKeyCode()){
				case KeyEvent.VK_ESCAPE:                
					Main.state.close();
					Main.state = Main.menu;
					Main.state.init();
				}}});

	}
}
