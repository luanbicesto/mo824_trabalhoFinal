package common;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
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
