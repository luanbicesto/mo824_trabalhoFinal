package common;

public class Instance {
    private int graphSize;
    private double[][] adjacentMatrix;
    
    public Instance(int graphSize) {
        this.graphSize = graphSize;
        this.adjacentMatrix = new double[graphSize][graphSize]; //TODO: improving the coding to save space in the matrix
    }
    
    public int getGraphSize() {
        return graphSize;
    }
    public void setGraphSize(int graphSize) {
        this.graphSize = graphSize;
    }
    public double[][] getAdjacentMatrix() {
        return adjacentMatrix;
    }
    public void setAdjacentMatrix(double[][] adjacentMatrix) {
        this.adjacentMatrix = adjacentMatrix;
    }
    
}
