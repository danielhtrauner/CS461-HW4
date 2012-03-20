/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nehe_opengl_tutorials;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;

import com.jogamp.newt.opengl.GLWindow;

import com.jogamp.opengl.util.FPSAnimator;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Tutorial 10: Loading And Moving Through A 3D World
 * @author Sebastiaan
 */
public class Tutorial_10 {  
    private final String WORLD_FILE_PATH = "./nehe_opengl_tutorials/data/world.txt";        // path to our world file (NEW)
    private final String TEXTURE_PATH = "./nehe_opengl_tutorials/data/mud.bmp";             // path to our texture file (NEW)
    private final float ONE_DEGREE_RADIANS = 0.01745329251994329576923690768489f;
    
    private Sector sector;                            // our sector we load from the file (NEW)
    
    private GLWindow window;
    private boolean fullscreen = false;
    private float heading;
    private float xpos;                               // X position
    private float yrot;                               // Y rotation
    private float zpos = 5.0f;                        // Z position
    private float walkbias = 0.0f;                    // 
    private float walkbiasangle = 0.0f;               //
    private float lookupdown = 0.0f;                  //
    
    private boolean lightning = false;                // Lightning flag 
    private boolean blending = false;                  // Blending flag 
    private FloatBuffer lightAmbient= FloatBuffer.wrap(new float[]{ 0.5f, 0.5f, 0.5f, 1.0f });        // Ambient Light Values
    private FloatBuffer lightDiffuse = FloatBuffer.wrap(new float[]{ 1.0f, 1.0f, 1.0f, 1.0f });       // Diffuse Light Values     
    private FloatBuffer LightPosition = FloatBuffer.wrap(new float[]{ 0.0f, 0.0f, 2.0f, 1.0f });      // Light Position
    
    private Texture[] cubeTexture;                                      // Array Storing our BMP as texture with different styles
    private int currentFilter = 0;                                      // Index indicating our current use texture filter 
    
    public Tutorial_10() {
        // Choose the GL profile
        GLProfile glp = GLProfile.getMaxFixedFunc();
        // Configure the GL capabilities
        GLCapabilities caps = new GLCapabilities(glp);

        // Create a new GL NEWT window with the GL capabilities
        window = GLWindow.create(caps);
        window.setSize(400, 300);
        window.setVisible(true);
        window.setTitle("Tutorial 10: Loading And Moving Through A 3D World");

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
                      
                
                // Setup the lightning
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient);             // Setup The Ambient Light
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse);             // Setup The Diffuse Light
                gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, LightPosition);           // Setup The Position of the Light
                gl.glEnable(GL2.GL_LIGHT1);                                            // Enable Light One
                gl.glEnable(GL2.GL_LIGHTING);
                
                // setup the blending
                gl.glColor4f(1.0f,1.0f,1.0f,0.5f);                  // Full Brightness, 50% Alpha ( NEW )
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);       // Blending Function For Translucency Based On Source Alpha Value ( NEW )
                
                createTextures(gl);
        
                setupWorld();
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
                     
                float xtrans = -xpos;                           // Used For Player Translation On The X Axis
                float ztrans = -zpos;                           // Used For Player Translation On The Z Axis
                float ytrans = -walkbias-0.25f;               // Used For Bouncing Motion Up And Down
                float sceneroty = 360.0f - yrot;              // 360 Degree Angle For Player Direction
                gl.glLoadIdentity();
                gl.glRotatef(lookupdown,1.0f,0,0);                 // Rotate Up And Down To Look Up And Down
                gl.glRotatef(sceneroty, 0, 1.0f ,0);                  // Rotate Depending On Direction Player Is Facing

                gl.glTranslatef(xtrans, ytrans, ztrans);               // Translate The Scene Based On Player Position
                cubeTexture[currentFilter].bind(gl);         // Select A Texture Based On filter

                // Process Each Triangle
                for (int counterTriangles = 0; counterTriangles < sector.getNumberOfTriagles(); counterTriangles++)        // Loop Through All The Triangles
                {
                    gl.glBegin(GL2.GL_TRIANGLES);                  // Start Drawing Triangles
                        gl.glNormal3f( 0.0f, 0.0f, 1.0f);          // Normal Pointing Forward
                        // process each vertex in our triangle
                        for(Vertex v : sector.getTriangleAt(counterTriangles).getVertexArray()){
                            gl.glTexCoord2f(v.getU(), v.getV());                           // Set The TexCoord And Vertice
                            gl.glVertex3f(v.getX(), v.getY(), v.getZ());                                                     
                        }
                    gl.glEnd();                                                 // Done Drawing Triangles
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
                    
                    // enable or disable lightning by pressing the 'l' key                    
                    if(ke.getKeyCode() == KeyEvent.VK_L)
                        lightning = !lightning;
                                        
                    // enable or disable blending by pressing the 'b' key (NEW)           
                    if(ke.getKeyCode() == KeyEvent.VK_B)
                        blending = !blending;
                    
                    
                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_UP)              // Is Page Down Being Pressed?  (CHANGED)
                        lookupdown += 5.0f;                                     // Look up

                    if (ke.getKeyCode() == KeyEvent.VK_PAGE_DOWN)              // Is Page Down Being Pressed?  (CHANGED)
                        lookupdown -= 5.0f;                                     // Look down
    
                    if (ke.getKeyCode() == KeyEvent.VK_UP)                             // Is The Up Arrow Being Pressed?(CHANGED)
                    {
                        xpos -= (float)Math.sin(heading*ONE_DEGREE_RADIANS) * 0.05f;          // Move On The X-Plane Based On Player Direction                        
                        zpos -= (float)Math.cos(heading*ONE_DEGREE_RADIANS) * 0.05f;          // Move On The Z-Plane Based On Player Direction
                        
                        if (walkbiasangle >= 359.0f)                 // Is walkbiasangle>=359?
                            walkbiasangle = 0.0f;                   // Make walkbiasangle Equal 0
                        else                                // Otherwise
                             walkbiasangle+= 10;                    // If walkbiasangle < 359 Increase It By 10
                        
                        walkbias = (float)Math.sin(walkbiasangle * ONE_DEGREE_RADIANS)/20.0f;     // Causes The Player To Bounce
                    }

                    if (ke.getKeyCode() == KeyEvent.VK_DOWN)                            // Is The Down Arrow Being Pressed? (CHANGED)
                    {
                         xpos += (float)Math.sin(heading*ONE_DEGREE_RADIANS) * 0.05f;          // Move On The X-Plane Based On Player Direction
                         zpos += (float)Math.cos(heading*ONE_DEGREE_RADIANS) * 0.05f;          // Move On The Z-Plane Based On Player Direction
                         
                         if (walkbiasangle <= 1.0f)                   // Is walkbiasangle<=1?
                             walkbiasangle = 359.0f;                 // Make walkbiasangle Equal 359
                         else                                // Otherwise
                             walkbiasangle-= 10;                 // If walkbiasangle > 1 Decrease It By 10
                         
                         walkbias = (float)Math.sin(walkbiasangle * ONE_DEGREE_RADIANS)/20.0f;     // Causes The Player To Bounce
                    }
                     
                    if (ke.getKeyCode() == KeyEvent.VK_RIGHT)   {          // Is Right Arrow Being Pressed?
                        heading -= 1.5f;                                    // If So, Rotate Right (CHANGED)
                        yrot = heading;
                    }
                    
                    if (ke.getKeyCode() == KeyEvent.VK_LEFT) {             // Is Left Arrow Being Pressed?
                        heading += 1.5f;                                  // If So, Rotate LEFT (CHANGED)
                        yrot = heading;
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
    
    // Loads the world from a txt file (NEW)
    private void setupWorld()                           // Setup Our World
    {
        File worldFile = null;
        try {
            URL url = this.getClass().getClassLoader().getResource(WORLD_FILE_PATH);
            worldFile = new File(url.getFile());
        } 
        catch (Exception e) {
            System.out.println("failed openning file... " + WORLD_FILE_PATH);
            System.out.println(e);
            closeTutorial();
        }
        
        try {
            System.out.println("STARTING setup world");
            // read the data
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(worldFile)));
            
            String line = br.readLine();
            // search for number of triangles to load
            while (line != null && !line.startsWith("NUMPOLLIES"))
                line = br.readLine();
            
            int numberOfTriangles = Integer.parseInt(line.substring(11));
            sector = new Sector(numberOfTriangles);
            
            // now keep reading till we have processed 36 triangles
            // 1 triangle = 3 lines containing 1 vertex definition each
            for(int i = 0; i < numberOfTriangles; i++){
                // keep reading if line is not starting with a - or a number
                while(line.isEmpty() || (line.charAt(0) != '-' && !Character.isDigit(line.trim().charAt(0))))
                    line = br.readLine();
                
                Triangle triangle = new Triangle(3);
                // read next 3 lines and parse them to a triangle
                for(int j = 0; j < 3; j++){
                    // found a line for our vertex, now process it
                    String parts[] = line.trim().split("\\s+");
                
                    Vertex v = new Vertex();
                    v.setX(Float.parseFloat(parts[0]));
                    v.setY(Float.parseFloat(parts[1]));
                    v.setZ(Float.parseFloat(parts[2]));
                    v.setU(Float.parseFloat(parts[3]));
                    v.setV(Float.parseFloat(parts[4]));
                    
                    triangle.setVertexAt(j, v);
                    
                    line = br.readLine();
                }
                
                sector.setTriangleAt(i, triangle);  
            }
            
            br.close();        
            
            System.out.println("FINISHING setup world");
        } 
        catch (Exception e) {
            System.out.println("Failed to parse txt file");
            e.printStackTrace();
            closeTutorial();
        }
    }
    
    /**
     * Creates our texture
     * @param gl 
     */
    private void createTextures(GL2 gl) {
        // gonna use 3 different types of textures
        cubeTexture = new Texture[3];

        // load the texture for the cube (NEW)
        cubeTexture[0] = loadTexture();

        // Something went wrong loading texture, return to selecting menu
        if(cubeTexture[0] == null) closeTutorial();

        // Create Nearest Filtered Texture (NEW)
        cubeTexture[0].enable(gl);                                          // enable the texture we just loaded
        cubeTexture[0].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        cubeTexture[0].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_NEAREST);

        // load the texture for the cube (NEW)
        cubeTexture[1] = loadTexture();

        // Create Linear Filtered Texture (NEW)
        cubeTexture[1].enable(gl);                                          // enable the texture we just loaded
        cubeTexture[1].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        cubeTexture[1].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR);

        // load the texture for the cube (NEW)
        cubeTexture[2] = loadTexture();

        // Create MipMapped Texture (NEW)
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
            URL url = this.getClass().getClassLoader().getResource(TEXTURE_PATH);
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
