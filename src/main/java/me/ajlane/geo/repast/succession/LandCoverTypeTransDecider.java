package me.ajlane.geo.repast.succession;

import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import me.ajlane.neo4j.EmbeddedGraphInstance;

import static java.lang.Math.toIntExact;

import java.util.HashMap;
import java.util.Map;

public class LandCoverTypeTransDecider {
	
	private HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
					  EnvironmentalConsequent<String>> transLookup;
	

    public LandCoverTypeTransDecider(EmbeddedGraphInstance graphDatabase) {
    	this.transLookup = getLandCoverTransitionMap(graphDatabase);
    }

    /**
     * @param graph
     * 		An established connection to a running EmbeddedGraphInstance database containing succession
     * 		pathway data. At the time of writing it is assumed that 
     * @return
     */
    HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
	  EnvironmentalConsequent<String>> getLandCoverTransitionMap(EmbeddedGraphInstance graph) {
    	
    	HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
    		EnvironmentalConsequent<String>> transLookup;  
    	transLookup = new HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
        					EnvironmentalConsequent<String>>();
    	
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	params.put("model_ID", "AgroSuccess-dev");
    	
    	String landCoverTransitionQuery 
    		= "MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType) " +
    		  "WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code " +
    		  "WITH lct1, lct2, t MATCH (e:EnvironCondition)-[:CAUSES]->(t) " +
    		  "RETURN lct1.code as start_code, e.succession as succession, e.aspect as aspect," + 
    		  "e.pine as pine, e.oak as oak, e.deciduous as deciduous, e.water as water, " + 
    		  "lct2.code as end_code, e.delta_t as delta_t; ";
    	
    	try (Transaction tx = graph.beginTx()) {
    		Result landCoverTransitionResults = graph.execute(landCoverTransitionQuery, params);
    		while (landCoverTransitionResults.hasNext()) {
    			Map<String, Object> transData = landCoverTransitionResults.next();
    			
    			EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> envAntecedent
    				= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
    						transData.get("start_code").toString(),
    						transData.get("succession").toString(),
    						transData.get("aspect").toString(),
    						(boolean)transData.get("pine"),
    						(boolean)transData.get("oak"),
    						(boolean)transData.get("deciduous"),
    						transData.get("water").toString());
    			
    			EnvironmentalConsequent<String> envConsequent
    				= new EnvironmentalConsequent<String>(
    						transData.get("end_code").toString(),
    						toIntExact((long)transData.get("delta_t")));
    			
    			transLookup.put(envAntecedent, envConsequent);    			
    			tx.success();
    		}
    		
    	}
    	return transLookup;
    }
    
    HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
	  EnvironmentalConsequent<String>> getTransLookup() {
    	return this.transLookup;
    }
    
    
    
    /*
    private static HashMap<String, String> getLandCoverTransitionData( Transaction tx, String model_ID ) {
        HashMap<String, String> map = new HashMap<String, String>();
        StatementResult result = tx.run( 
                "MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType) " +
                "WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code " +
                "WITH lct1, lct2, t MATCH (e:EnvironCondition)-[:CAUSES]->(t) " +
                "RETURN lct1.code as start_code, e.succession as succession, e.aspect as aspect," + 
                "e.pine as pine, e.oak as oak, e.deciduous as deciduous, e.water as water, " + 
                "lct2.code as end_code, e.delta_t as delta_t; ",
				parameters( "model_ID", model_ID ) );
        
        // Loop through resulting combinations of environmental conditions, adding a k/v pair
        // to `map` where the keys are concatenated string of conditions (specified in the order
        // of start_code, succession, aspect, pine, oak, deciduous, water, delta_t) and the 
        // values are the code for the end state.       
        
        while ( result.hasNext() )
        {
            Record thisResult = result.next();
            // construct key string by concatenating individual environmental conditions
            String key = thisResult.get("start_code").toString().replaceAll("^\"|\"$", "") + 
            		   	 thisResult.get("succession").toString().replaceAll("^\"|\"$", "") +
            		     thisResult.get("aspect").toString().replaceAll("^\"|\"$", "") +
            		     String.valueOf(thisResult.get("pine")) +
            		     String.valueOf(thisResult.get("oak")) +
            		     String.valueOf(thisResult.get("deciduous")) +
            		     thisResult.get("water").toString().replaceAll("^\"|\"$", "") +
            		     thisResult.get("delta_t").toString().replaceAll("^\"|\"$", "");
            // map conditions to resulting land cover type
            map.put(key,  thisResult.get("end_code").toString().replaceAll("^\"|\"$", ""));

        }
        return map;
    }
    
    public String getNextLandCoverType(String startState, String succession, String aspect, 
    				Boolean pine, Boolean oak, Boolean deciduous, String water, String delta_t) {
    	String key = startState + succession + aspect + 
    			String.valueOf(pine).toUpperCase() +
    			String.valueOf(oak).toUpperCase() + 
    			String.valueOf(deciduous).toUpperCase() + 
    			water + delta_t;
    	
    	return this.transLookup.get(key);    	
    }

    public static void main( String... args ) throws Exception
    {
    	String user = args[0];
    	String password = args[1];
        try ( LandCoverTypeTransDecider decider = new LandCoverTypeTransDecider( "bolt://localhost:7687", user, password, "AgroSuccess-dev" ) )
        {
        	System.out.println(decider.getLandCoverTransitionMap());
        	System.out.println(decider.getNextLandCoverType("Burnt", "regeneration", "north", false, true, true, "xeric", "2"));
        	
        	// how to handle cases where environmental conditions change part way through transition? Refer
        	// back to Millington2009. Maybe this is an argument for passing the whole SimulationCell object to 
        	// getNextLandCoverType?
        }
    }
    
    */
}
