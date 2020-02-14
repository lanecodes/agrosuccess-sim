package me.ajlane.geo.repast.fire;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import cern.jet.random.Poisson;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

public class FireManager {

  final static Logger logger = Logger.getLogger(FireManager.class);

  private FireSpreader fireSpreader;
  private Poisson distr;

  public FireManager(double meanNumFiresPerYear, FireSpreader fireSpreader) {
    this.fireSpreader = fireSpreader;
    this.distr = RandomHelper.createPoisson(meanNumFiresPerYear);
  }

  public int numFires() {
    return distr.nextInt();
  }

  /**
   * Attempts to find a flammable grid cell for each of the numFiresToStart 1000 times.
   * After this many attempts to find a flammable cell it gives up.
   * @return
   */
  @ScheduledMethod(start = 1, interval = 1, priority = 1)
  public List<GridPoint> startFires() {
    List<GridPoint> firesStarted = new ArrayList<GridPoint>();
    int numFiresToStart = numFires();
    for (int i=0; i<numFiresToStart; i++) {
      int attemptCounter = 0;
      while (attemptCounter < 1000) {
        GridPoint randomPoint = randomGridPoint(this.fireSpreader.getLct());
        if (isFlammable(this.fireSpreader.getLct(), randomPoint)) {
          firesStarted.add(randomPoint);
          this.fireSpreader.spreadFire(randomPoint);
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
   return firesStarted;

  }

  private GridPoint randomGridPoint(ValueLayer layer) {
    Dimensions dims = layer.getDimensions();
    int xCoord = RandomHelper.nextIntFromTo(0, (int) dims.getWidth() - 1);
    int yCoord = RandomHelper.nextIntFromTo(0, (int) dims.getHeight() - 1);
    return new GridPoint(xCoord, yCoord);
  }

  /**
   * TODO: See about refactoring this code, copied from FireSpreader
   *
   * @param lctLayer {@code ValueLayer} encoding land cover types
   * @param gridPoint Point on grid to query for flammability.
   * @return {@code true} if the cell at {@code gridPoint} has a flammable land cover type.
   */
  private static boolean isFlammable(ValueLayer lctLayer, GridPoint gridPoint) {
    int lctCode = (int) lctLayer.get(gridPoint.getX(), gridPoint.getY());
    return isFlammable(lctCode);
  }

  /**
   * TODO: See about refactoring this code, copied from FireSpreader
   *
   * @param lctCode Code for land cover type
   * @return {@code true} if {@code lctCode} corresponds to a flammable land cover type.
   */
  private static boolean isFlammable(int lctCode) {
    boolean isWaterQuarry = Lct.WaterQuarry.getCode() == lctCode;
    boolean isBurnt = Lct.Burnt.getCode() == lctCode;
    return !(isWaterQuarry || isBurnt); // true if neither water/quarry or burnt
  }

}
