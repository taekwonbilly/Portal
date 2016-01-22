package portal;

/**
 * Represents a spatial that can have a portal created on top of it
 */
public interface Portalable {	
	
	/**
	 * Gets the orientation to create the portal onto.
	 * 
	 * @return the orientation
	 */
	public surface.Orientation getOrientation();
}
