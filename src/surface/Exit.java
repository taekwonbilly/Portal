
package surface;

import com.ardor3d.math.type.ReadOnlyVector3;

import backbone.Constants;
import backbone.Imaging;
/**
 * A surface for which upon collision with will immediately complete the level.
 * There is only allowed to be one of these per level, since only the last created
 * exit will result in completion
 */
public class Exit extends Surface{
   /**
	 * Instantiates a new exit.
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
    public Exit(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
        super(w, h, pos, a);
        Constants.exit = this;
        setRenderState(Imaging.exit);
    }
    /**
     * Returns the writable string representation of this exit
	 */
	public String toWritableString() {
		return "{ Exit "+width+" "+height+" "+getPosition().toWritableString()+" "+toString(getOrientation())+" }";
	}
}
