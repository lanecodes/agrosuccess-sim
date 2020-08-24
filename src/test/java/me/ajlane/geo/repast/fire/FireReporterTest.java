package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.GridLoc;

public class FireReporterTest {

  FireEvent<GridLoc> testFire1, testFire2, testFire3;

  @Before
  public void setUp() {
    testFire1 = new FireEvent<GridLoc>(
        1,
        new GridLoc(0, 0),
        Arrays.asList(new GridLoc(0, 0), new GridLoc(0, 1)));

    testFire2 = new FireEvent<GridLoc>(
        1,
        new GridLoc(1, 1),
        Arrays.asList(new GridLoc(1, 1), new GridLoc(1, 1), new GridLoc(2, 1)));

    testFire3 = new FireEvent<GridLoc>(
        2,
        new GridLoc(2, 2),
        Arrays.asList(new GridLoc(2, 2), new GridLoc(2, 1), new GridLoc(1, 2)));
  }

  @Test
  public void testAddFire() {
    FireReporter<GridLoc> reporter = new FireReporter<>();
    reporter.addFire(testFire1);
  }

  @Test
  public void testGetNextFire() {
    FireReporter<GridLoc> reporter = new FireReporter<>();
    reporter.addFire(testFire1);
    reporter.addFire(testFire2);

    assertEquals(testFire1, reporter.getNextFire());
    assertEquals(testFire2, reporter.getNextFire());
    assertNull(reporter.getNextFire());
    assertNull(reporter.getNextFire());
  }

  @Test
  public void testIteration() {
    FireReporter<GridLoc> reporter = new FireReporter<>();

    reporter.addFire(testFire1);
    reporter.addFire(testFire2);
    List<FireEvent<GridLoc>> expectedList1 = Arrays.asList(testFire1, testFire2);
    List<FireEvent<GridLoc>> actualList1 = new ArrayList<>();
    reporter.forEach(actualList1::add);
    assertEquals(expectedList1, actualList1);

    // Fires added before previous query have been removed
    reporter.addFire(testFire3);
    List<FireEvent<GridLoc>> expectedList2 = Arrays.asList(testFire3);
    List<FireEvent<GridLoc>> actualList2 = new ArrayList<>();
    reporter.forEach(actualList2::add);
    assertEquals(expectedList2, actualList2);

    // No fires added so expect empty list
    List<FireEvent<GridLoc>> expectedList3 = new ArrayList<>();
    List<FireEvent<GridLoc>> actualList3 = new ArrayList<>();
    assertEquals(expectedList3, actualList3);
  }

}
