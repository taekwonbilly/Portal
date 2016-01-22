
package surface;

import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Imaging;
/**
 * A surface for which upon collision with (standing on top of) will immediately catapult the user in a specified direction
 */
public class FaithPlate extends Surface{
	/**
	 * The direction for the user to be catapulted in
	 */
	public ReadOnlyVector3 jump;
   /**
	 * Instantiates a new faith plate
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
    public FaithPlate(final double w, final double h, final ReadOnlyVector3 pos, final ReadOnlyVector3 jump, final Orientation a) {
        super(w, h, pos, a);
        this.jump = jump;
        setRenderState(Imaging.plate);
    }
    /**
     * Returns the writable string representation of this faith plate
	 */
	public String toWritableString() {
		return "{ FaithPlate "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
}

