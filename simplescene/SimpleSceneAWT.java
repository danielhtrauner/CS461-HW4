// SimpleSceneAWT.java
// Simple JOGL program to display rotating triangle using AWT
// derived from Justin's JOGL tutorials https://sites.google.com/site/justinscsstuff/jogl-tutorials

package simplescene;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class SimpleSceneAWT implements GLEventListener {
    
    public static void main(String[] args) {
        new SimpleSceneAWT();
    }

    // instance variable for rotating triangle
    private double theta = 0;

    // constructor
    public SimpleSceneAWT() {
        //System.out.print("Getting profile...");
        GLProfile glp = GLProfile.getDefault();
        //System.out.print("...");
        GLCapabilities caps = new GLCapabilities(glp);
        //System.out.println();

        GLCanvas canvas = new GLCanvas(caps);
        Frame frame = new Frame("AWT Window Test");
        frame.setSize(800, 600);
        frame.add(canvas);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        canvas.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(canvas, 60);
        //Animator animator = new Animator(canvas);
        animator.add(canvas);
        animator.start();
    }

    // Need these because we're implementing GLEventListener
    public void init(GLAutoDrawable drawable) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

    // this is continually called from the animator
    public void display(GLAutoDrawable drawable) {
        update();
        render(drawable);
    }
    
    // update instance variables here
    private void update() {
        theta += 0.02;
    }
    
    // the actual rendering
    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        double s = Math.sin(theta);
        double c = Math.cos(theta);

        // draw a triangle filling the window
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex2d(-c, -c);
        gl.glColor3f(0, 1, 0);
        gl.glVertex2d(0, c);
        gl.glColor3f(0, 0, 1);
        gl.glVertex2d(s, -s);
        gl.glEnd();
    }
}
