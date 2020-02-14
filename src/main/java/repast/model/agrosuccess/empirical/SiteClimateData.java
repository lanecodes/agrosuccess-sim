package repast.model.agrosuccess.empirical;

public interface SiteClimateData {

  /**
   * @return Monthly precipitation in millimetres, averaged across the year.
   */
  double getMeanMonthlyPrecipitation();

  /**
   * @return Total annual precipitation in millimetres.
   */
  double getTotalAnnualPrecipitation();

  /**
   * @return Mean temperature in degree Celsius, averaged across the year.
   */
  double getMeanAnnualTemperature();

}
