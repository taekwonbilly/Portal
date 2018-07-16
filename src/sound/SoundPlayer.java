package sound;

import java.io.*;
import java.net.*;

import state.Menu;

import javazoom.jl.decoder.JavaLayerException;

/**
 * The Class to play mp3's
 */
public class SoundPlayer{
   /**the name of the file*/
	public String name;
   /** The thread updating music frames */
   public MusicThread mt = new MusicThread();{
 	  mt.start();
   }
  
  /** The boolean denoting if this is playing */
  volatile boolean playing;
   
   /** The player. */
   public javazoom.jl.player.Player player;
   
   /** The inputstream */
   public InputStream is;
   
   /**
	 * Checks if is playing.
	 * 
	 * @return true, if is playing
	 */
   public boolean isPlaying(){
 	  return playing;
   }
   
   /** The length of song */
   int length;
   
   /**
	 * Checks if is complete.
	 * 
	 * @return true, if is complete
	 */
   public boolean isComplete()
   {
      return getPosition() == getLength();
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param is
	 *            the inputstream
	 * @param i
	 *            the length
	 */
   public SoundPlayer(InputStream is, int i){
 	  this.is = new PushbackInputStream(is);
 	  this.is.mark(Integer.MAX_VALUE);
 	  length = i;
       try {
    	   is.mark(Integer.MAX_VALUE);
			player = new javazoom.jl.player.Player(this.is);
		} catch (javazoom.jl.decoder.JavaLayerException e) {
			e.printStackTrace();
		}
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param is
	 *            the inputstream
	 */
   public SoundPlayer(InputStream is){
 	  this.is = is;
       try {
    	   is.mark(Integer.MAX_VALUE);
			player = new javazoom.jl.player.Player(this.is);
		} catch (javazoom.jl.decoder.JavaLayerException e) {
			e.printStackTrace();
		}
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param f
	 *            the file
	 */
   public SoundPlayer(File f){
 	  try {
			this.is = (new FileInputStream(f));
	          try {
	  			player = new javazoom.jl.player.Player(this.is);
	  		} catch (javazoom.jl.decoder.JavaLayerException e) {
	  			e.printStackTrace();
	  		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param r
	 *            the url of the file
	 */
   public SoundPlayer(URI r){
 	  try {
			this.is = (r.toURL().openStream());
	          try {
	  			player = new javazoom.jl.player.Player(this.is);
	  		} catch (javazoom.jl.decoder.JavaLayerException e) {
	  			e.printStackTrace();
	  		}
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param s
	 *            the string path of the file
	 */
   public SoundPlayer(String s){
	   this(s, false);
	   name = s;
   }
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param r
	 *            the url of the file
	 */
   public SoundPlayer(URL r){
 	  try {
			this.is = (r.openStream());
	          try {
	  			player = new javazoom.jl.player.Player(this.is);
	  		} catch (javazoom.jl.decoder.JavaLayerException e) {
	  			e.printStackTrace();
	  		}
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
   
   
   /**
	 * Instantiates a new sound player.
	 * 
	 * @param s
	 *            the string file
	 * @param a
	 *            the boolean flag, not used
	 */
   public SoundPlayer(String s, boolean a){
	   this(SoundPlayer.class.getResourceAsStream(s));
	   name = s;
	   }
   
   
   /**
	 * Closes the player
	 */
   public void close(){
 	  stop();
 	  try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
   
   /** The itself */
   SoundPlayer self = this;
   
   /**
	 * The Class MusicThread.
	 */
   public class MusicThread extends Thread
   {
      
      /**
       * plays the song
       */
      public void run()
      {
     	 while(true){
if(playing){
	try {
		player.play(1);
		pos++;
	} catch (JavaLayerException e) {
		e.printStackTrace();
	}
	if(isComplete()){
Menu.finished(self);
pos = 0;
	}
}
     	 }
      }
   }
   
   /**
	 * Play.
	 */
   public void play(){
 	  playing = true;
   }
   
   /**
	 * Resume.
	 */
   public void resume(){
 	  play();
   }
      
   /**
	 * Gets the position.
	 * 
	 * @return the position
	 */
   public int getPosition(){
 	  return pos;
   }
   
   /**
	 * Gets the length.
	 * 
	 * @return the length
	 */
   public int getLength(){
 	  return length;
   }
   
   /** The pos. */
   int pos = 0;
   
   /**
	 * Stop.
	 */
   public void stop(){
       playing = false;
 	  pos = 0;
	  try {
		player = new javazoom.jl.player.Player(this.is);
	} catch (javazoom.jl.decoder.JavaLayerException e) {
		e.printStackTrace();
	}
   }
	
	/**
	 * Pause.
	 */
	public void pause()
   {
       playing = false;
   }
}
