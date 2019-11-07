package repast.model.agrosuccess;

import static org.junit.Assert.*;
import org.junit.Test;
import me.ajlane.geo.repast.seeddispersal.SeedDispersalParams;
import me.ajlane.geo.repast.seeddispersal.SeedViabilityParams;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.DefaultParameters;

public class ModelParamsRepastParserTest {
  
  private DefaultParameters buildCorrectParameters(DefaultParameters params) {
    // seed lifetime parameters
    params.addParameter("oakSeedLifetime", "Oak seed lifetime", java.lang.Integer.class, 3, true);
    params.addParameter("pineSeedLifetime", "Pine seed lifetime", java.lang.Integer.class, 7, true);
    params.addParameter("deciduousSeedLifetime", "Deciduous seed lifetime", 
        java.lang.Integer.class, 5, true);
    
    // Oak dispersal parameters
    params.addParameter("acornLocationParam", "Acorn location parameter", java.lang.Double.class, 
        3.844, true);
    params.addParameter("acornScaleParam", "Acorn scale parameter", java.lang.Double.class, 
        0.851, true);
    params.addParameter("acornMaxLognormalDist", "Max lognormal acorn dispersal distance", 
        java.lang.Double.class, 550.0, true);
    
    // Wind dispersal parameters
    params.addParameter("windDistDecreaseParam", "Wind distance decrease parameter", 
        java.lang.Double.class, 5.0, true);
    params.addParameter("windMinExpDist", "Wind minimum exponential dispersal probability distance",
        java.lang.Double.class, 75.0, true);
    params.addParameter("windMaxExpDist", "Wind maximum exponential dispersal probability distance",
        java.lang.Double.class, 100.0, true);
    
    // Soil moisture parameters
    params.addParameter("mesicThreshold", "Mesic soil moisture threshold", 
        java.lang.Integer.class, 500, true);
    params.addParameter("hydricThreshold", "Hydric soil moisture threshold", 
        java.lang.Integer.class, 1000, true);    
    
    return params;
  }


  @Test
  public void correctRepastParamsShouldYieldCorrectParamObjects() {
    Parameters repastParams = buildCorrectParameters(new DefaultParameters());
    EnvrModelParams envrModelParams = new ModelParamsRepastParser(repastParams);
    
    assertEquals(new SeedViabilityParams(3, 7, 5), envrModelParams.getSeedViabilityParams());
    assertEquals(new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100), 
        envrModelParams.getSeedDispersalParams());
    assertEquals(new SoilMoistureParams(500, 1000), envrModelParams.getSoilMoistureParams());    
  }
  
  @Test(expected = UnspecifiedParameterException.class)
  public void shouldThrowAnExceptionIfAnExpectedParameterNotSpecified() {
    DefaultParameters tmpRepastParams = buildCorrectParameters(new DefaultParameters());
    tmpRepastParams.removeParameter("acornScaleParam");
    Parameters repastParams = (Parameters) tmpRepastParams; tmpRepastParams = null;
    EnvrModelParams envrModelParams = new ModelParamsRepastParser(repastParams);
    envrModelParams.getSeedDispersalParams();
  }

}
