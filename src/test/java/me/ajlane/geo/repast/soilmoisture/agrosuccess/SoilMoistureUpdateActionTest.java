package me.ajlane.geo.repast.soilmoisture.agrosuccess;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.MockType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureUpdater;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;

public class SoilMoistureUpdateActionTest {

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock(MockType.STRICT)
  private SoilMoistureUpdater smUpdater;

  public static Schedule schedule;

  @Before
  public void setUp() {
    schedule = new Schedule();
    RunEnvironment.init(schedule, null, null, true);

  }

  @Test
  public void testConstantSoilMoisture() {
    this.smUpdater.updateSoilMoisture(30.1);
    expectLastCall().times(3);
    replay(this.smUpdater);

    IAction updateSM = new SoilMoistureUpdateAction(this.smUpdater, 30.1);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 0), updateSM);

    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    for (int i = 0; i < 3; i++) {
      schedule.execute();
    }

    verify(this.smUpdater);
  }

  @Test
  public void testVariableSoilMoisture() {
    this.smUpdater.updateSoilMoisture(12.1);
    expectLastCall().once();
    this.smUpdater.updateSoilMoisture(40.8);
    expectLastCall().once();
    this.smUpdater.updateSoilMoisture(24.2);
    expectLastCall().once();
    replay(this.smUpdater);

    double[] annualPrecip = {12.1, 40.8, 24.2};
    IAction updateSM = new SoilMoistureUpdateAction(this.smUpdater, annualPrecip);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 0), updateSM);

    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    for (int i = 0; i < 3; i++)  {
      schedule.execute();
    }

    verify(this.smUpdater);
  }

}
