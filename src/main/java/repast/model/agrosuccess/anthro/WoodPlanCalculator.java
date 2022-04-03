package repast.model.agrosuccess.anthro;

public class WoodPlanCalculator {

  private final WoodPlanParams woodPlanParams;
  private final double rasterCellAreaInSqm;

  public WoodPlanCalculator(WoodPlanParams woodPlanParams, double rasterCellAreaInSqm) {
    this.woodPlanParams = woodPlanParams;
    this.rasterCellAreaInSqm = rasterCellAreaInSqm;
  }

  public int estimateNumWoodPatchesRequired(int population) {
    // Divide by 10,000 to convert from square metres to hectares
    double denom = this.woodPlanParams.getFirewoodBiomassRemovalRate() * this.woodPlanParams
        .getClimaxForestBiomassDensity() * this.rasterCellAreaInSqm / 10000;
    return (int) Math.ceil(
        population * this.woodPlanParams.getFirewoodPerCapitaPerYear() / denom);
  }

}
