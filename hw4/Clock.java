// CS461 HW4
// Clock.java
// Daniel Trauner and Will Potter
// Time Spent: 6 hours

// A 3D clock written in OpenGL using JOGL.

package hw4;

import java.awt.Font;
import java.util.Calendar;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.awt.TextRenderer;



public class Clock implements GLEventListener {
    private float aspect = 1;
    private double camDist = -3.60;
    private double camPhi = 0;    // horizontal (azimuth) angle
    private double camTheta = 0;  // vertical (elevation) angle
    private int lightMode = 1;
    private boolean shiftKeyDown = false;
        
    private int mouseDownx, mouseDowny;
    private double mouseDownDist, mouseDownPhi, mouseDownTheta;

    GLU glu = new GLU();
    GLUT glut = new GLUT();
    TextRenderer renderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));

    public static void main(String[] args) {
        System.out.print("Starting...");
        new Clock();
    }

    public Clock() {
        System.out.print("1...");
        GLProfile glp = GLProfile.getDefault();
        System.out.print("2...");
        GLCapabilities caps = new GLCapabilities(glp);
        System.out.println("3");

        GLWindow window = GLWindow.create(caps);
        int ww = 600, wh = 400;
        window.setSize(ww, wh);
        window.setVisible(true);
        window.setTitle("SimpleScene2");
        int sw = window.getScreen().getWidth();
        //System.out.println(sw);
        window.setPosition((sw-ww)/2, 50);
       // window.setPosition(100, 50);

        window.addWindowListener(new WindowAdapter() {
                public void windowDestroyNotify(WindowEvent arg0) {
                    System.exit(0);
                }
            });

        window.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke){
                    switch(ke.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                    case KeyEvent.VK_Q:
                        System.exit(0);
                    case KeyEvent.VK_SHIFT:
                        shiftKeyDown = true; break;
                    case KeyEvent.VK_A:
                        break;
                    case KeyEvent.VK_UP:
                        camDist += shiftKeyDown ? 1 : .2; break;
                    case KeyEvent.VK_DOWN:
                        camDist -= shiftKeyDown ? 1 : .2; break;
                    case KeyEvent.VK_LEFT:
                        camPhi -= 5; break;
                    case KeyEvent.VK_RIGHT:
                        camPhi += 5; break;
                    case KeyEvent.VK_SPACE:
                        camDist = -3; camPhi = 0; camTheta = 0; break;
                    case KeyEvent.VK_1:
                        lightMode = 1; break;
                    case KeyEvent.VK_2:
                        lightMode = 2; break;
                    case KeyEvent.VK_3:
                        lightMode = 3; break;
                    }
                    //System.out.printf("camDist=%.2g  camTheta=%d ortho=%s\n", camDist, camTheta, useOrtho);
                }
                public void keyReleased(KeyEvent ke){
                    switch(ke.getKeyCode()) {
                    case KeyEvent.VK_SHIFT:
                        shiftKeyDown = false; break;
                    }
                }
            });
                
        window.addMouseListener(new MouseListener() {
                public void mousePressed(MouseEvent e) {
                    mouseDownx = e.getX();
                    mouseDowny = e.getY();      
                    mouseDownDist = camDist;
                    mouseDownPhi = camPhi;
                    mouseDownTheta = camTheta;
                }
                public void mouseDragged(MouseEvent e) {
                    int dx =   e.getX() - mouseDownx;
                    int dy = -(e.getY() - mouseDowny);  // screen coords are upside down
                    //System.out.printf("moved mouse by %d, %d\n", dx, dy);
                    if (shiftKeyDown) { // mouse controls zoom
                        double zoomFactor = 0.05;
                        camDist = mouseDownDist + zoomFactor * dy;
                    } else { // mouse controls azimuth / elevation
                        double panFactor = 0.5;
                        camPhi = mouseDownPhi + panFactor * dx;
                        camTheta = Math.min(89, Math.max(-89, mouseDownTheta + panFactor * dy));
                    }
                }
                public void mouseClicked(MouseEvent e) {}
                public void mouseEntered(MouseEvent e) {}
                public void mouseExited(MouseEvent e) {}
                public void mouseReleased(MouseEvent e) {}
                public void mouseMoved(MouseEvent e) {}
                public void mouseWheelMoved(MouseEvent e) {}
            });

        window.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.add(window);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);     // Enables Depth Testing
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);    // draw front faces filled
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE );    // draw back faces with lines
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    public void display(GLAutoDrawable drawable) {
        update();
        render(drawable);
    }

    private void update() {
    }

    private void render(GLAutoDrawable drawable) {
    	Calendar c = Calendar.getInstance();

        GL2 gl = drawable.getGL().getGL2();

        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     

        // Sets the background color of our window to black (RGB alpha)
        gl.glClearColor(0.0f,0.0f,0.0f, 0.0f);

        // reset modelview matrix
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
                
        setCam(gl);  // position camera
        setLights(gl,.5f,.5f,.9f);
        
        // Draw Shapes Here
        
        // Draw Background Cube
        gl.glPushMatrix();
        gl.glTranslated(0, 0, -5);
        glut.glutSolidCube(8);
        gl.glPopMatrix();
        
        // Draw Back Clock Face
        gl.glPushMatrix();
        setLights(gl,1,1,1);
        gl.glTranslated(0, 0, -1.5);
        glut.glutSolidCylinder(1.5, 1, 100, 20);

        // Draw Front Clock Face
        gl.glTranslated(0, 0, 1);
        setLights(gl,0,0,0);
        glut.glutSolidCylinder(1.3, .1, 100, 20);
        gl.glPopMatrix();
        
        // Draw Spheres for Clock Positions
        setLights(gl,.8f,.4f,.4f);
        for(double i=30;i<=360;i+=30) {
        	double rad = i*Math.PI/180; 
        	double size = .05;
	        gl.glTranslated(Math.cos(rad), Math.sin(rad), 0);
	        if(i==90) size = .075;
	        glut.glutSolidSphere(size, 20, 20);
	        gl.glTranslated(-Math.cos(rad), -Math.sin(rad), 0);
        }
        glut.glutSolidSphere(.05, 20, 20);
        // Draw Hour Hand
        gl.glPushMatrix();
        gl.glRotated(-90, Math.sin(hourToRotation(c.get(Calendar.HOUR),c.get(Calendar.MINUTE))), -Math.cos(hourToRotation(c.get(Calendar.HOUR),c.get(Calendar.MINUTE))), 0);
        glut.glutSolidCone(.05, .6, 20, 20);
        gl.glPopMatrix();
        // Draw Minute hand
        gl.glPushMatrix();
        gl.glRotated(-90, Math.sin(minuteToRotation(c.get(Calendar.MINUTE))), -Math.cos(minuteToRotation(c.get(Calendar.MINUTE))), 0);
        glut.glutSolidCone(.05, .8, 20, 20);
        gl.glPopMatrix();
        // Draw Second Hand
        gl.glPushMatrix();
        gl.glRotated(-90, Math.sin(secondToRotation(c.get(Calendar.SECOND))), -Math.cos(secondToRotation(c.get(Calendar.SECOND))), 0);
        glut.glutSolidCone(.02, .8, 20, 20);
        gl.glPopMatrix();

        //renderText(drawable);

        gl.glFlush();
    }
    /**
     * Input an hour on the clock and converts it into the degrees of rotation in radians.
     * @param hour a double that represents the 12-number time format.
     * @param minute a double that represents the number of minutes that have passed
     * @return a double that represents the transformation rotation in radians
     */
    public double hourToRotation(double hour,double minute) {
    	return (-(hour*30+minute*.5)+90)*Math.PI/180;
    }
    /**
     * 
     * @param minute
     * @return
     */
    public double minuteToRotation(double minute) {
    	return (-(minute*6)+90)*Math.PI/180;
    }
    /**
     * 
     * @param second
     * @return
     */
    public double secondToRotation(double second) {
    	return (-(second*6)+90)*Math.PI/180;
    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();

        // avoid a divide by zero error!
        if (height <= 0) height = 1;

        gl.glViewport(0, 0, width, height);
        aspect = (float)width / (float)height;
        setCam(gl);
    }

    public void setLights(GL2 gl,float r,float g, float b) {
        float[] lightAmbient = {.2f, .2f, .2f, 1};
        float[] lightSpecular = {0.5f, 0.5f, 0.5f, 1};
        float[] lightDiffuse = {r, g, b, 1};

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, lightSpecular, 0);


        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glDisable(GL2.GL_LIGHT1);
    }

    public void setCam(GL2 gl) {

        // set projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        double near = 1;
        double far = 20;
        glu.gluPerspective(45.0f, aspect, near, far);
                
        // set camera location and angle
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        double phi = camPhi / 180.0 * Math.PI;
        double thet = camTheta / 180.0 * Math.PI;
        double cx =  camDist * Math.sin(phi) * Math.cos(thet);
        double cz = -camDist * Math.cos(phi) * Math.cos(thet);
        double cy =  camDist * Math.sin(thet);

        glu.gluLookAt(cx, cy, cz, 0, 0, 0, 0, 1, 0);
    }
        

    public void renderText(GLAutoDrawable drawable) {
        int w = drawable.getWidth(), h = drawable.getHeight();
        renderer.setColor(1, 1, 1, 0.8f);
        renderer.beginRendering(w, h);
        String st = "<-,->, Mouse: change viewpoint;  Up/Down Arrows, Shift-Mouse: zoom";
        renderer.draw(st, 5, h-14);
        st = "Space: reset view, 1/2/3: lighting,  Esc/Q: quit";
        renderer.draw(st, 5, h-28);
        st = String.format("camDist=%.2f  camPhi=%d  camTheta=%d  ", camDist, (int)camPhi, (int)camTheta);
        st += "   lighting: " + lightMode;
        renderer.draw(st, 5, 5);
        renderer.endRendering();
    }
}
