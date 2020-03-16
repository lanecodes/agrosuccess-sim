package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class FarmingReturnCalculatorTest {

  private final static double TOLERANCE = 0.5;

  @Test
  public void testGetReturns() {
    double maxWheatYieldPerHaInKg = 3500.; // after Ullah 2013 Ch. 6
    double rasterCellAreaInSqm = 625.; // 25 m X 25 m
    double precipitationMm = 500.;

    double patchFertility = 0.6;
    double slopeModValue = 0.75;
    Set<PatchOption> wheatPatches = new HashSet<>();
    wheatPatches.add(new PatchOption(new GridPoint(0, 0), patchFertility, 0, slopeModValue, 0));

    FarmingReturnCalculator farmingRC =
        new FarmingReturnCalculator(maxWheatYieldPerHaInKg, rasterCellAreaInSqm);

    double returns = farmingRC.getReturns(wheatPatches, precipitationMm);

    // r_i^(w) = [(0.51 * ln(500/1000) + 1.03) + (0.19 * ln(0.6) + 1)] / 2 = 0.78972
    // R_i^(w) = 0.78972 * 0.75 * 3500 * 625 / 10,000 = 129.56 (5 s.f.)
    assertEquals(130.0, returns, TOLERANCE);
  }

  @Test
  public void testGetReturnsMultiplePatches() {
    double maxWheatYieldPerHaInKg = 3500.; // after Ullah 2013 Ch. 6
    double rasterCellAreaInSqm = 625.; // 25 m X 25 m
    double precipitationMm = 500.;

    double patchFertility1 = 0.6;
    double patchFertility2 = 0.4;

    double slopeModValue1 = 0.75;
    double slopeModValue2 = 0.50;

    Set<PatchOption> wheatPatches = new HashSet<>();

    wheatPatches.add(new PatchOption(new GridPoint(0, 0), patchFertility1, 0, slopeModValue1, 0));
    wheatPatches.add(new PatchOption(new GridPoint(0, 0), patchFertility2, 0, slopeModValue2, 0));

    FarmingReturnCalculator farmingRC =
        new FarmingReturnCalculator(maxWheatYieldPerHaInKg, rasterCellAreaInSqm);

    double returns = farmingRC.getReturns(wheatPatches, precipitationMm);

    // r_1^(w) = [(0.51 * ln(500/1000) + 1.03) + (0.19 * ln(0.6) + 1)] / 2 = 0.78972
    // r_2^(w) = [(0.51 * ln(500/1000) + 1.03) + (0.19 * ln(0.4) + 1)] / 2 = 0.75120
    //
    // R_1^(w) = 0.78972 * 0.75 * 3500 * 625 / 10,000 = 129.56 (5 s.f.)
    // R_2^(w) = 0.75120 * 0.50 * 3500 * 625 / 10,000 = 82.163 (5 s.f.)

    // R_total^(w) = 129.56 + 82.163 = 211.72
    assertEquals(211.7, returns, TOLERANCE);
  }

}
