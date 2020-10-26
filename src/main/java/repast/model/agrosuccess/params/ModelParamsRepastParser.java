package repast.model.agrosuccess.params;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import me.ajlane.geo.repast.colonisation.csr.CompletelySpatiallyRandomParams;
import me.ajlane.geo.repast.fire.FireParams;
import me.ajlane.geo.repast.fire.LcfReplicate;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import repast.simphony.parameter.IllegalParameterException;
import repast.simphony.parameter.Parameters;

/**
 * <p>
 * Given a set of parameters generated by Repast Simphony in the course of parsing parameters.xml
 * this class will bundle up parameters corresponding xParams classes ready for consumption in the
 * model classes which need them.
 * </p>
 *
 * <p>
 * The parameters which are expected to be specified in the
 * {@code repast.simphony.parameter.Parameters} object passed to the constructor are as follows:
 * </p>
 *
 * <p>
 * <b>Land-cover colonisation parameters</b>
 * </p>
 * <ul>
 * <li>{@code landCoverColonisationBaseRate} (rate of colonisation from outside grid)</li>
 * <li>{@code landCoverColonisationSpreadRate} (rate of colonisation from inside grid)</li>
 * </ul>
 *
 * TODO In future it might be useful to have this class -- or a descendent of it -- implement
 * AnthroModelParams (providing access to anthropogenic model patrameters) in addition to the
 * EnvrModelParams already provided.
 *
 * TODO A future development might also add a WildFireParams interface
 *
 * @author Andrew Lane
 *
 */
public class ModelParamsRepastParser implements EnvrModelParams {

  final static Logger logger = Logger.getLogger(ModelParamsRepastParser.class);

  private Parameters rp;
  private Map<String, Double> defaultColonisationParams = getDefaultLandCoverColonisationParams();
  private Map<String, Integer> defaultSmParams = getDefaultSoilMoistureParams();
  private Map<String, Object> defaultFireParams = getDefaultFireParams();

  public ModelParamsRepastParser(Parameters repastParams) {
    this.rp = repastParams;
  }

  private Map<String, Double> getDefaultLandCoverColonisationParams() {
    // TODO Review default land-cover colonisation model parameters
    Map<String, Double> m = new HashMap<>();
    m.put("landCoverColonisationBaseRate", 0.05);
    m.put("landCoverColonisationSpreadRate", 0.2);
    return m;
  }

  private Map<String, Integer> getDefaultSoilMoistureParams() {
    Map<String, Integer> m = new HashMap<>();
    m.put("mesicThreshold", 500);
    m.put("hydricThreshold", 1000);
    return m;
  }

  private Map<String, Object> getDefaultFireParams() {
    Map<String, Object> m = new HashMap<>();
    m.put("lcfReplicate", LcfReplicate.Default);
    m.put("meanNumFiresPerYear", 4.1);
    return m;
  }

  @Override
  public CompletelySpatiallyRandomParams getLandCoverColoniserParams() {
    return new CompletelySpatiallyRandomParams(
        getDoubleParam("landCoverColonisationBaseRate", this.defaultColonisationParams.get(
            "landCoverColonisationBaseRate")),
        getDoubleParam("landCoverColonisationSpreadRate", this.defaultColonisationParams.get(
            "landCoverColonisationSpreadRate")));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SoilMoistureParams getSoilMoistureParams() {
    return new SoilMoistureParams(
        getIntegerParam("mesicThreshold", this.defaultSmParams.get("mesicThreshold")),
        getIntegerParam("hydricThreshold", this.defaultSmParams.get("hydricThreshold")));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireParams getFireParams() {
    String lcfString = getStringParam("lcfReplicate", "Default");
    double meanNumFiresParam = getDoubleParam("meanNumFiresPerYear",
        (double) this.defaultFireParams.get("meanNumFiresPerYear"));

    return new FireParams(meanNumFiresParam, LcfReplicate.valueOf(lcfString));
  }

  /**
   * @param paramName The name of a parameter expected to be specified in the {@code Parameters}
   *        object passed to this class's constructor. If no parameter of that name is found, a
   *        sensible default will be used instead.
   * @param defaultValue Value to use if {@code paramName} not found in the parameters object.
   * @return Value corresponding to the given parameter name
   */
  private Double getDoubleParam(String paramName, Double defaultValue) {
    Double value;
    try {
      value = this.rp.getDouble(paramName);
    } catch (IllegalParameterException e) {
      logger.warn(missingParamWarningMsg(paramName, defaultValue));
      value = defaultValue;
    }
    return value;
  }

  /**
   * @param paramName The name of a parameter expected to be specified in the {@code Parameters}
   *        object passed to this class's constructor. If no parameter of that name is found, a
   *        sensible default will be used instead.
   * @param defaultValue Value to use if {@code paramName} not found in the parameters object.
   * @return Value corresponding to the given parameter name
   */
  private Integer getIntegerParam(String paramName, Integer defaultValue) {
    Integer value;
    try {
      value = this.rp.getInteger(paramName);
    } catch (IllegalParameterException e) {
      logger.warn(missingParamWarningMsg(paramName, defaultValue));
      value = defaultValue;
    }
    return value;
  }

  /**
   * @param paramName The name of a parameter expected to be specified in the {@code Parameters}
   *        object passed to this class's constructor. If no parameter of that name is found, a
   *        sensible default will be used instead.
   * @param defaultValue Value to use if {@code paramName} not found in the parameters object.
   * @return Value corresponding to the given parameter name
   */
  private String getStringParam(String paramName, String defaultValue) {
    String value;
    try {
      value = this.rp.getString(paramName);
    } catch (IllegalParameterException e) {
      logger.warn(missingParamWarningMsg(paramName, defaultValue));
      value = defaultValue;
    }
    return value;
  }

  private <T> String missingParamWarningMsg(String paramName, T defaultValue) {
    return "Could not find entry for '" + paramName + "' in parameters.xml."
        + " Using default value of " + defaultValue;
  }

}
