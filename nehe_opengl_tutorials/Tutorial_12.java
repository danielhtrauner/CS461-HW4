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

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import javax.media.opengl.glu.GLU;

import nehe_opengl_tutorials.Nehe_Opengl_Tutorials;

/**
 * Tutorial 12: Display Lists
 * @author Sebastiaan
 */
public class Tutorial_12 {    
    private GLWindow window;
    private boolean fullscreen = false;
        
    private Texture[] cubeTexture;                    // Array Storing our BMP as texture with different styles
    private int currentFilter = 0;                    // Index indicating our current use texture filter 
    
    private float xrot;
    private float yrot;
    
    private int box;                                        // Storage For The Display List (NEW)
    private int top;                                        // Storage For The Second Display List (NEW)
    private int xloop;                                      // Loop For X Axis (NEW)
    private int yloop;                                      // Loop For Y Axis (NEW)

    private final float BOX_COLORS[][] =                           // Array For Box Colors (NEW)
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

    public Tutorial_12() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 12: Display Lists");

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
                gl.glEnable(GL2.GL_COLOR_MATERIAL);                             // Enable Material Coloring (NEW)      
                gl.glEnable(GL2.GL_TEXTURE_2D);  
                gl.glEnable(GL2.GL_LIGHT0);                                     // Quick And Dirty Lighting (Assumes Light0 Is Set Up) (NEW)
                gl.glEnable(GL2.GL_LIGHTING);                                   // Enable Lighting
      
                createTextures(gl);   
                buildLists(gl);                                                 // Jump To The Code That Creates Our Display Lists  (NEW)
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
                
                
                cubeTexture[currentFilter].bind(gl);
                
                // draws the cubes  (NEW)
                for (yloop = 1; yloop < 6; yloop++)                             // Loop Through The Y Plane
                {
                    for (xloop = 0; xloop < yloop; xloop++)                     // Loop Through The X Plane
                    {
                        gl.glLoadIdentity();                                    // reset the scene to its origin point
                        // Position The Cubes On The Screen
                        gl.glTranslatef(1.4f+((float)xloop*2.8f)-((float)yloop*1.4f), ((6.0f-(float)yloop)*2.4f)-7.0f, -20.0f);

                        gl.glRotatef(45.0f-(2.0f*yloop)+xrot,1.0f,0.0f,0.0f);   // Tilt The Cubes Up And Down
                        gl.glRotatef(45.0f+yrot,0.0f,1.0f,0.0f);                // Spin Cubes Left And Right
                        
                        gl.glColor3fv(BOX_COLORS[yloop-1], 0);                  // Select A Box Color   
                        gl.glCallList(box);                                     // Draw The Box
                        
                        gl.glColor3fv(TOP_COLORS[yloop-1], 0);                  // Select The Top Color
                        gl.glCallList(top);                                     // Draw The Top
                    }
                }
                
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
                    
                    if (ke.getKeyCode() == KeyEvent.VK_LEFT)            // Is Left Arrow Being Pressed (NEW)
                        yrot -= 2f;                                   // Spin The Cube Left
 
                    if (ke.getKeyCode() == KeyEvent.VK_RIGHT)           // Is Right Arrow Being Pressed (NEW)
                        yrot += 2f;                                   // Spin The Cube Right 
                    
                    if (ke.getKeyCode() == KeyEvent.VK_UP)              // Is Up Arrow Being Pressed (NEW)
                        xrot -= 2;                                   // Tilt The Cube Up
 
                    if (ke.getKeyCode() == KeyEvent.VK_DOWN)            // Is Down Arrow Being Pressed (NEW)
                        xrot += 2f;                                   // Tilt The Cube Down 
                    
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
     * Build Box Display List (NEW)
     * @param gl
     */
    private void buildLists(GL2 gl)                                 
    {
        box = gl.glGenLists(2);                              // Building Two Lists
        
        gl.glNewList(box, GL2.GL_COMPILE);                   // New Compiled box Display List
        
        gl.glBegin(GL2.GL_QUADS);                            // Start Drawing Quads
            // Bottom Face
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            // Front Face
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Top Left Of The Texture and Quad
            // Back Face
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
            // Right face
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f, -1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 1.0f, -1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            // Left Face
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Top Right Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
        gl.glEnd();                                          // Done Drawing Quads
                	
        gl.glEndList();                                      // Done Building The box List
        
        top=box+1;                                           // top List Value Is box List Value +1
    
        gl.glNewList(top, GL2.GL_COMPILE);                          // New Compiled top Display List
        
        gl.glBegin(GL2.GL_QUADS);                          // Start Drawing Quad
            // Top Face
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f,  1.0f, -1.0f);  // Top Left Of The Texture and Quad
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,  1.0f,  1.0f);  // Bottom Left Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,  1.0f,  1.0f);  // Bottom Right Of The Texture and Quad
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f,  1.0f, -1.0f);  // Top Right Of The Texture and Quad
        gl.glEnd();                                // Done Drawing Quad

        gl.glEndList();
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
            URL url = this.getClass().getClassLoader().getResource("./nehe_opengl_tutorials/data/cube.bmp"); // (CHANGED) 
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
