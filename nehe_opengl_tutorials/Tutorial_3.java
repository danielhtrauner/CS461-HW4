package nehe_opengl_tutorials;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.opengl.util.FPSAnimator;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import javax.media.opengl.glu.GLU;

import nehe_opengl_tutorials.Nehe_Opengl_Tutorials;

/**
 * Tutorial 3: Adding color to the shapes
 * @author Sebastiaan
 */
public class Tutorial_3 {    
    private GLWindow window;
    private boolean fullscreen = false;
    
    public Tutorial_3() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(300, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 3: Adding Color");

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
                gl.glClearDepth(1.0f);                                          // Depth Buffer Setup
                gl.glEnable(GL2.GL_DEPTH_TEST);					// Enables Depth Testing
                gl.glDepthFunc(GL2.GL_LEQUAL);					// The Type Of Depth Testing To Do
                gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);	// Really Nice Perspective Calculations
		gl.glShadeModel(GL2.GL_SMOOTH);
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
                
                // Clear The Screen And The Depth Buffer
                gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     
                                
                // Sets the background color of our window to black (RGB alpha)
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                
                // reset the scene to its origin point
                gl.glLoadIdentity();
                
                // Move Left 1.5 Units And Into The Screen 6.0
                gl.glTranslatef(-1.5f,0.0f,-6.0f);                 
                
                // draw the quad
                drawQuad(gl);
                
                // move Right 3
                gl.glTranslatef(3.0f,0.0f,0.0f);
                
                // draw the triangle
                drawTriangle(gl);
                
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
                // We stop the tutorial if we press the escape key
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
    
    /**
     * This method draw a triangle on the screen
     */
    private void drawTriangle(GL2 gl){
        // order is always glColor then glVertex
        // glColor remains current color till changed (NEW)

        /**
         * As soon as the glEnd() command is issued, the polygon will be filled in. 
         * But because it has a different color at each vertex, rather than one solid 
         * color throughout, the color will spread out from each corner, eventually 
         * meeting in the middle, where the colors will blend together. This is smooth coloring.
        */
        gl.glBegin(GL2.GL_TRIANGLES);		// Drawing Using Triangles
            gl.glColor3f(1.0f, 0.0f, 0.0f);         // Set the current drawing color to red (NEW)
            gl.glVertex3f( 0.0f, 1.0f, 0.0f);       // Top
            gl.glColor3f(0.0f, 1.0f, 0.0f);         // Set the current drawing color to green (NEW)
            gl.glVertex3f(-1.0f,-1.0f, 0.0f);       // Bottom Left
            gl.glColor3f(0.0f, 0.0f, 1.0f);         // Set the current drawing color to blue (NEW)
            gl.glVertex3f( 1.0f,-1.0f, 0.0f);       // Bottom Right
        gl.glEnd();				// Finished Drawing The Triangle
    }
    
    /**
     * This method draws a quad on the screen
     * @param gl The gl context on which we draw
     */
    private void drawQuad(GL2 gl) { 
        gl.glColor3f(0.5f,0.5f,1.0f);               // Set The Color To Blue Shade One Time Only (NEW)
        gl.glBegin(GL2.GL_QUADS);                   // Draw A Quad
            gl.glVertex3f(-1.0f, 1.0f, 0.0f);           // Top Left
            gl.glVertex3f( 1.0f, 1.0f, 0.0f);           // Top Right
            gl.glVertex3f( 1.0f,-1.0f, 0.0f);           // Bottom Right
            gl.glVertex3f(-1.0f,-1.0f, 0.0f);           // Bottom Left
        gl.glEnd();                                 // Done Drawing The Quad
    }
}
