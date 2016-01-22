package surface;

/**
 * The orientation of a surface, used to speedily calculate the euleran angles of rotation
 *
 */
public enum Orientation {
	/**
	 * The tile always to infront of or behind the user if facing (0, +/-, 0)
	 * Plane representation has the form of x = constant
	 */
	Right,
	/**
	 * The tile always above/under the user
	 * Plane representation has the form of y = constant
	 */
	Floor,
	/**
	 * The tile always to infront of or behind the user if facing (0, 0, +/-1)
	 * Plane representation has the form of z = constant
	 */
	Forward

	/**
	 * The Writable String form 
	 * @param a
	 *            the orientation
	 * @return the string
	 */
}
