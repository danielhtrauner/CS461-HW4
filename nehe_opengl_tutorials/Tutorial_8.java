package nehe_opengl_tutorials;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.nio.FloatBuffer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import javax.media.opengl.glu.GLU;

import nehe_opengl_tutorials.Nehe_Opengl_Tutorials;

/**
 * Tutorial 8: Blending
 * @author Sebastiaan
 */
public class Tutorial_8 {    
    private GLWindow window;
    private boolean fullscreen = false;
    
    private float xrot;                               // X Rotation
    private float yrot;                               // Y Rotation
    private float xspeed;                             // X Rotation Speed
    private float yspeed;                             // Y Rotation Speed
    private float z=-5.0f;                            // Depth Into The Screen
    
    private boolean lightning =  true;                // Lightning flag 
    private boolean blending = true;                  // Blending flag (NEW)
    private FloatBuffer lightAmbient= FloatBuffer.wrap(new float[]{ 0.5f, 0.5f, 0.5f, 1.0f });        // Ambient Light Values
    private FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{ 1.0f, 1.0f, 1.0f, 1.0f });       // Diffuse Light Values     
    private FloatBuffer LightPosition = FloatBuffer.wrap(new float[]{ 0.0f, 0.0f, 2.0f, 1.0f });      // Light Position
    
    private Texture[] cubeTexture;                                      // Array Storing our BMP as texture with different styles
    private int currentFilter = 0;                                      // Index indicating our current use texture filter 
    
    public Tutorial_8() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 8: Blending");

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
                
                createTextures(gl);               
                
                // Setup the lightning
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient);             // Setup The Ambient Light
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse);             // Setup The Diffuse Light
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, LightPosition);           // Setup The Position of the Light
                gl.glEnable(GL2.GL_LIGHT1);                                            // Enable Light One
                gl.glEnable(GL2.GL_LIGHTING);
                
                // setup the blending
                gl.glColor4f(1.0f,1.0f,1.0f,0.5f);                  // Full Brightness, 50% Alpha ( NEW )
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);       // Blending Function For Translucency Based On Source Alpha Value ( NEW )
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
                
                // disable or enable lightning
                if(lightning) gl.glEnable(GL2.GL_LIGHTING);
                else gl.glDisable(GL2.GL_LIGHTING);
                
                //disable or enable blending (NEW)
                if(blending) gl.glEnable(GL2.GL_BLEND);
                else gl.glDisable(GL2.GL_BLEND);
                
                // Clear The Screen And The Depth Buffer
                gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);     
                                
                // Sets the background color of our window to light gray (RGB alpha)
                gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);                                
                
                // reset the scene to its origin point
                gl.glLoadIdentity();
                                
                gl.glTranslatef(0.0f,0.0f,z);               // Translate Into/Out Of The Screen By z 
 
                gl.glRotatef(xrot,1.0f,0.0f,0.0f);          // Rotate On The X Axis By xrot 
                gl.glRotatef(yrot,0.0f,1.0f,0.0f);          // Rotate On The Y Axis By yrot
	
                xrot+=xspeed;                               // Add xspeed To xrot
                yrot+=yspeed;                               // Add yspeed To yrot
                
                // draw the cube
                drawTexturedCube(gl);
                            
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
                    
                    // change texture filter by pressing the 'f' key                    
                    if(ke.getKeyCode() == KeyEvent.VK_F)
                        currentFilter = (currentFilter+1)%cubeTexture.length;
                    
                    // enable or disable lightning by pressing the 'l' key                    
                    if(ke.getKeyCode() == KeyEvent.VK_L)
                        lightning = !lightning;
                                        
                    // enable or disable blending by pressing the 'b' key (NEW)           
                    if(ke.getKeyCode() == KeyEvent.VK_B)
                        blending = !blending;
                    
                    
                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP)              // Is Page Down Being Pressed? 
                        z+=0.05f;               // If So, Move Towards The Viewer

                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN)              // Is Page Down Being Pressed? 
                        z-=0.05f;               // If So, Move Towards The Viewer
    
                    if (ke.getKeyCode() == KeyEvent.VK_UP)                // Is Up Arrow Being Pressed? 
                        xspeed -= 0.05f;                                    // If So, Decrease xspeed
                    
                    if (ke.getKeyCode() == KeyEvent.VK_DOWN)              // Is Down Arrow Being Pressed? 
                        xspeed += 0.05f;                                    // If So, Increase xspeed
                    
                    if (ke.getKeyCode() == KeyEvent.VK_RIGHT)             // Is Right Arrow Being Pressed?
                        yspeed += 0.05f;                                    // If So, Increase yspeed
                    
                    if (ke.getKeyCode() == KeyEvent.VK_LEFT)              // Is Left Arrow Being Pressed?
                        yspeed -= 0.05f;                                    // If So, Decrease yspeed                    
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
     * This method draws a textured cube on the screen (NEW)
     * @param gl The gl context on which we draw
     */
    private void drawTexturedCube(GL2 gl) { 
        // Use the texture on our cube
        cubeTexture[currentFilter].bind(gl);
        
        gl.glBegin(GL2.GL_QUADS);
            // Front Face    
            gl.glNormal3f( 0.0f, 0.0f, 1.0f);                  // Normal Pointing Towards Viewer
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Top Left Of The Texture and Quad
            // Back Face
            gl.glNormal3f( 0.0f, 0.0f,-1.0f);                  // Normal Pointing Away From Viewer
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
            // Top Face
            gl.glNormal3f( 0.0f, 1.0f, 0.0f);                  // Normal Pointing Up
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
            // Bottom Face
            gl.glNormal3f( 0.0f,-1.0f, 0.0f);                  // Normal Pointing Down
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            // Right face
            gl.glNormal3f( 1.0f, 0.0f, 0.0f);                  // Normal Pointing Right
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            // Left Face
            gl.glNormal3f( 1.0f, 0.0f, 0.0f);                  // Normal Pointing Right
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
        gl.glEnd();
    }
    
    /**
     * Creates our texture with 3 different filters
     * @param gl 
     */
    private void createTextures(GL2 gl) {
        // gonna use 3 different types of textures
        cubeTexture = new Texture[3];

        // load the texture for the cube
        cubeTexture[0] = loadTexture();

        // Something went wrong loading texture, return to selecting menu
        if(cubeTexture[0] == null) closeTutorial();

        // Create Nearest Filtered Texture
        cubeTexture[0].enable(gl);                                          // enable the texture we just loaded
        cubeTexture[0].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        cubeTexture[0].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_NEAREST);

        // load the texture for the cube
        cubeTexture[1] = loadTexture();

        // Create Linear Filtered Texture
        cubeTexture[1].enable(gl);                                          // enable the texture we just loaded
        cubeTexture[1].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        cubeTexture[1].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR);

        // load the texture for the cube
        cubeTexture[2] = loadTexture();

        // Create MipMapped Texture
        cubeTexture[2].enable(gl);
        cubeTexture[2].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        cubeTexture[2].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR_MIPMAP_NEAREST);
    }
    
    /**
     * Loads the texture for the cube
     * @return The texture made of the image
     */
    private Texture loadTexture() {
        Texture texture = null;
        File imgFile = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource("./nehe_opengl_tutorials/data/glass.png"); // (CHANGED) 
            imgFile = new File(url.getFile());
            texture = TextureIO.newTexture(imgFile, true);// bool - mipmap.
        } 
        catch (IOException e) {
            System.out.println("fail openning file... " + imgFile.getName());
            System.out.println(e);
        }
        
        return texture;
    }    
}
