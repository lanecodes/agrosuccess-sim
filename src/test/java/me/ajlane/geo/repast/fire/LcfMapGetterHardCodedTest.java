package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertEquals;
import java.util.Map;
import org.junit.Test;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

public class LcfMapGetterHardCodedTest {

  private static final double TOLERANCE = 0.0001;

  /**
   * Test method {@code LcfMapGetterHardCoded#getMap} using LCF Map data built into the class.
   */
  @Test
  public void testGetMap() {
    LcfMapGetter defaultGetter = new LcfMapGetterHardCoded(LcfReplicate.Default);
    Map<Lct, Double> defaultLcfMap = defaultGetter.getMap();
    assertEquals(0.23, defaultLcfMap.get(Lct.Pine), TOLERANCE);
    assertEquals(0.22, defaultLcfMap.get(Lct.Deciduous), TOLERANCE);
    assertEquals(0.24, defaultLcfMap.get(Lct.Wheat), TOLERANCE);

    LcfMapGetter tf4Getter = new LcfMapGetterHardCoded(LcfReplicate.TF4);
    Map<Lct, Double> tf4LcfMap = tf4Getter.getMap();
    assertEquals(0.22, tf4LcfMap.get(Lct.TransForest), TOLERANCE);
    assertEquals(0.23, tf4LcfMap.get(Lct.Shrubland), TOLERANCE);
    assertEquals(0.23, tf4LcfMap.get(Lct.Grassland), TOLERANCE);
  }

}
