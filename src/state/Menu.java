package state;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import backbone.*;
import sound.SoundPlayer;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ReadOnlyTimer;

/**
 * The Main Menu.
 */
@SuppressWarnings("serial")
public class Menu extends JComponent implements State{


	public void update() {
		requestFocusInWindow();
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
		if(!sp.isPlaying())
		sp.play();
		Main.mainPanel.add(this, BorderLayout.CENTER);
		requestFocusInWindow();
		inited = true;
	}

	public void close() {
		inited = false;
		Main.mainPanel.remove(this);
	}
	
	/** The selection index onscreen */
	static int selection = 0;
	
	/**
	 * paints correctly selected image to screen
	 */
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(Imaging.menuItems[selection], 0, 0, getWidth(), getHeight(), null);
	}
	
	/**
	 * Selects a state to move to
	 */
	public static void select(){
		switch(selection){
		case 0:
			Main.state.close();
			sp.pause();
			Main.state = Main.playing;
			Main.state.init();
			return;
		case 1:
			Main.state.close();
			Main.state = Main.load;
			Main.state.init();
			return;
		case 2:
			Main.state.close();
			Main.state = Main.settings;
			Main.state.init();
			return;
		case 3:
			Main.state.close();
			Main.state = Main.build;
			Main.state.init();
			return;
		}
	}
	
	/**
	 * Finishes playing a sound and starts the other
	 * 
	 * @param s
	 *            the s
	 */
	@SuppressWarnings("deprecation")
	public static void finished(SoundPlayer s){
		if(s == sp){
			SoundPlayer old = s;
			String n = sp.name;
			sp = other;
			sp.play();
			other = new SoundPlayer(n, false);
			try{
				old.mt.interrupt();
			old.mt.stop();
			}
			catch(Exception e){}
		}
	}
	
	/** The current sound */
	public static SoundPlayer sp = new SoundPlayer("/music/wantyougon.mp3", false);
	
	/** The other sound. */
	static SoundPlayer other = new SoundPlayer("/music/stillAlive.mp3", false);
	/**
	 * choose a sound
	 */
	static{
		if(Math.random()<.5){
			SoundPlayer t = sp;
			sp = other;
			other = t;
		}
	}
	
	/**
	 * Instantiates a new menu.
	 * adds key listeners/mouse listeners
	 */
	public Menu(){
		setFocusable(true);

		addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e){
				switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					selection=(selection+3)%4;
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					selection=(selection+1)%4;
					break;
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_ENTER:
					select();
					break;
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
				}}});
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1)
					select();
			}
		});
		addMouseMotionListener(new MouseAdapter(){
			@Override
			public void mouseMoved(MouseEvent e){
				double relx = (double)e.getX()/Constants.drawX;
				double rely = (double)e.getY()/Constants.drawY;
				if(rely>0.863350785 || rely<0.148046875)
					return;
				if(relx>0.100390625 && relx<0.547851563){
					if(rely<0.555061082){
						if(rely<0.42591623){
							selection = 0;
						}
						else{
							selection = 1;
						}
					}
					else{
						if(rely<0.682460733){
							selection = 2;
						}
						else{
							selection = 3;
						}
					}
				}
			}
		});
	}
}
