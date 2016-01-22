package state;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ReadOnlyTimer;

import backbone.*;

/**
 * The Menu for loading a specific chamber
 */
@SuppressWarnings("serial")
public class Loader extends JPanel implements State{
	
	/**
	 * paints the loading picture
	 */
	public void paintComponent(Graphics g){
		g.drawImage(Imaging.loading, 0, 0, getWidth(), getHeight(), null);
	}
	/**
	 * A list of all levels in sequential order
	 */
	public static String[] levels = new String[]{Playing.def,
		"/maps/first.map",
		"/maps/tunnel.map",
		"/maps/blitz.map",
		"/maps/ariel.map",
		"/maps/killerplatform.map",
		"/maps/blueness.map",
		"/maps/intheloop.map",
		"/maps/final.map",
		"/maps/win.map"
	};
	/**
	 * initializes all components (buttons, listeners, etc)
	 */
   	public void init() {

			Main.mainPanel.add(this, BorderLayout.CENTER);
			Main.mainPanel.repaint();
			requestFocusInWindow();
	        init = true;
	    }
	    
    	/** The temporary variables. */
    	int r, c;
	    
    	/** The number of rows. */
    	public static int rows = 3;
    	/** The number of columns. */
    	public static int cols = 3;
	    
    	/** The buttons. */
    	static JButton[] buttons = new JButton[rows*cols];
        
        /** The chamber number. */
        static int cham = 0;
	    
    	/**
		 * Instantiates a new loader.
		 * adds key countrols/components
		 */
    	public Loader() {
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
	        
	    	cham = 0;
	        setLayout(new GridLayout(1, 2));
	        JPanel temp = new JPanel();
	        temp.setOpaque(false);
	        JPanel panel = new JPanel();
	        panel.setLayout(new GridLayout(3, 3));
	        add(panel);
	        add(temp);
	    	for (r = 0; r < rows; r++) {
	            JButton button = null;
	            for (c = 0; c < cols; c++) {

	                button = new JButton(""){
	                	public final int num = r*cols+c;
	                	public void paintComponent(Graphics g){
	                		if(num == 0)
	                			g.drawImage(Imaging.def, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 1)
	                			g.drawImage(Imaging.first, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 2)
	                			g.drawImage(Imaging.tunnel, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 3)
	                			g.drawImage(Imaging.blitz, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 4)
	                			g.drawImage(Imaging.aerial, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 5)
	                			g.drawImage(Imaging.kplatform, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 6)
	                			g.drawImage(Imaging.blueness, 0, 0, getWidth(), getHeight(), null);
	                		else if (num == 7)
	                			g.drawImage(Imaging.intheloop, 0, 0, getWidth(), getHeight(), null);
	                		else
	                			g.drawImage(Imaging.finale, 0, 0, getWidth(), getHeight(), null);
	                		}
	                };
	                buttons[r*cols+c] = button;
	                button.addActionListener(
	                		new ActionListener(){
	                	final int num = cham;
	                	public void actionPerformed(ActionEvent e){
	                		Main.state.close();
	    	                Menu.sp.pause();
	                		Main.state = Main.playing;
		                		Playing.cur = Playing.def;
		                		if(num<levels.length)
		                			Playing.cur = levels[num];
	                		Main.state.init();
	                	}
	                });
	                panel.add(button);
	                cham++;
	            }
	        }
	    }


public void update(final ReadOnlyTimer timer) {
	    }
    	public void render(final Renderer renderer) {
	    }
	public void update() {
	}
	
	public void close() {
		init = false;
		Main.mainPanel.remove(this);
	}
	
	/** boolean denoting if inited. */
	public boolean init = false;
	
	public boolean inited() {
		return init;
	}

}
