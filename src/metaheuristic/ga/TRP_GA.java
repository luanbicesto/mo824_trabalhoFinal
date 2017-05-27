package metaheuristic.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TRP_GA {

    private int popSize;
    private int chromosomeSize;
    private Random rng = new Random(0);
    
    public TRP_GA(int popSize, int chromosomeSize) {
        this.popSize = popSize;
        this.chromosomeSize = chromosomeSize;
    }
    
    public void solve() {
        Population population = createInitialPopulation();
    }
    
    private Population createInitialPopulation() {
        Population population = new Population();
        ArrayList<Integer> genes = buildGenes();
        
        while(population.size() < this.popSize) {
            population.add(generateRandomChromosome(genes));
        }
        
        return population;
    }
    
    private ArrayList<Integer> buildGenes() {
        ArrayList<Integer> genes = new ArrayList<>();
        int i = 1;
        
        while(i < chromosomeSize) {
            genes.add(i);
            i++;
        }
        
        return genes;
    }
    
    private Chromosome generateRandomChromosome(ArrayList<Integer> genes) {
        Collections.shuffle(genes, rng);
        Chromosome chromosome = new Chromosome(genes);
        return chromosome;
    }
    
    public static void main(String[] args) {
        TRP_GA trp = new TRP_GA(5, 4);
        trp.solve();
    }
}
