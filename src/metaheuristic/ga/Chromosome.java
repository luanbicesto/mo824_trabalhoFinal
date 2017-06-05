package metaheuristic.ga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

public class Chromosome extends ArrayList<ArrayList<Integer>>{
    private Double fitnessValue;
    public Integer[] permutation;
    
    public Chromosome() {
        fitnessValue = null;
    }
    
    public Chromosome(ArrayList<Integer> genes, Random rng) {
        this.add(new ArrayList<Integer>());
        for(int geneIndex = 0; geneIndex < genes.size(); geneIndex++) {
            ArrayList<Integer> gene = new ArrayList<>();
            for(int locus = 0; locus < genes.size(); locus++) {
                gene.add(locus, rng.nextInt(2));
            }
            this.add(genes.get(geneIndex), gene);
        }
        this.remove(0);
        buildPermutation();
    }
    
    public Chromosome(Chromosome chromosome) {
        super(chromosome);
        this.fitnessValue = chromosome.getFitnessValue();
        this.permutation = chromosome.permutation;
    }

    public Double getFitnessValue() {
        return fitnessValue;
    }

    public void setFitnessValue(Double fitnessValue) {
        this.fitnessValue = fitnessValue;
    }
    
    public Integer getGene(Integer index) {
        return this.permutation[index];
    }
    
    public void buildPermutation() {
        //Set<Integer> alreadyUsedGenes = new HashSet<>();
        //Set<Integer> alreadyUsedLocus = new HashSet<>();
        this.permutation = new Integer[this.size()]; 
        setPermutationLineSum();
        //setPermutationByLocus(genes, alreadyUsedGenes, alreadyUsedLocus);
        //setPermutationByGenes(genes, alreadyUsedGenes, alreadyUsedLocus);
        //setPermutation(genes, alreadyUsedGenes, alreadyUsedLocus);
    }
    
    public void setPermutationLineSum() {
        Set<Integer> alreadyUsedLocus = new HashSet<>();
        for (int geneIndex = 0; geneIndex < this.size(); geneIndex++) {
            int locus = sumArray(this.get(geneIndex)) % this.permutation.length;

            while (alreadyUsedLocus.contains(locus)) {
                locus = (locus + 1) % this.permutation.length;
            }

            this.permutation[locus] = geneIndex+1;
            alreadyUsedLocus.add(locus);
        }
    }
    
    private int sumArray(ArrayList<Integer> array) {
        int totalSum = 0;
        
        for(int i = 0; i < array.size(); i++) {
            totalSum += array.get(i); 
        }
        
        return totalSum;
    }
    
    private void setPermutation(ArrayList<Integer> genes, Set<Integer> alreadyUsedGenes, Set<Integer> alreadyUsedLocus) {
        for(int geneIndex = 0; geneIndex < genes.size(); geneIndex++) {
            int gene = genes.get(geneIndex);
            if(alreadyUsedGenes.contains(gene)) continue;
            
            for(int locus = 0; locus < genes.size(); locus++) {
                if(alreadyUsedLocus.contains(locus) || this.get(gene).get(locus) == 0) continue;
                
                permutation[locus] = gene;
                alreadyUsedGenes.add(gene);
                alreadyUsedLocus.add(locus);
                break;
            }
        }
    }
    
    private void setPermutationByGenes(ArrayList<Integer> genes, Set<Integer> alreadyUsedGenes, Set<Integer> alreadyUsedLocus) {
        for(int geneIndex = 0; geneIndex < genes.size(); geneIndex++) {
            int setLocus = 0;
            int sum = 0;
            int gene = genes.get(geneIndex);
            if(alreadyUsedGenes.contains(gene)) continue;
            
            for(int locus = 0; locus < genes.size(); locus++) {
                
                if(alreadyUsedLocus.contains(locus)) continue;
                
                if(this.get(gene).get(locus) == 1) {
                    sum++;
                    setLocus = locus;
                }
                if(sum > 1) break;
            }
            
            if(sum == 1) {
                permutation[setLocus] = gene;
                alreadyUsedGenes.add(gene);
                alreadyUsedLocus.add(setLocus);
            }
        }
    }
    
    private void setPermutationByLocus(ArrayList<Integer> genes, Set<Integer> alreadyUsedGenes, Set<Integer> alreadyUsedLocus) {
        
        for(int locus = 0; locus < genes.size(); locus++) {
            int setGene = 0;
            int sum = 0;
            for(int geneIndex = 0; geneIndex < genes.size(); geneIndex++) {
                int gene = genes.get(geneIndex);
                
                if(alreadyUsedGenes.contains(gene)) continue;
                
                if(this.get(gene).get(locus) == 1) {
                    sum++;
                    setGene = gene;
                }
                if(sum > 1) break;
            }
            
            if(sum == 1) {
                permutation[locus] = setGene;
                alreadyUsedGenes.add(setGene);
                alreadyUsedLocus.add(locus);
            }
        }
    }
    
    private void removeLocusFromMatrix(Chromosome copy, int locus) {
        for(int gene = 0; gene < this.size(); gene++) {
            copy.get(gene).remove(locus);
        }
    }
    
    private void removeGeneFromMatrix(Chromosome copy, int gene) {
        copy.remove(gene);
    }
  
}
