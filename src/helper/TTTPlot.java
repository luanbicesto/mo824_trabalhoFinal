package helper;

import common.BestSolution;
import common.Instance;
import common.InstanceManager;
import metaheuristic.ga.TRP_GA;

public class TTTPlot {
    public static void main(String[] args) {
        new TTTPlot().execute();
    }
    
    public void execute() {
        BestSolution solution;
        InstanceManager instanceMg = new InstanceManager();
        
        Instance instance = instanceMg.readInstance("instances/converted/TRP-S100-R1.trp");
        
        //for(int i = 0; i < 90; i++) {
            TRP_GA trp = new TRP_GA(100, instance.getGraphSize()-1, 100000, 1/(double)instance.getGraphSize(), instance, 40000.0); //population, chromosomeSize, generations, mutation
            solution = trp.solve();
            printSolution(solution);
        //}
    }
    
    private void printSolution(BestSolution solution) {
        System.out.print(solution.getValue());
        System.out.println(" - " + solution.getTime());
    }
}
