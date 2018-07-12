package me.ajlane.loadcypher;

import me.ajlane.loadcypher.GraphLoaderType;
import me.ajlane.loadcypher.GraphLoaderFactory;

public class Main {

    private static void print(GraphLoaderType graphLoader) {

    	Boolean moreQueries = true;   
    	int queryNo = 0;
    	while (moreQueries) {    		
    		String nextQuery = graphLoader.getNextQuery();
    		
    		if (nextQuery == null) {
    			System.out.println("Null query!"); //moreQueries = false;
    			moreQueries  = false;
    		} else {
        		System.out.println("Query No " + queryNo + ":\n" +
                        graphLoader.getNextQuery());   
        		queryNo++;
    		} 	
    				
    	}
        
    }


    public static void main(String[] args) {
    	
    	String projectRoot = "/home/andrew/Gredos/";
    	String cypherRoot = projectRoot + "views";
    	String fnameSuffix = "_w";
    	String globalParamFile = projectRoot + "global_parameters.json";    			
    	
    	GraphLoaderFactory factory = new GraphLoaderFactory();
    	GraphLoaderType graphLoader = factory.create(cypherRoot, fnameSuffix, 
    											globalParamFile);
    	print(graphLoader);
    }
}