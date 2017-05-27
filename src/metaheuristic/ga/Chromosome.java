package metaheuristic.ga;

import java.util.ArrayList;

public class Chromosome extends ArrayList<Integer>{
    private Double fitnessValue;
    
    public Chromosome() {
        fitnessValue = null;
    }
    
    public Chromosome(ArrayList<Integer> genes) {
        super(genes);
    }

    public Double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(Double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }
  
}
