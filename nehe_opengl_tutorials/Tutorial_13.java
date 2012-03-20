package nehe_opengl_tutorials;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.opengl.util.gl2.GLUT;
//import com.jogamp.opengl.util.texture.Texture;
//import com.jogamp.opengl.util.texture.TextureIO;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;

//import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import javax.media.opengl.glu.GLU;

import nehe_opengl_tutorials.Nehe_Opengl_Tutorials;

/**
 * Tutorial 13: Bitmap Fonts
 * @author Sebastiaan
 */
public class Tutorial_13 {    
    private GLWindow window;
    private boolean fullscreen = false;
    
    private GLUT glut;                                              // GLUT needed to print string as BMP (NEW)
    
    //private int base;                                               // Base Display List For The Font Set (NEW)
    private float counterOne;                                       // 1st Counter Used To Move Text & For Coloring (NEW)
    private float counterTwo;                                       // 2nd Counter Used To Move Text & For Coloring (NEW)
/*
    private final float BOX_COLORS[][] =                            // Array For Box Colors (NEW)
    {
        // Bright:  Red, Orange, Yellow, Green, Blue
        {1.0f,0.0f,0.0f},
        {1.0f,0.5f,0.0f},
        {1.0f,1.0f,0.0f},
        {0.0f,1.0f,0.0f},
        {0.0f,1.0f,1.0f}
    };
 
    private final float TOP_COLORS[][]=                                // Array For Top Colors (NEW)
    {
        // Dark:  Red, Orange, Yellow, Green, Blue
        {.5f,0.0f,0.0f},
        {0.5f,0.25f,0.0f},
        {0.5f,0.5f,0.0f},
        {0.0f,0.5f,0.0f},
        {0.0f,0.5f,0.5f}
    };
*/
    public Tutorial_13() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 13: Bitmap Fonts");

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent arg0) {
                closeTutorial();
            };
        });
        
        // Add a GL eventlistener to the window
        window.addGLEventListener(new GLEventListener() {        
            /**
             * This method is called when we first create our opengl window
             */    
            @Override
            public void init(GLAutoDrawable drawable) {
                final GL2 gl = drawable.getGL().getGL2();      
                // Sets the background color of our window to black (RGB alpha)
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);                
                gl.glClearDepth(1.0f);                                          // Depth Buffer Setup
                gl.glEnable(GL2.GL_DEPTH_TEST);					// Enables Depth Testing                
                gl.glDepthFunc(GL2.GL_LEQUAL);					// The Type Of Depth Testing To Do
                
                gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);	// Really Nice Perspective Calculations
                
		gl.glShadeModel(GL2.GL_SMOOTH);
                
                gl.glEnable(GL2.GL_COLOR_MATERIAL);                             // Enable Material Coloring (NEW)      
                gl.glEnable(GL2.GL_TEXTURE_2D);  
                
                gl.glEnable(GL2.GL_LIGHT0);                                     // Quick And Dirty Lighting (Assumes Light0 Is Set Up) (NEW)
                gl.glEnable(GL2.GL_LIGHTING);                                   // Enable Lighting
            }

            /**
             * This method is called when we destroy our opengl window
             */
            @Override
            public void dispose(GLAutoDrawable drawable) {}

            /**
             * Here happens the actual opengl drawing
             */
            @Override
            public void display(GLAutoDrawable drawable) {
                // get the GL2 context on which we can draw
                final GL2 gl = drawable.getGL().getGL2();
                glut = new GLUT();
                
                // Clear The Screen And The Depth Buffer
                gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     
                                                
                gl.glLoadIdentity();                            // Reset The View
                gl.glTranslatef(0.0f,0.0f,-1.0f);               // Move One Unit Into The Screen (NEW)
                
                // Pulsing Colors Based On Text Position (NEW)
                float red = (float)Math.cos(counterOne);
                float green = (float)Math.sin(counterTwo);
                float blue = (float)Math.cos(counterOne+counterTwo);
                gl.glColor3f(red, green, 1.0f-0.5f*blue);
                
                // Position The Text On The Screen (NEW)
                float posX = (float)Math.cos(counterOne);
                float posY = (float)Math.sin(counterTwo);
                
                gl.glRasterPos2f(0.05f * (posX - 9.0f), 0.35f * posY);
                
                // Print GL Text To The Screen (NEW)
                //Take a string and make it a bitmap, put it in the 'gl' passed over and pick
                //the GLUT font, then provide the string to show
                
                glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18,
                                        "Active OpenGL, in JoGL, Text With NeHe - " + counterOne); 
                    
                counterOne += 0.051f;                       // Increase The First Counter (NEW)
                counterTwo += 0.005f;                       // Increase The Second Counter (NEW)
                
                gl.glFlush();
            }

            /**
             * This method is called when our window his dimension is changed
             */
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                  final GL2 gl = drawable.getGL().getGL2();
                  final GLU glu = new GLU();

                  if (height <= 0) // avoid a divide by zero error!
                    height = 1;
                  
                  gl.glViewport(0, 0, width, height);
                  // change matrixmode to projection for perspective
                  gl.glMatrixMode(GL2.GL_PROJECTION);
                  gl.glLoadIdentity();
                  
                  final float h = (float)width / (float)height;
                  glu.gluPerspective(45.0f, h, 1.0, 20.0);
                  // restore the matrix mode to modelview
                  gl.glMatrixMode(GL2.GL_MODELVIEW);
                  gl.glLoadIdentity();
            }
        });
            
        // add a KeyListener so we can respond to user input
        window.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent ke){
                    if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
                        closeTutorial();
                    
                    if(ke.getKeyCode() == KeyEvent.VK_F1){
                        fullscreen = !fullscreen;
                        window.setFullscreen(fullscreen);
                    }
                }
            }
        );
        
        // create a new FPS Animator
        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.add(window);
        animator.start();
    }
    
    /**
     * Closes the tutorial and retuns to the selection menu
     */
    private void closeTutorial(){
        if(window != null){
            window.setVisible(false);
            window.destroy();                
            // Show the tutorial menu again
            Nehe_Opengl_Tutorials.getInstance().showTutorialSelectionMenu();
        }
    }
}
