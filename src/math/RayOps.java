package math;

import java.util.LinkedList;

import java.util.List;

import com.ardor3d.bounding.CollisionTree;
import com.ardor3d.bounding.CollisionTreeManager;
import com.ardor3d.intersection.PrimitiveKey;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Mesh;
import com.google.common.collect.Lists;
//Please note that this was from the opengl wrapper, but modified to suit the needs of this application

/**
 * The Class to handle math (ray operations)
 */
public class RayOps {

	/**
	 * Ray vs triangle implementation of whether a ray intersects a triangle, and if so, what is the amount the ray's vector must be multiplied by to reach it
	 * 
	 * @param ray
	 *            the ray
	 * @param pointA
	 *            the point a
	 * @param pointB
	 *            the point b
	 * @param pointC
	 *            the point c
	 * @param locationStore
	 *            the location store
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points.
	 */
    protected static double intersects(final Ray3 ray, final ReadOnlyVector3 pointA, final ReadOnlyVector3 pointB,
            final ReadOnlyVector3 pointC, final Vector3 locationStore) {
        final Vector3 diff = Vector3.fetchTempInstance().set(ray.getOrigin()).subtractLocal(pointA);
        final Vector3 edge1 = Vector3.fetchTempInstance().set(pointB).subtractLocal(pointA);
        final Vector3 edge2 = Vector3.fetchTempInstance().set(pointC).subtractLocal(pointA);
        final Vector3 norm = Vector3.fetchTempInstance().set(edge1).crossLocal(edge2);

        double dirDotNorm = ray.getDirection().dot(norm);
        double sign;
        if (dirDotNorm > MathUtils.EPSILON) {
            sign = 1.0;
        } else if (dirDotNorm < -MathUtils.EPSILON) {
            sign = -1.0;
            dirDotNorm = -dirDotNorm;
        } else {
            return Double.POSITIVE_INFINITY;
        }

        final double dirDotDiffxEdge2 = sign * ray.getDirection().dot(diff.cross(edge2, edge2));
        if (dirDotDiffxEdge2 >= 0.0) {
            final double dirDotEdge1xDiff = sign * ray.getDirection().dot(edge1.crossLocal(diff));
            if (dirDotEdge1xDiff >= 0.0) {
                if (dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm) {
                    final double diffDotNorm = -sign * diff.dot(norm);
                    if (diffDotNorm >= 0.0) {
                        // ray intersects triangle
                        // if storage vector is null, just return true,
                        if (locationStore == null) {
                            throw new RuntimeException("FAIL?");
                            // return true;
                        }
                        // else fill in.
                        final double inv = 1f / dirDotNorm;
                        final double t = diffDotNorm * inv;
                        Vector3.releaseTempInstance(diff);
                        Vector3.releaseTempInstance(edge1);
                        Vector3.releaseTempInstance(edge2);
                        Vector3.releaseTempInstance(norm);
                        return t;
                    }
                }
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
	 * Intersects.
	 * 
	 * @param ray
	 *            the ray
	 * @param polygonVertices
	 *            the polygon vertices
	 * @return the double
	 */
    public static double intersects(final Ray3 ray, final Vector3[] polygonVertices) {
        final Vector3 locationStore = Vector3.fetchTempInstance();
        if (polygonVertices.length == 3) {
            // TRIANGLE
            return intersectsTriangle(ray, polygonVertices[0], polygonVertices[1], polygonVertices[2], locationStore);
        } else if (polygonVertices.length == 4) {
            // QUAD
            return intersectsQuad(ray, polygonVertices[0], polygonVertices[1], polygonVertices[2], polygonVertices[3],
                    locationStore);
        }
        throw new RuntimeException("Illegal Size");
    }

    /**
	 * Intersects quad.
	 * 
	 * @param ray
	 *            the ray
	 * @param pointA
	 *            the point a
	 * @param pointB
	 *            the point b
	 * @param pointC
	 *            the point c
	 * @param pointD
	 *            the point d
	 * @param locationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this Vector3
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points. The points are assumed to be coplanar.
	 */
    public static double intersectsQuad(final Ray3 ray, final ReadOnlyVector3 pointA, final ReadOnlyVector3 pointB,
            final ReadOnlyVector3 pointC, final ReadOnlyVector3 pointD, final Vector3 locationStore) {
        return Math.min(intersects(ray, pointA, pointB, pointC, locationStore),
                intersects(ray, pointA, pointD, pointC, locationStore));
    }

    /**
	 * Intersects triangle.
	 * 
	 * @param ray
	 *            the ray
	 * @param pointA
	 *            the point a
	 * @param pointB
	 *            the point b
	 * @param pointC
	 *            the point c
	 * @param locationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this Vector3
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points.
	 */
    public static double intersectsTriangle(final Ray3 ray, final ReadOnlyVector3 pointA, final ReadOnlyVector3 pointB,
            final ReadOnlyVector3 pointC, final Vector3 locationStore) {
        return intersects(ray, pointA, pointB, pointC, locationStore);
    }

    /**
	 * Along d.
	 * 
	 * @param sp
	 *            the sp
	 * @param ray
	 *            the ray
	 * @return the linked list
	 */
    public static LinkedList<Double> alongD(final com.ardor3d.scenegraph.Mesh sp, final Ray3 ray) {
        final List<PrimitiveKey> primitives = Lists.newArrayList();
        final CollisionTree ct = CollisionTreeManager.getInstance().getCollisionTree(sp);
        if (ct != null) {
            ct.getBounds().transform(sp.getWorldTransform(), ct.getWorldBounds());
            ct.intersect(ray, primitives);
        }

        if (primitives.isEmpty()) {
            return new LinkedList<Double>();
        }

        Vector3[] vertices = null;
        final LinkedList<Double> hi = new LinkedList<Double>();
        for (int i = 0; i < primitives.size(); i++) {
            final PrimitiveKey key = primitives.get(i);
            vertices = sp.getMeshData().getPrimitiveVertices(key.getPrimitiveIndex(), key.getSection(), vertices);
            // convert to world coord space
            final int max = sp.getMeshData().getIndexMode(key.getSection()).getVertexCount();
            for (int j = 0; j < max; j++) {
                if (vertices[j] != null) {
                    sp.getWorldTransform().applyForward(vertices[j]);
                }
            }
            hi.add(intersects(ray, vertices));
        }
        return hi;
    }

    /**
	 * Along.
	 * 
	 * @param sp
	 *            the sp
	 * @param ray
	 *            the ray
	 * @return the linked list
	 */
    public static LinkedList<Vector3> along(final com.ardor3d.scenegraph.Mesh sp, final Ray3 ray) {
        final List<PrimitiveKey> primitives = Lists.newArrayList();
        final CollisionTree ct = CollisionTreeManager.getInstance().getCollisionTree(sp);
        if (ct != null) {
            ct.getBounds().transform(sp.getWorldTransform(), ct.getWorldBounds());
            ct.intersect(ray, primitives);
        }

        if (primitives.isEmpty()) {
            return new LinkedList<Vector3>();
        }

        Vector3[] vertices = null;
        final LinkedList<Vector3> hi = new LinkedList<Vector3>();
        for (int i = 0; i < primitives.size(); i++) {
            final PrimitiveKey key = primitives.get(i);
            vertices = sp.getMeshData().getPrimitiveVertices(key.getPrimitiveIndex(), key.getSection(), vertices);
            // convert to world coord space
            final int max = sp.getMeshData().getIndexMode(key.getSection()).getVertexCount();
            for (int j = 0; j < max; j++) {
                if (vertices[j] != null) {
                    sp.getWorldTransform().applyForward(vertices[j]);
                }
            }
            hi.add(ray.getOrigin().add(ray.getDirection().multiply(RayOps.intersects(ray, vertices), null), null));
        }
        return hi;
    }

    /**
	 * Intersects.
	 * 
	 * @param sp
	 *            the sp
	 * @param ray
	 *            the ray
	 * @param units
	 *            the units
	 * @return the double
	 */
    public static double intersects(final com.ardor3d.scenegraph.Mesh sp, final Ray3 ray, final boolean units) {
        final List<PrimitiveKey> primitives = Lists.newArrayList();
        final CollisionTree ct = CollisionTreeManager.getInstance().getCollisionTree(sp);
        if (ct != null) {
            ct.getBounds().transform(sp.getWorldTransform(), ct.getWorldBounds());
            ct.intersect(ray, primitives);
        }

        if (primitives.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }

        Vector3[] vertices = null;
        double amount = Double.POSITIVE_INFINITY;
        for (int i = 0; i < primitives.size(); i++) {
            final PrimitiveKey key = primitives.get(i);
            vertices = sp.getMeshData().getPrimitiveVertices(key.getPrimitiveIndex(), key.getSection(), vertices);
            // convert to world coord space
            final int max = sp.getMeshData().getIndexMode(key.getSection()).getVertexCount();
            for (int j = 0; j < max; j++) {
                if (vertices[j] != null) {
                    sp.getWorldTransform().applyForward(vertices[j]);
                }
            }
            final double triDistanceSq = RayOps.intersects(ray, vertices);
            if ((triDistanceSq < 1 && triDistanceSq > .00001)
                    || (units && ray.getOrigin().add(ray.getDirection().multiply(triDistanceSq, null), null)
                            .distanceSquared(ray.getOrigin().add(ray.getDirection(), null)) <= 4)
                            ) {
                if(amount == Double.POSITIVE_INFINITY || triDistanceSq<amount){
            		amount = triDistanceSq;
            	}
            }
        }
        return amount;
    }

    /**
	 * Min along.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param dir
	 *            the dir
	 * @return the double
	 */
    public static double minAlong(final Mesh start, final Mesh end, final ReadOnlyVector3 dir) {
    	Ray3 ray = new Ray3(start.getTransform().getTranslation(), dir);
        List<PrimitiveKey> primitives = Lists.newArrayList();
        CollisionTree ct = CollisionTreeManager.getInstance().getCollisionTree(start);
        if (ct != null) {
            ct.getBounds().transform(start.getWorldTransform(), ct.getWorldBounds());
            primitives = ct.intersect(ray, primitives);
        }

    //	if((start instanceof Cube) && (end instanceof Piece) && ((Piece)end).position.equals(new Vector3(0, 0, 0)))
    	//	System.out.println(primitives);

        if (primitives.isEmpty()) {
            return Double.POSITIVE_INFINITY;
        }

        Vector3[] vertices = null;
        double amount = Double.POSITIVE_INFINITY;
        for (int i = 0; i < primitives.size(); i++) {
            final PrimitiveKey key = primitives.get(i);
            vertices = start.getMeshData().getPrimitiveVertices(key.getPrimitiveIndex(), key.getSection(), vertices);
            // convert to world coord space
            final int max = start.getMeshData().getIndexMode(key.getSection()).getVertexCount();
            for (int j = 0; j < max; j++) {
                if (vertices[j] != null) {
                    start.getWorldTransform().applyForward(vertices[j]);
                }
            }
            final double triDistanceSq = RayOps.intersects(ray, vertices);
            if (triDistanceSq < 1 && triDistanceSq >=-1E-6) {
                if(amount == Double.POSITIVE_INFINITY || triDistanceSq<amount){
            		amount = triDistanceSq;
            	}
            }
        }
        if(amount ==Double.POSITIVE_INFINITY)
        	return amount;
        ray.setDirection(ray.getDirection().negate(null));
        ray.setOrigin(ray.getOrigin().add(ray.getDirection().multiply(amount, null), null));
        ///Part 2
        primitives.clear();
        ct = CollisionTreeManager.getInstance().getCollisionTree(end);
        if (ct != null) {
            ct.getBounds().transform(end.getWorldTransform(), ct.getWorldBounds());
            primitives = ct.intersect(ray, primitives);
        }

        if (primitives.isEmpty()) {
            return amount;
        }

        for (int i = 0; i < primitives.size(); i++) {
            final PrimitiveKey key = primitives.get(i);
            vertices = end.getMeshData().getPrimitiveVertices(key.getPrimitiveIndex(), key.getSection(), vertices);
            // convert to world coord space
            final int max = end.getMeshData().getIndexMode(key.getSection()).getVertexCount();
            for (int j = 0; j < max; j++) {
                if (vertices[j] != null) {
                    end.getWorldTransform().applyForward(vertices[j]);
                }
            }
            final double triDistanceSq = RayOps.intersects(ray, vertices);
            if (triDistanceSq < 1 && triDistanceSq >=-1E-6) {
                if(amount == Double.POSITIVE_INFINITY || triDistanceSq<amount){
            		amount = triDistanceSq;
            	}
            }
        }
        return amount;
    }

    /**
	 * To plane.
	 * 
	 * @param floor
	 *            the floor
	 * @return the double[]
	 */
    public static double[] toPlane(Mesh floor){

        final Vector3[] hi = floor.getMeshData().getPrimitiveVertices(0, 0, null);
        for (int i = 0; i < 3; i++) {
          //  floor.getWorldTransform().applyForward(
            		floor.getTransform().applyForward(hi[i])
            		//)
            		;
        }
        return toPlane(hi);
    }
    
    /**
	 * To plane.
	 * 
	 * @param hi
	 *            the hi
	 * @return the double[]
	 */
    public static double[] toPlane(final Vector3[] hi) {
    //	System.out.println(hi[0]+", "+hi[1]+", "+hi[2]);
        final Vector3 a = hi[0].subtract(hi[1], null);
        final Vector3 b = hi[0].subtract(hi[2], null);
        a.crossLocal(b);
        final double[] ar = new double[4];
        ar[0] = a.getX();
        ar[1] = a.getY();
        ar[2] = a.getZ();
        ar[3] = -hi[0].dot(a);
        return ar;
    }
}
