package surface;


	import com.ardor3d.math.Matrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
	import com.ardor3d.scenegraph.shape.Quad;

import file.Savable;
	public abstract class Surface extends Quad implements Savable{
	    
	    /** The width of the surface (x direction for floor/forward, z direction for right)*/
	    public double width;

	    /** The height of the surface (y direction for forwards/right, z direction for floor)*/
	    public double height;
	    
	    /** The orientation */
	    public Orientation orientation;

	    /**
		 * Instantiates a new piece.
		 * 
		 * @param w
		 *            the width
		 * @param h
		 *            the height
		 * @param pos
		 *            the center of the surface
		 * @param a
		 *            the orientation
		 */
	    public Surface(final double w, final double h, final ReadOnlyVector3 pos, final Orientation a) {
	        super("", w, h);
	        width = w;
	        height = h;
	        orientation = a;
	        final Matrix3 rotate = new Matrix3();
	        switch (a) {
	            case Floor:
	                rotate.fromAngles(Math.PI / 2, 0, 0);
	                break;
	            case Forward:
	                rotate.fromAngles(0, 0, Math.PI / 2);
	                break;
	            case Right:
	                rotate.fromAngles(0, Math.PI / 2, 0);
	                break;
	        }
	        setRotation(rotate);
	        setTranslation(pos);
	    }
	    /**
	     * Returns the center position of the surface
	     * @return
	     */
	    public ReadOnlyVector3 getPosition(){
	    	return getTranslation();
	    }

	    /**
	     * Sets the center position of the surface
	     * @return
	     */
	    public void setPosition(ReadOnlyVector3 vec){
	    	setTranslation(vec);
	    }
		/**
		 * Returns the orientation of the surface
		 */
		public Orientation getOrientation() {
			return orientation;
		}
		/**
		 * Sets the orientation of the surface
		 * @param ori the orientation to set to
		 */
		public void setOrientation(Orientation ori){
	        final Matrix3 rotate = new Matrix3();
	        switch (orientation = ori) {
	            case Floor:
	                rotate.fromAngles(Math.PI / 2, 0, 0);
	                break;
	            case Forward:
	                rotate.fromAngles(0, 0, Math.PI / 2);
	                break;
	            case Right:
	                rotate.fromAngles(0, Math.PI / 2, 0);
	                break;
	        }
	        setRotation(rotate);
		}
		/**
		 * The Writable String form of an orientation
		 * @param a
		 *            the orientation
		 * @return the writable string
		 */
		public static String toString(Orientation a){
	        switch (a) {
	            case Floor:
	            	return "Floor";
	            case Forward:
	            	return "Forward";
	            case Right:
	            	return "Right";
	        }
	        throw new RuntimeException("Illegal Orientation: "+a);
		}
}
