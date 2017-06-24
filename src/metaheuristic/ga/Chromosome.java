package metaheuristic.ga;

import java.util.ArrayList;

public class Chromosome extends ArrayList<Integer>{
    private static final long serialVersionUID = 1L;
    private Double fitnessValue;
    
    public Chromosome() {
        fitnessValue = null;
    }
    
    public Chromosome(ArrayList<Integer> genes) {
        super(genes);
    }
    
    public Chromosome(Chromosome chromosome) {
        super(chromosome);
        this.fitnessValue = chromosome.getFitnessValue();
    }

    public Double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(Double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }
  
}
