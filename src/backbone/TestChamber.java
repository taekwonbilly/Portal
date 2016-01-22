package backbone;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 * A class representing the current testchamber
 */
public class TestChamber extends Node {
/**
	 * Adds a spaital to the testchamber
	 * 
	 * @param the spatial
	 */
  public void add(final Spatial s) {
      attachChild(s);
    }
  
    /**
	 * Instantiates a new test chamber from file
	 * 
	 * @param the string map file
	 */
   public TestChamber(String s){
        file.MapReader.setUp(this, s);
    }
    
    /**
	 * Instantiates a new test chamber from the defualt
	 */
    public TestChamber() {
    file.MapReader.setUp(this, "/maps/default.map");
    }
}
