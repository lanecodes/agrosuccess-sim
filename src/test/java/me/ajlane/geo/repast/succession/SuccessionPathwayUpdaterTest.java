package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Succession;

/**
 * Tests to ensure {@link SuccessionPathwayUpdater} correctly implements rule S5.
 *
 * @author Andrew Lane
 *
 */
public class SuccessionPathwayUpdaterTest {

  @Test
  public void testPathwayChangeOccursIfFreqHighEnough() {
    SuccessionPathwayUpdater updater = new SuccessionPathwayUpdater(200.);

    Integer newSuccessionPathway = updater.updatedSuccessionPathway(new EnvrSimState(10, 1., 100),
        Succession.Regeneration.getCode());

    assertEquals((Integer) Succession.Secondary.getCode(), newSuccessionPathway);
  }

  @Test
  public void testPathwayRemainsSameIfFreqLowEnough() {
    SuccessionPathwayUpdater updater = new SuccessionPathwayUpdater(200.);

    Integer newSuccessionPathway = updater.updatedSuccessionPathway(new EnvrSimState(10, 0.25, 100),
        Succession.Regeneration.getCode());

    assertEquals((Integer) Succession.Regeneration.getCode(), newSuccessionPathway);
  }

  @Test
  public void testSuccessionPathwayPersistsIfFreqHigh() {
    SuccessionPathwayUpdater updater = new SuccessionPathwayUpdater(200.);

    Integer newSuccessionPathway = updater.updatedSuccessionPathway(new EnvrSimState(10, 1., 100),
        Succession.Secondary.getCode());

    assertEquals((Integer) Succession.Secondary.getCode(), newSuccessionPathway);
  }

  @Test
  public void testSuccessionPathwayPersistsIfFreqLow() {
    SuccessionPathwayUpdater updater = new SuccessionPathwayUpdater(200.);

    Integer newSuccessionPathway = updater.updatedSuccessionPathway(new EnvrSimState(10, 0.25, 100),
        Succession.Secondary.getCode());

    assertEquals((Integer) Succession.Secondary.getCode(), newSuccessionPathway);
  }

}
