package backbone;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.ardor3d.image.Texture;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;

/**
 * A class for loading/handling all images/textures
 */
public class Imaging {
	/**
	 * The texturestate for the Bounce surface
	 */
	public static TextureState bounce;
	
	/** The texturestate for a piece */
	public static TextureState background;

	/** The texturestate for an orange portal/bullet */
	public static TextureState orange;

	/** The texturestate for an blue portal/bullet */
	public static TextureState blue;

	/** The texturestate for the killer surface */
	public static TextureState killer;

	/** The texturestate for the exit surface */
	public static TextureState exit;

	/** The texturestate for the aerial faith plate surface */	
	public static TextureState plate;

	/** The texturestate for the winning surface */	
	public static TextureState winTS;

	/** The texturestate for the pkiller surface */	
	public static TextureState lava;

	/** A hashmap of textures for fast lookup */
	static HashMap<String, Texture> textures = new HashMap<String, Texture>();
	
	//Menus
	
/** An array of images for the main menu, where each slot represents a selected index in the menu */
public static BufferedImage[] menuItems = new BufferedImage[4];

/**The image for the settings menu*/
public static BufferedImage settings;
/**The image for the extras menu*/
public static BufferedImage extras;
/**The image for the loading menu*/
public static BufferedImage loading;


//Previews	
/** The preview for the aerial map*/
	public static BufferedImage aerial;
	/** The preview for the def map*/
	public static BufferedImage def;
	/** The preview for the blitz map*/
	public static BufferedImage blitz;
	/** The preview for the first map*/
	public static BufferedImage first;
	/** The preview for the tunnel map*/
	public static BufferedImage tunnel;
	/** The preview for the kplatform map*/
	public static BufferedImage kplatform;
	/** The preview for the blueness map*/
	public static BufferedImage blueness;
	/** The preview for the intheloop map*/
	public static BufferedImage intheloop;
	/** The preview for the finale map*/
	public static BufferedImage finale;
	/** The preview for an incomplete map*/
	public static BufferedImage test1;
	
	/** The image for the portal cursor*/
	public static BufferedImage cursor;
	
	/**The image for the icon for the application (at bottom of screen/top left of JFrame if not fullscreen)*/
	public static BufferedImage icon;
		
	/**
	 * Loads all images/textures
	 */
	public static void load(){
 try {
            SimpleResourceLocator srl = new SimpleResourceLocator(ResourceLocatorTool.getClassPathResource(Main.class,
                    "images/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }

 AWTImageLoader.registerLoader();
		background = getState("703lab.png");
		blue = getState("Solid_blue.png");
	        orange = getState("Orange.jpg");
	        killer = getState("not.png");
	        bounce = getState("bounce.jpg");
	        lava = getState("lava.jpg");
	        exit = getState("exit.png");
	        winTS = getState("WINNER.jpg");

	       plate = getState("faithplate.png");
	        
	        def = getBImage("previews/default.png");
	        first= getBImage("previews/first.png");
	        blitz = getBImage("previews/blitz.png");
	        tunnel = getBImage("previews/tunnel.png");
	        aerial = getBImage("previews/aerial.png");
	        kplatform = getBImage("previews/killerplatform.png");
	        blueness = getBImage("previews/blueness.png");
	        intheloop = getBImage("previews/intheloop.png");
	        finale = getBImage("previews/final.png");
	        
	        loading = getBImage("stars.png");
	        settings= getBImage("instructions.png");
	        extras = getBImage("Credits.png");
	        test1 = getBImage("test1.png");
	        cursor = getBImage("mouse.png");
	        menuItems[0] = getBImage("0.png");
	        menuItems[1] = getBImage("1.png");
	        menuItems[2] = getBImage("2.png");
	        menuItems[3] = getBImage("3.png");
	        icon = getBImage("icon128.png");
	    }

    /**
	 * Gets the BufferedImage from a string
	 * 
	 * @param s
	 *            the Filepath of the image
	 * @return the BufferedImage
	 */
    public static BufferedImage getBImage(String s){
    	return getBImage(Imaging.class.getResource("/images/"+s));
    }

    /**
	 * Gets the BufferedImage from a URL
	 * 
	 * @param u
	 *            the Filepath of the image
	 * @return the BufferedImage
	 */
    public static BufferedImage getBImage(URL u){
    	try {
			return ImageIO.read(u);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Gets the texturestate of an image from a string
     * @param s the string filepath
     * @return the texturestate
     */
    public static TextureState getState(String s){
    	return getState(getTexture(s));
    }
    /**
	 * Gets the texturestate of an image from a texture
	 * 
	 * @param the texture
	 * @return the texturestate
	 */
    private static TextureState getState(Texture s){
    //	if
        final TextureState ts = new TextureState();
        ts.setTexture((s));
        return ts;
    }
    
    /**
	 * Gets a texture from a string
	 * 
	 * @param s
	 *            the filepath of a texture
	 * @return the texture
	 */
    public static Texture getTexture(String s){
    	if(!textures.containsKey(s))
    	{
    		Texture t = TextureManager.load(s, Texture.MinificationFilter.Trilinear, true);
    		textures.put(s, t);
    		return t;
    	}
    	return textures.get(s);
    }
}
