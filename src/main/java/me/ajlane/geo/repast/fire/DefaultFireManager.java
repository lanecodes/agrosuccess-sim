package me.ajlane.geo.repast.fire;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import cern.jet.random.Poisson;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Runs the AgroSuccess wildfire model for a simulation time step
 *
 * @author Andrew Lane
 */
public class DefaultFireManager implements FireManager {

  final static Logger logger = Logger.getLogger(DefaultFireManager.class);

  private final FireSpreader fireSpreader;
  private final FlammabilityChecker<GridPoint> flammabilityChecker;
  private final Poisson distr;
  private final Double vegetationMoistureParam;

  /**
   * @param fireSpreader Object used to spread fire given an initial ignition
   * @param flammabilityChecker Object that indicates whether or not grid cells are flammable (e.g.
   *        depending on land-cover type)
   * @param meanNumFiresPerYear Expected number of fires in the landscape in a year
   * @param vegetationMoistureParam Dimensionless quantity parameterising the amount of moisture in
   *        the fuel at the time of the fire
   */
  public DefaultFireManager(FireSpreader fireSpreader,
      FlammabilityChecker<GridPoint> flammabilityChecker,
      Double meanNumFiresPerYear, Double vegetationMoistureParam) {
    this.fireSpreader = fireSpreader;
    this.flammabilityChecker = flammabilityChecker;
    this.distr = RandomHelper.createPoisson(meanNumFiresPerYear);
    this.vegetationMoistureParam = vegetationMoistureParam;
  }

  /**
   * Attempts to find a flammable grid cell for each of the numFiresToStart 1000 times. After this
   * many attempts to find a flammable cell it gives up.
   */
  @Override
  @ScheduledMethod(start = 1, interval = 1, priority = 1)
  public void startFires() {
    List<GridPoint> firesStarted = new ArrayList<GridPoint>();
    int numFiresToStart = numFires();
    for (int i = 0; i < numFiresToStart; i++) {
      int attemptCounter = 0;
      while (attemptCounter < 1000) {
        GridPoint randomPoint = randomGridPoint(this.fireSpreader.getLct());
        if (this.flammabilityChecker.isFlammable(randomPoint)) {
          firesStarted.add(randomPoint);
          this.fireSpreader.spreadFire(randomPoint, this.vegetationMoistureParam);
          break;
        } else {
          attemptCounter++;
        }
      }
      if (attemptCounter == 1000) {
        logger.warn("Could not find a flammable cell to initialise fire "
            + i + "of" + numFiresToStart);
      }
    }

  }

  /**
   * Generates the number of fires that will be started in the current simulated year
   *
   * @return The number of fires to start
   */
  int numFires() {
    return distr.nextInt();
  }

  private GridPoint randomGridPoint(ValueLayer layer) {
    Dimensions dims = layer.getDimensions();
    int xCoord = RandomHelper.nextIntFromTo(0, (int) dims.getWidth() - 1);
    int yCoord = RandomHelper.nextIntFromTo(0, (int) dims.getHeight() - 1);
    return new GridPoint(xCoord, yCoord);
  }

}
