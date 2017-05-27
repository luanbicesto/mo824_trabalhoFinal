package lpi.gurobi;

import common.Instance;
import common.InstanceManager;
import gurobi.GRB;
import gurobi.GRBConstr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

public class TRPSolver {
    
    private Instance instance;
    
    public static void main(String[] args) {
        TRPSolver solver = new TRPSolver();
        solver.solve();
    }
    
    public void solve() {
        GRBVar[][] permutationVariables;
        GRBVar[] distances;
        readInstance("TRP-S10-R1.trp");

        try {
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.set(GRB.DoubleParam.TimeLimit, 1800);

            permutationVariables = createPermutationVariables(model);
            distances = createDistanceVariables(model, permutationVariables);
            setObjectiveFunction(model, distances);
            optimize(model);
            finilize(model, env);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void finilize(GRBModel model, GRBEnv env) throws GRBException {
        model.dispose();
        env.dispose();
    }
    
    private void optimize(GRBModel model) throws GRBException {
        model.optimize();
        for(int i = 1; i < instance.getGraphSize(); i++) {
            for(int j = 1; j < instance.getGraphSize(); j++) {
                GRBVar x = model.getVarByName("x" + Integer.toString(j) + Integer.toString(i));
                if(x != null && Double.compare(x.get(GRB.DoubleAttr.X), 1) == 0) {
                    System.out.print(j + " ");
                    break;
                }
            }
        }
        System.out.println();
        System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
    }
    
    private void setObjectiveFunction(GRBModel model, GRBVar[] distances) throws GRBException {
        GRBLinExpr objectiveExpr = new GRBLinExpr();
        
        for(int i = 0; i < distances.length; i++) {
            objectiveExpr.addTerm(1, distances[i]);
        }
        model.setObjective(objectiveExpr, GRB.MINIMIZE);
    }
    
    private GRBVar[] createDistanceVariables(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        GRBVar[] distances = new GRBVar[instance.getGraphSize()-1];
        
        //d0
        GRBLinExpr distanceOneExpr = new GRBLinExpr();
        for(int i = 1; i < instance.getGraphSize(); i++) {
            distanceOneExpr.addTerm(instance.getAdjacentMatrix()[0][i], permutationVariables[i][1]);
        }
        GRBVar d0 = model.addVar(0.0, instance.getTotalEdgesSum(), 0.0, GRB.CONTINUOUS, "d0");
        model.addConstr(d0, GRB.EQUAL, distanceOneExpr, "d0Constr");
        distances[0] = d0;
        
        int vertex = 2;
        GRBVar previousDistance = d0;
        while(vertex < instance.getGraphSize()) {
            GRBQuadExpr otherDistancesExpr = new GRBQuadExpr();
            for(int i = 1; i < instance.getGraphSize(); i++) {
                for(int j = 1; j < instance.getGraphSize(); j++) {
                    if(i != j) {
                        otherDistancesExpr.addTerm(instance.getAdjacentMatrix()[i][j], permutationVariables[i][vertex-1], permutationVariables[j][vertex]);
                    }
                }
            }
            otherDistancesExpr.addTerm(1.0, previousDistance);
            GRBVar distance = model.addVar(0.0, instance.getTotalEdgesSum(), 0.0, GRB.CONTINUOUS, "d" + vertex);
            model.addQConstr(distance, GRB.EQUAL, otherDistancesExpr, "d" + vertex + "Constr");
            previousDistance = distance;
            distances[vertex-1] = distance;
            vertex++;
        }
        
        return distances;
    }
    
    private void createRestrictionsPermutationVariables(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        createVertexOnlyOnePosition(model, permutationVariables);
        createPositionHasOneVertex(model, permutationVariables);
    }
    
    private void createPositionHasOneVertex(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        GRBLinExpr positionOneVertex;
        for(int i = 1; i < instance.getGraphSize(); i++) {
            positionOneVertex = new GRBLinExpr();
            for (int j = 1; j < instance.getGraphSize(); j++) {
                positionOneVertex.addTerm(1, permutationVariables[j][i]);
            }
            model.addConstr(positionOneVertex, GRB.EQUAL, 1, "positionOneVertex" + Integer.toString(i));
        }
    }
    
    private void createVertexOnlyOnePosition(GRBModel model, GRBVar[][] permutationVariables) throws GRBException {
        GRBLinExpr vetexOnePosition;
        for(int i = 1; i < instance.getGraphSize(); i++) {
            vetexOnePosition = new GRBLinExpr();
            for (int j = 1; j < instance.getGraphSize(); j++) {
                vetexOnePosition.addTerm(1, permutationVariables[i][j]);
            }
            model.addConstr(vetexOnePosition, GRB.EQUAL, 1, "vertexOnePosition" + Integer.toString(i));
        }
    }
    
    private GRBVar[][] createPermutationVariables(GRBModel model) throws GRBException {
        //creating variables to represent a permutation
        GRBVar[][] permutationVariables = new GRBVar[instance.getGraphSize()][instance.getGraphSize()];
        for(int i = 1; i < instance.getGraphSize(); i++) {
            for (int j = 1; j < instance.getGraphSize(); j++) {
                permutationVariables[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                        "x" + Integer.toString(i) + Integer.toString(j));
            }
        }
        
        createRestrictionsPermutationVariables(model, permutationVariables);
        
        return permutationVariables;
    }
    
    private void readInstance(String name) {
        InstanceManager instanceMg = new InstanceManager();
        instance = instanceMg.readInstance("instances/converted/" + name);
    }
}
