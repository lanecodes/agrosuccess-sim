package repast.model.agrosuccess;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//import me.ajlane.geo.FlowConnectivityNetwork;
import repast.simphony.valueLayer.GridValueLayer;

public class StudySiteDataContainerTest {
	
	private StudySiteDataContainer dataContainer;

	@Before
	public void setUp() {
		File dataDirRoot = new File("data/geographic"); 
		dataContainer =	new StudySiteDataContainer(new File(dataDirRoot, "navarres"));
	}
	
	@After
	public void tearDown() {
		dataContainer =	null;
	}

	@Test 
	public void navarresPrecipitationShouldBe41() {
		assertEquals(41.0, dataContainer.getPrecipitation(), 0.01);		
	}
	
	@Ignore
	@Test
	public void navarresFlowConnectivityNetworkShouldInitialise() {
		//FlowConnectivityNetwork flowNet = dataContainer.getFlowConnectivityNetwork();		
	}
	
	@Test 
	public void navarresSlopeShouldInitialise() {
		GridValueLayer slope = dataContainer.getSlopeMap();
		assertNotNull(slope);
	}
	
	@Test
	public void navarresAspectShouldInitialise() {
		GridValueLayer aspect = dataContainer.getAspectMap();
		assertNotNull(aspect);
	}
	
	@Test
	public void navarresFlowDirectionShouldInitialise() {
		GridValueLayer flowDirection = dataContainer.getFlowDirectionMap();
		assertNotNull(flowDirection);
	}
	
	@Test
	public void navarresSoilTypeShouldInitialise() {
		GridValueLayer soilType = dataContainer.getSoilTypeMap();
		assertNotNull(soilType);
	}
	
	@Test
	public void navarresLandcoverTypeShouldInitialise() {
		GridValueLayer landcoverType = dataContainer.getLandcoverTypeMap();
		assertNotNull(landcoverType);
	}

}
