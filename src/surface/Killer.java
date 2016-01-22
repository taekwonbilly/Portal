
package surface;

import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Imaging;
/**
 * The default surface that portals cannot be created onto
 */
public class Killer extends Surface{
   /**
	 * Instantiates a new killer.
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
    public Killer(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
        super(w, h, pos, a);
        setRenderState(Imaging.killer);
    }
    /**
     * Returns the writable string representation of this killer
	 */
	public String toWritableString() {
		return "{ Killer "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
}
