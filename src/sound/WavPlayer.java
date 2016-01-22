package sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;

/**
 * The Class to play wave files.
 */
public class WavPlayer
{	
	
	/** The burn sound */
	public static WavPlayer burn = new WavPlayer("/music/burn.wav");
	
	/** The faithplate sound */
	public static WavPlayer faith = new WavPlayer("/music/faith.wav");
	
	/** The bounce sound */
	public static WavPlayer bounce = new WavPlayer("/music/bounce.wav");
	
	/** The door sound */
	public static WavPlayer door = new WavPlayer("/music/door.wav");
	
	/** The s blue portal sound */
	public static WavPlayer sBlue = new WavPlayer("/music/sBlue.wav");
	
	/** The s red portal soun */
	public static WavPlayer sRed = new WavPlayer("/music/sRed.wav"); 
   
   /** The clip. */
   public AudioClip clip;
   
   /**
	 * Play.
	 */
   public void play()
   {
    	  clip.play();
   }
   
   /**
	 * Stop.
	 */
   public void stop(){
	   clip.stop();
   }
   
   /**
	 * Loop.
	 */
   public void loop(){
	   clip.loop();
   }
   /**
	 * gets all wavplayers from a given directory given by a string
	 * 
	 * @param a
	 *            the a
	 * @return the linked list
	 */
   public static LinkedList<WavPlayer> all(String a){
		 Enumeration<URL> en;
		try {
			en = WavPlayer.class.getClassLoader().getResources(a);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		System.out.println(en);
		 LinkedList<WavPlayer> ap = new LinkedList<WavPlayer>();
		 if (en.hasMoreElements()) {
		     ap.add(new WavPlayer(en.nextElement()));
		 }
		 return ap;
	 }
   
   /**
	 * Instantiates a new wav player.
	 * 
	 * @param s
	 *            the string location of the file
	 */
   public WavPlayer(String s){
	   this(WavPlayer.class.getResource(s));
   }
   
   /**
	 * Instantiates a new wav player.
	 * 
	 * @param t
	 *            the file location of the file
	 */
   @SuppressWarnings("deprecation")
public WavPlayer(File t){
  	 try {
		clip = Applet.newAudioClip(t.toURL());
	} catch (MalformedURLException e) {
		e.printStackTrace();
	}
   }
   
   /**
	 * Instantiates a new wav player.
	 * 
	 * @param t
	 *            the t
	 */
   public WavPlayer(URL t)
   {
        	 clip = Applet.newAudioClip(t);
   }
}