package state;

import backbone.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.Timer;

import org.lwjgl.LWJGLException;


import portal.*;
import sound.*;
import surface.Piece;
import surface.Platform;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.lwjgl.LwjglAwtCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.*;
import com.ardor3d.util.ReadOnlyTimer;

/**
 * The Playing state (where user is actually playing game)
 */
public class Playing implements State{
	
	/** The list comment sounds to play. */
	public static volatile LinkedList<String> comments = new LinkedList<String>();
	
	/** The current comment sound being played. */
	static volatile SoundPlayer current = null;
	
	/** The background music */
	static WavPlayer pla = new WavPlayer("/music/music.wav");
	
	/** The timer to update sounds*/
	static Timer tim;
	
	/**
	 * if you win and there's another test, do next test, otherwise move to win box, if no next test or winbox exists, exit
	 */
	public static void win(){
		for(int i = 0; i<Loader.levels.length-1; i++)
			if(Playing.cur.equals(Loader.levels[i])){
		Main.playing.close();
		Playing.cur = Loader.levels[i+1];
		Main.playing.init();
		return;
			}
		System.exit(0);
//		System.exit(0);
//		System.out.println("back2");              
//		Main.state.close();
//		Main.state = Main.menu;
//		Main.state.init();
	}
	
	/**
	 * Gets the urls contained by the directory path
	 * 
	 * @param path
	 *            the path
	 * @return the subs
	 */
	public static LinkedList<URL> getSubs(String path){
		java.net.URLConnection con = null;
		URL url = Playing.class.getResource(path);
		try {
			con = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			con.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		java.io.BufferedReader in = null;
		try {
			in = new java.io.BufferedReader
			(new java.io.InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		LinkedList<URL> urls = new LinkedList<URL>();
		String line = null;
		try {
			for (; (line = in.readLine()) != null; ) {
				urls.add(Playing.class.getResource(path+line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urls;
	}

	/**
	 * Gets the string files contained by the directory path
	 * 
	 * @param path
	 *            the path
	 * @return the subs string
	 */
	public static LinkedList<String> getSubsString(String path){
		java.net.URLConnection con = null;
		URL url = Playing.class.getResource(path);
		try {
			con = url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			con.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		java.io.BufferedReader in = null;
		try {
			in = new java.io.BufferedReader
			(new java.io.InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		LinkedList<String> urls = new LinkedList<String>();
		String line = null;
		try {
			for (; (line = in.readLine()) != null; ) {
				urls.add((path+line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return urls;
	}
/**
 * add all dounds
 */
	static{
		for(String url:getSubsString("/music/annon/"))
			comments.add(url);
		for(String url:getSubsString("/music/glad/"))
			comments.add(url);
		for(String url:getSubsString("/music/quote/"))
				comments.add(url);
		Collections.shuffle(comments);//KNUTH SHUFFLE XD
		comments.addFirst(("/music/intro.mp3"));
		tim = new Timer(40000, new ActionListener(){public void actionPerformed(ActionEvent e){
		if(!comments.isEmpty() && Main.state == Main.playing && inited){
			try{
				String s = comments.pollFirst();
				current = new SoundPlayer(s);
				current.play();
			}catch(Exception ea){
				ea.printStackTrace();
			}
		}
		}});
		}
	
	
	/**
	 * renders the portal effect
	 *
	 */
	public void render(Renderer renderer){
		 if (Constants.blue != null && Constants.orange != null) {
             if (!Constants.blue.inited) {
                 Constants.blue.initRtt(renderer);
             }
             if (!Constants.orange.inited) {
                 Constants.orange.initRtt(renderer);
             }
             final Vector3 toBlue = Constants.blue.position.subtract(Main._canvas.getCanvasRenderer().getCamera().getLocation(), null);
             Constants.blue.textureRenderer.getCamera().setLocation(Constants.orange.position);
             Vector3 forwards = Constants.orange.toInertia(Constants.blue.fromInertia(toBlue));
             //Vector3 hi = 
             // TODO fix frustam for realism
             //Constants.blue.textureRenderer.getCamera().setLocation(Constants.orange.position.subtract(forwards, null));
             Constants.blue.textureRenderer.getCamera().setLocation(Constants.orange.position);
             Constants.blue.textureRenderer.getCamera().lookAt(forwards.normalizeLocal().addLocal(Constants.orange.position),
                     Constants._worldUp);

             final Vector3 toOrange = Constants.orange.position.subtract(Main._canvas.getCanvasRenderer().getCamera().getLocation(), null);
             forwards = Constants.blue.toInertia(Constants.orange.fromInertia(toOrange));

             // TODO fix frustam for realism
             //Constants.orange.textureRenderer.getCamera().setLocation(Constants.blue.position.subtract(forwards, null));
             Constants.orange.textureRenderer.getCamera().setLocation(Constants.blue.position);
             Constants.orange.textureRenderer.getCamera().lookAt(forwards.addLocal(Constants.blue.position),
                     Constants._worldUp);
   
             Constants.blue.textureRenderer.render(_root, Constants.blue.fakeTex, Renderer.BUFFER_COLOR_AND_DEPTH);

             Constants.orange.textureRenderer.render(_root, Constants.orange.fakeTex, Renderer.BUFFER_COLOR_AND_DEPTH);
		 }
	}

    /**
     * updates player movement from intertia (gravity), runs into things
     * update bullets
     * update portals
     * update anything else that needs
     */
    public void update() {
    	Main._canvas.requestFocusInWindow();
        
        while (!TDCanvas.toadd.isEmpty()) {
            bullets.attachChild(TDCanvas.toadd.poll());
        }
        while (!TDCanvas.toremove.isEmpty()) {
            bullets.detachChild(TDCanvas.toremove.poll());
        }
        if (Constants.inertia.getY() - Constants.gravity >= Constants.TERMINAL) {
            Constants.inertia.setY(Constants.inertia.getY() - Constants.gravity);
        } else if (Constants.inertia.getY() + Constants.gravity <= Constants.TERMINAL) {
            Constants.inertia.setY(Constants.inertia.getY() + Constants.gravity);
        } else {
            Constants.inertia.setY(Constants.TERMINAL);
        }
        Constants.inertia = Main._controlHandle.move(Main._canvas.getCanvasRenderer().getCamera(), Constants.inertia, true);
for (final ListIterator<Spatial> it = chamber.getChildren().listIterator(); it.hasNext();) {
            final Spatial r = it.next();
            if(r instanceof Platform){
            	((Platform)r).update();
            }
        }
        for (final ListIterator<Spatial> it = bullets.getChildren().listIterator(); it.hasNext();) {
            final Spatial r = it.next();
            if (r instanceof Bullet) {
                final Bullet b = (Bullet) r;
                final Piece e = (Piece) b.move();
                if (e != null) {
                    it.remove();
                    if (b.type) {
                        if (Constants.blue != null) {
                            portals.detachChild(Constants.blue);
                        }
                        portals.attachChild(Constants.blue = new Portal(e, new Ray3(b.position, b.motion)));
                    } else {
                        if (Constants.orange != null) {
                            portals.detachChild(Constants.orange);
                        }
                        portals.attachChild(Constants.orange = new Portal(e, new Ray3(b.position, b.motion), false));
                    }
                }
            }
        }
    }
    
    /** The node containing all data */
    public static Node _root = new Node();

/** The Constant default map */
final static String def = "/maps/default.map";

/** The current map */
static String cur = def;

/** The inited. */
static volatile boolean inited = false;

/** The native timer. */
public static com.ardor3d.util.Timer _timer;

/** The native frame handler. */
public static FrameHandler _frameHandler;
	
	/**
	 * Initalizes native frames if not started
	 * resets position/chamber
	 */
	public void init(){
		if(!inited()){
		Main.main = new TDCanvas();
		Main.main.init();
        Main.canvasRenderer = new LwjglCanvasRenderer(Main.main) ;      
       try {
			Main._canvas = new LwjglAwtCanvas();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
       _timer = new com.ardor3d.util.Timer();
       _frameHandler = new FrameHandler(_timer);
        _frameHandler.addUpdater(Main.main);
        _frameHandler.addCanvas(Main._canvas);
        _frameHandler.init();
		Main.frame.add(Main._canvas);
		Main._canvas.requestFocusInWindow();
       TDCanvas.center.attachChild(_root);
       if(comments.size()>0){
			String s = comments.pollFirst();
			current = new SoundPlayer(s);
			current.play();
       }
       tim.start();
		pla.loop();
		
	}
        _root.attachChild(portals);
        _root.attachChild(bullets);
		_root.attachChild(chamber = new TestChamber(cur));
        Main._canvas.getCanvasRenderer().getCamera().setLocation(new Vector3(0, Constants.height + 15.1, 0));
        Main._canvas.getCanvasRenderer().getCamera().setDirection(new Vector3(0, 0, -1));
        Main._canvas.getCanvasRenderer().getCamera().setLeft(new Vector3(-1, 0, 0));
        Main._canvas.getCanvasRenderer().getCamera().setUp(new Vector3(0, 1, 0));
        Constants.inertia.multiplyLocal(0);
        inited = true;
        WavPlayer.door.play();
	}
	
	/**
	 * Resets the user
	 */
	public static void reset(){
		WavPlayer.burn.play();
		portals.detachAllChildren();
		bullets.detachAllChildren();
		Constants.blue = null;
		Constants.orange = null;
		Main._canvas.getCanvasRenderer().getCamera().setLocation(new Vector3(0, Constants.height + 15.1, 0));
        Main._canvas.getCanvasRenderer().getCamera().setDirection(new Vector3(0, 0, -1));
        Main._canvas.getCanvasRenderer().getCamera().setLeft(new Vector3(-1, 0, 0));
        Main._canvas.getCanvasRenderer().getCamera().setUp(new Vector3(0, 1, 0));
       
	}
	
	public boolean inited(){
		return inited;
	}
	
	/**
	 * Destroys the current chamber
	 */
	public void close(){
	//	inited = false;
		//current = null;
      //  chamber = null;
		portals.detachAllChildren();
		bullets.detachAllChildren();
		Constants.blue = null;
		Constants.orange = null;
		_root.detachAllChildren();
		Main._canvas.getCanvasRenderer().getCamera().setLocation(new Vector3(0, Constants.height + 15.1, 0));
		Main._canvas.getCanvasRenderer().getCamera().setDirection(new Vector3(0, 0, -1));
        //TDCanvas.center.detachChild(_root);
	}
	
	/** The current chamber. */
	public static TestChamber chamber;

	public void update(ReadOnlyTimer timer){
        Main._controlHandle.move(Main._canvas.getCanvasRenderer().getCamera(), timer.getTimePerFrame());
	}

	/** The Constant bullets. */
	public final static Node bullets = new Node();
	
	/** The Constant portals. */
	public final static Node portals = new Node();

	/**
	 * kills the player
	 */
	public static void die() {
		reset();
	}
}
