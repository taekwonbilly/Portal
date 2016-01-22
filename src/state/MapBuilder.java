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
 * The Extras Menu
 */
@SuppressWarnings("serial")
public class MapBuilder extends JComponent implements State{

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
	
/**
 * Initializes
 */
	public void init() {
		Main.mainPanel.add(this, BorderLayout.CENTER);
		Main.mainPanel.repaint();
		requestFocusInWindow();
		inited = true;
	}
	
	/**
	 * Paints extras image
	 */
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(Imaging.extras, 0, 0, getWidth(), getHeight(), null);
	}
	
	/**
	 * Closes
	 */
	public void close() {
		inited = false;
		Main.mainPanel.remove(this);
	}
	
	/**
	 * Instantiates a new map builder.
	 * adds key controls
	 */
	public MapBuilder(){
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
