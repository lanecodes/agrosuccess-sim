package me.ajlane.geo.repast.fire;

import java.util.HashMap;
import java.util.Map;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

/**
 * Get hard coded land cover flammability data.
 *
 * @see me.ajlane.geo.repast.fire.LcfMapGetter
 *
 * @author Andrew Lane
 *
 */
public class LcfMapGetterHardCoded implements LcfMapGetter {

  LcfReplicate replicate;

  public LcfMapGetterHardCoded(LcfReplicate replicate) {
    this.replicate = replicate;
  }

  /**
   * @param replicate
   * @return Map from land cover types to land-cover flammability values after Millington et al.
   *         2009.
   */
  @Override
  public Map<Lct, Double> getMap() {
    Map<Lct, Double> result;
    switch (replicate) {
      case TFN4:
        result = tfN4Map();
      case TFN3:
        result = tfN3Map();
      case TFN2:
        result = tfN2Map();
      case TFN1:
        result = tfN1Map();
      case TF0:
        result = tf0Map();
      case TF1:
        result = tf1Map();
        break;
      case TF2:
        result = tf2Map();
        break;
      case TF3:
        result = tf3Map();
        break;
      case TF4:
        result = tf4Map();
        break;
      case Default:
        result = defaultMap();
        break;
      case TF5:
        result = tf5Map();
        break;
      case TF6:
        result = tf6Map();
        break;
      case TF7:
        result = tf7Map();
        break;
      case TF8:
        result = tf8Map();
        break;
      default:
        throw new RuntimeException("Lcf replicate unexpectedly not recognised.");
    }

    return result;
  }

  private static Map<Lct, Double> tfN4Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.14);
    m.put(Lct.TransForest, 0.14);
    m.put(Lct.Deciduous, 0.13);
    m.put(Lct.Shrubland, 0.15);
    m.put(Lct.Oak, 0.13);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tfN3Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.15);
    m.put(Lct.TransForest, 0.15);
    m.put(Lct.Deciduous, 0.14);
    m.put(Lct.Shrubland, 0.16);
    m.put(Lct.Oak, 0.14);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tfN2Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.16);
    m.put(Lct.TransForest, 0.16);
    m.put(Lct.Deciduous, 0.15);
    m.put(Lct.Shrubland, 0.17);
    m.put(Lct.Oak, 0.15);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tfN1Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.17);
    m.put(Lct.TransForest, 0.17);
    m.put(Lct.Deciduous, 0.16);
    m.put(Lct.Shrubland, 0.18);
    m.put(Lct.Oak, 0.16);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf0Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.18);
    m.put(Lct.TransForest, 0.18);
    m.put(Lct.Deciduous, 0.17);
    m.put(Lct.Shrubland, 0.19);
    m.put(Lct.Oak, 0.17);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf1Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.19);
    m.put(Lct.TransForest, 0.19);
    m.put(Lct.Deciduous, 0.18);
    m.put(Lct.Shrubland, 0.20);
    m.put(Lct.Oak, 0.18);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf2Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.20);
    m.put(Lct.TransForest, 0.20);
    m.put(Lct.Deciduous, 0.19);
    m.put(Lct.Shrubland, 0.21);
    m.put(Lct.Oak, 0.19);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf3Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.21);
    m.put(Lct.TransForest, 0.21);
    m.put(Lct.Deciduous, 0.20);
    m.put(Lct.Shrubland, 0.22);
    m.put(Lct.Oak, 0.20);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf4Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.22);
    m.put(Lct.TransForest, 0.22);
    m.put(Lct.Deciduous, 0.21);
    m.put(Lct.Shrubland, 0.23);
    m.put(Lct.Oak, 0.21);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> defaultMap() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.23);
    m.put(Lct.TransForest, 0.23);
    m.put(Lct.Deciduous, 0.22);
    m.put(Lct.Shrubland, 0.24);
    m.put(Lct.Oak, 0.22);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf5Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.24);
    m.put(Lct.TransForest, 0.24);
    m.put(Lct.Deciduous, 0.23);
    m.put(Lct.Shrubland, 0.25);
    m.put(Lct.Oak, 0.23);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf6Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.25);
    m.put(Lct.TransForest, 0.25);
    m.put(Lct.Deciduous, 0.24);
    m.put(Lct.Shrubland, 0.26);
    m.put(Lct.Oak, 0.24);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf7Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.26);
    m.put(Lct.TransForest, 0.26);
    m.put(Lct.Deciduous, 0.25);
    m.put(Lct.Shrubland, 0.27);
    m.put(Lct.Oak, 0.25);
    m = fillCropTypes(m);
    return m;
  }

  private static Map<Lct, Double> tf8Map() {
    Map<Lct, Double> m = new HashMap<>();
    m.put(Lct.Pine, 0.27);
    m.put(Lct.TransForest, 0.27);
    m.put(Lct.Deciduous, 0.26);
    m.put(Lct.Shrubland, 0.28);
    m.put(Lct.Oak, 0.26);
    m = fillCropTypes(m);
    return m;
  }

  /**
   * In all scenarios, types Barley, Wheat, and Dal should be treated the same as Shrubland for
   * Land-Cover Flammability puurposes.
   *
   * @return LCF map with Barley, Wheat and Dal set to behave the same as Shrubland.
   */
  private static Map<Lct, Double> fillCropTypes(Map<Lct, Double> m) {
    m.put(Lct.Barley, m.get(Lct.Shrubland));
    m.put(Lct.Wheat, m.get(Lct.Shrubland));
    m.put(Lct.Dal, m.get(Lct.Shrubland));
    return m;
  }

}
