package common;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class InstanceManager {
    private Stream<String> stream;

    public Instance readInstance(String path) {
        Instance instance = null;
        
        try{
            stream = Files.lines(Paths.get(path));
            Iterator<String> iterator = stream.iterator();
            instance = new Instance(Integer.parseInt(iterator.next().toString()));
            
            while(iterator.hasNext()) {
                String line = iterator.next().toString();
                StringTokenizer lineTokens = new StringTokenizer(line);
                setAdjacentMatrix(lineTokens, instance);
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return instance;
    }
    
    public void convertInstance(String name) throws IOException {
        List<VertexPosition> vertices = readEuclidianInstance("instances/otherFormat/" + name + ".txt");
        createInstance(vertices, name);
    }
    
    private void createInstance(List<VertexPosition> vertices, String name) throws IOException {
        List<String> instanceLines = new ArrayList<String>();
        instanceLines.add(Integer.toString(vertices.size())); //first line contains the vertices quantity
        
        for(int i = 0; i < vertices.size(); i++) {
            for(int j = i + 1; j < vertices.size(); j++) {
                instanceLines.add(buildLine(vertices.get(i), vertices.get(j)));
            }
        }
        
        Path file = Paths.get("instances/converted/" + name + ".trp");
        Files.write(file, instanceLines, Charset.forName("UTF-8"));
    }
    
    private String buildLine(VertexPosition source, VertexPosition destiny) {
        int xSquareDifference = (int) Math.pow((double)(source.getX() - destiny.getX()), 2.0);
        int ySquareDifference = (int) Math.pow((double)(source.getY() - destiny.getY()), 2.0);
        double distance = Math.floor(Math.sqrt(xSquareDifference + ySquareDifference));
        String line = Integer.toString(source.getId()) + " " +
                      Integer.toString(destiny.getId()) + " " +
                      Double.toString(distance);
        
        return line;
    }
    
    private List<VertexPosition> readEuclidianInstance(String path) {
        List<VertexPosition> vertices = new ArrayList<VertexPosition>();
        
        try{
            stream = Files.lines(Paths.get(path));
            Iterator<String> iterator = stream.iterator();
            
            while (iterator.hasNext()) {
                String line = iterator.next().toString();
                StringTokenizer lineTokens = new StringTokenizer(line);
                vertices.add(createVertex(lineTokens));

            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        return vertices;
    }
    
    private VertexPosition createVertex(StringTokenizer line) {
        VertexPosition vertex = new VertexPosition();
        vertex.setId(Integer.parseInt(line.nextToken()));
        vertex.setX(Integer.parseInt(line.nextToken()));
        vertex.setY(Integer.parseInt(line.nextToken()));
        
        return vertex;
    }
    
    private void setAdjacentMatrix(StringTokenizer lineTokens, Instance instance) {
        int i,j;
        double edgeValue;
        
        i = Integer.parseInt(lineTokens.nextToken());
        j = Integer.parseInt(lineTokens.nextToken());
        edgeValue = Double.parseDouble(lineTokens.nextToken());
        
        instance.getAdjacentMatrix()[i][j] = edgeValue;
        instance.getAdjacentMatrix()[j][i] = edgeValue;
    }
}
