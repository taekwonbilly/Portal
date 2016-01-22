package surface;

import backbone.Main;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;

/**
 * A class which will handle all movements of any surface. 
 */
public class Platform extends Node implements file.Savable{
	/**
	 * The surface to be moved
	 */
	public Surface self;
	/**
	 * 
	 * @param a The surface to be moved
	 * @param ar An array of coordinated to move between
	 * @param speed the speed for the surface to move between the coordinates
	 */
	public Platform(Surface a, Vector3[] ar, double speed){
		self = a;
		attachChild(a);
		this.speed = speed;
		positions = ar;
		a.setTranslation(now = ar[0].clone());
	}
	/**
	 * A boolean denoting whether or not the player is on the surface
	 */
	public boolean hit = false;
	/**
	 * The index of the most recently visited point in the motion
	 */
	int pos = 0;
	/**
	 * The current center of the surface
	 */
	public Vector3 now;
	/**
	 * The speed for the spatial to move between coordinates
	 */
	public double speed;
	/**
	 * The coordinated to move between
	 */
	public Vector3[] positions;
	public String toWritableString(){
		String s =  "{ Platform "+self.toWritableString()+" "+positions.length;
		for(Vector3 a:positions)
			s+=" "+a;
		return s+" }";
	}
	/**
	 * Moves the position of the spatial, and the player if neccesary
	 */
	public void update(){
		double dist = now.distance(positions[(pos+1)%positions.length]);
		if(dist<speed){
			{
				pos=(pos+1)%positions.length;
				now = positions[pos].clone();
				Vector3 move = positions[(pos+1)%positions.length].subtract(now, null).normalizeLocal().multiplyLocal(speed-dist);
				now.addLocal(move);
				if(hit)
				Main._controlHandle.move(Main._canvas.getCanvasRenderer().getCamera(), move, true);
				
				self.setTranslation(now);
			}
		}
		else{
			Vector3 move = positions[(pos+1)%positions.length].subtract(now, null).normalizeLocal().multiplyLocal(speed);
			now.addLocal(move);
			if(hit)
			Main._controlHandle.move(Main._canvas.getCanvasRenderer().getCamera(), move, true);
			
			self.setTranslation(now);
		}
	}
}
