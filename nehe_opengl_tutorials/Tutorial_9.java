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

//import java.nio.FloatBuffer;
import java.util.Random;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import javax.media.opengl.glu.GLU;

import nehe_opengl_tutorials.Nehe_Opengl_Tutorials;

/**
 * Tutorial 9: Moving Bitmaps In 3D Space
 * @author Sebastiaan
 */
public class Tutorial_9 {    
    private final int NUMBER_OF_STARS = 50;         // the number of stars to draw (NEW)
    
    private GLWindow window;
    private boolean fullscreen = false;
    
    private float zoom = -15.0f;                    // Viewing Distance Away From Stars (NEW)
    private float tilt = 90.0f;                     // Tilt The View (NEW)
    private float spin;                             // Spin Twinkling Stars (NEW)
     
    private boolean lightning =  true;              // Lightning flag 
    private boolean blending = true;                // Blending flag
    private boolean twinkle = true;                 // Twinkle flag (NEW)
    
    //private FloatBuffer lightAmbient= FloatBuffer.wrap(new float[]{ 0.5f, 0.5f, 0.5f, 1.0f });        // Ambient Light Values
    //private FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{ 1.0f, 1.0f, 1.0f, 1.0f });       // Diffuse Light Values     
    //private FloatBuffer LightPosition = FloatBuffer.wrap(new float[]{ 0.0f, 0.0f, 2.0f, 1.0f });      // Light Position
    
    private Texture starTexture;                                      // Array Storing our BMP as texture with different styles (NEW)
    private Star[] stars;                                             // Array storing our stars (NEW)
    
    public Tutorial_9() {
        // create the stars (NEW)
        stars = new Star[NUMBER_OF_STARS];
        for(int i = 0; i < NUMBER_OF_STARS; i++) {
            stars[i] = new Star();
            stars[i].setDistance(((float)i/(float)NUMBER_OF_STARS)*5.0f);     // Calculate Distance From The Center
        }
        
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 9: Moving Bitmaps In 3D Space");

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
                gl.glEnable(GL2.GL_TEXTURE_2D);							// Enable Texture Mapping
                gl.glShadeModel(GL2.GL_SMOOTH);							// Enable Smooth Shading
                gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);				// Black Background
                gl.glClearDepth(1.0f);									// Depth Buffer Setup
                gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);	// Really Nice Perspective Calculations
                gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE);					// Set The Blending Function For Translucency
                gl.glEnable(GL2.GL_BLEND);
                
                createTextures(gl);
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
                                
                // Sets the background color of our window to black (RGB alpha)
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);                                
                                
                // draw stars
                drawStars(gl);
                
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
                                        
                    // enable or disable lightning by pressing the 'l' key                    
                    if(ke.getKeyCode() == KeyEvent.VK_L)
                        lightning = !lightning;
                                        
                    // enable or disable blending by pressing the 'b' key  
                    if(ke.getKeyCode() == KeyEvent.VK_B)
                        blending = !blending;
                    
                    // enable or disable twinkling by pressing the 't' key (NEW)
                    if(ke.getKeyCode() == KeyEvent.VK_T)
                        twinkle = !twinkle;   
                    
                    if (ke.getKeyCode() == KeyEvent.VK_UP)              // Is Up Arrow Being Pressed (CHANGED)
                        tilt-=0.5f;                                     // Tilt The Screen Up
 
                    if (ke.getKeyCode() == KeyEvent.VK_DOWN)            // Is Down Arrow Being Pressed (CHANGED)
                        tilt+=0.5f;                                     // Tilt The Screen Down 
                    
                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP)         // Is Page Up Being Pressed (CHANGED)
                        zoom-=0.2f;                                     // Zoom Out
 
                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN)       // Is Page Down Being Pressed (CHANGED)
                        zoom+=0.2f;                                     // Zoom In
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
     * Draws the stars on the screen (NEW)
     */
    private void drawStars(GL2 gl) {
                    
        for (int loop = 0; loop < NUMBER_OF_STARS; loop++)                  // Loop Through All The Stars
        {
            gl.glLoadIdentity();                                            // Reset The View Before We Draw Each Star
            gl.glTranslatef(0.0f,0.0f,zoom);                                // Zoom Into The Screen (Using The Value In 'zoom')
            gl.glRotatef(tilt,1.0f,0.0f,0.0f);                              // Tilt The View (Using The Value In 'tilt')
            
            gl.glRotatef(stars[loop].getAngle(),0.0f,1.0f,0.0f);            // Rotate To The Current Stars Angle
            gl.glTranslatef(stars[loop].getDistance(),0.0f,0.0f);           // Move Forward On The X Plane
            
            gl.glRotatef(-stars[loop].getAngle(),0.0f,1.0f,0.0f);           // Cancel The Current Stars Angle
            gl.glRotatef(-tilt,1.0f,0.0f,0.0f);                             // Cancel The Screen Tilt
            
            starTexture.bind(gl);
            
            if (twinkle)                                                    // Twinkling Stars Enabled
            {
                // Assign A Color Using Bytes
                gl.glColor4ub((byte)stars[NUMBER_OF_STARS-loop-1].getR(), (byte)stars[NUMBER_OF_STARS-loop-1].getG(), (byte)stars[NUMBER_OF_STARS-loop-1].getB(), (byte)255);
                gl.glBegin(GL2.GL_QUADS);               // Begin Drawing The Textured Quad
                    gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,-1.0f, 0.0f);
                    gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,-1.0f, 0.0f);
                    gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, 1.0f, 0.0f);
                    gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 0.0f);
                gl.glEnd();                // Done Drawing The Textured Quad
            }
            
            gl.glRotatef(spin,0.0f,0.0f,1.0f);                              // Rotate The Star On The Z Axis
            
            // Assign A Color Using Bytes
            gl.glColor4ub((byte)stars[loop].getR(), (byte)stars[loop].getG(), (byte)stars[loop].getB(), (byte)255);
            gl.glBegin(GL2.GL_QUADS);              // Begin Drawing The Textured Quad
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 1.0f,-1.0f, 0.0f);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, 1.0f, 0.0f);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 0.0f);
            gl.glEnd();                    // Done Drawing The Textured Quad
            
            spin+=0.01f;                    // Used To Spin The Stars
            stars[loop].setAngle(stars[loop].getAngle() + ((float)loop/(float)NUMBER_OF_STARS));      // Changes The Angle Of A Star
            stars[loop].setDistance(stars[loop].getDistance() - 0.01f);             // Changes The Distance Of A Star
            
            if (stars[loop].getDistance() <0.0f)                                // Is The Star In The Middle Yet
            {   
                stars[loop].setDistance(stars[loop].getDistance()+5.0f);        // Move The Star 5 Units From The Center
                stars[loop].setR(new Random().nextInt(256));                     // Give It A New Red Value
                stars[loop].setG(new Random().nextInt(256));                     // Give It A New Green Value
                stars[loop].setB(new Random().nextInt(256));                     // Give It A New Blue Value
            }
        }
    }
    
    /**
     * Creates our texture
     * @param gl 
     */
    private void createTextures(GL2 gl) {
        // load the texture for the star
        starTexture = loadTexture();

        // Something went wrong loading texture, return to selecting menu
        if(starTexture == null) closeTutorial();

        // Create Linear Filtered Texture
        starTexture.enable(gl);                                          // enable the texture we just loaded
        starTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        starTexture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR);
    }
    
    /**
     * Loads the texture for the cube
     * @return The texture made of the image
     */
    private Texture loadTexture() {
        Texture texture = null;
        File imgFile = null;
        
        try {
            URL url = this.getClass().getClassLoader().getResource("./nehe_opengl_tutorials/data/star.bmp");
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
