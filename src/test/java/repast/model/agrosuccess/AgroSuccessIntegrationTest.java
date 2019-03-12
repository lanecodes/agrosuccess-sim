package repast.model.agrosuccess;

import static org.junit.Assert.*;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.valueLayer.GridValueLayer;
import me.ajlane.geo.repast.RepastGridUtils;
import me.ajlane.neo4j.EmbeddedGraphInstance;

public class AgroSuccessIntegrationTest {
  
  public Context<Object> context;
  public Schedule schedule;
  
  @BeforeClass
  public static void setUpLogging() {
    LogManager.getRootLogger().setLevel(Level.ALL);
    BasicConfigurator.configure();
  }  

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    schedule = new Schedule();
    RunEnvironment.init(schedule, null, null, true);
    
    context = new DefaultContext<Object>();
    ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
    context = builder.build(context);
    
    RunState.init().setMasterContext(context);    
  }

  @After
  public void tearDown() throws Exception {
    for (Object graph: context.getObjects(EmbeddedGraphInstance.class)) {      
      ((GraphDatabaseService)graph).shutdown();      
    }
    context = null;
    schedule = null;    
  }
  
  @Test
  public void testAllValueLayersAccessibleFromContext() {
    assertTrue(context.getValueLayer(LscapeLayer.SoilMoisture.name()) instanceof GridValueLayer);     
    assertTrue(context.getValueLayer(LscapeLayer.SoilType.name()) instanceof GridValueLayer);   
    assertTrue(context.getValueLayer(LscapeLayer.Lct.name()) instanceof GridValueLayer); 
    assertTrue(context.getValueLayer(LscapeLayer.Slope.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Aspect.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Pine.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Oak.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Deciduous.name()) instanceof GridValueLayer);
  }
  
  @Test
  public void spatialVariationInSoilMoistureShouldEmerge() {
    GridValueLayer sm = (GridValueLayer)context.getValueLayer(LscapeLayer.SoilMoisture.name());
    System.out.println(RepastGridUtils.gridValueLayerToString(sm));
    for (int i=0; i<5; i++) {
      schedule.execute();
    }
    System.out.println(RepastGridUtils.gridValueLayerToString(sm));
    fail("not implemented");
  }
  
  @Test
  public void seedsSouldBeDepositedOverTime() {
    // initially there are no seeds in the model, but these should be distributed by seed sources
    fail("not implemented");
  }

}
