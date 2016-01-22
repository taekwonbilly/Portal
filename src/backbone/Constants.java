package backbone;

import java.awt.Dimension;
import java.awt.Toolkit;

import portal.Portal;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.math.Vector3;

/**
 * Constants for the game
 */
public class Constants {
	
	/** The Constant TERMINAL Velocity. */
	public final static double TERMINAL = -5;
    
    /** The Constant height that the player remains above blocks. */
    public final static double height = 2;
    
    /** The Constant universal upvector */
    public final static Vector3 _worldUp = new Vector3(0, 1, 0);
    
    /** The Constant gravity. */
    public final static double gravity = .03;
    
    /** The inertia. */
    public static Vector3 inertia = new Vector3(0, 0, 0);
    
    /** The Constant for the change in velocity due to a jump*/
    public final static double jump = 1;
    
    /** The blue portal */
    public static Portal blue = null;
    
    /** The orange portal*/
    public static Portal orange = null;
    
    /** The exit gate */
    public static surface.Exit exit = null;
    
    /** A boolean for designating whether or not fullscreen (used to debug) */
    public static boolean fullscreen = true;
      
      /** The Constant for the screenSize of the monitor */
      public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      
      /** The Constant for the width of the window */
      public static final int windowX = screenSize.width;
      /** The Constant for the height of the window */
      public static final int windowY = screenSize.height;
      
      /** The Constant for a default screen width if not fullscreen*/
      public static final int defaultX = 1080;
      
      /** The Constant for a default screen height if not fullscreen*/
      public static final int defaultY = 720;
      
      /** The Constant designating the actual width of the screen */
      public static final int drawX;
      /** The Constant designating the actual height of the screen */
      public static final int drawY;
      /**
       * Determines screen size
       */
      static{
      	if(fullscreen){
      		drawX = windowX;
      		drawY = windowY;
      	}
      	else{
      		drawX = defaultX;
      		drawY = defaultY;
      	}
      }
      
      /** The settings for the native OpenGl renderer -- based on previous constants */
      public static DisplaySettings settings = new DisplaySettings(drawX, drawY, 16, 60, 0, 8, 0, 0, fullscreen, false);
    
}
