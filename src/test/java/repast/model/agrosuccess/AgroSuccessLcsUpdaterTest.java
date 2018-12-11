package repast.model.agrosuccess;

import static org.junit.Assert.*;
import org.junit.Test;
import me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDecider;
import me.ajlane.geo.repast.succession.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.GridValueLayer;

public class AgroSuccessLcsUpdaterTest {

  /**
   * 
   * Note that no entry is made for pathway 2, as it has no target state so is not expected to be
   * found in the transition map
   * 
   * @return The transition map which will be used for each test case, according to the transition
   *         pathways described in the javadoc for this class.
   * 
   * @see me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDeciderTest#makeTestCodedLcsTransitionMap()
   */
  private CodedLcsTransitionMap makeTestCodedLcsTransitionMap() {
    CodedLcsTransitionMap transMap = new CodedLcsTransitionMap();

    // Add transition pathway 1 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0), new CodedEnvrConsequent(6, 15));

    // Add transition pathway 3 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(9, 20));

    // Add transition pathway 4 to transition map
    transMap.put(new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(7, 15));

    return transMap;
  }

  private Context<Object> getContext1() {
    Context<Object> context = new DefaultContext<>();

    context.addValueLayer(new GridValueLayer("lct", 5, true, 3, 3)); // everywhere shrubland
    // regenerative oak nowhere
    context.addValueLayer(new GridValueLayer("oak regeneration", 0, true, 3, 3));
    // southerly aspect everywhere
    context.addValueLayer(new GridValueLayer("aspect", 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer("pine seeds", 1, true, 3, 3)); // pine seeds everywhere
    context.addValueLayer(new GridValueLayer("oak seeds", 0, true, 3, 3)); // oak seeds nowhere
    // deciduous seeds everywhere
    context.addValueLayer(new GridValueLayer("deciduous seeds", 1, true, 3, 3));
    // everywhere xeric
    context.addValueLayer(new GridValueLayer("soil moisture", 200, true, 3, 3));
    // everywhere 14 years in lcs
    context.addValueLayer(new GridValueLayer("time in lcs", 14, true, 3, 3));
    // everywhere target lcs is pine
    context.addValueLayer(new GridValueLayer("target lcs", 6, true, 3, 3));
    // everywhere trans time is 15 years
    context.addValueLayer(new GridValueLayer("lcs transition time", 15, true, 3, 3));

    return context;
  }

  private String errorStr(int row, int col, double value, String layerName) {
    return "row " + row + ", column " + col + "in layer " + layerName + " should be " + value
        + " but isn't.";
  }

  @Test
  public void testContext1After1Timestep() {
    Context<Object> context1 = getContext1();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context1,
        new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap()));

    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, 5, "lct"), 5, (int) context1.getValueLayer("lct").get(i, j));
        assertEquals(errorStr(i, j, 15, "time in lcs"), 15,
            (int) context1.getValueLayer("time in lcs").get(i, j));
      }
    }

    fail("Not yet implemented");
  }

  @Test
  public void testContext1After2Timesteps() {
    Context<Object> context1 = getContext1();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context1,
        new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap()));

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();
    fail("Not yet implemented");
  }

  @Test
  public void testContext1After3Timesteps() {
    Context<Object> context1 = getContext1();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context1,
        new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap()));

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();
    fail("Not yet implemented");
  }


}

