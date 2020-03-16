package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class WoodReturnCalculatorTest {

  private final static double TOLERANCE = 0.5;

  @Test
  public void testGetReturns() {
    double woodGatheringIntensityInKgPerSqm = 0.08; // after Ullah 2013 Ch. 6
    double rasterCellAreaInSqm = 625.; // 25 m X 25 m

    Set<PatchOption> woodPatches = new HashSet<>();
    // Note amount of wood gathered independent of wood value (or any other attribute) of patch
    woodPatches.add(new PatchOption(new GridPoint(0, 0), 0, 0.4, 0, 0));

    WoodReturnCalculator woodRC =
        new WoodReturnCalculator(woodGatheringIntensityInKgPerSqm, rasterCellAreaInSqm);

    double returns = woodRC.getReturns(woodPatches);

    // \massWoodReturnFromCell{i} = \woodGatheringIntensity \cdot \rasterCellArea
    // = 0.08 * 625 = 50.0
    assertEquals(50, returns, TOLERANCE);
  }

  @Test
  public void testGetReturnsMultiplePatches() {
    double woodGatheringIntensityInKgPerSqm = 0.08; // after Ullah 2013 Ch. 6
    double rasterCellAreaInSqm = 625.; // 25 m X 25 m

    Set<PatchOption> woodPatches = new HashSet<>();
    // Note amount of wood gathered independent of wood value (or any other attribute) of patch
    woodPatches.add(new PatchOption(new GridPoint(0, 0), 0, 0.3, 0, 0));
    woodPatches.add(new PatchOption(new GridPoint(0, 1), 0, 0.2, 0, 0));
    woodPatches.add(new PatchOption(new GridPoint(1, 0), 0, 0.5, 0, 0));

    WoodReturnCalculator woodRC =
        new WoodReturnCalculator(woodGatheringIntensityInKgPerSqm, rasterCellAreaInSqm);

    double returns = woodRC.getReturns(woodPatches);

    // \massWoodReturnFromCell{i} = \woodGatheringIntensity \cdot \rasterCellArea
    // = 0.08 * 625 * 3 cells = 150.0
    assertEquals(150, returns, TOLERANCE);
  }

  @Test(expected = MissingEnvrResourceException.class)
  public void testGetReturnsThrowsErrorWithZeroWoodValue() {
    double woodGatheringIntensityInKgPerSqm = 0.08; // after Ullah 2013 Ch. 6
    double rasterCellAreaInSqm = 625.; // 25 m X 25 m

    Set<PatchOption> woodPatches = new HashSet<>();
    // Note amount of wood gathered independent of wood value (or any other attribute) of patch
    woodPatches.add(new PatchOption(new GridPoint(0, 0), 0, 0, 0, 0));

    WoodReturnCalculator woodRC =
        new WoodReturnCalculator(woodGatheringIntensityInKgPerSqm, rasterCellAreaInSqm);

    woodRC.getReturns(woodPatches);
  }

}
