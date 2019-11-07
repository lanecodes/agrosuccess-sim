package repast.model.agrosuccess.reporting;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import repast.model.agrosuccess.LscapeLayer;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.valueLayer.IGridValueLayer;

public abstract class LandscapeReporter {

  boolean writeLctMap;
  boolean writeLctProps;
  int bufferSize;
  Queue<IGridValueLayer> lctMapQueue;
  Queue<Map<Lct, Double>> lctPropsQueue;

  IGridValueLayer lctMap;
  LctProportionAggregator lctPropAgg;
  
  ISchedule schedule;
  String simID;
  

  public LandscapeReporter(Context<Object> context, ISchedule schedule, String simID,
      boolean writeLctMap, boolean writeLctProps, int bufferSize) {
    this.writeLctMap = writeLctMap;
    this.writeLctProps = writeLctProps;
    this.lctPropAgg = new LctProportionAggregator(
        (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name()));
    this.lctMap = (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());
    
    this.simID = simID;
    this.schedule = schedule;    

    if (bufferSize < 1) {
      throw new IllegalArgumentException("Buffer must be at least 1, but " + bufferSize + "given.");
    } else {
      this.bufferSize = bufferSize;
    }

    if (bufferSize > 1) {
      this.lctMapQueue = new LinkedList<IGridValueLayer>();
      this.lctPropsQueue = new LinkedList<>();
    }

    // if buffer size is exactly 1, we don't need to use the buffers as each datapoint will be
    // recorded immediately.
  }

  /**
   * Persists land cover map to disk in a manner determined by implementing classes.
   */
  abstract void reportLctMap();

  /**
   * Persists land cover type proportions to disk in a manner determined by implementing classes.
   */
  abstract void reportLctProportions();

  @ScheduledMethod(start = 1, interval = 1, priority = -10)
  public void step() {
    if (writeLctMap) {
      reportLctMap();
    }
    if (writeLctProps) {
      reportLctProportions();
    }
  }

}
