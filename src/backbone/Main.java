
package backbone;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;

import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.renderer.*;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;
import state.*;

/**
 * The Main Class
 * Handles much of the GUI components
 */
public class Main {
    
    /** The frame. */
    public static JFrame frame = new JFrame("70Three Laboratories");
    
    /** The main panel. */
    public static JPanel mainPanel = new JPanel();
    
    /**Initializes the frame*/
    static{
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setContentPane(mainPanel);
        mainPanel.setFocusable(true);
        mainPanel.setLayout(new BorderLayout());
    	if(Constants.fullscreen){
        	frame.setSize(Constants.windowX, Constants.windowY);
    		frame.setUndecorated(true);
            mainPanel.setPreferredSize(new Dimension(Constants.windowX, Constants.windowY));
    	}
    	else{
        	frame.setSize(Constants.drawX, Constants.drawY);
            mainPanel.setPreferredSize(new Dimension(Constants.drawX, Constants.drawY));
    	}
    }
   /** The canvas renderer. */
  public static LwjglCanvasRenderer canvasRenderer;
    
    /** The main native scene */
    public static TDCanvas main;
    
    /**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
    public static void main(final String[] args) {
    	
         main = new TDCanvas();
         Main._controlHandle = new Controls();
         
         canvasRenderer = new LwjglCanvasRenderer(main) ;      
        try {
			_canvas = new LwjglAwtCanvas();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
        Imaging.load();
        frame.setIconImage(Imaging.icon);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImage = Imaging.cursor;
        Point cursorHotSpot = new Point(16,16);
        Cursor customCursor = toolkit.createCustomCursor(cursorImage, cursorHotSpot, "Cursor");
        frame.setCursor(customCursor);
        main.init();
      
       TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());

        frame.setVisible(true);
        if(state!=null)
        state.init();
        new javax.swing.Timer(10, new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
            	//constantly repaint/update the frame and all components
        		Main.frame.repaint();
        		Main.mainPanel.repaint();
        		//update to only include any additions, if necessary
        		Main.frame.validate();
            	if(state!=null && state.inited())
                state.update();
            }
        }).start();
        new Thread(main).start();
    }
   
   /** The native canvas. */
   public static LwjglAwtCanvas _canvas;

    /** The controls (input/movement) */
    public static Controls _controlHandle;

    /** The main menu. */
    public static Menu menu = new Menu();
    
    /** The playing menu. */
    public static Playing playing = new Playing();
    
    /** The extras menu. */
    public static MapBuilder build = new MapBuilder();
    
    /** The settings menu. */
    public static Settings settings = new Settings();
    
    /** The loading menu. */
    public static Loader load = new Loader();
    
    /** The current menu. */
    public static volatile State state = menu;
    /**
     * Boolean denoting wheter the program has ended or not
     */
    public static volatile boolean _exit = false;
}