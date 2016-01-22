package backbone;

import java.util.LinkedList;

import state.Playing;

import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.image.util.ScreenShotImageExporter;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.screen.ScreenExporter;

/**
 * The Class for updating all geometry and links to all native methods/classes
 */
public class TDCanvas implements Runnable, Updater, Scene{

	/** The screen shot exporter */
	protected ScreenShotImageExporter _screenShotExp = new ScreenShotImageExporter();
    
    /** The light state. */
    protected LightState _lightState;

    /**
     * Initializes all lights and rendering utils
     */
    public void init() {
    	//_canvas.setIcon(Imaging.getIconImages());
        /**
         * Create a ZBuffer to display pixels closest to the camera above farther ones.
         */
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        center.setRenderState(buf);

        // ---- LIGHTS
        /** Set up a basic, default light. */
        light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.90f, 0.90f, 0.90f, 0.85f));
        light.setAmbient(new ColorRGBA(0.85f, 0.85f, 0.85f, 1.0f));
        light.setLocation(new Vector3(0, 0, 0));
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        _lightState = new LightState();
        _lightState.setEnabled(true);
        _lightState.attach(light);
        center.setRenderState(_lightState);

        center.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);


    }
    
    /** The main node containing everything to be displayed as its child, or recursive child */
    public static Node center = new Node();

    /** The main light */
    protected PointLight light;

    /** The list of bullets to be added (for concurrency issues) */
    public static LinkedList<Spatial> toadd = new LinkedList<Spatial>();

    public PickResults doPick(final Ray3 pickRay) {
    	return null;
    }

    protected void processPicks(final PrimitivePickResults pickResults) {
    }

    /** Boolean denoting whether or not to take a screenshot */
    public static volatile boolean _doShot = false;
	
	/** The list of bullets to remove */
	public static LinkedList<Spatial> toremove = new LinkedList<Spatial>();
    
    /**
     * Renders the screen onto the native canvas
     * Also take picture if necessary
     */
    public boolean renderUnto(final Renderer renderer) {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
        
       GameTaskQueueManager.getManager(Main._canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER)
                .execute(renderer);

        ContextGarbageCollector.doRuntimeCleanup(renderer);
        	try{
                center.onDraw(renderer);
            	}
            	catch(Exception e){e.printStackTrace();}
        	try{
        		if(Main.state!=null)
           Main.state.render(renderer);}
        	catch(Throwable t){t.printStackTrace();}
        	  if (_doShot) {
                  renderer.renderBuckets();
                  ScreenExporter.exportCurrentScreen(Main._canvas.getCanvasRenderer().getRenderer(), _screenShotExp);
                  _doShot = false;
              }
            return true;
        } 

    /**
     * Update the frame if playing the game
     */
    public void run() {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
        
            while (!Main._exit) 
                try {
            	if(Main.state == Main.playing)
            {
                Playing._frameHandler.updateFrame();
                Thread.yield();
            }
        } catch (final Throwable t) {
        	//t.printStackTrace();
        }
    }
    
    /**
     * Update the geomoetry of the world
     */
    public void update(final ReadOnlyTimer timer) {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
        
    		try{
        GameTaskQueueManager.getManager(Main._canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE)
                .execute();
        center.updateGeometricState(timer.getTimePerFrame(), true);
        if(Main.state!=null)
        Main.state.update(timer);
    	}
    	catch(Exception e){
    		//e.printStackTrace();
    	}
    }
}