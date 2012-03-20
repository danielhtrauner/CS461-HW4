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
public class Sector {
    private int numberOfTriagles;                     // Number Of Triangles In Sector
    private Triangle triangles[];                     // Pointer To Array Of Triangles
    
    public Sector(int numberOfTriagles){
        this.numberOfTriagles = numberOfTriagles;
        this.triangles = new Triangle[numberOfTriagles];
    }

    public void setTriangleAt(int index, Triangle triangle) {
        this.triangles[index] = triangle;
    }

    
    /**
     * @return the triangles
     */
    public Triangle getTriangleAt(int index) {
        return this.triangles[index];
    }

    /**
     * @return the numberOfTriagles
     */
    public int getNumberOfTriagles() {
        return numberOfTriagles;
    }
    
    /**
     * @return 
     */
    @Override
    public String toString(){
        String result = numberOfTriagles+" triangles\n";
        for(int i = 0; i < numberOfTriagles; i++)
            result += "Triangle "+(i+1)+"\n"+triangles[i].toString()+"\n";
        
        return result;
    }
}
