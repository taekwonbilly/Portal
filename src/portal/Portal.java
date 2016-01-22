
package portal;

import math.*;
import backbone.*;

import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRenderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Disk;
import com.ardor3d.scenegraph.shape.Torus;

import file.Savable;
import surface.Orientation;
import surface.Surface;

/**
 * The Portal class, by entering a portal, your velocity and angle of entry are conserved and you exit through the other portal, if it exists,
 * see attached text files for more info
 */
public class Portal extends Node implements Savable {
	/** The orientation of the portal */
  public Orientation ori;
    
    /** The position of the portal */
    public final Vector3 position;
    
    /** The disk where the image of the other portal will be overlayed */
    public Disk disk;
    
    /** The torus surrounding the disk */
    Torus tor;
    
    /** The type of portal */
    public boolean type;
    
    /**
     * Returns the .map representation of this
     */
    public String toWritableString(){
    	return "{ Portal "+position.toWritableString()+Surface.toString(ori)+" "+negate+" "+type+" }";
    }
    
    /**
	 * Instantiates a new portal.
	 * 
	 * @param pos
	 *            the position
	 * @param o
	 *            the orientation
	 * @param neg
	 *            the negation (what side the portal is facing front, back)
	 * @param d
	 *            the type of portal
	 */
    public Portal(final Vector3 pos, final Orientation o, final boolean neg, final boolean d) {
        disk = new Disk("", 10, 10, 10);
        tor = new Torus("", 10, 10, 1, 10);
        ori = o;
        position = pos;
        negate = neg;
        type = d;
        final Matrix3 rotate = new Matrix3();
        switch (getOrientation()) {
            case Floor:
                if (negate) {
                    rotate.fromAngles(-Math.PI / 2, 0, 0);
                    // rotate.fromAngles(Math.PI / 2, 0, Math.PI);
                } else {
                    rotate.fromAngles(Math.PI / 2, 0, 0);
                }
                break;
            case Forward:
                if (negate) {
                    rotate.fromAngles(0, 0, 0);
                } else {
                    rotate.fromAngles(Math.PI, 0, Math.PI);
                }
                break;
            case Right:
                if (negate) {
                    rotate.fromAngles(0, Math.PI / 2, 0);
                } else {
                    rotate.fromAngles(0, -Math.PI / 2, 0);
                }
                break;
        }
        disk.setRotation(rotate);
        tor.setRotation(rotate);
        disk.setTranslation(position);
        tor.setTranslation(position);

        if(type){
        disk.setRenderState((Imaging.blue));
        tor.setRenderState((Imaging.blue));
        }
        else{
            tor.setRenderState((Imaging.orange));
            disk.setRenderState((Imaging.orange));
        }
        disk.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        attachChild(disk);
        attachChild(tor);
    }

    /**
	 * Renders the other portal on top of this
	 * 
	 * @param renderer
	 *            the renderer
	 */
    public void initRtt(final Renderer renderer) {
        inited = true;

        textureRenderer = TextureRendererFactory.INSTANCE.createTextureRenderer(Constants.settings, false, renderer,
                ContextManager.getCurrentContext().getCapabilities());

            textureRenderer.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1));
            fakeTex = new Texture2D();
            textureRenderer.setupTexture(fakeTex);
            final TextureState screen = new TextureState();
            screen.setTexture(fakeTex);
            screen.setEnabled(true);
            disk.setRenderState(screen);
    }

    
    /**
	 * Instantiates a new portal.
	 * 
	 * @param p
	 *            the portalable hit
	 * @param start
	 *            the starting position/direction
	 * @param a
	 *            a flag, extra argument to avoid confusion, does nothing
	 */
    public Portal(final Portalable p, final Ray3 start, final boolean a) {
        this(p, start);
        tor.setRenderState((Imaging.orange));
        disk.setRenderState((Imaging.orange));
        type = false;
    }
    
    /**
	 * Gets the orientation.
	 * 
	 * @return the orientation
	 */
    public Orientation getOrientation(){
    	return ori;
    }
    
    /**
	 * Gets the forward vector relative to this portal
	 * 
	 * @return the forward vector
	 */
    public Vector3 getForwardVector() {
        switch (getOrientation()) {
            case Floor:
                if (negate) {
                    return new Vector3(0, 1, 0);
                } else {
                    return new Vector3(0, -1, 0);
                }
            case Forward:
                if (negate) {
                    return new Vector3(0, 0, 1);
                } else {
                    return new Vector3(0, 0, -1);
                }
            case Right:
                if (negate) {
                    return new Vector3(1, 0, 0);
                } else {
                    return new Vector3(-1, 0, 0);
                }
        }
        return null;
    }

    /**
	 * Gets the left vector relative to this portal 
	 * @return the left vector
	 */
    public Vector3 getLeftVector() {
        switch (getOrientation()) {
            case Floor:
                if (negate) {
                    return new Vector3(-1, 0, 0);
                } else {
                    return new Vector3(1, 0, 0);
                }
            case Forward:
                if (negate) {
                    return new Vector3(1, 0, 0);
                } else {
                    return new Vector3(-1, 0, 0);
                }
            case Right:
                if (negate) {
                    return new Vector3(0, 0, -1);
                } else {
                    return new Vector3(0, 0, 1);
                }
        }
        return null;
    }

    /**
	 * Gets the up vector relative to this portal
	 * 
	 * @return the up vector
	 */
    public Vector3 getUpVector() {
        switch (getOrientation()) {
            case Floor:
                if (negate) {
                    return new Vector3(0, 0, 1);
                } else {
                    return new Vector3(0, 0, 1);
                }
            case Forward:
                if (negate) {
                    return new Vector3(0, 1, 0);
                } else {
                    return new Vector3(0, 1, 0);
                }
            case Right:
                if (negate) {
                    return new Vector3(0, 1, 0);
                } else {
                    return new Vector3(0, 1, 0);
                }
        }
        return null;
    }

    /** The boolean dictating whether or not the orientation is negated (front/back) */
    public boolean negate;
    
    /** The count of times this portal rendered to avoid an infinite recusion */
    public int count = 0;

    /**
	 * Takes a vector relative to this portal and decomposes it to a standard form
	 * 
	 * @param inert
	 *            the relative vector
	 * @return the standard vector
	 */
    public Vector3 fromInertia(final ReadOnlyVector3 inert) {
        double forw = 0, left = 0, up = 0;
        switch (getOrientation()) {
            case Floor:
                forw = -inert.getY() / getForwardVector().getY();
                left = -inert.getX() / getLeftVector().getX();
                up = inert.getZ() / getUpVector().getZ();
                break;
            case Forward:
                forw = -inert.getZ() / getForwardVector().getZ();
                left = -inert.getX() / getLeftVector().getX();
                up = inert.getY() / getUpVector().getY();
                break;
            case Right:
                forw = -inert.getX() / getForwardVector().getX();
                left = -inert.getZ() / getLeftVector().getZ();
                up = inert.getY() / getUpVector().getY();
                break;
        }
        return new Vector3(forw, left, up);
    }

    /**
	 * In form forward, left, up;.
	 * Takes a decomposed vector and returns the correct vector, relative to this vector
	 * @param stand
	 *            the standard decomposed vector
	 * @return the vector3 relative to this portal
	 */
    public Vector3 toInertia(final Vector3 stand) {
        return getForwardVector().multiplyLocal(stand.getX()).addLocal(getLeftVector().multiplyLocal(stand.getY()))
                .addLocal(getUpVector().multiplyLocal(stand.getZ()));
    }

    /** The texture renderer. */
    public TextureRenderer textureRenderer;
    
    /** The texture showing the view from the other portal */
    public Texture2D fakeTex;
    
    /** The boolean designating whether the texture renderer has inialized*/
    public boolean inited = false;

    /**
	 * Instantiates a new portal.
	 * 
	 * @param p
	 *            the portalable reference
	 * @param ray
	 *            the ray (position/direction)
	 */
    public Portal(final Portalable p, final Ray3 ray) {
        disk = new Disk("", 10, 10, 10);
        tor = new Torus("", 10, 10, 1, 10);
        ori = p.getOrientation();
        type = true;
        final Matrix3 rotate = new Matrix3();
        switch (p.getOrientation()) {
            case Floor:
                negate = ray.getDirection().getY() < 0;
                if (negate) {
                    rotate.fromAngles(-Math.PI / 2, 0, 0);
                    // rotate.fromAngles(Math.PI / 2, 0, Math.PI);
                } else {
                    rotate.fromAngles(Math.PI / 2, 0, 0);
                }
                break;
            case Forward:
                negate = ray.getDirection().getZ() < 0;
                if (negate) {
                    rotate.fromAngles(0, 0, 0);
                } else {
                    rotate.fromAngles(Math.PI, 0, Math.PI);
                }
                break;
            case Right:
                negate = ray.getDirection().getX() < 0;
                if (negate) {
                    rotate.fromAngles(0, Math.PI / 2, 0);
                } else {
                    rotate.fromAngles(0, -Math.PI / 2, 0);
                }
                break;
        }
        disk.setRotation(rotate);
        tor.setRotation(rotate);
        final double hi = RayOps.alongD((Mesh)p, ray).poll() - .01;
        position = ray.getOrigin().add(ray.getDirection().multiply(hi, null), null);
        disk.setTranslation(position);
        tor.setTranslation(position);

        disk.setRenderState((Imaging.blue));
        tor.setRenderState((Imaging.blue));
        disk.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        attachChild(disk);
        attachChild(tor);
    }
}
