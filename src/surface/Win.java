package surface;

import backbone.Imaging;

import com.ardor3d.math.type.ReadOnlyVector3;

public class Win extends Piece {

	public Win(double w, double h, ReadOnlyVector3 pos, Orientation a) {
		super(w, h, pos, a);
		setRenderState(Imaging.winTS);
	}

}
