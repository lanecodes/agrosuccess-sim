package me.ajlane.geo.repast.fire;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.grid.GridPoint;

/**
 * An integration test establishing that fires are successfully logged with a {@link FireReporter}
 *
 * <p>
 * As {@code ReportingRepastFireSpreader} decorates {@link DefaultFireSpreader}, see
 * {@link DefaultFireSpreaderTest} for tests demonstrating the correctness of the underlying fire
 * spreading functionality.
 * </p>
 *
 * @author Andrew Lane
 */
public class ReportingRepastFireSpreaderTest {

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private FireSpreader<GridPoint> baseFireSpreader;

  @Before
  public void setUp() throws Exception {
    ISchedule schedule = new Schedule();
    RunEnvironment.init(schedule, null, null, true);
    Context<Object> context = new DefaultContext<>();
    RunState.init().setMasterContext(context);
  }

  @Test
  public void testFiresAreCorrectlyLoggedInReporter() {
    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    ScheduleParameters params = ScheduleParameters.createRepeating(0, 1);
    DummyAgent dummyAgent = new DummyAgent();
    schedule.schedule(params, dummyAgent, "doNothing");

    FireReporter<GridPoint> reporter = new FireReporter<>();

    GridPoint igPt1 = new GridPoint(2, 2);
    List<GridPoint> burntCells1 = Arrays.asList(
        new GridPoint(2, 2), new GridPoint(2, 1), new GridPoint(1, 1), new GridPoint(3, 1));
    FireEvent<GridPoint> event1 = new FireEvent<>(0, igPt1, burntCells1);

    GridPoint igPt2 = new GridPoint(15, 10);
    List<GridPoint> burntCells2 = Arrays.asList(
        new GridPoint(15, 10), new GridPoint(16, 10), new GridPoint(15, 9));
    FireEvent<GridPoint> event2 = new FireEvent<>(0, igPt2, burntCells2);

    GridPoint igPt3 = new GridPoint(7, 8);
    List<GridPoint> burntCells3 = Arrays.asList(new GridPoint(7, 7));
    FireEvent<GridPoint> event3 = new FireEvent<>(1, igPt3, burntCells3);

    expect(this.baseFireSpreader.spreadFire(igPt1)).andReturn(burntCells1);
    expect(this.baseFireSpreader.spreadFire(igPt2)).andReturn(burntCells2);
    expect(this.baseFireSpreader.spreadFire(igPt3)).andReturn(burntCells3);

    replay(this.baseFireSpreader);

    FireSpreader<GridPoint> reportingFireSpreader =
        new ReportingRepastFireSpreader(this.baseFireSpreader, reporter, schedule);

    schedule.execute(); // tick 0
    reportingFireSpreader.spreadFire(igPt1);
    reportingFireSpreader.spreadFire(igPt2);

    schedule.execute(); // tick 1
    reportingFireSpreader.spreadFire(igPt3);

    List<FireEvent<GridPoint>> reportedFires = StreamSupport
        .stream(reporter.spliterator(), false)
        .collect(Collectors.toList());

    assertEquals(3, reportedFires.size());
    assertTrue(reportedFires.contains(event1));
    assertTrue(reportedFires.contains(event2));
    assertTrue(reportedFires.contains(event3));

    verify(this.baseFireSpreader);
  }

  /**
   * Agent who does nothing but cause the tick counter to progress
   *
   * <p>
   * The Repast Simphony tick counter does not progress unless there are actions scheduled (see Use
   * Case 2 in the <a href="https://repast.github.io/docs/RepastModelTesting.pdf">Repast Model
   * Testing Guide</a>). This dummy agent is used in the tests as a target to schedule methods on.
   * </p>
   *
   * @author Andrew Lane
   */
  public static class DummyAgent {
    public void doNothing() {}
  }

}
