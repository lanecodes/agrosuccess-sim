package me.ajlane.geo.repast.fire;

import java.util.HashMap;
import java.util.Map;
import me.ajlane.geo.Direction;

/**
 * Calculate the risk factor of a fire spreading into a cell neighbouring a burning cell. Designed
 * to factor into account the effect of wind speed, and wind direction with respect to the direction
 * of fire spread.
 *
 * @author Andrew Lane
 *
 */
public class WindRiskCalculator {

  private Map<WindOrientation, Double> lowWindSpeedRiskMap;
  private Map<WindOrientation, Double> mediumWindSpeedRiskMap;
  private Map<WindOrientation, Double> highWindSpeedRiskMap;

  public WindRiskCalculator() {
    this.lowWindSpeedRiskMap = lowWindSpeedRiskMap();
    this.mediumWindSpeedRiskMap = mediumWindSpeedRiskMap();
    this.highWindSpeedRiskMap = highWindSpeedRiskMap();
  }

  /**
   * See Table 3 in <a href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al.
   * 2009</a>.
   *
   * @return Mapping from angle between wind direction and direction of fire spread, to wind spread
   *         risk factor when the wind speed is low.
   */
  private Map<WindOrientation, Double> lowWindSpeedRiskMap() {
    Map<WindOrientation, Double> m = new HashMap<>();
    m.put(WindOrientation.Parallel, 1.10);
    m.put(WindOrientation.Acute, 1.05);
    m.put(WindOrientation.Perpendicular, 1.00);
    m.put(WindOrientation.Obtuse, 0.95);
    m.put(WindOrientation.Antiparallel, 0.90);
    return m;
  }

  /**
   * See Table 3 in <a href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al.
   * 2009</a>.
   *
   * @return Mapping from angle between wind direction and direction of fire spread, to wind spread
   *         risk factor when the wind speed is medium.
   */
  private Map<WindOrientation, Double> mediumWindSpeedRiskMap() {
    Map<WindOrientation, Double> m = new HashMap<>();
    m.put(WindOrientation.Parallel, 1.20);
    m.put(WindOrientation.Acute, 1.10);
    m.put(WindOrientation.Perpendicular, 1.00);
    m.put(WindOrientation.Obtuse, 0.90);
    m.put(WindOrientation.Antiparallel, 0.80);
    return m;
  }

  /**
   * See Table 3 in <a href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al.
   * 2009</a>.
   *
   * @return Mapping from angle between wind direction and direction of fire spread, to wind spread
   *         risk factor when the wind speed is high.
   */
  private Map<WindOrientation, Double> highWindSpeedRiskMap() {
    Map<WindOrientation, Double> m = new HashMap<>();
    m.put(WindOrientation.Parallel, 1.50);
    m.put(WindOrientation.Acute, 1.00);
    m.put(WindOrientation.Perpendicular, 0.90);
    m.put(WindOrientation.Obtuse, 0.80);
    m.put(WindOrientation.Antiparallel, 0.70);
    return m;
  }

  /**
   * @param windSpeed Wind speed
   * @param windDir Wind direction
   * @param targetCellDir Direction of potential fire spread
   * @return Wind risk factor considering wind speed, and the direction of wind with respect to
   *         direction of potential fire spread
   */
  public Double getRisk(WindSpeed windSpeed, Direction windDir, Direction targetCellDir) {
    WindOrientation windOrientation = getWindOrientation(windDir, targetCellDir);
    Double windRisk;
    if (windSpeed == WindSpeed.Low) {
      windRisk = this.lowWindSpeedRiskMap.get(windOrientation);
    } else if (windSpeed == WindSpeed.Medium) {
      windRisk = this.mediumWindSpeedRiskMap.get(windOrientation);
    } else {
      windRisk = this.highWindSpeedRiskMap.get(windOrientation);
    }
    return windRisk;
  }

  /**
   * @see me.ajlane.geo.repast.fire.WindOrientation
   *
   * @param windDir Wind direction
   * @param targetCellDir Direction of potential fire spread
   * @return Characterisation of the angle between wind direction and the direction of potential
   *         fire spread
   */
  public static WindOrientation getWindOrientation(Direction windDir, Direction targetCellDir) {
    int directionOrdinalDiff = windDir.ordinal() - targetCellDir.ordinal();
    int normedOrdinalDiff = normaliseDirectionOrdinalDiff(directionOrdinalDiff);
    WindOrientation wo;

    switch (normedOrdinalDiff) {
      case 0:
        wo = WindOrientation.Parallel;
        break;
      case 1:
        wo = WindOrientation.Acute;
        break;
      case 2:
        wo = WindOrientation.Perpendicular;
        break;
      case 3:
        wo = WindOrientation.Obtuse;
        break;
      case 4:
        wo = WindOrientation.Antiparallel;
        break;
      default:
        throw new RuntimeException("Unexpectedly failed to match a WindOrientation.");
    }

    return wo;
  }

  private static int normaliseDirectionOrdinalDiff(int ordinalDiff) {
    return Math.abs(Math.abs(ordinalDiff + 4) - 4);
  }

}
