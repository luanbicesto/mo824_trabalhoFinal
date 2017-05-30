package metaheuristic.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
    private enum LS_TYPE {BEST_IMPROVING, FIRST_IMPROVING}
    
    private ArrayList<Integer> genes;
    
    public TRP_GA(int popSize, int chromosomeSize, int generations, double mutatationRate, Instance instance) {
        this.popSize = popSize;
        this.chromosomeSize = chromosomeSize;
        this.generations = generations;
        this.mutatationRate = mutatationRate;
        this.instance = instance;
    }
    
    public void solve() {
        population = createInitialPopulation();
        setBestChromosome();
        
        for(int g = 0; g < this.generations; g++) {
            Population parents = selectParentsCrossover();
            Population offspring = crossover(parents);
            mutate(offspring);
            localSearchBestChromosome(offspring, LS_TYPE.BEST_IMPROVING);
            keepBestChromosomeNextGeneration(offspring);
            this.population = offspring;
        }
        
        setBestChromosome();
        printBestChromossome();
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
            if(rng.nextDouble() < mutatationRate) {
                adjustChromosome(chromosome);
                chromosome.setFitnessValue(evaluateFitness(chromosome));
            }
        }
    }
    
    private void adjustChromosome(Chromosome chromosome) {
        Set<Integer> setDistinct = new HashSet<>(this.genes);
        Set<Integer> set = new HashSet<>();
        
        for(int i = 0; i < chromosome.size(); i++) {
            setDistinct.remove(chromosome.get(i));
        }
        
        for(int i = 0; i < chromosome.size(); i++) {
            if(set.contains(chromosome.get(i))) {
                Integer gene = chooseNextGene(chromosome, i, setDistinct);
                chromosome.set(i, gene);
                setDistinct.remove(gene);
            } else {
                set.add(chromosome.get(i));
            }
        }
    }
    
    private Integer chooseNextGene(Chromosome chromosome, int locus, Set<Integer> setDistinct) {
        if(setDistinct.size() == 1) {
            return setDistinct.iterator().next();
        }
        
        Integer previousGene = chromosome.get(locus - 1);
        Integer nextGene = chromosome.get(locus + 1);
        Integer bestGene = chromosome.get(locus);
        double cost = 0.0;
        double bestCost = Double.MAX_VALUE;
        
        for(Integer gene : setDistinct) {
            cost = 0.0;
            cost = instance.getAdjacentMatrix()[previousGene][gene];
            cost += instance.getAdjacentMatrix()[gene][nextGene];
            
            if(cost < bestCost) {
                bestCost = cost;
                bestGene = gene;
            }
        }
        
        return bestGene;
    }
    
    private void swapGeneMutation(Chromosome chromosome, int locus1, int locus2) {
        int gene = chromosome.get(locus1);
        chromosome.set(locus1, chromosome.get(locus2));
        chromosome.set(locus2, gene);
    }
    
    private void keepBestChromosomeNextGeneration(Population offspring) {
        Chromosome lastBest = new Chromosome(bestChromosome);
        setBestChromosome();
        
        if(bestChromosome.getFitnessValue() < lastBest.getFitnessValue()) {
            printBestChromossome();
        }
        
        Chromosome worstChromosome = findWorstChromosome(offspring);
        if (worstChromosome.getFitnessValue() > bestChromosome.getFitnessValue()) {
            offspring.remove(worstChromosome);
            offspring.add(bestChromosome);
        }
    }
    
    private void printBestChromossome() {
        System.out.print(bestChromosome.getFitnessValue() + " - ");
        System.out.println(bestChromosome.toString());
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
            int crosspoint1 = rng.nextInt(chromosomeSize);
            int crosspoint2 = crosspoint1 + rng.nextInt(chromosomeSize - crosspoint1);
            
            Chromosome offspring1 = new Chromosome(parent1);
            Chromosome offspring2 = new Chromosome(parent2);
            
            for(int c = crosspoint1; c <= crosspoint2; c++) {
                offspring1.set(c, parent2.get(c));
                offspring2.set(c, parent1.get(c));
            }
            
            offspring1.setFitnessValue(evaluateFitness(offspring1));
            offspring2.setFitnessValue(evaluateFitness(offspring2));
            offspring.add(offspring1);
            offspring.add(offspring2);
        }
        
        return offspring;
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
    
    private void setBestChromosome() {
        bestChromosome = findBestChromosome(population);
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
        genes = new ArrayList<>();
        int i = 1;
        
        while(i <= chromosomeSize) {
            genes.add(i);
            i++;
        }
        
        return genes;
    }
    
    private Chromosome generateRandomChromosome(ArrayList<Integer> genes) {
        Collections.shuffle(genes);
        Chromosome chromosome = new Chromosome(genes);
        chromosome.setFitnessValue(evaluateFitness(chromosome));
        return chromosome;
    }
    
    public static void main(String[] args) {
        InstanceManager instanceMg = new InstanceManager();
        Instance instance = instanceMg.readInstance("instances/converted/TRP-S10-R1.trp");
        
        TRP_GA trp = new TRP_GA(100, instance.getGraphSize()-1, 100000, 1 - (1/(double)instance.getGraphSize()), instance); //population, chromosomeSize, generations, mutation
        trp.solve();
    }
}
