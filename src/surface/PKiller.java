
package surface;

import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Imaging;
/**
 * The default surface that kills the player, it also does not allow portals to be created on it
 */
public class PKiller extends Surface{
   /**
	 * Instantiates a new pkiller.
	 * 
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 * @param pos
	 *            the position
	 * @param a
	 *            the orientation
	 */
    public PKiller(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
        super(w, h, pos, a);
        setRenderState(Imaging.lava);
    }
    /**
     * Returns the writable string representation of this pkiller
	 */
	public String toWritableString() {
		return "{ PKiller "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
}
