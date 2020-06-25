package me.ajlane.geo.repast.soilmoisture.agrosuccess;

import java.util.HashMap;
import java.util.Map;
import me.ajlane.geo.repast.soilmoisture.CurveNumberGenerator;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.AgroSuccessCodeAliases.SoilType;

/**
 * An implementation of {@link CurveNumberGenerator} specifically for the AgroSuccess model. This
 * relies on the soil and land-cover types that are included in AgroSuccess in particular.
 *
 * @author Andrew Lane
 */
public class AgroSuccessCurveNumberGenerator implements CurveNumberGenerator {

  Map<CellAttrs, Integer> map;

  public AgroSuccessCurveNumberGenerator() {
    map = new HashMap<>();

    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Pine.getCode()), 35);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.TransForest.getCode()), 35);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Wheat.getCode()), 62);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Dal.getCode()), 62);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Deciduous.getCode()), 35);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Shrubland.getCode()), 46);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Oak.getCode()), 35);
    map.put(new CellAttrs(Slope.Gentle, SoilType.A.getCode(), Lct.Burnt.getCode()), 91);

    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Pine.getCode()), 54);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.TransForest.getCode()), 54);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Wheat.getCode()), 72);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Dal.getCode()), 72);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Deciduous.getCode()), 54);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Shrubland.getCode()), 68);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Oak.getCode()), 54);
    map.put(new CellAttrs(Slope.Gentle, SoilType.B.getCode(), Lct.Burnt.getCode()), 91);

    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Pine.getCode()), 69);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.TransForest.getCode()), 69);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Wheat.getCode()), 78);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Dal.getCode()), 78);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Deciduous.getCode()), 69);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Shrubland.getCode()), 78);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Oak.getCode()), 69);
    map.put(new CellAttrs(Slope.Gentle, SoilType.C.getCode(), Lct.Burnt.getCode()), 91);

    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Pine.getCode()), 77);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.TransForest.getCode()), 77);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Wheat.getCode()), 82);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Dal.getCode()), 82);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Deciduous.getCode()), 77);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Shrubland.getCode()), 83);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Oak.getCode()), 77);
    map.put(new CellAttrs(Slope.Gentle, SoilType.D.getCode(), Lct.Burnt.getCode()), 91);

    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Pine.getCode()), 39);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.TransForest.getCode()), 39);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Wheat.getCode()), 65);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Dal.getCode()), 65);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Deciduous.getCode()), 39);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Shrubland.getCode()), 46);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Oak.getCode()), 39);
    map.put(new CellAttrs(Slope.Steep, SoilType.A.getCode(), Lct.Burnt.getCode()), 94);

    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Pine.getCode()), 60);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.TransForest.getCode()), 60);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Wheat.getCode()), 76);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Dal.getCode()), 76);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Deciduous.getCode()), 60);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Shrubland.getCode()), 68);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Oak.getCode()), 60);
    map.put(new CellAttrs(Slope.Steep, SoilType.B.getCode(), Lct.Burnt.getCode()), 94);

    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Pine.getCode()), 73);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.TransForest.getCode()), 73);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Wheat.getCode()), 84);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Dal.getCode()), 84);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Deciduous.getCode()), 73);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Shrubland.getCode()), 78);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Oak.getCode()), 73);
    map.put(new CellAttrs(Slope.Steep, SoilType.C.getCode(), Lct.Burnt.getCode()), 94);

    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Pine.getCode()), 78);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.TransForest.getCode()), 78);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Wheat.getCode()), 87);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Dal.getCode()), 87);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Deciduous.getCode()), 78);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Shrubland.getCode()), 83);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Oak.getCode()), 78);
    map.put(new CellAttrs(Slope.Steep, SoilType.D.getCode(), Lct.Burnt.getCode()), 94);
  }

  private class CellAttrs {

    Slope slope;
    int soilType;
    int lct;

    CellAttrs(Slope slope, int soilType, int lct) {
      this.slope = slope;
      this.soilType = soilType;
      this.lct = lct;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + lct;
      result = prime * result + ((slope == null) ? 0 : slope.hashCode());
      result = prime * result + soilType;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CellAttrs other = (CellAttrs) obj;
      if (lct != other.lct)
        return false;
      if (slope != other.slope)
        return false;
      if (soilType != other.soilType)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "CellAttrs [slope=" + slope + ", soilType=" + soilType + ", lct=" + lct + "]";
    }

  }

  enum Slope {
    Gentle, Steep;

    static Slope discretise(double pctSlope) {
      if (pctSlope < 3) {
        return Gentle;
      }
      return Steep;
    }
  }

  @Override
  public int getCurveNumber(double pctSlope, int soilType, int landCoverType) {
    CellAttrs cellAttrs = new CellAttrs(Slope.discretise(pctSlope), soilType, landCoverType);
    try {
      return this.map.get(cellAttrs);
    } catch (NullPointerException e) {
      return -1;
    }
  }

}
