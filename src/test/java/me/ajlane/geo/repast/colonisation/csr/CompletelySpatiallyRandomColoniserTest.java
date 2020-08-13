package me.ajlane.geo.repast.colonisation.csr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.DefaultCartesianGridDouble2D;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.AgroSuccessCodeAliases.SeedPresence;
import repast.model.agrosuccess.LscapeLayer;

public class CompletelySpatiallyRandomColoniserTest {

  CartesianGridDouble2D landCoverType;
  WriteableCartesianGridDouble2D juvenilePine, juvenileOak, juvenileDeciduous;
  CompletelySpatiallyRandomParams params;
  CompletelySpatiallyRandomColoniser coloniser;
  List<GridLoc> allGridLocations;

  @Before
  public void setUp() {
    landCoverType = new DefaultCartesianGridDouble2D(new double[][] {
        {Lct.Shrubland.getCode(), Lct.Oak.getCode(), Lct.Oak.getCode()},
        {Lct.Shrubland.getCode(), Lct.Oak.getCode(), Lct.Pine.getCode()},
        {Lct.Shrubland.getCode(), Lct.Deciduous.getCode(), Lct.Pine.getCode()},
    });

    juvenilePine = getEmptyJuvenilePresenceLayer();
    juvenileOak = getEmptyJuvenilePresenceLayer();
    juvenileDeciduous = getEmptyJuvenilePresenceLayer();

    allGridLocations = new ArrayList<>();
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        allGridLocations.add(new GridLoc(x, y));
      }
    }
  }

  @After
  public void tearDown() {
    coloniser = null;
  }

  private WriteableCartesianGridDouble2D getEmptyJuvenilePresenceLayer() {
    return new DefaultCartesianGridDouble2D(new double[][] {
        {SeedPresence.False.getCode(), SeedPresence.False.getCode(), SeedPresence.False.getCode()},
        {SeedPresence.False.getCode(), SeedPresence.False.getCode(), SeedPresence.False.getCode()},
        {SeedPresence.False.getCode(), SeedPresence.False.getCode(), SeedPresence.False.getCode()},
    });
  }

  @Test
  public void testNumCellsColonisedFromOutsideGrid() {
    // if baseRate = 0.1 then 9 * 0.1 = 0.9 ~ 1 cells colonised from outside grid
    CompletelySpatiallyRandomParams params1 = new CompletelySpatiallyRandomParams(0.1, 0);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params1);
    assertEquals(1, coloniser.getNumCellsColonisedFromOutsideGrid());

    // if baseRate = 0.5 then 9 * 0.5 = 4.5 ~ 5 cells colonised from outside grid
    CompletelySpatiallyRandomParams params2 = new CompletelySpatiallyRandomParams(0.5, 0);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params2);
    assertEquals(5, coloniser.getNumCellsColonisedFromOutsideGrid());
  }

  @Test
  public void testNumCellsSpreadingFromInsideGrid() {
    // 3 cells contain oak, 2 contain pine and 1 contains deciduous.
    // If spreadRate is 0.5 we expect:
    // 0.5 * 3 = 1.5 ~ 2 new cells containing juvenile oak
    // 0.5 * 2 = 1 new cells containing juvenile pine
    // 0.5 * 1 = 0.5 ~ 1 new cells containing juvenile deciduous
    params = new CompletelySpatiallyRandomParams(0, 0.5);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params);

    assertEquals(2, coloniser.getNumCellsColonisedFromInsideGrid(LscapeLayer.Oak));
    assertEquals(1, coloniser.getNumCellsColonisedFromInsideGrid(LscapeLayer.Pine));
    assertEquals(1, coloniser.getNumCellsColonisedFromInsideGrid(LscapeLayer.Deciduous));
  }

  @Test
  public void testJuvenilesAddedToCellsWithCorrespondingMatureLct() {
    // Set both baseRate and spreadRate to 0 to isolate process of adding juveniles to cells
    // containing the corresponding mature vegetation
    params = new CompletelySpatiallyRandomParams(0, 0);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params);
    coloniser.updateJuvenilePresenceLayers();

    List<GridLoc> cellsWithMatureOak = new ArrayList<>(Arrays.asList(
        new GridLoc(1, 1), new GridLoc(1, 2), new GridLoc(2, 2)));

    List<GridLoc> cellsWithMaturePine = new ArrayList<>(Arrays.asList(
        new GridLoc(2, 0), new GridLoc(2, 1)));

    List<GridLoc> cellsWithMatureDeciduous = new ArrayList<>(Arrays.asList(
        new GridLoc(1, 0)));

    for (GridLoc loc : cellsWithMatureOak) {
      assertEquals(SeedPresence.True.getCode(), (int) juvenileOak.getValue(loc));
      assertEquals(SeedPresence.False.getCode(), (int) juvenilePine.getValue(loc));
      assertEquals(SeedPresence.False.getCode(), (int) juvenileDeciduous.getValue(loc));
    }

    for (GridLoc loc : cellsWithMaturePine) {
      assertEquals(SeedPresence.False.getCode(), (int) juvenileOak.getValue(loc));
      assertEquals(SeedPresence.True.getCode(), (int) juvenilePine.getValue(loc));
      assertEquals(SeedPresence.False.getCode(), (int) juvenileDeciduous.getValue(loc));
    }

    for (GridLoc loc : cellsWithMatureDeciduous) {
      assertEquals(SeedPresence.False.getCode(), (int) juvenileOak.getValue(loc));
      assertEquals(SeedPresence.False.getCode(), (int) juvenilePine.getValue(loc));
      assertEquals(SeedPresence.True.getCode(), (int) juvenileDeciduous.getValue(loc));
    }
  }

  @Test
  public void testJuvenilesSpreadFromOutsideInsideGrid() {
    // Set spreadRate to 0 to isolate process of juveniles entering the grid from outside
    // baseRate = 0.5 -> all types should have at least 5 cells with juveniles,
    // 5 from colonisation + number of cells containing corresponding mature vegetation.
    // Note that if the randomly sampled cells coincide with those already containing corresponding
    // mature vegetation, replacements /aren't/ sought.
    params = new CompletelySpatiallyRandomParams(0.5, 0);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params);
    coloniser.updateJuvenilePresenceLayers();

    assertTrue(countJuvenilesInGrid(juvenilePine) >= 5);
    assertTrue(countJuvenilesInGrid(juvenileOak) >= 5);
    assertTrue(countJuvenilesInGrid(juvenileDeciduous) >= 5);
  }

  @Test
  public void testJuvenilesSpreadFromCellsWithCorrespondingMatureLctFromInsideGrid() {
    // Set baseRate to 0 to isolate process of juveniles colonising from inside grid
    // If spreadRate is 3.0 we expect:
    // 3 * 3 = 9 new cells containing juvenile oak
    // 3 * 2 = 6 new cells containing juvenile pine
    // 3 * 1 = 3 new cells containing juvenile deciduous
    //
    // Note that if the randomly sampled cells coincide with those already containing corresponding
    // mature vegetation, replacements /aren't/ sought. Therefore these values only provide a lower
    // bound on the number of cells containing juveniles for each type
    params = new CompletelySpatiallyRandomParams(3, 0);
    coloniser = new CompletelySpatiallyRandomColoniser(landCoverType, juvenilePine, juvenileOak,
        juvenileDeciduous, params);
    coloniser.updateJuvenilePresenceLayers();

    assertTrue(countJuvenilesInGrid(juvenilePine) >= 6);
    assertTrue(countJuvenilesInGrid(juvenileOak) >= 9);
    assertTrue(countJuvenilesInGrid(juvenileDeciduous) >= 3);
  }

  private int countJuvenilesInGrid(CartesianGridDouble2D juvenileLayer) {
    int count = 0;
    for (GridLoc loc : allGridLocations) {
      if ((int) juvenileLayer.getValue(loc) == SeedPresence.True.getCode()) {
        count += 1;
      }
    }
    return count;
  }

}
