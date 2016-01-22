
package surface;

import portal.Portalable;

import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Imaging;
/**
 * The default surface that portals can be created onto
 */
public class Piece extends Surface implements Portalable{
   /**
	 * Instantiates a new piece.
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
    public Piece(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
        super(w, h, pos, a);
        setRenderState(Imaging.background);
    }
    /**
     * Returns the writable string representation of this piece
	 */
	public String toWritableString() {
		return "{ Piece "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
}
