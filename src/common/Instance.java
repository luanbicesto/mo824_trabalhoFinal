package common;

import java.util.stream.DoubleStream;

public class Instance {
    private int graphSize;
    private double[][] adjacentMatrix;
    private double totalEdgesSum;

    public Instance(int graphSize) {
        this.graphSize = graphSize;
        this.adjacentMatrix = new double[graphSize][graphSize]; //TODO: improving the coding to save space in the matrix
        this.totalEdgesSum = 0;
    }
    
    private double computeEdgesSum() {
        double totalSum = 0;
        for(int i = 0; i < this.graphSize; i++) {
            totalSum += DoubleStream.of(adjacentMatrix[i]).sum();
        }
        
        return totalSum;
    }
    
    public double getTotalEdgesSum() {
        if(Double.compare(this.totalEdgesSum, 0.0) == 0) {
            this.totalEdgesSum = computeEdgesSum();
        }
        
        return totalEdgesSum;
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
