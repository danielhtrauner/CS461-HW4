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
 * Tutorial 5: 3D shapes with cube & pyramid
 * @author Sebastiaan
 */
public class Tutorial_5 {    
	private GLWindow window;
	private boolean fullscreen = false;
	// NEW holds the angle for pyramid rotation
	private float rotAnglePyramid = 0.0f;
	// NEW holds the angle for cube rotation
	private float rotAngleCube = 0.0f;

	public Tutorial_5() {
		// Choose the GL profile
		GLProfile glp = GLProfile.getMaxFixedFunc();
		// Configure the GL capabilities
		GLCapabilities caps = new GLCapabilities(glp);

		// Create a new GL NEWT window with the GL capabilities
		window = GLWindow.create(caps);
		window.setSize(400, 300);
		window.setVisible(true);
		window.setTitle("Tutorial 5: 3D Shapes");

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
				gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);                                

				// reset the scene to its origin point
				gl.glLoadIdentity();

				// move Right 2.0 Units And Into The Screen 6.0
				gl.glTranslatef(2.0f,0.0f,-6.0f);

				// Rotate The Pyramid On The Y axis (NEW)
				gl.glRotatef(rotAnglePyramid,0.0f,1.0f,0.0f);             

				// adjust the rotation angle
				rotAnglePyramid += 0.2f;

				// draw the pyramid (NEW)
				drawPyramid(gl);

				// reset the scene to its origin point
				gl.glLoadIdentity();

				// Move Left 1.5 Units And Into The Screen 6.0
				gl.glTranslatef(-1.0f,0.0f,-6.0f);                 

				// Rotate The Cube around X and Y Axis (NEW)
				gl.glRotatef(rotAngleCube,1.0f,1.0f,0.0f);            

				// adjust the rotation angle
				rotAngleCube += 0.30f;

				// draw the cube (NEW)
				drawCube(gl);

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
	 * This method draw a pyramid on the screen
	 * @param gl The gl context on which we draw
	 */
	private void drawPyramid(GL2 gl){
		gl.glBegin(GL2.GL_TRIANGLES);		    // Drawing Using Triangles
		gl.glColor3f(1.0f,0.0f,0.0f);			// Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);			// Top Of Triangle (Front)            
		gl.glColor3f(0.0f,1.0f,0.0f);			// Green
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);			// Left Of Triangle (Front)
		gl.glColor3f(0.0f,0.0f,1.0f);			// Blue
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);			// Right Of Triangle (Front)

		gl.glColor3f(1.0f,0.0f,0.0f);			// Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);			// Top Of Triangle (Right)
		gl.glColor3f(0.0f,0.0f,1.0f);			// Blue
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);			// Left Of Triangle (Right)
		gl.glColor3f(0.0f,1.0f,0.0f);			// Green
		gl.glVertex3f( 1.0f,-1.0f, -1.0f);			// Right Of Triangle (Right)

		gl.glColor3f(1.0f,0.0f,0.0f);			// Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);			// Top Of Triangle (Back)
		gl.glColor3f(0.0f,1.0f,0.0f);			// Green
		gl.glVertex3f( 1.0f,-1.0f, -1.0f);			// Left Of Triangle (Back)
		gl.glColor3f(0.0f,0.0f,1.0f);			// Blue
		gl.glVertex3f(-1.0f,-1.0f, -1.0f);			// Right Of Triangle (Back)

		gl.glColor3f(1.0f,0.0f,0.0f);			// Red
		gl.glVertex3f( 0.0f, 1.0f, 0.0f);			// Top Of Triangle (Left)
		gl.glColor3f(0.0f,0.0f,1.0f);			// Blue
		gl.glVertex3f(-1.0f,-1.0f,-1.0f);			// Left Of Triangle (Left)
		gl.glColor3f(0.0f,1.0f,0.0f);			// Green
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);			// Right Of Triangle (Left)
		gl.glEnd();				// Finished Drawing The Pyramid
	}

	/**
	 * This method draws a cube on the screen
	 * @param gl The gl context on which we draw
	 */
	private void drawCube(GL2 gl) { 
		gl.glBegin(GL2.GL_QUADS);                      // Draw A Quad
		gl.glColor3f(0.0f,1.0f,0.0f);          // Set The Color To Green
		gl.glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Top)
		gl.glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Top)
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);          // Bottom Left Of The Quad (Top)
		gl.glVertex3f( 1.0f, 1.0f, 1.0f);          // Bottom Right Of The Quad (Top)

		gl.glColor3f(1.0f,0.5f,0.0f);          // Set The Color To Orange
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);          // Top Right Of The Quad (Bottom)
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);          // Top Left Of The Quad (Bottom)
		gl.glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Bottom)
		gl.glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Bottom)

		gl.glColor3f(1.0f,0.0f,0.0f);          // Set The Color To Red
		gl.glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Front)
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Front)
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Front)
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Front)

		gl.glColor3f(1.0f,1.0f,0.0f);          // Set The Color To Yellow
		gl.glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Back)
		gl.glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Back)
		gl.glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Back)
		gl.glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Back)

		gl.glColor3f(0.0f,0.0f,1.0f);          // Set The Color To Blue
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);          // Top Right Of The Quad (Left)
		gl.glVertex3f(-1.0f, 1.0f,-1.0f);          // Top Left Of The Quad (Left)
		gl.glVertex3f(-1.0f,-1.0f,-1.0f);          // Bottom Left Of The Quad (Left)
		gl.glVertex3f(-1.0f,-1.0f, 1.0f);          // Bottom Right Of The Quad (Left)

		gl.glColor3f(1.0f,0.0f,1.0f);          // Set The Color To Violet
		gl.glVertex3f( 1.0f, 1.0f,-1.0f);          // Top Right Of The Quad (Right)
		gl.glVertex3f( 1.0f, 1.0f, 1.0f);          // Top Left Of The Quad (Right)
		gl.glVertex3f( 1.0f,-1.0f, 1.0f);          // Bottom Left Of The Quad (Right)
		gl.glVertex3f( 1.0f,-1.0f,-1.0f);          // Bottom Right Of The Quad (Right)        
		gl.glEnd();                            // Done Drawing The Quad
	}
}
