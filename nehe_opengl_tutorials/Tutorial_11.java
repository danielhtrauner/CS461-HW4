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
 * Tutorial 11: Flag Effect (Waving Texture)
 * @author Sebastiaan
 */
public class Tutorial_11 {    
    private GLWindow window;
    private boolean fullscreen = false;
    
    private float xrot;                               // X Rotation
    private float yrot;                               // Y Rotation
    private float zrot;                               // Z Rotation Speed (NEW)
    
    private float points[][][];                       // The Array For The Points On The Grid Of Our "Wave" (NEW)
    private int wiggle_count = 0;                     // Counter Used To Control How Fast Flag Waves (NEW)
    private float hold;                               // Temporarily Holds A Floating Point Value (NEW)

    private Texture[] cubeTexture;                    // Array Storing our BMP as texture with different styles
    private int currentFilter = 0;                    // Index indicating our current use texture filter 
    
    public Tutorial_11() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 11: Flag Effect (Waving Texture)");

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
                
                gl.glPolygonMode( GL2.GL_BACK, GL2.GL_FILL );          // Back Face Is Filled In (NEW)
                gl.glPolygonMode( GL2.GL_FRONT, GL2.GL_LINE );         // Front Face Is Drawn With Lines (NEW)
                
                // initialize the points 
                points = new float[45][45][3];
                
                // Loop Through The X Plane
                for(int x = 0;  x < 45; x++) {
                    // Loop Through The Y Plane
                    for(int y=0; y<45; y++) {
                        // Apply The Wave To Our Mesh
                        points[x][y][0] = (float)((x/5.0f)-4.5f);
                        points[x][y][1] = (float)((y/5.0f)-4.5f);
                        points[x][y][2] = (float)(Math.sin((((x/5.0f)*40.0f)/360.0f)*3.141592654*2.0f));
                    }
                }
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
                                
                // Sets the background color of our window to light gray (RGB alpha)
                gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);                                
                
                // reset the scene to its origin point
                gl.glLoadIdentity();
                                
                gl.glTranslatef(0.0f,0.0f,-12.0f);             // Translate 12 Units Into The Screen (NEW)
 
                gl.glRotatef(xrot,1.0f,0.0f,0.0f);             // Rotate On The X Axis (NEW)
                gl.glRotatef(yrot,0.0f,1.0f,0.0f);             // Rotate On The Y Axis (NEW)
                gl.glRotatef(zrot,0.0f,0.0f,1.0f);             // Rotate On The Z Axis (NEW)
                    
                float float_x, float_y, float_xb, float_yb;     // Used To Break The Flag Into Tiny Quads (NEW)
                
                gl.glBegin(GL2.GL_QUADS);                  // Start Drawing Our Quads  (NEW)
                for(int x = 0; x < 44; x++ )                    // Loop Through The X Plane (44 Points)
                {
                    for(int y = 0; y < 44; y++ )                // Loop Through The Y Plane (44 Points)
                    {
                        float_x = (float)((x)/44.0f);           // Create A Floating Point X Value
                        float_y = (float)((y)/44.0f);           // Create A Floating Point Y Value
                        float_xb = (float)((x+1)/44.0f);        // Create A Floating Point Y Value+0.0227f
                        float_yb = (float)((y+1)/44.0f);        // Create A Floating Point Y Value+0.0227f

                        gl.glTexCoord2f( float_x, float_y);    // First Texture Coordinate (Bottom Left)
                        gl.glVertex3f( points[x][y][0], points[x][y][1], points[x][y][2] );

                        gl.glTexCoord2f( float_x, float_yb );  // Second Texture Coordinate (Top Left)
                        gl.glVertex3f( points[x][y+1][0], points[x][y+1][1], points[x][y+1][2] );

                        gl.glTexCoord2f( float_xb, float_yb ); // Third Texture Coordinate (Top Right)
                        gl.glVertex3f( points[x+1][y+1][0], points[x+1][y+1][1], points[x+1][y+1][2] );

                        gl.glTexCoord2f( float_xb, float_y );  // Fourth Texture Coordinate (Bottom Right)
                        gl.glVertex3f( points[x+1][y][0], points[x+1][y][1], points[x+1][y][2] );
                    }
                } 
                gl.glEnd();                        // Done Drawing Our Quads
	
                if( wiggle_count == 2 )                 // Used To Slow Down The Wave (Every 2nd Frame Only) (NEW)
                {
                    for(int y = 0; y < 45; y++ )            // Loop Through The Y Plane
                    {
                        hold = points[0][y][2];           // Store Current Value One Left Side Of Wave
                        for(int x = 0; x < 44; x++)     // Loop Through The X Plane
                            // Current Wave Value Equals Value To The Right
                            points[x][y][2] = points[x+1][y][2];
                        points[44][y][2] = hold;          // Last Value Becomes The Far Left Stored Value
                    }
                    wiggle_count = 0;               // Set Counter Back To Zero
                }
                wiggle_count++;                     // Increase The Counter

                xrot += 0.3f;                     // Increase The X Rotation Variable (NEW) 
                yrot += 0.2f;                     // Increase The Y Rotation Variable (NEW)
                zrot += 0.4f;                     // Increase The Z Rotation Variable (NEW)
                 
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
            URL url = this.getClass().getClassLoader().getResource("./nehe_opengl_tutorials/data/alps.png"); // (CHANGED) 
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
