package nehe_opengl_tutorials;

import javax.swing.JOptionPane;

/**
 * Simple dialog menu to select a tutorial to launch
 * @author Sebastiaan
 */
@SuppressWarnings("rawtypes")
public class Nehe_Opengl_Tutorials {    
    private final int nrOfLessons = 10;
	private Class currentClass = null;
    private String tutorialNumbers[];
    private String tutorials[] = { "Tutorial 1: Simple OpenGL window",
                                   "Tutorial 2: First Polygons",
                                   "Tutorial 3: Adding Color",
                                   "Tutorial 4: Rotation",
                                   "Tutorial 5: 3D Shapes",
                                   "Tutorial 6: Texture Mapping",
                                   "Tutorial 7: Texture Filters, Lighting & Keyboard Control",
                                   "Tutorial 8: Blending",
                                   "Tutorial 9: Moving Bitmaps In 3D Space",
                                   "Tutorial 10: Loading And Moving Through A 3D World",
                                   "Tutorial 11: Flag Effect (Waving Texture)",
                                   "Tutorial 12: Display Lists",
                                   "Tutorial 13: Bitmap Fonts"};
    
    private int lastSelectedTutorial = 0;
    
    private static Nehe_Opengl_Tutorials _instance = null;
 
    // Private constructor replaces the default public constructor 
    private Nehe_Opengl_Tutorials () {
        tutorialNumbers = new String[nrOfLessons];
        
        for (int i = 1; i <= nrOfLessons; i++)
            tutorialNumbers[i-1] = String.valueOf(i);
    }
 
    // Synchronized creator to prevent multi-threading-problems and 
    // check to prevent instanciating more then 1 object
    private synchronized static void createInstance () {
        if (_instance == null) {
            _instance = new Nehe_Opengl_Tutorials ();
        }
    }
 
    public static Nehe_Opengl_Tutorials getInstance () {
        if (_instance == null) 
            createInstance ();
        return _instance;
    }    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Nehe_Opengl_Tutorials.getInstance().showTutorialSelectionMenu();
    }
    
    public void showTutorialSelectionMenu(){
        // destroy the current tutorial if one is running
        if(currentClass != null)
            currentClass = null;
                
        Object returnVal = JOptionPane.showInputDialog(null, 
                                    "Pick a tutorial (Cancel to quit)",
                                    "NEHE's OpenGL tutorials JOGL 2.0", 
                                    JOptionPane.PLAIN_MESSAGE,
                                    null, 
                                    tutorials, 
                                    tutorials[lastSelectedTutorial]);
        
        if(returnVal == null)
            System.exit(0);
        else{
            try {
                int index = 0;
                while (index < tutorials.length && !tutorials[index].equals(returnVal.toString()))
                    index++;
                
                if(index >= 0 && index < tutorials.length) {
                    index++;
                    lastSelectedTutorial = index;
                    if(lastSelectedTutorial >= tutorials.length)
                        lastSelectedTutorial = tutorials.length-1;
                    //currentClass = Class.forName("nehe_opengl_tutorials.tutorial_"+String.valueOf(index)+".Tutorial_"+String.valueOf(index));
                    currentClass = Class.forName("nehe_opengl_tutorials.Tutorial_"+String.valueOf(index));
                    currentClass.newInstance();
                }
                else {
                    throw new Exception("Invalid Index");
                }
            }
            catch(Exception e){
                System.err.println("Could not launch Tutorial "+returnVal);
            }
        }
    }
}
