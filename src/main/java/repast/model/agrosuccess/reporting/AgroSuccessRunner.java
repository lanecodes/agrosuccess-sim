package repast.model.agrosuccess.reporting;

import org.apache.log4j.Logger;
import repast.model.agrosuccess.AgroSuccessContextBuilder;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;

public class AgroSuccessRunner {

  final static Logger logger = Logger.getLogger(AgroSuccessRunner.class);

  public static Context<Object> context;
  public static Schedule schedule;

  @SuppressWarnings("unchecked")
  public static void setUp() {
    schedule = new Schedule();
    RunEnvironment.init(schedule, null, null, true);

    context = new DefaultContext<Object>();
    ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
    context = builder.build(context);

    // trigger the Heatbug's @ScheduledMethods to be added to the scheduler
    logger.debug("Scheduling methods...");
    for (Object agent: context.getObjects(Object.class)) {
      logger.debug(schedule.schedule(agent));
    }
    logger.debug("Finished scheduling methods");
  }

  public static void main(String[] args) {
    // eventually args will contain parameters which will be varied according to ABC protocol.
    // See  ModelParamsRepastParserTest for how parameters can be added to params object.
    // See AgroSuccessContextBuilderTest for how schedule can be added to run environment.

    // This program can be called on the command line, and its output fed into abrox.
    // Since abrox compares whole patterns my original plan to terminate simulations early
    // if they had deviated from time series too much might not be possible with abrox as it
    // is. In any case, that will be an optimisation. For the time being focus on being able
    // to generate complete simulation outputs from repast and feed them into abrox. Feasibly
    // I might contribute to abrox to add this feature.
    setUp();
    for (int i=0; i<200; i++) { schedule.execute(); }
  }

}
