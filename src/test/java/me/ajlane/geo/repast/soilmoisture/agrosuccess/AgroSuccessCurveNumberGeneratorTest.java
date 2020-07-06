package me.ajlane.geo.repast.soilmoisture.agrosuccess;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import me.ajlane.geo.repast.soilmoisture.CurveNumberGenerator;

public class AgroSuccessCurveNumberGeneratorTest {

  /**
   * <p>
   * Land-cover type codes:
   * </p>
   * <ul>
   * <li>WaterQuarry, 0</li>
   * <li>Burnt, 1</li>
   * <li>Barley, 2</li>
   * <li>Wheat, 3</li>
   * <li>Dal, 4</li>
   * <li>Shrubland, 5</li>
   * <li>Pine, 6</li>
   * <li>TransForest, 7</li>
   * <li>Deciduous, 8</li>
   * <li>Oak, 9</li>
   * </ul>
   *
   * <p>
   * Soil type codes:
   * </p>
   * <ul>
   * <li>A, 0</li>
   * <li>B, 1</li>
   * <li>C, 2</li>
   * <li>D, 3</li>
   * </ul>
   *
   * <p>
   * See Thesis Appendices for table containing correct codes.
   * </p>
   */
  @Test
  public void testSpotChecksCorrect() {
    CurveNumberGenerator cng = new AgroSuccessCurveNumberGenerator();

    assertEquals(54, cng.getCurveNumber(2.1, 1, 6)); // trans forest
    assertEquals(46, cng.getCurveNumber(5.2, 0, 4)); // shrubland
    assertEquals(94, cng.getCurveNumber(3.0, 2, 1)); // burnt
    assertEquals(82, cng.getCurveNumber(1.1, 3, 3)); // DAL
    assertEquals(-1, cng.getCurveNumber(1.1, 0, 0)); // Water/ quarry, undefined

  }

}
