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
public class Triangle {
    private Vertex vertex[];                       // Array Of Three Vertices
    
    public Triangle(int numberOfVertices){
        this.vertex = new Vertex[numberOfVertices];
    }
    
    public void setVertexAt(int index, Vertex vert){
        this.vertex[index] = vert;
    }
    
    public Vertex getVertexAt(int index){
        return this.vertex[index];
    }
    
    public Vertex[] getVertexArray(){
        return this.vertex;
    }
    
    /**
     * @return 
     */
    @Override
    public String toString(){
        String result = "";
        for(int i = 0; i < vertex.length; i++)
            result += "\tVertex "+(i+1)+": "+vertex[i].toString()+"\n";
        
        return result;
    }
}
