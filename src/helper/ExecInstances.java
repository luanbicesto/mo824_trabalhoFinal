package helper;

import java.util.ArrayList;

import common.BestSolution;
import common.Instance;
import common.InstanceManager;
import metaheuristic.ga.TRP_GA;

public class ExecInstances {
    public static void main(String[] args) {
        new ExecInstances().execute();
    }
    
    public void execute() {
        BestSolution solution;
        InstanceManager instanceMg = new InstanceManager();
        ArrayList<String> instances = getInstancesName();
        
        for(String instancePath : instances) {
            System.out.println(instancePath);
            Instance instance = instanceMg.readInstance(instancePath);
            
            TRP_GA trp = new TRP_GA(100, instance.getGraphSize()-1, 1000000, 1/(double)instance.getGraphSize(), instance); //population, chromosomeSize, generations, mutation
            solution = trp.solve();
            printSolution(solution);
        }
        
    }
    
    private void printSolution(BestSolution solution) {
        System.out.print(solution.getValue());
        System.out.println(" - " + solution.getTime());
    }
    
    private ArrayList<String> getInstancesName() {
        ArrayList<String> instances = new ArrayList<>();
        
        //instances.add("instances/converted/TRP-S50-R2.trp");
        //instances.add("instances/converted/TRP-S100-R1.trp");
        instances.add("instances/converted/TRP-S20-R1.trp");
        //instances.add("instances/converted/TRP-S200-R2.trp");
        //instances.add("instances/converted/TRP-S500-R1.trp");
        
        return instances;
    }
}
