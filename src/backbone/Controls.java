package backbone;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import portal.*;
import math.RayOps;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;

import sound.WavPlayer;
import state.*;
import surface.Bounce;
import surface.FaithPlate;
import surface.PKiller;
import surface.Platform;

/**
 * Class which delegates itself with both the key and mouse controls for the user
 * and also all movement throughout the environment including the player
 */
public class Controls implements KeyListener, MouseListener, MouseMotionListener{

    /** 3.1415926535.../2 */
    public static final double HALF_PI = 0.5 * Math.PI;
	
    /** The speed for the camera to rotate because of the mouse */
    protected double _mouseRotateSpeed = .005;
    
    /** The speed at which the camera should move forwards */
    protected double _moveSpeed = 20;
    
    /** The speed for the camera to rotate because of the keyboard */
    protected double _keyRotateSpeed = 2.25;
    
    /** A matrix to store camera data */
    protected final Matrix3 _workerMatrix = new Matrix3();
    
    /** A vector to store camera data */
    protected final Vector3 _workerStoreA = new Vector3();

    
    /** The _min vertical angle. */
    protected double _minVerticalAngle = -88 * Math.PI / 180.0;
    
    /** The _max vertical angle. */
    protected double _maxVerticalAngle = 88 * Math.PI / 180.0;


   
    /**
	 * Moves an object represented by a position, direction its facing, and move vector
	 * 
	 * @param startingPos
	 *            the starting position
	 * @param startingDir
	 *            the starting facing direction
	 * @param orig
	 *            the move vector
	 * @param portaled
	 *            a boolean delegating whether it should move through portals
	 * @return the vector3[] data in form new position, new direction, new move
	 */
    public Vector3[] move(final ReadOnlyVector3 startingPos, final ReadOnlyVector3 startingDir, final ReadOnlyVector3 orig,
            final boolean portaled) {
        final Ray3 ray = new Ray3(startingPos, orig);
        Vector3[] posDir = new Vector3[] { new Vector3(startingPos), new Vector3(startingDir), new Vector3(orig)};
        if (portaled && Constants.blue != null && Constants.orange != null) {
            if (RayOps.intersects(Constants.blue.disk, ray, false)!=Double.POSITIVE_INFINITY) {
                posDir[0] = Constants.orange.position;
                final Vector3 forwards = Constants.orange.toInertia(Constants.blue.fromInertia(startingDir));
                posDir[1] = forwards;
                posDir = move(posDir[0], posDir[1], posDir[1].multiply(10.001, null), false);
                posDir[2] = Constants.orange.toInertia(Constants.blue.fromInertia(orig));
                return posDir;
            } else if (RayOps.intersects(Constants.orange.disk, ray, false)!=Double.POSITIVE_INFINITY) {
                posDir[0] = (Constants.blue.position);
                final Vector3 forwards = Constants.blue.toInertia(Constants.orange.fromInertia(posDir[1]));
                posDir[1] = forwards;
                posDir = move(posDir[0], posDir[1], posDir[1].multiply(10.001, null), false);
                posDir[2] = Constants.blue.fromInertia(Constants.orange.fromInertia(orig));
                return posDir;
            }
        }
        double hi = Double.POSITIVE_INFINITY;
        for (final Spatial p : Playing.chamber.getChildren()) {
        	if(p instanceof Mesh){
            hi = Math.min(RayOps.intersects((Mesh)p, ray, false), hi);
            }
        }
        if (hi==Double.POSITIVE_INFINITY) {
            posDir[0] = (orig.add(posDir[0], null));
            return posDir;
        } else {
        	posDir[0] = ray.getOrigin().add(ray.getDirection().multiply(hi-.0001, null), null);
       //     System.out.println(posDir[0]);
        	posDir[2] = orig.multiply(0, null);
            return posDir;
        }
    }
    
    /** A boolean delegating whether or not the player has jumped since hitting the ground */
    public volatile boolean jumped = false;
    
    /**
	 * Moves the camera throughout the environment
	 * 
	 * @param camera
	 *            the camera
	 * @param orig
	 *            the vector to move the camera
	 * @param portaled
	 *            a boolean delegating whether or not you should move through a portal
	 * @return the vector3
	 * 			  the modified vector to move (0 if hits wall, refleted if bounced, etc)
	 */
    public Vector3 move(final Camera camera, final Vector3 orig, final boolean portaled) {
        final Ray3 ray = new Ray3(camera.getLocation(), orig);
        if (portaled && Constants.blue != null && Constants.orange != null) {
            if (RayOps.intersects(Constants.blue.disk, ray, true)!=Double.POSITIVE_INFINITY) {
                jumped = false;
            	camera.setLocation(Constants.orange.position);
                final Vector3 forwards = Constants.orange.toInertia(Constants.blue.fromInertia(camera.getDirection()));
                camera.lookAt(forwards.addLocal(Constants.orange.position), Constants._worldUp);
                move(camera, Constants.orange.getForwardVector().multiply(1, null), false);
         //       System.out.println(Constants.blue.fromInertia(orig)+"||"+orig+"|"+Constants.orange.toInertia(Constants.blue.fromInertia(orig)));
                return Constants.orange.toInertia(Constants.blue.fromInertia(orig));
            } else if (RayOps.intersects(Constants.orange.disk, ray, true)!=Double.POSITIVE_INFINITY) {
                jumped = false;
            	camera.setLocation(Constants.blue.position);
                final Vector3 forwards = Constants.blue.toInertia(Constants.orange.fromInertia(camera.getDirection()));
                camera.lookAt(forwards.addLocal(Constants.blue.position), Constants._worldUp);
                move(camera, Constants.blue.getForwardVector().multiply(1, null), false);
          //      System.out.println(Constants.orange.fromInertia(orig)+"||"+orig+"|"+Constants.blue.toInertia(Constants.orange.fromInertia(orig)));
                return Constants.blue.toInertia(Constants.orange.fromInertia(orig));
            }
        }
        if (Constants.exit != null && RayOps.intersects(Constants.exit, ray, true)!=Double.POSITIVE_INFINITY) 
        	{
        	Playing.win();
        	}
        double hi = Double.POSITIVE_INFINITY;
        Spatial sp = null;
        for (final Spatial p : Playing.chamber.getChildren()) {
        	double temp;
        	if(p instanceof Mesh && (temp = RayOps.intersects((Mesh)p, ray, true))<hi){
            hi = temp;
            sp = p;
            }
        	else if (p instanceof Platform){
        		boolean hit = false;
                	if((temp = RayOps.intersects(((Platform)p).self, ray, true))<hi){
                    hi = temp;
                    sp = ((Platform)p).self;
                    hit = true;
                    }
        		if(orig == Constants.inertia){
        			((Platform)p).hit = hit;
        		}
        	}
        }
        if (hi==Double.POSITIVE_INFINITY) {
           camera.setLocation(orig.add(camera.getLocation(), null));
            return orig;
        } else {
        	if(sp instanceof PKiller){
        		Playing.die();
            	return orig.multiply(0, null);
        	}
        	if(orig.getY()<0)
        	jumped = false;
        	if(sp instanceof Bounce){
        		WavPlayer.bounce.play();
            	return ((Bounce)sp).reflect(orig);
        	}
        	else if(sp instanceof FaithPlate){
        		WavPlayer.faith.play();
                	return orig.set(((FaithPlate)sp).jump);
        	}
        	else
        		return orig.multiply(0, null);
        }
    }

    /**
	 * Moves the camera based on key/mouse input
	 * 
	 * @param camera
	 *            the camera
	 * @param tpf
	 *            the time between updates
	 */
    public void move(final Camera camera, final double tpf) {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
        
        // MOVEMENT
        int moveFB = 0, strafeLR = 0;
        if (up || w) {
            moveFB += 1;
        }
        if (down || s) {
            moveFB -= 1;
        }
        if (a) {
            strafeLR += 1;
        }
        if (d) {
            strafeLR -= 1;
        }

        if (moveFB != 0 || strafeLR != 0) {
            final Vector3 loc = _workerStoreA.zero();
            if (moveFB == 1) {
                loc.addLocal(camera.getDirection());
            } else if (moveFB == -1) {
                loc.subtractLocal(camera.getDirection());
            }
            if (strafeLR == 1) {
                loc.addLocal(camera.getLeft());
            } else if (strafeLR == -1) {
                loc.subtractLocal(camera.getLeft());
            }

            loc.setY(0);
          move(camera, loc.normalize(null).multiplyLocal(_moveSpeed * tpf), true);
        }

        double rotX = mouseX;
        double rotY = mouseY;
//        if (w) {
//            rotY -= 1;
//        }
//        if (s) {
//            rotY += 1;
//        }
        if (left) {
            rotX += 1;
        }
        if (right) {
            rotX -= 1;
        }
        if (rotX != 0 || rotY != 0) {
            rotate(camera, rotX * (_keyRotateSpeed / _mouseRotateSpeed) * tpf, rotY
                    * (_keyRotateSpeed / _mouseRotateSpeed) * tpf);
        }
    }

    /**
	 * Rotates the camera a certain amount
	 * 
	 * @param camera
	 *            the camera
	 * @param dx
	 *            the change in yaw
	 * @param dy
	 *            the change in pitch
	 */
    protected void rotate(final Camera camera, final double dx, final double dy) {
        if (dx != 0) {
            applyDx(dx, camera);
        }

        if (dy != 0) {
            applyDY(dy, camera);
        }

        if (dx != 0 || dy != 0) {
            camera.normalize();
        }
    }

    /**
	 * Applies the change in yaw
	 * 
	 * @param dx
	 *            the change in yaw
	 * @param camera
	 *            the camera
	 */
    private void applyDx(final double dx, final Camera camera) {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
        
        _workerMatrix.fromAngleNormalAxis(_mouseRotateSpeed * dx, Constants._worldUp);
        _workerMatrix.applyPost(camera.getLeft(), _workerStoreA);
        camera.setLeft(_workerStoreA);
        _workerMatrix.applyPost(camera.getDirection(), _workerStoreA);
        camera.setDirection(_workerStoreA);
        _workerMatrix.applyPost(camera.getUp(), _workerStoreA);
        camera.setUp(_workerStoreA);
    }
    
    /**
	 * Applies the change in pitch
	 * 
	 * @param dy
	 *            the change in pitch
	 * @param camera
	 *            the camera
	 */
    private void applyDY(final double dy, final Camera camera) {
    	//Please note that this was from the opengl wrapper, but modified to suit the needs of this application
    	// apply dy angle change to direction vector
        _workerMatrix.fromAngleNormalAxis(_mouseRotateSpeed * dy, camera.getLeft());
        _workerMatrix.applyPost(camera.getDirection(), _workerStoreA);
        camera.setDirection(_workerStoreA);
        {
            // check if we went out of bounds and back up
            final double angleV = HALF_PI - _workerStoreA.smallestAngleBetween(Constants._worldUp);
            if (angleV > _maxVerticalAngle || angleV < _minVerticalAngle) {
                // clamp the angle to our range
                final double newAngle = (angleV < _minVerticalAngle) ? _minVerticalAngle : (angleV > _maxVerticalAngle ? _maxVerticalAngle : angleV);
                // take the difference in angles and back up the direction vector
                _workerMatrix.fromAngleNormalAxis(-(newAngle - angleV), camera.getLeft());
                _workerMatrix.applyPost(camera.getDirection(), _workerStoreA);
                camera.setDirection(_workerStoreA);
                // figure out new up vector by crossing direction and left.
                camera.getDirection().cross(camera.getLeft(), _workerStoreA);
                camera.setUp(_workerStoreA);
                return;
            }
        }

        // just apply to up vector
        _workerMatrix.applyPost(camera.getUp(), _workerStoreA);
        camera.setUp(_workerStoreA);
    }

    /** A boolean denoting whether the w key is down. */
    public static volatile boolean w = false;
    /** A boolean denoting whether the a key is down. */
    public static volatile boolean a = false;
    /** A boolean denoting whether the s key is down. */
    public static volatile boolean s = false;
    /** A boolean denoting whether the d key is down. */
    public static volatile boolean d = false;
    /** A boolean denoting whether the left key is down. */
    public static volatile boolean left = false;
    /** A boolean denoting whether the right key is down. */
    public static volatile boolean right = false;
    /** A boolean denoting whether the up key is down. */
    public static volatile boolean up = false;
    /** A boolean denoting whether the down key is down. */
    public static volatile boolean down = false;
  

	public void mouseDragged(MouseEvent arg0) {
	}
	
	/** The amount to turn in yaw because of the mouse. */
	public double mouseX = 0;
	/** The amount to turn in pitch because of the mouse. */
	public double mouseY = 0;
	
	/**
	 * Calulate rotation due to moving the mouse
	 */
	public void mouseMoved(MouseEvent arg0) {
//		mouseX = -(double)(arg0.getX()-Constants.drawX/2)/(Constants.drawX/200);
//		mouseY = (double)(arg0.getY()-Constants.drawY/2)/(Constants.drawY/200);
//		temp.mouseMove(Constants.drawX/2, Constants.drawY/2);
		int oldx = arg0.getX();
		int oldy = arg0.getY();
if(oldx>=Constants.drawX*7/8)
	mouseX = -1;//*(Constants.drawX*7/8-oldx)*(Constants.drawX*7/8-oldx)/Constants.drawX/Constants.drawX*64;
else if(oldx<=Constants.drawX/8)
	mouseX = 1;//*(Constants.drawX/8-oldx)*(Constants.drawX/8-oldx)/Constants.drawX/Constants.drawX*64;
else
	mouseX = 0;

if(oldy>=Constants.drawY*7/8)
	mouseY = 1;//*(Constants.drawY*7/8-oldy)*(Constants.drawY*7/8-oldy)/Constants.drawY/Constants.drawY*64;
else if(oldy<=Constants.drawY/8)
	mouseY = -1;//*(Constants.drawY/8-oldy)*(Constants.drawY/8-oldy)/Constants.drawY/Constants.drawY*64;
else
	mouseY = 0;
	}

	public void mouseClicked(MouseEvent arg0) {
	}
	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Fires portals, blue if left mouse button, orange if right mouse button
	 */
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON3){
			   final Vector2 pos = Vector2.fetchTempInstance().set(
                       arg0.getX(),
                       Main._canvas.getHeight()-arg0.getY());
               final Ray3 pickRay = new Ray3();
               Main._canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
               TDCanvas.toadd.add(new Bullet(pickRay, false));
        
		}
		else if (arg0.getButton()==MouseEvent.BUTTON1){
	          final Vector2 pos = Vector2.fetchTempInstance().set(
                      arg0.getX(),
                      Main._canvas.getHeight()-arg0.getY());
              final Ray3 pickRay = new Ray3();
              Main._canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
              final Bullet a = new Bullet(pickRay, true);
              TDCanvas.toadd.add(a);
		}
	}

	public void mouseReleased(MouseEvent arg0) {
	}
	
	/**
	 * Checks all booleans and jump/quit keys
	 */
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_T:
           	TDCanvas.toadd.add(new Bullet(false));
           	break;
		case KeyEvent.VK_R:
			 final Bullet a = new Bullet(true);
                TDCanvas.toadd.add(a);
                break;
		case KeyEvent.VK_ESCAPE:  
			System.exit(0);
			System.out.println("back");              
			Main.state.close();
			Main.state = Main.menu;
			Main.state.init();
			break;
		case KeyEvent.VK_SPACE:
			if(!jumped){
			Constants.inertia.setY(Constants.inertia.getY() + Constants.jump);
			jumped = true;
			}
            break;
		case KeyEvent.VK_W:
			Controls.w = true;
			break;
		case KeyEvent.VK_S:
			Controls.s = true;
			break;
		case KeyEvent.VK_A:
			Controls.a = true;
			break;
		case KeyEvent.VK_D:
			Controls.d = true;
			break;
		case KeyEvent.VK_UP:
			Controls.up = true;
			break;
		case KeyEvent.VK_DOWN:
			Controls.down = true;
			break;
		case KeyEvent.VK_LEFT:
			Controls.left = true;
			break;
		case KeyEvent.VK_RIGHT:
			Controls.right = true;
			break;
		case KeyEvent.VK_P:
			TDCanvas._doShot = true;
			break;
		case KeyEvent.VK_Q:
			Playing.reset();
			break;
		}}
	
	/**
	 * Reassigns variables based on key released
	 */
	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_W:
			Controls.w = false;
			break;
		case KeyEvent.VK_S:
			Controls.s = false;
			break;
		case KeyEvent.VK_A:
			Controls.a = false;
		case KeyEvent.VK_D:
			Controls.d = false;
			break;
		case KeyEvent.VK_UP:
			Controls.up = false;
			break;
		case KeyEvent.VK_DOWN:
			Controls.down = false;
			break;
		case KeyEvent.VK_LEFT:
			Controls.left = false;
			break;
		case KeyEvent.VK_RIGHT:
			Controls.right = false;
			break;
		}}

	public void keyTyped(KeyEvent arg0) {
	}
}
