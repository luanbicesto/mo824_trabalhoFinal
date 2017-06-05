package metaheuristic.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import common.Instance;
import common.InstanceManager;

public class TRP_GA {

    private int popSize;
    private int chromosomeSize;
    Population population;
    private int generations;
    private double mutatationRate;
    
    private Chromosome bestChromosome;
    private Random rng = new Random(0);
    private Instance instance;
    private ArrayList<Integer> genes;
    private enum LS_TYPE {BEST_IMPROVING, FIRST_IMPROVING}
    private int generation;
    private int generationsWithoutImproving;
    
    private static int CROSSPOINT_SIZE = 4;
    private static double NEW_POPULATION_PERCENTAGE = 0.5;
    private static double HYBRID_POPULATION_PERCENTAGE = 0.2;
    private static int EXECUTION_TIME = 300;
    private static int MAX_GENERATIONS_WITHOUT_IMPROVING = 100;
    
    public TRP_GA(int popSize, int chromosomeSize, int generations, double mutatationRate, Instance instance) {
        this.popSize = popSize;
        this.chromosomeSize = chromosomeSize;
        this.generations = generations;
        this.mutatationRate = mutatationRate;
        this.instance = instance;
        this.generationsWithoutImproving = 0;
    }
    
    public void solve() {
        long initialTime = System.nanoTime();
        population = createInitialPopulation();
        setBestChromosome(population);
        
        for(generation = 0; generation < this.generations && getElapsedTime(initialTime) <= EXECUTION_TIME; generation++) {
            Population parents = selectParentsCrossover();
            Population offspring = crossover(parents);
            mutate(offspring);
            //localSearchBestChromosome(offspring, LS_TYPE.BEST_IMPROVING);
            keepBestChromosomeNextGeneration(offspring);
            //this.population = selectNewGeneration(offspring);
            this.population = offspring;
        }
    }
    
    private long getElapsedTime(long initialTime) {
        return (System.nanoTime() - initialTime) / 1000000000;
    }
    
    private Population createHybridPopulation() {
        Population hybrids = new Population();
        Population parents = selectParentsCrossover();
        ArrayList<Integer> genes = buildGenes();
        int hybridPopulationSize = (int)Math.ceil(popSize * HYBRID_POPULATION_PERCENTAGE);
        
        while(hybrids.size() <= hybridPopulationSize) {
            ArrayList<Integer> genesCopy = new ArrayList<>(genes);
            Chromosome parent = parents.get(rng.nextInt(parents.size()));
            Chromosome hybrid = new Chromosome(parent);
            /*int crosspoint = rng.nextInt(parent.size());
            
            for(int i = 0; i <= crosspoint; i++) {
                genesCopy.remove(hybrid.get(i));
            }
            
            for(int i = crosspoint + 1; i < hybrid.size(); i++) {
                int indexGene = rng.nextInt(genesCopy.size());
                hybrid.set(i, genesCopy.get(indexGene));
                genesCopy.remove(indexGene);
            }*/
            
            localSearch(hybrid, LS_TYPE.FIRST_IMPROVING);
            hybrids.add(hybrid);
        }
        
        return hybrids;
    }
    
    private void includeNewPopulation(Population nextGeneration, Population newPopulation) {
        int newPopulationSize = (int)Math.floor(this.popSize * NEW_POPULATION_PERCENTAGE);
        
        for(int i = 0; i < newPopulationSize; i++) {
            nextGeneration.remove(rng.nextInt(nextGeneration.size()));
        }
        
        for(int i = 0; i < newPopulationSize; i++) {
            Chromosome newChromosome = newPopulation.get(rng.nextInt(newPopulation.size())); 
            //localSearch(newChromosome, LS_TYPE.FIRST_IMPROVING);
            nextGeneration.add(newChromosome);
        }
    }
    
    private void includeHybridPopulation(Population nextGeneration, Population hybridPopulation) {
        for(int i = 0; i < hybridPopulation.size(); i++) {
            nextGeneration.remove(rng.nextInt(nextGeneration.size()));
        }
        
        nextGeneration.addAll(hybridPopulation);
    }
    
    private Population selectNewGeneration(Population offspring) {
        Population hybridPopulation = createHybridPopulation();
        includeHybridPopulation(offspring, hybridPopulation);
        
        if(this.generationsWithoutImproving == MAX_GENERATIONS_WITHOUT_IMPROVING) {
            Population newPopulation = createInitialPopulation();
            includeNewPopulation(offspring, newPopulation);
            this.generationsWithoutImproving = 0;
            System.out.println("New population arrived");
        }
        
        return offspring;
    }
    
    private void localSearchBestChromosome(Population population, LS_TYPE lsType) {
        Chromosome bestChromosome = findBestChromosome(population);
        localSearch(bestChromosome, lsType);
    }
    
    private void localSearch(Chromosome chromosome, LS_TYPE lsType) {
        int bestMovimentI = 0;
        int bestMovimentJ = 0;
        double bestMovimentCost = chromosome.getFitnessValue();
        double movimentFinalCost = 0.0;
        boolean improved = true;
        
        while(improved) {
            improved = false;
            for(int i = 0; i < chromosome.size(); i++) {
                for(int j = i+1; j < chromosome.size(); j++) {
                    swapGeneMutation(chromosome, i, j);
                    movimentFinalCost = evaluateFitness(chromosome);
                    
                    if(Double.compare(movimentFinalCost, bestMovimentCost) < 0) {
                        if(lsType == LS_TYPE.BEST_IMPROVING) {
                            bestMovimentI = i;
                            bestMovimentJ = j;
                            swapGeneMutation(chromosome, i, j);
                        }
                        bestMovimentCost = movimentFinalCost;
                        improved = true;
                    } else {
                        swapGeneMutation(chromosome, i, j);
                    }
                }
            }
            if(improved && lsType == LS_TYPE.BEST_IMPROVING) {
                swapGeneMutation(chromosome, bestMovimentI, bestMovimentJ);
            }
        }
        
        chromosome.setFitnessValue(evaluateFitness(chromosome));
    }
    
    private void mutate(Population offspring) {
        for(Chromosome chromosome : offspring) {
            for(int i = 0; i < chromosome.size(); i++) {
                for(int j = 0; j < chromosome.size(); j++) {
                    if(rng.nextDouble() < mutatationRate) {
                        int newGene = chromosome.get(i).get(j) == 1 ? 0 : 1;
                        chromosome.get(i).set(j, newGene);
                    }
                }
            }
            chromosome.buildPermutation();
            chromosome.setFitnessValue(evaluateFitness(chromosome));
        }
    }
    
    private void swapGeneMutation(Chromosome chromosome, int locus1, int locus2) {
        int gene = chromosome.getGene(locus1);
        chromosome.set(locus1, chromosome.get(locus2));
        //chromosome.set(locus2, gene);
    }
    
    private void keepBestChromosomeNextGeneration(Population offspring) {
        Chromosome bestChromosomeOffspring = findBestChromosome(offspring);
        if(bestChromosomeOffspring.getFitnessValue() < this.bestChromosome.getFitnessValue()) {
            this.generationsWithoutImproving = 0;
            this.bestChromosome = new Chromosome(bestChromosomeOffspring);
            printBestChromossome();
            
            Chromosome worstChromosome = findWorstChromosome(offspring);
            if (worstChromosome.getFitnessValue() > bestChromosome.getFitnessValue()) {
                offspring.remove(worstChromosome);
                offspring.add(bestChromosome);
            }
        } else {
            this.generationsWithoutImproving++;
        }
    }
    
    private void printBestChromossome() {
        System.out.println("Generation: " + generation);
        System.out.print(" - " + bestChromosome.getFitnessValue() + " - ");
        System.out.println(bestChromosome.permutation.toString());
    }
    
    private Population crossover(Population parents) {
        Population offspring = new Population();
        
        /*PontoMelhoria: 
         * swapGenesCrossover: verificar o metodo do professor
         * Escolher os pais aleatoriamente ?
         */
        for(int i = 0; i < popSize; i+=2) {
            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i+1);
            
            crossover2PointsMatrix(parent1, parent2, offspring);
            //crossoverTwoPoints(parent1, parent2, offspring);
            //crossoverPMX(parent1, parent2, offspring);
            //crossoverRandom(parent1, parent2, offspring);
        }
        
        return offspring;
    }
    
    private void crossover2PointsMatrix(Chromosome parent1, Chromosome parent2, Population offspring) {
        int crosspointX1 = rng.nextInt(parent1.size());
        int crosspointX2 = crosspointX1 + rng.nextInt(parent1.size() - crosspointX1);
        int crosspointY1 = rng.nextInt(parent1.size());
        int crosspointY2 = crosspointY1 + rng.nextInt(parent1.size() - crosspointY1);
        
        Chromosome offspring1 = new Chromosome(parent1);
        Chromosome offspring2 = new Chromosome(parent2);
        
        for(int i = crosspointX1; i <= crosspointX2; i++) {
            for(int j = crosspointY1; j <= crosspointY2; j++) {
                int offspring1Value = offspring1.get(i).get(j);
                int offspring2Value = offspring2.get(i).get(j);
                offspring1.get(i).set(j, offspring2Value);
                offspring2.get(i).set(j, offspring1Value);
            }
        }
        
        offspring1.buildPermutation();
        offspring2.buildPermutation();
        offspring1.setFitnessValue(evaluateFitness(offspring1));
        offspring2.setFitnessValue(evaluateFitness(offspring2));
        offspring.add(offspring1);
        offspring.add(offspring2);
    }
    
    /*
     * PontoMelhoria: sera que o tamanho da nova populacao nao deveria ser menor que o tamanho da populacao atual ?
     * sera que algo diferente pode ser feito nesta escolha ?
     * Vale a pena realizar alguns testes aqui  
     */
    private Population selectParentsCrossover() {
        Population population = new Population();
        
        while(population.size() < this.popSize) {
            Chromosome parent1 = getRandomChromosome();
            Chromosome parent2 = getRandomChromosome();
            population.add(tournament(parent1, parent2));
        }
        
        return population;
    }
    
    private Chromosome tournament(Chromosome chromosome1, Chromosome chromosome2) {
        return chromosome1.getFitnessValue() < chromosome2.getFitnessValue() ? chromosome1 : chromosome2;
    }
    
    private Chromosome getRandomChromosome() {
        return this.population.get(rng.nextInt(popSize));
    }
    
    private Chromosome findWorstChromosome(Population population) {
        return Collections.max(population, new Comparator<Chromosome>(){
            @Override
            public int compare(Chromosome first, Chromosome second) {
                return Double.compare(first.getFitnessValue(), second.getFitnessValue());
            }
         });
    }
    
    private Chromosome findBestChromosome(Population population) {
        return Collections.min(population, new Comparator<Chromosome>(){
            @Override
            public int compare(Chromosome first, Chromosome second) {
                return Double.compare(first.getFitnessValue(), second.getFitnessValue());
            }
         });
    }
    
    private void setBestChromosome(Population population) {
        bestChromosome = new Chromosome(findBestChromosome(population));
    }
    
    private Double evaluateFitness(Chromosome chromosome) {
        Double totalLatency = 0.0;
        Double latencyLastPath = 0.0;
        int lastIdPath = 0;
        
        for(int i = 0; i < chromosome.permutation.length; i++) {
            totalLatency += latencyLastPath + instance.getAdjacentMatrix()[lastIdPath][chromosome.getGene(i)];
            latencyLastPath += instance.getAdjacentMatrix()[lastIdPath][chromosome.getGene(i)];
            lastIdPath = chromosome.getGene(i);
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
        this.genes = new ArrayList<>();
        int i = 1;
        
        while(i <= chromosomeSize) {
            genes.add(i);
            i++;
        }
        
        return genes;
    }
    
    private Chromosome generateRandomChromosome(ArrayList<Integer> genes) {
        //Collections.shuffle(genes);
        Chromosome chromosome = new Chromosome(genes, rng);
        chromosome.setFitnessValue(evaluateFitness(chromosome));
        return chromosome;
    }
    
    public static void main(String[] args) {
        InstanceManager instanceMg = new InstanceManager();
        Instance instance = instanceMg.readInstance("instances/converted/TRP-S100-R1.trp");
        
        TRP_GA trp = new TRP_GA(100, instance.getGraphSize()-1, 100000, 1/(double)instance.getGraphSize(), instance); //population, chromosomeSize, generations, mutation
        trp.solve();
    }
}
