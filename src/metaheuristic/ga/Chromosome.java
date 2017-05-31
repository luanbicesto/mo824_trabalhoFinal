package metaheuristic.ga;

import java.util.ArrayList;

public class Chromosome extends ArrayList<Integer>{
    private Double fitnessValue;
    private boolean isFeasible;
    
    public Chromosome() {
        fitnessValue = null;
        isFeasible = true;
    }
    
    public boolean isFeasible() {
        return isFeasible;
    }

    public void setFeasible(boolean isFeasible) {
        this.isFeasible = isFeasible;
    }

    public Chromosome(ArrayList<Integer> genes) {
        super(genes);
        this.isFeasible = true;
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
