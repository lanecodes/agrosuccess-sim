package repast.model.agrosuccess.reporting;

import static org.junit.Assert.*;
import java.time.LocalDateTime;
import org.junit.Test;

import repast.model.agrosuccess.reporting.SimulationID;

public class SimulationIDTest {

  // 11:15 on 13th July 1991
  private static final LocalDateTime TEST_DATE = LocalDateTime.of(1991, 07, 13, 11, 15);

  /**
   * Tests constructor used when time since epoch is specified explicitly
   */
  @Test
  public void testExplicitTimeConstructor() {
    SimulationID simID = new SimulationID("navarres", TEST_DATE);
    assertEquals("navarres_19910713T111500-000", simID.toString());
  }

  /**
   * Tests constructor used when time since epoch is expected to be generated
   * within the {@code SimulationID} class.
   */
  @Test
  public void testImplicitTimeConstructor() {
    LocalDateTime priorDate = LocalDateTime.now();
    SimulationID simID = new SimulationID("navarres");
    LocalDateTime postDate = LocalDateTime.now();

    String idString = simID.toString();
    String idTimePart = idString.split("_")[1];
    LocalDateTime idDate = LocalDateTime.parse(idTimePart, SimulationID.DATE_FORMATTER);

    assertTrue(idDate.isAfter(priorDate.minusSeconds(1)));
    assertTrue(idDate.isBefore(postDate.plusSeconds(1)));

  }

}
