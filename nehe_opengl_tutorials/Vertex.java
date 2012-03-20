/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nehe_opengl_tutorials;
// used in tutorial 10

/**
 *
 * @author Sebastiaan
 */
public class Vertex {
    private float x, y, z;                      // 3D Coordinates
    private float u, v;                         // Texture Coordinates

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public float getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * @return the u
     */
    public float getU() {
        return u;
    }

    /**
     * @param u the u to set
     */
    public void setU(float u) {
        this.u = u;
    }

    /**
     * @return the v
     */
    public float getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(float v) {
        this.v = v;
    }
    
    /**
     * @return 
     */
    @Override
    public String toString(){
        return this.x+" "+this.y+" "+this.z+" "+this.u+" "+this.v;
    }
}
