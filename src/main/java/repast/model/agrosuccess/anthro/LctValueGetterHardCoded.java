package repast.model.agrosuccess.anthro;

import me.ajlane.geo.repast.fire.LcfMapGetterHardCoded;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

/**
 * <p>
 * Holds data about the value of land-cover types needed by household agents to make decisions about
 * which land patches to manage.
 * </p>
 *
 * <p>
 * Implementation follows a similar pattern to {@link LcfMapGetterHardCoded}.
 * </p>
 *
 * <h4>TODOs</h4>
 * <ul>
 * <li>Investigate how to read this data from serialized file, like {@code Neo4jAgroSuccessLct} does
 * for a graph database</li>
 * </ul>
 *
 * @author Andrew Lane
 */
public class LctValueGetterHardCoded implements LctValueGetter {

  @Override
  public int getFertility(Lct lct) {
    int fertility = 0;
    switch (lct) {
      case WaterQuarry:
        fertility = 0;
        break;
      case Burnt:
        fertility = 5;
        break;
      case Barley:
        fertility = 4;
        break;
      case Wheat:
        fertility = 4;
        break;
      case Dal:
        fertility = 1;
        break;
      case Shrubland:
        fertility = 2;
        break;
      case Pine:
        fertility = 2;
        break;
      case TransForest:
        fertility = 3;
        break;
      case Deciduous:
        fertility = 3;
        break;
      case Oak:
        fertility = 3;
        break;
    }
    return fertility;
  }

  @Override
  public double getSlopeModificationValue(double pctSlope) {
    if (pctSlope < 18) {
      return 1.0;
    } else if (pctSlope < 36) {
      return 0.75;
    } else if (pctSlope < 173) {
      return 0.25;
    } else {
      return 0.0;
    }
  }

  @Override
  public int getLandCoverConversionCost(Lct lct) {
    int cost = -1;
    switch (lct) {
      case WaterQuarry:
        cost = -1;
        break;
      case Burnt:
        cost = 1;
        break;
      case Barley:
        cost = 1;
        break;
      case Wheat:
        cost = 1;
        break;
      case Dal:
        cost = 5;
        break;
      case Shrubland:
        cost = 2;
        break;
      case Pine:
        cost = 3;
        break;
      case TransForest:
        cost = 3;
        break;
      case Deciduous:
        cost = 3;
        break;
      case Oak:
        cost = 4;
        break;
    }
    return cost;
  }

}
