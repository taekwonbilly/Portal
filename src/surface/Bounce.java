
package surface;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Imaging;
/**
 * A surface for which upon collision with will immediately re-orient the intertial vector or cause it to "bounce off"
 */
public class Bounce extends Surface{
   /**
	 * Instantiates a new bounce.
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
    public Bounce(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
        super(w, h, pos, a);
        setRenderState(Imaging.bounce);
    }
    /**
     * Returns the writable string representation of this bounce
	 */
	public String toWritableString() {
		return "{ Bounce "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
	/**
	 * Sets and reflects a vector about this bounce.
	 * @param a the vector to be reflected
	 * @return the reflected vector
	 */
	public Vector3 reflect(Vector3 a){
		switch(getOrientation()){
        case Floor:
            return a.set(a.getX(), -a.getY(), a.getZ());
        case Forward:
            return a.set(a.getX(), a.getY(), -a.getZ());
        case Right:
            return a.set(-a.getX(), a.getY(), a.getZ());
		}
		throw new RuntimeException("Illegal Orientation");
	}
}
