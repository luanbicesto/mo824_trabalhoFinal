package lpi.gurobi;

import common.Instance;
import common.InstanceManager;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class TRPSolver {
    
    private Instance instance;
    
    public static void main(String[] args) {
        TRPSolver solver = new TRPSolver();
        solver.solve();
    }
    
    public void solve() {
        GRBVar[][] permutationVariables;
        readInstance("instance_5.trp");

        try {
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.set(GRB.DoubleParam.TimeLimit, 1800);

            permutationVariables = createPermutationVariables(model);
            createRestrictionsPermutationVariables(model, permutationVariables);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void createRestrictionsPermutationVariables(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        createVertexOnlyOnePosition(model, permutationVariables);
        createPositionHasOneVertex(model, permutationVariables);
    }
    
    private void createPositionHasOneVertex(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        GRBLinExpr positionOneVertex;
        for(int i = 1; i < instance.getGraphSize(); i++) {
            positionOneVertex = new GRBLinExpr();
            for(int j = 1; j < instance.getGraphSize(); j++) {
                if(i != j) {
                    positionOneVertex.addTerm(1, permutationVariables[j][i]);
                }
            }
            model.addConstr(positionOneVertex, GRB.EQUAL, 1, "positionOneVertex" + Integer.toString(i));
        }
    }
    
    private void createVertexOnlyOnePosition(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        GRBLinExpr vetexOnePosition;
        for(int i = 1; i < instance.getGraphSize(); i++) {
            vetexOnePosition = new GRBLinExpr();
            for(int j = 1; j < instance.getGraphSize(); j++) {
                if(i != j) {
                    vetexOnePosition.addTerm(1, permutationVariables[i][j]);
                }
            }
            model.addConstr(vetexOnePosition, GRB.EQUAL, 1, "vertexOnePosition" + Integer.toString(i));
        }
    }
    
    private GRBVar[][] createPermutationVariables(GRBModel model) throws GRBException {
        //creating variables to represent a permutation
        GRBVar[][] permutationVariables = new GRBVar[instance.getGraphSize()][instance.getGraphSize()];
        for(int i = 1; i < instance.getGraphSize(); i++) {
            for(int j = 1; j < instance.getGraphSize(); j++) {
                if(i != j) {
                    permutationVariables[i][j] = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, "x" + Integer.toString(i) + Integer.toString(j));
                }
            }
        }
        
        return permutationVariables;
    }
    
    private void readInstance(String name) {
        InstanceManager instanceMg = new InstanceManager();
        instance = instanceMg.readInstance("instances/" + name);
    }
}
