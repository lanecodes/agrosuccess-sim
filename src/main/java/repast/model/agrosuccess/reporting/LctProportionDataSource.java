package repast.model.agrosuccess.reporting;

import repast.simphony.data2.NonAggregateDataSource;

/**
 * Reports the proportion of the landscape occupied by different land-cover types
 *
 * <p>
 * For notes on how to configure Repast Simphony to use this class as a data source see
 * <code>reporting.html</code> in site documentation.
 * </p>
 *
 * @author Andrew Lane
 */
public class LctProportionDataSource implements NonAggregateDataSource {

  @Override
  public String getId() {
    return "lct_proportions";
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }

  @Override
  public Class<?> getSourceType() {
    return LctProportionAggregator.class;
  }

  @Override
  public Object get(Object obj) {
    LctProportionAggregator lctPropAggregator = (LctProportionAggregator) obj;
    return lctPropAggregator.getLctProportions().toString();
  }

}
