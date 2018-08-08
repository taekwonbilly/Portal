package file;

import com.ardor3d.math.type.ReadOnlyVector3;

public class WritableUtils {
    public static String toWritableString(ReadOnlyVector3 v3) {
        return "{ Vector3 " + v3.getX() + " " + v3.getY() + " " + v3.getZ() + "}";
    }
}
