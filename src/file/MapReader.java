package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import state.Playing;
import surface.*;
import portal.Bullet;
import portal.Portal;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;

import backbone.TestChamber;

/**
 * A class for parsing .map files into chambers
 * See Mapfiles.txt for more information
 */
public class MapReader {
	
	/**
	 * Sets the up the chamber from a bufferedreader
	 * 
	 * @param cham
	 *            the chamber to set up
	 * @param f
	 *            the buffered reader
	 */
	public static void setUp(TestChamber cham, BufferedReader f){
		//remove existing spatials
		cham.detachAllChildren();
		try {
			while(f.ready()){
				String s = f.readLine();
				//ignore blank lines/comments
				if(s.length()>0 && s.charAt(0)!='#')
				add(cham, s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the orientation from a number index
	 * 
	 * @param a
	 *            the index
	 * @return the orientation
	 */
	public static Orientation getOrientation(int a){
		switch(a){
		case 0:
			return Orientation.Floor;
		case 1:
			return Orientation.Forward;
		case 2:
			return Orientation.Right;
		}
		throw new RuntimeException("Illegal Orientation");
	}
	
	/**
	 * Sets the up the chamber from a string representing an article
	 * 
	 * @param cham
	 *            the chamber to set up
	 * @param s
	 *            the string representation of a spatial as specified by their toWritableString()
	 */
	public static void setUp(TestChamber cham, String s){
		if(s.length()==0)
			return;
		setUp(cham, new BufferedReader(new InputStreamReader(MapReader.class.getResourceAsStream(s))));
	}
	
	/**
	 * The Node class for handling parsing recursive list system
	 */
	public static class Node{
		
		/** The data in this node self. */
		public String self;
		
		/** The node after. */
		public Node after = null;
		
		/**
		 * Instantiates a new node.
		 * 
		 * @param a
		 *            the string representation of self
		 * @param b
		 *            the node after
		 */
		public Node(String a, Node b){
			if(b !=null)
			b.after = this;
			self = a;
		}
		
		/**
		 * The toString(), (for debugging)
		 */
		public String toString(){
			return self+", "+after;
		}
	}
	
	/**
	 * Reads the next class from the node
	 * 
	 * @param n
	 *            the node
	 * @return the object
	 */
	public static Object readT(Node n){
		String className = (String) n.self;
		n.self = n.after.self;
		n.after = n.after.after;
		if(className.equals("ori")){
			return getOrientation((Integer)readD(n));
		}
		if(className.equals("vector3")){
			return new Vector3(toDouble(readD(n)), toDouble(readD(n)), toDouble(readD(n)));
		}
		if(className.equals("piece")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new Piece(w, h, pos, ori);
		}
		if(className.equals("faithplate")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n), go = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new FaithPlate(w, h, pos, go, ori);}
		if(className.equals("bounce")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new Bounce(w, h, pos, ori);}
		if(className.equals("pkiller")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new PKiller(w, h, pos, ori);}
		if(className.equals("killer")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new Killer(w, h, pos, ori);}
		if(className.equals("exit")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new Exit(w, h, pos, ori);
		}
		if(className.equals("win")){
			double w = toDouble(readD(n));
			double h = toDouble(readD(n));
			Vector3 pos = (Vector3)readD(n);
			Orientation ori = (Orientation)readD(n);
			if(ori == Orientation.Forward){
				double temp = h;
				h = w;
				w = temp;
			}
			return new Win(w, h, pos, ori);
		}
		if(className.equals("platform")){
			Surface m = (Surface)(readD(n));
			//TODO FIX HARDCODE patch
			n.self = n.after.self;
			n.after = n.after.after;
			Vector3[] ar = new Vector3[(int)toDouble(readD(n))];
			for(int i = 0; i<ar.length; i++)
				ar[i] = (Vector3)(readD(n));
			return new Platform(m, ar, toDouble(readD(n)));
		}
		if(className.equals("bullet")){
			return new Bullet(toDouble(readD(n)), (Vector3)(readD(n)), (Vector3)readD(n), (Boolean)readD(n), (Boolean)readD(n));
		}
		if(className.equals("portal")){
			return new Portal((Vector3)readD(n),(Orientation)readD(n),(Boolean)readD(n), (Boolean)(readD(n)));
		}
		//TODO add back in functionality for new renderer, if time allows
//		if(className.equals("cube")){
//			return new Cube((Vector3)readD(n),(Vector3)readD(n),(Vector3)readD(n), toDouble(readD(n)), toDouble(readD(n)), toDouble(readD(n)));
//		}
//		if(className.equals("teapot")){
//			return new TeaPot((Vector3)readD(n), (Vector3)readD(n), (Vector3)readD(n));
//		}
		throw new RuntimeException("Failed to parse: "+n.self);
	}
	
	/**
	 * Turns an object to a double, and fixes all cast issues
	 * 
	 * @param o
	 *            the object
	 * @return the double
	 */
	public static double toDouble(Object o){
		if(o instanceof Integer)
			return ((Integer)o).doubleValue();
		return (Double)o;
	}
	
	/**
	 * Reads the next primitive
	 * 
	 * @param n
	 *            the node
	 * @return the primitive
	 */
	public static Object readD(Node n){
		
		try{
			int d = Integer.parseInt(n.self);
			if(n.after!=null)
			{
				n.self = n.after.self;
				n.after = n.after.after;
			}
			else
				n.self = null;
			return d;
		}catch(Exception e){}
		try{
			double d = Double.parseDouble(n.self);
			if(n.after!=null)
			{
				n.self = n.after.self;
				n.after = n.after.after;
			}
			else
				n.self = null;
			return d;
		}catch(Exception e){}
		if(n.self.equals("#"))
			return null;
		if(n.self.equals("false"))
			return false;
		if(n.self.equals("true"))
			return true;
		if(n.self.equals("floor"))
			return Orientation.Floor;
		if(n.self.equals("forward"))
			return Orientation.Forward;
		if(n.self.equals("right"))
			return Orientation.Right;
		if(n.self.equals("{")){
			if(n.after!=null)
			{
				n.self = n.after.self;
				n.after = n.after.after;
			}
			else
				n.self = null;
			Object hi = readT(n);
			if(n.after!=null)
			{
				n.self = n.after.self;
				n.after = n.after.after;
			}

			else
				n.self = null;
			return hi;
		}
		throw new RuntimeException("Failed to parse: "+n.self);
	}
	
	/**
	 * Adds the string representation of a spatial to the chamber
	 * 
	 * @param cham
	 *            the chamber
	 * @param s
	 *            the string
	 */
	public static void add(TestChamber cham, String s){
		StringTokenizer st = new StringTokenizer(s);
		Node start = new Node(st.nextToken().toLowerCase(), null);
		Node current = start;
		while(st.hasMoreTokens()){
			current = new Node(st.nextToken().toLowerCase(), current);
		}
		Spatial t = (Spatial)readD(start);
		if(t instanceof Bullet)
			Playing.bullets.attachChild(t);
		else if (t instanceof Portal)
			Playing.portals.attachChild(t);
		else
			cham.add(t);
	}
	
	/**
	 * Writes a chamber to file
	 * 
	 * @param testChamber
	 *            the test chamber to write
	 * @param string
	 *            the string representation of the filepath
	 */
	public static void write(TestChamber testChamber, String string) {
		try {
			write(testChamber, new PrintWriter(new BufferedWriter(new FileWriter(string))));
			System.out.println(new File(string).getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a chamber to file
	 * 
	 * @param testChamber
	 *            the test chamber to write
	 * @param out
	 *            the printwriter to write to
	 */
	public static void write(TestChamber testChamber, PrintWriter out) {
		for(Spatial t:testChamber.getChildren()){
			if(t instanceof Savable){
				out.println(((Savable)t).toWritableString());
			}
		}
		out.close();
	}
	
	//TODO add functionality to import valve map files, similar to XML format
	/**
	 * Import xml.
	 * 
	 * @param out
	 *            the out
	 * @param in
	 *            the in
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void importXML(PrintWriter out, BufferedReader in) throws IOException{
		in.readLine();//world
		in.readLine();//{
		String s = in.readLine().trim();
		while(s.equals("") || s.charAt(0) == '\"'){
		}
		System.out.println(s);
		if(s.equals("solid")){
			in.readLine().trim(); // {
			String p = in.readLine().trim(); // id
			s = in.readLine().trim(); //solid
			if(s.equals("side")){
				in.readLine(); // {
				s = in.readLine(); //id
				
			p = p.substring(p.indexOf('\"')+1);
			p = p.substring(p.indexOf('\"')+1);
			p = p.substring(p.indexOf('\"')+1);
			p = p.substring(0, p.indexOf('\"'));
			String v1 = p.substring(p.indexOf('(')+1, p.indexOf(')'));
			p = p.substring(p.indexOf(')')+1);
			StringTokenizer st = new StringTokenizer(v1);
			Vector3 a = new Vector3(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));

			String v2 = p.substring(p.indexOf('(')+1, p.indexOf(')'));
			p = p.substring(p.indexOf(')')+1);
			st = new StringTokenizer(v2);
			Vector3 b = new Vector3(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));

			String v3 = p.substring(p.indexOf('(')+1, p.indexOf(')'));
			p = p.substring(p.indexOf(')')+1);
			st = new StringTokenizer(v3);
			Vector3 c = new Vector3(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));

			String smoothing_groups = in.readLine();
			String materal = in.readLine();
			

			String u_axis = in.readLine();
			String v_axis = in.readLine();

			String lightmapscale = in.readLine();
//			Plane p = new Plane();
			System.out.println(a);
			System.out.println(b);
			System.out.println(c);
		}
		}
		in.readLine();//}
	}
}
