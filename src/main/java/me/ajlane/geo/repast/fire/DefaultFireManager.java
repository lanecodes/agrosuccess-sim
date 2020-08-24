package me.ajlane.geo.repast.fire;

import org.apache.log4j.Logger;
import cern.jet.random.Poisson;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPoint;

/**
 * Runs the AgroSuccess wildfire model for a simulation time step
 *
 * @author Andrew Lane
 */
public class DefaultFireManager implements FireManager {

  final static Logger logger = Logger.getLogger(DefaultFireManager.class);

  private final FireSpreader fireSpreader;
  private final FlammabilityChecker<GridPoint> flammabilityChecker;
  private final Dimensions gridDims;
  private final Poisson distr;
  private final Double vegetationMoistureParam;

  /**
   * @param fireSpreader Object used to spread fire given an initial ignition
   * @param flammabilityChecker Object that indicates whether or not grid cells are flammable (e.g.
   *        depending on land-cover type)
   * @param gridDims Grid dimensions
   * @param meanNumFiresPerYear Expected number of fires in the landscape in a year
   * @param vegetationMoistureParam Dimensionless quantity parameterising the amount of moisture in
   *        the fuel at the time of the fire
   */
  public DefaultFireManager(FireSpreader fireSpreader,
      FlammabilityChecker<GridPoint> flammabilityChecker, Dimensions gridDims,
      Double meanNumFiresPerYear, Double vegetationMoistureParam) {
    this.fireSpreader = fireSpreader;
    this.flammabilityChecker = flammabilityChecker;
    this.gridDims = gridDims;
    this.distr = RandomHelper.createPoisson(meanNumFiresPerYear);
    this.vegetationMoistureParam = vegetationMoistureParam;
  }

  /**
   * Runs the fire model for a simulated time step
   *
   * <p>
   * Draws {@code numFiresToStart} from a poisson distribution with parameter provided to the class
   * constructor at the beginning of the simulation. Then sets that many fires, using a
   * {@link FireSpreader} to determine where those fires spread. If it takes more than 1000 attempts
   * to randomly draw a flammable cell at random, the algorithm ends.
   * <p>
   */
  @Override
  @ScheduledMethod(start = 1, interval = 1, priority = 1)
  public void startFires() {
    // List<GridPoint> firesStarted = new ArrayList<GridPoint>();
    int numFiresToStart = numFires();
    for (int i = 0; i < numFiresToStart; i++) {
      GridPoint initialIgnitionPoint = findFlammablePoint(this.gridDims);
      if (initialIgnitionPoint == null) {
        logger.warn("Could not find a flammable cell to initialise fire " + i + "of"
            + numFiresToStart);
        break;
      }
      // firesStarted.add(initialIgnitionPoint);
      this.fireSpreader.spreadFire(initialIgnitionPoint);
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

  /**
   * Draws random grid points until a flammable grid cell is found
   *
   * <p>
   * If no flammable cell is found after 1000 random draws, return {@code null}.
   * </p>
   *
   * @param gridDims Dimensions of the simulation grid
   * @return Randomly drawn flammable grid cell, or {@code null} if no flammable cell could be found
   */
  private GridPoint findFlammablePoint(Dimensions gridDims) {
    int attemptCounter = 0;
    while (attemptCounter < 1000) {
      GridPoint randomPoint = randomGridPoint(gridDims);
      if (this.flammabilityChecker.isFlammable(randomPoint)) {
        return randomPoint;
      }
      attemptCounter++;
    }
    return null;
  }

  /**
   * Draws a grid cell at random
   *
   * @param gridDims Dimensions of the simulation grid
   * @return Randomly selected cell
   */
  private static GridPoint randomGridPoint(Dimensions gridDims) {
    int xCoord = RandomHelper.nextIntFromTo(0, (int) gridDims.getWidth() - 1);
    int yCoord = RandomHelper.nextIntFromTo(0, (int) gridDims.getHeight() - 1);
    return new GridPoint(xCoord, yCoord);
  }

}
