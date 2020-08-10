package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to ensure {@link SeedStateUpdater} correctly implements rule S4.
 *
 * @author Andrew Lane
 *
 */
public class SeedStateUpdaterTest {

  Set<Integer> matureVegCodes;
  SeedState noJuvenilesState;

  @Before
  public void setUp() {
    this.matureVegCodes = makeTestMatureVegCodes();
    this.noJuvenilesState = new SeedState(0, 0, 0);
  }

  /**
   * Records whether the land-cover type corresponding to a land-cover type code represents a mature
   * vegetation community or not.
   *
   * @return Set of codes corresponding to mature vegetation for testing purposes
   */
  private Set<Integer> makeTestMatureVegCodes() {
    Set<Integer> matureVegCodes = new HashSet<>();
    matureVegCodes.add(6); // pine
    matureVegCodes.add(9); // oak
    return matureVegCodes;
  }

  @Test
  public void testNoChangeIfNewStateNotMatureVeg() {
    SeedStateUpdater updater = new SeedStateUpdater(this.matureVegCodes);
    SeedState oldSeedState = new SeedState(0, 1, 1);
    SeedState newSeedState = updater.updatedSeedState(oldSeedState, 4, 5);
    assertEquals(oldSeedState, newSeedState);
  }

  @Test
  public void testNoChangeIfMatureVegPersists() {
    SeedStateUpdater updater = new SeedStateUpdater(this.matureVegCodes);
    SeedState oldSeedState = new SeedState(1, 0, 0);
    SeedState newSeedState = updater.updatedSeedState(oldSeedState, 6, 6);
    assertEquals(oldSeedState, newSeedState);
  }

  @Test
  public void testJuvenilesKilledIfTransitionToMatureVegState() {
    SeedStateUpdater updater = new SeedStateUpdater(this.matureVegCodes);
    SeedState oldSeedState = new SeedState(1, 0, 1);
    SeedState newSeedState = updater.updatedSeedState(oldSeedState, 5, 6);
    assertEquals(this.noJuvenilesState, newSeedState);
  }

  @Test
  public void testJuvenilesKilledIfTransitionBetweenMatureVegStates() {
    SeedStateUpdater updater = new SeedStateUpdater(this.matureVegCodes);
    SeedState oldSeedState = new SeedState(1, 0, 0);
    SeedState newSeedState = updater.updatedSeedState(oldSeedState, 6, 9);
    assertEquals(this.noJuvenilesState, newSeedState);
  }

}
