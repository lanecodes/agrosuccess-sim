package me.ajlane.neo4j;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.HashMap;

public class LCTTransDecider implements AutoCloseable
{
	private String model_ID;
	private final Driver driver;
	private final HashMap<String, String> transLookup; 

    public LCTTransDecider( String uri, String user, String password, String model_ID )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
        this.model_ID = model_ID;
        this.transLookup = getLandCoverTransitionMap();
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    
    public HashMap<String, String>  getLandCoverTransitionMap() {
        try ( Session session = driver.session() )
        {
            return session.readTransaction( new TransactionWork<HashMap<String, String>>()
            {
                @Override
                public HashMap<String, String>  execute( Transaction tx )
                {
                    return getLandCoverTransitionData( tx, model_ID );
                }
            } );
        }
    }
    
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
        try ( LCTTransDecider decider = new LCTTransDecider( "bolt://localhost:7687", user, password, "AgroSuccess-dev" ) )
        {
        	System.out.println(decider.getLandCoverTransitionMap());
        	System.out.println(decider.getNextLandCoverType("Burnt", "regeneration", "north", false, true, true, "xeric", "2"));
        	
        	// how to handle cases where environmental conditions change part way through transition? Refer
        	// back to Millington2009. Maybe this is an argument for passing the whole SimulationCell object to 
        	// getNextLandCoverType?
        }
    }
}
