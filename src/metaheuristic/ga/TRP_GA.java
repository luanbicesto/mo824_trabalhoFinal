package metaheuristic.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import common.Instance;
import common.InstanceManager;

public class TRP_GA {

    private int popSize;
    private int chromosomeSize;
    Population population;
    private Random rng = new Random(0);
    private Chromosome bestChromosome;
    private Instance instance;
    
    public TRP_GA(int popSize, int chromosomeSize, Instance instance) {
        this.popSize = popSize;
        this.chromosomeSize = chromosomeSize;
        this.instance = instance;
    }
    
    public void solve() {
        population = createInitialPopulation();
        setBestChromosome();
    }
    
    private void setBestChromosome() {
        bestChromosome = Collections.min(population, new Comparator<Chromosome>(){
           @Override
           public int compare(Chromosome first, Chromosome second) {
               return Double.compare(first.getFitnessValue(), second.getFitnessValue());
           }
        });
    }
    
    private Double evaluateFitness(Chromosome chromosome) {
        Double totalLatency = 0.0;
        Double latencyLastPath = 0.0;
        int lastIdPath = 0;
        
        for(int i = 0; i < chromosome.size(); i++) {
            totalLatency += latencyLastPath + instance.getAdjacentMatrix()[lastIdPath][chromosome.get(i)];
            latencyLastPath += instance.getAdjacentMatrix()[lastIdPath][chromosome.get(i)];
            lastIdPath = chromosome.get(i);
        }
        return totalLatency;
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
        
        while(i <= chromosomeSize) {
            genes.add(i);
            i++;
        }
        
        return genes;
    }
    
    private Chromosome generateRandomChromosome(ArrayList<Integer> genes) {
        Collections.shuffle(genes, rng);
        Chromosome chromosome = new Chromosome(genes);
        chromosome.setFitnessValue(evaluateFitness(chromosome));
        return chromosome;
    }
    
    public static void main(String[] args) {
        InstanceManager instanceMg = new InstanceManager();
        Instance instance = instanceMg.readInstance("instances/converted/instance_5.trp");
        
        TRP_GA trp = new TRP_GA(5, instance.getGraphSize()-1, instance);
        trp.solve();
    }
}
