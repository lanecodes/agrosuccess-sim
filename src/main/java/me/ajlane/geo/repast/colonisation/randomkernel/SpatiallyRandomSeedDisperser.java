/**
 *
 */
package me.ajlane.geo.repast.colonisation.randomkernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import me.ajlane.geo.GridUtils;
import me.ajlane.geo.repast.colonisation.LandCoverColoniser;
import me.ajlane.random.ArrayUtils;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Number of <strong>new</strong> seeds given by N_{\sigma} - No cells currently occupied by seeds
 * of this species.
 *
 *
 * @author Andrew Lane
 *
 */
public class SpatiallyRandomSeedDisperser extends SeedDisperser implements LandCoverColoniser {

  final static Logger logger = Logger.getLogger(SpatiallyRandomSeedDisperser.class);

  Map<String, GridValueLayer> seedLayersMap = new HashMap<String, GridValueLayer>();

  /**
   * @param xCellSize Horizontal length of raster grid cells
   * @param yCellSize Vertical length of raster grid cells
   * @param seedViabilityParams Parameters specifying how long different types of seeds survive in
   *        the seed bank after being deposited.
   * @param seedDispersalParams Parameters specifying the probability distributions which control
   *        how seeds are dispersed in the model.
   * @param context Repast Context the SpatiallyRandomSeedDisperser belongs to. This is used in the
   *        constructor to retrieve references to the GridValueLayer-s storing the spatial
   *        configuration of each type seed.
   */
  public SpatiallyRandomSeedDisperser(double xCellSize, double yCellSize,
      SeedViabilityParams seedViabilityParams, SeedDispersalParams seedDispersalParams,
      Context<Object> context) {

    landCoverType = (GridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());


    // move these into seedLayersMap, referring to individual names get confusing
    pineSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Pine.name());
    oakSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Oak.name());
    deciduousSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Deciduous.name());

    seedLayersMap.put("pine", pineSeeds);
    seedLayersMap.put("oak", oakSeeds);
    seedLayersMap.put("deciduous", deciduousSeeds);

    // seed state monitoring
    time = 0;
    svm = new SeedViabilityMonitor(seedViabilityParams);

    // check GridValueLayer-s are valid and extract grid dimensions
    checkValueLayersAccessible();
    checkValueLayerDimensionsMatch();
    processGridShape(xCellSize, yCellSize);

    // initialise seed presence probability generators
    acornPresenceProbGenerator = new AcornPresenceProbGenerator(n, cellSize, seedDispersalParams);
    windSeedPresenceProbGenerator =
        new WindSeedPresenceProbGenerator(n, cellSize, seedDispersalParams);
  }



  /**
   * Use the seedSourceMap (landcover types -> seed sources) and landCoverType GridValuyeLayer to
   * calculate the number of pine, oak and deciduous seed sources.
   *
   * @param speciesName Species (pine, oak, deciduous) whose seed sources we want to count.
   * @return Number of seed source pixels for given species
   */
  private int getSeedSourceCount(String speciesName) {
    int seedSourceCount = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (seedSourceMap.get(speciesName).contains((int) landCoverType.get(j, i))) {
          seedSourceCount++;
        }
      }
    }
    logger.debug(speciesName + " seed source count: " + seedSourceCount);

    return seedSourceCount;
  }

  /**
   * <p>
   * <emph>Assuming</emph> a spatially uniform distribution of seed sources across the spatial grid,
   * calculate the average distance between a seed source and a non-seed source cell. This is given
   * as:
   * </p>
   * <p>
   * \[ d_{\sigma} = \frac{N_{\sigma}}{2\sqrt{N_{tot}}} l \]
   * <p>
   * <p>
   * where $N_{\sigma}$ is the number of seed sources for species $\sigma$ and $N_{tot}$ is the
   * total number of model cells.
   * </p>
   *
   * @param numSeedSources Number of seed sources in grid
   * @return Typical distance between a seed source of {@code speciesName}, and a cell which is not
   *         a source of that species, assuming seed sources are evenly and uniformly distributed in
   *         the model grid.
   *
   */
  private double typicalDistanceToSeedSource(int numSeedSources) {
    return 0.5 * numSeedSources * cellSize / Math.sqrt(n);
  }

  double probCellHasSeed(String speciesName) {
    int numSeedSources = getSeedSourceCount(speciesName);
    if (speciesName == "pine" || speciesName == "deciduous") {
      if (numSeedSources == 0) {
        return windSeedPresenceProbGenerator.getMinProb();
      } else {
        return windSeedPresenceProbGenerator.getProb(typicalDistanceToSeedSource(numSeedSources));
      }
    } else if (speciesName == "oak") {
      if (numSeedSources == 0) {
        return acornPresenceProbGenerator.getMinProb();
      } else {
        return acornPresenceProbGenerator.getProb(typicalDistanceToSeedSource(numSeedSources));
      }
    } else {
      throw new IllegalArgumentException(
          "speciesName must be one of 'pine', " + "'deciduous' or 'oak'");
    }
  }

  /**
   * Factor in the number of seed sources, the typical distance between them assuming they are
   * uniformly scattered in space, and the likely distribution distance on that basis using the
   * species-appropriate distribution kernel.
   *
   * @param speciesName
   * @return the number of cells we expect to have seeds of speciesName in them.
   */
  int getExpectedNumCellsWithSeeds(String speciesName) {
    double prob = probCellHasSeed(speciesName);
    logger.debug("prob of cell having " + speciesName + " seed:" + prob);
    logger.debug("expected number of " + speciesName + " seeds: " + (int) Math.round(n * prob));
    return (int) Math.round(n * prob);

  }

  /**
   * Calculate the number of seeds to add to the model depending on the difference between the
   * expected number of cells with seeds assuming a uniform spread of seed-containing cells.
   *
   * Implementation note: the current number of seed-containing cells for the given species is
   * calculated using the SeedViabilityMonitor rather than the GridValueLayer as it is presumed
   * faster to get the length of an array than to iterate through all the elements of the array.
   *
   * @param speciesName Name of species for which we want to know how many cells to add seeds to
   * @return The number of cells to add speciesName seeds to. If there are already more than the
   *         expected number of seeds in the model, don't add any more seeds (return 0)
   */
  int getNumSeedsToAdd(String speciesName) {
    int nSeedsAlreadyInModel = svm.getNumSeeds(speciesName);
    int nSeedsToAdd = getExpectedNumCellsWithSeeds(speciesName) - nSeedsAlreadyInModel;
    if (nSeedsToAdd > 0) {
      return nSeedsToAdd;
    } else {
      return 0; // can't add a negative number of seeds
    }
  }

  void removeSeedFromGridValueLayer(Seed seed, GridValueLayer gvl) {
    int row = seed.getRow();
    int col = seed.getCol();
    // check GridValueLayer agrees that there is a seed to remove
    int currentValue = (int) gvl.get(col, row);
    if (currentValue == 1) {
      gvl.set(0.0, col, row);
    } else {
      logger.debug("Tried to remove the following seed from GridValueLayer containing no seed. "
          + "If there has been no transition to mature land-cover type in this "
          + "time step this might be a programming error.\n" + seed.toString());
    }
  }

  /**
   * Get a list of the indices of grid cells <emph>not</emph> occupied by seeds of speciesName.
   *
   * @param speciesName
   * @return
   */
  List<Integer> unoccupiedCellIndices(String speciesName) {
    List<Integer> cellIndices = new ArrayList<Integer>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if ((int) seedLayersMap.get(speciesName).get(j, i) == 0) {
          cellIndices.add(GridUtils.spatialCoordsToIndex(i, j, width));
        }
      }
    }
    return cellIndices;
  }

  /**
   * For given speciesName, add seed occupying cells to GridValueLayer and SeedViabilityMonitor
   *
   * @param speciesName
   */
  void addSeeds(String speciesName) {
    List<Integer> targetCells = ArrayUtils.randomDrawFromList(getNumSeedsToAdd(speciesName),
        unoccupiedCellIndices(speciesName));


    for (Integer cellID : targetCells) {

      int[] coords = GridUtils.indexToSpatialCoords(cellID, width, height);
      int row = coords[0];
      int col = coords[1];

      // add to GridValueLayer
      seedLayersMap.get(speciesName).set(1.0, col, row);

      // add to SeedViabilityMonitor
      svm.addSeed(new Seed(speciesName, time, row, col));
    }
  }

  void removeDeadSeeds() {
    Set<Seed> deadSeeds = svm.deadSeeds(time);
    for (Seed seed : deadSeeds) {
      for (Map.Entry<String, GridValueLayer> speciesEntry: seedLayersMap.entrySet()) {
        if (seed.getType() == speciesEntry.getKey()) {
          removeSeedFromGridValueLayer(seed, speciesEntry.getValue());
        }
      }
    }
  }

  /**
   * Step time forwards one step, remove dead seeds and add new ones according to the spatially
   * random seed dispersal protocol.
   *
   * @see me.ajlane.geo.repast.colonisation.LandCoverColoniser#updateJuvenilePresenceLayers()
   */
  @Override
  // @ScheduledMethod(start = 1, interval = 1, priority = 1)
  public void updateJuvenilePresenceLayers() {
    time++;
    removeDeadSeeds();
    String seedTypes[] = {"pine", "deciduous", "oak"};
    for (String type : seedTypes) {
      addSeeds(type);
    }
  }

}
