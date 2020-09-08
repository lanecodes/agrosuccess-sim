package me.ajlane.geo.repast.colonisation.csr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import me.ajlane.geo.repast.colonisation.LandCoverColoniser;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.AgroSuccessCodeAliases.SeedPresence;
import repast.model.agrosuccess.LscapeLayer;

/**
 * Implements the completely spatially random land-cover colonisation model.
 *
 * @author Andrew Lane
 *
 */
public class CompletelySpatiallyRandomColoniser implements LandCoverColoniser {

  final static Logger logger = Logger.getLogger(CompletelySpatiallyRandomColoniser.class);
  private final CartesianGridDouble2D landCoverType;
  private final Map<LscapeLayer, WriteableCartesianGridDouble2D> juvenilePresenceLayerMap;
  private final Map<LscapeLayer, Lct> juvenileToLctMap;
  private final CompletelySpatiallyRandomParams params;
  private final List<GridLoc> allGridLoc;
  private final Random rnd;

  /**
   * @see #CompletelySpatiallyRandomColoniser(CartesianGridDouble2D, WriteableCartesianGridDouble2D,
   *      WriteableCartesianGridDouble2D, WriteableCartesianGridDouble2D,
   *      CompletelySpatiallyRandomParams, Random)
   */
  public CompletelySpatiallyRandomColoniser(CartesianGridDouble2D landCoverType,
      WriteableCartesianGridDouble2D juvenilePine,
      WriteableCartesianGridDouble2D juvenileOak, WriteableCartesianGridDouble2D juvenileDeciduous,
      CompletelySpatiallyRandomParams params) {
    this(landCoverType, juvenilePine, juvenileOak, juvenileDeciduous, params, new Random());
  }

  /**
   * @param landCoverType Layer specifying land-cover types
   * @param juvenilePine Layer indicating whether juvenile pine is present in each cell
   * @param juvenileOak Layer indicating whether juvenile oak is present in each cell
   * @param juvenileDeciduous Layer indicating whether juvenile deciduous is present in each cell
   * @param params Parameters for the completely spatially random land-cover colonisation model
   * @param rnd The source of randomness used to sample grid cells to add new seed sources to.
   *        Defaults to {@link java.util.Random}
   */
  public CompletelySpatiallyRandomColoniser(CartesianGridDouble2D landCoverType,
      WriteableCartesianGridDouble2D juvenilePine,
      WriteableCartesianGridDouble2D juvenileOak, WriteableCartesianGridDouble2D juvenileDeciduous,
      CompletelySpatiallyRandomParams params, Random rnd) {
    this.landCoverType = landCoverType;
    this.params = params;
    this.rnd = rnd;

    this.allGridLoc = getAllGridLoc();

    this.juvenilePresenceLayerMap = new EnumMap<>(LscapeLayer.class);
    this.juvenilePresenceLayerMap.put(LscapeLayer.Pine, juvenilePine);
    this.juvenilePresenceLayerMap.put(LscapeLayer.Oak, juvenileOak);
    this.juvenilePresenceLayerMap.put(LscapeLayer.Deciduous, juvenileDeciduous);

    this.juvenileToLctMap = new EnumMap<>(LscapeLayer.class);
    this.juvenileToLctMap.put(LscapeLayer.Pine, Lct.Pine);
    this.juvenileToLctMap.put(LscapeLayer.Oak, Lct.Oak);
    this.juvenileToLctMap.put(LscapeLayer.Deciduous, Lct.Deciduous);
  }

  /**
   * Used for iterating over in {@link #addJuvenilesInCellsWithCorrespondingMatureVegetation} and
   * {@link #countSpreadSources}
   *
   * @return A list of all the grid locations in the simulation grid
   */
  private List<GridLoc> getAllGridLoc() {
    List<GridLoc> allGridLoc = new ArrayList<>();
    for (int x = 0; x < getGridWidth(); x++) {
      for (int y = 0; y < getGridHeight(); y++) {
        allGridLoc.add(new GridLoc(x, y));
      }
    }
    return allGridLoc;
  }

  @Override
  // @ScheduledMethod(start = 1, interval = 1, priority = 1)
  // TODO To remove dependency on repast, consider scheduling this action in some other way.
  // E.g. follow the pattern in {@link SoilMoistureUpdateAction}
  public void updateJuvenilePresenceLayers() {
    for (Map.Entry<LscapeLayer, WriteableCartesianGridDouble2D> e : juvenilePresenceLayerMap
        .entrySet()) {
      addJuvenilesInCellsWithCorrespondingMatureVegetation(e.getKey(), e.getValue());
      addColonisingJuveniles(e.getKey(), e.getValue());
    }
    logger.debug("Juvenile vegetation presence layers updated");
  }

  /**
   * Modify juvenile vegetation presence layer for {@code juvenileType} to ensure that cells
   * containing the corresponding mature vegetation also contain juveniles.
   *
   * @param juvenileType
   * @param juvenilePresenceLayer
   */
  private void addJuvenilesInCellsWithCorrespondingMatureVegetation(LscapeLayer juvenileType,
      WriteableCartesianGridDouble2D juvenilePresenceLayer) {
    for (GridLoc cell : allGridLoc) {
      double lctCode = this.juvenileToLctMap.get(juvenileType).getCode();
      if ((int) this.landCoverType.getValue(cell) == lctCode) {
        juvenilePresenceLayer.setValue(SeedPresence.True.getCode(), cell);
      }
    }
  }

  /**
   * Modify juvenile vegetation presence layer for {@code juvenileType} to include juvenile
   * vegetation arising due to colonisation from mature vegetation sources both within and outside
   * the simulation grid.
   *
   * @param juvenileType
   * @param juvenilePresenceLayer
   */
  private void addColonisingJuveniles(LscapeLayer juvenileType,
      WriteableCartesianGridDouble2D juvenilePresenceLayer) {
    int numJuvenileCellsToAdd = (getNumCellsColonisedFromOutsideGrid()
        + getNumCellsColonisedFromInsideGrid(juvenileType));
    List<GridLoc> cellsToColonise = randomSampleGridLoc(numJuvenileCellsToAdd);
    for (GridLoc cell : cellsToColonise) {
      juvenilePresenceLayer.setValue(SeedPresence.True.getCode(), cell);
    }
  }

  /**
   * @param n
   * @return Random sample of {@code n} elements from {@code allGridLoc}
   */
  private List<GridLoc> randomSampleGridLoc(int n) {
    if (n < allGridLoc.size()) {
      List<GridLoc> allGridLocCopy = new ArrayList<>(allGridLoc);
      Collections.shuffle(allGridLocCopy, this.rnd);
      return allGridLocCopy.subList(0, n);
    }
    return allGridLoc;
  }

  /**
   * @return Number of cells colonised from sources outside the simulation grid
   */
  int getNumCellsColonisedFromOutsideGrid() {
    return (int) Math.round(getGridWidth() * getGridHeight() * params.getBaseRate());
  }

  /**
   * @param juvenileType Type of juvenile vegetation for which newly colonised cells considered
   * @return Number of cells colonised from sources (i.e. mature vegetation) inside the simulation
   *         grid
   */
  int getNumCellsColonisedFromInsideGrid(LscapeLayer juvenileType) {
    return (int) Math.round(countSpreadSources(juvenileType) * params.getSpreadRate());
  }

  /**
   * @param juvenileType
   * @return The number of sources of mature vegetation corresponding to {@code juvenileType} in the
   *         simulation grid that can colonise other cells
   */
  private int countSpreadSources(LscapeLayer juvenileType) {
    double lctCode = this.juvenileToLctMap.get(juvenileType).getCode();
    int count = 0;
    for (GridLoc loc : allGridLoc) {
      if ((int) this.landCoverType.getValue(loc) == lctCode) {
        count += 1;
      }
    }
    return count;
  }

  private int getGridWidth() {
    return this.landCoverType.getDimensions().getWidth();
  }

  private int getGridHeight() {
    return this.landCoverType.getDimensions().getHeight();
  }

}
