/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nehe_opengl_tutorials;
//used in tutorial 9

//import java.util.Random;

/**
 *
 * @author Sebastiaan
 */
public class Star {
    private int r, g, b;                    // Stars Color
    private float dist;                       // Stars Distance From Center
    private float angle;                      // Stars Current Angle

    public Star(){
        this.angle = 0.0f;
        
        this.r = 200;//new Random().nextInt(256);
        this.g = 0;//new Random().nextInt(256);
        this.b = 0;//new Random().nextInt(256);
    }
    
    /**
     * @return the r
     */
    public int getR() {
        return r;
    }

    /**
     * @param r the r to set
     */
    public void setR(int r) {
        this.r = r;
    }

    /**
     * @return the g
     */
    public int getG() {
        return g;
    }

    /**
     * @param g the g to set
     */
    public void setG(int g) {
        this.g = g;
    }

    /**
     * @return the b
     */
    public int getB() {
        return b;
    }

    /**
     * @param b the b to set
     */
    public void setB(int b) {
        this.b = b;
    }

    /**
     * @return the distance
     */
    public float getDistance() {
        return dist;
    }

    /**
     * @param dist the distance to set
     */
    public void setDistance(float dist) {
        this.dist = dist;
    }

    /**
     * @return the angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * @param angle the angle to set
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }
    
    /*
     * ToString
     */
    @Override
    public String toString(){
        return this.getAngle()+"° - ("+this.getB()+","+this.getG()+","+this.getR()+") - "+this.getDistance();
    }
}
