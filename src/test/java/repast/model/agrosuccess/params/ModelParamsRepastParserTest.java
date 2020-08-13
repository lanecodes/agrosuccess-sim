package repast.model.agrosuccess.params;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import me.ajlane.geo.repast.colonisation.csr.CompletelySpatiallyRandomParams;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.Parameters;

public class ModelParamsRepastParserTest {

  private DefaultParameters buildCorrectParameters(DefaultParameters params) {
    // Land-cover colonisation parameters
    params.addParameter("landCoverColonisationBaseRate", "Rate of colonisation from outside grid",
        java.lang.Double.class, 0.05, true);
    params.addParameter("landCoverColonisationSpreadRate", "Rate of colonisation from within grid",
        java.lang.Double.class, 0.2, true);

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

    assertEquals(new CompletelySpatiallyRandomParams(0.05, 0.2), envrModelParams
        .getLandCoverColoniserParams());
    assertEquals(new SoilMoistureParams(500, 1000), envrModelParams.getSoilMoistureParams());
  }

}
