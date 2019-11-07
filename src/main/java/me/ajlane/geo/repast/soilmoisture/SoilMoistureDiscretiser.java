package me.ajlane.geo.repast.soilmoisture;

public interface SoilMoistureDiscretiser {
  /**
   * Classify soil moisture (measured in mm) to one of the classes specified by the implementing
   * Discretiser.
   * 
   * @param soilMoistureMM
   * @return integer value specifying soil moisture category
   */
  int getSoilMoistureLevel(int soilMoistureMM); // Soil moisture given in millilitres

  /**
   * Identical to {@link SoilMoistureDiscretiser#getSoilMoistureLevel(int)} except soilMoistureMM is
   * type double.
   * 
   * @param soilMoistureMM
   * @return
   */
  int getSoilMoistureLevel(double soilMoistureMM);
}
