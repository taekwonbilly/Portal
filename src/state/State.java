package state;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.ReadOnlyTimer;

/**
 * The Interface State (menu or playing)
 */
public interface State {
	
	/**
	 * Update math
	 */
	public abstract void update();
	
	/**
	 * Updates geometry
	 * 
	 * @param t
	 *            the timer to keep constant despite varying frame rates
	 */
	public abstract void update(ReadOnlyTimer t);
	
	/**
	 * Renders to screen
	 * 
	 * @param renderer
	 *            the renderer
	 */
	public abstract void render(Renderer renderer);
	
	/**
	 * Initializes
	 */
	public abstract void init();
	
	/**
	 * Closes
	 */
	public abstract void close();
	
	/**
	 * returns it initialized
	 */
	public abstract boolean inited();
}
