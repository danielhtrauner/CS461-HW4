// SimpleSceneNEWT.java
// Simple JOGL program to display rotating teapot using NEWT
// derived from Justin's JOGL tutorials https://sites.google.com/site/justinscsstuff/jogl-tutorials

package simplescene;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;   // for camera setting methods
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;  // for sphere, teapot, etc.

public class SimpleSceneNEWT implements GLEventListener {

    public static void main(String[] args) {
        new SimpleSceneNEWT();
    }

    // instance variables
    private double theta = 0;
    private double camDist = -3;

    // constructor
    public SimpleSceneNEWT() {
        //System.out.print("Getting profile...");
        GLProfile glp = GLProfile.getDefault();
        //System.out.print("...");
        GLCapabilities caps = new GLCapabilities(glp);
        //System.out.println();

        GLWindow window = GLWindow.create(caps);
        window.setTitle("NEWT Window Test");
        window.setSize(800, 600);
        window.setVisible(true);
        window.setPosition(100, 50);
        //window.setPosition(1800, 50); // for projection

        window.addWindowListener(new WindowAdapter() {
            public void windowDestroyNotify(WindowEvent e) {
                System.exit(0);
            }
        });

        window.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke){
                switch(ke.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                case KeyEvent.VK_Q:
                    System.exit(0);
                case KeyEvent.VK_UP:
                    camDist += .2; break;
                case KeyEvent.VK_DOWN:
                    camDist -= .2; break;
                }
            }
        });

        window.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(window, 60);
        //Animator animator = new Animator(canvas);
        animator.add(window);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();      
        gl.glEnable(GL2.GL_DEPTH_TEST);                    // Enables Depth Testing
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);    // draw front faces filled
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE );    // draw back faces with lines
    }

    public void dispose(GLAutoDrawable drawable) {}

    public void display(GLAutoDrawable drawable) {
        update();
        render(drawable);
    }

    private void update() {
        theta += 1;  // openGL measure angles in degrees!
    }

    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLUT glut = new GLUT();
        
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     

        // Sets the background color of our window to black (RGB alpha)
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // reset modelview matrix
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // translate camera away from scene
        gl.glTranslated(0, 0, camDist);

        // draw a yellow wire frame cube
        gl.glColor3d(1, 1, 0);
        glut.glutWireCube(1);

        // rotate around y axis
        gl.glRotated(theta, 0, 1, 0);

        // draw a red object
        gl.glColor3d(1, 0, 0);
        //glut.glutSolidSphere(.5, 20, 20);

        // for teapot to render correctly, need to define front of faces in CW order
        gl.glFrontFace(GL.GL_CW);
        glut.glutSolidTeapot(.6);
        gl.glFrontFace(GL.GL_CCW);
        
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();

        // avoid a divide by zero error
        if (height <= 0) height = 1;
        
        gl.glViewport(0, 0, width, height);
        // change matrixmode to projection for perspective
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        final float h = (float)width / (float)height;
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
    }
}
