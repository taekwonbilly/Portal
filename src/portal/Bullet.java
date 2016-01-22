package portal;

import backbone.Imaging;
import backbone.Main;
import sound.WavPlayer;
import state.*;
import surface.Platform;
import math.RayOps;
import backbone.*;

import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Sphere;
import com.ardor3d.util.export.Savable;

/**
 * The Class made to hold inside of it the ability to create a portal
 */
public class Bullet extends Sphere implements Savable{
    
    /** The position. */
    public Vector3 position;
    
    /** The motion. */
    public Vector3 motion;
    
    /** The radius. */
    public double radius;

    /** The portal type, if applicable (blue/orange) */
    public boolean type;

    /** The type of bullet (creates portal/other) */
    public boolean portaled;
    
    /**
	 * Instantiates a new bullet.
	 * 
	 * @param r
	 *            the radius
	 * @param pos
	 *            the position
	 */
    public Bullet(final double r, final ReadOnlyVector3 pos) {
    	this(r, pos, new Vector3(Main._canvas.getCanvasRenderer().getCamera().getDirection().multiply(10, null)), false, true);
    }
    
    /**
	 * Instantiates a new bullet.
	 * 
	 * @param r
	 *            the radius
	 * @param pos
	 *            the position
	 * @param motion
	 *            the motion of the bullet
	 * @param ty
	 *            the type
	 * @param po
	 *            the portal type
	 */
    public Bullet(final double r, final ReadOnlyVector3 pos, final ReadOnlyVector3 motion, boolean ty, boolean po){
    	super("", 10, 10, r);
    	radius = r;
    	type = ty;
        setTranslation(position = new Vector3(pos));
        this.motion = new Vector3(motion);
   //     setRenderState(Imaging.bullet);
        portaled = po;
    }
    
    /**
	 * To writable string.
	 * 
	 * @return the string
	 */
    public String toWritableString(){
    	return "{ Bullet "+radius+" "+position.toWritableString()+" "+motion.toWritableString()+" "+type+" "+portaled+" }";
    }
    
    /**
	 * Instantiates a new bullet.
	 * 
	 * @param r
	 *            the radius
	 * @param type
	 *            the type of bullet
	 */
    public Bullet(final Ray3 r, boolean type) {
        this(.2, r.getOrigin(), new Vector3(r.getDirection()).normalizeLocal().multiplyLocal(Main._canvas.getCanvasRenderer().getCamera().getDirection().length()*10), false, true);
        this.type = type;
        if(type){
        	WavPlayer.sBlue.play();
        	setRenderState((Imaging.blue));
        }
        else{
        	WavPlayer.sRed.play();
        	setRenderState((Imaging.orange));
        }
    }
    
    /**
	 * Instantiates a new bullet.
	 * 
	 * @param type
	 *            the type of bullet
	 */
    public Bullet(boolean type) {
    	this(.2, Main._canvas.getCanvasRenderer().getCamera().getLocation());
    	this.type = type;
        if(type){
        	WavPlayer.sBlue.play();
        	setRenderState((Imaging.blue));
        }
        else{
        	WavPlayer.sRed.play();
        	setRenderState((Imaging.orange));
        }
    }
    
    /** The boolean denoting death of the bullet. */
    public boolean dead = false;
    
    /**
	 * Moves the bullet, kills it if neccessary, or returns the portalable surface if it will create a portal
	 * 
	 * @return the portalable
	 */
    public Portalable move() {
    	if(dead)
    		return null;
        // final boolean t = false;
    	Spatial closest = null;
    	double close = Double.POSITIVE_INFINITY;
        final Ray3 r = new Ray3(position, motion);
        for (final Spatial p : Playing.chamber.getChildren()) {
        	double temp;
            if (p instanceof Mesh && (temp = RayOps.intersects((Mesh)p, r, false))<close)
            	{
            	close = temp;
            	closest = p;
            	}
            else if (p instanceof Platform && (temp = RayOps.intersects((Mesh)((Platform)p).self, r, false))<close)
        	{
            	close = temp;
            	closest = ((Platform)p).self;
        	}
        }
        if(closest!=null){
        	if(closest instanceof Portalable){
        		return (Portalable)closest;
        	}
        	else{
            	dead = true;
            	TDCanvas.toremove.add(this);
            	return null;
        	}
        }
        position.add(motion, position);
        setTranslation(position);
        return null;
    }
}
