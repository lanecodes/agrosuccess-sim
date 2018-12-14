package repast.model.agrosuccess;

public enum LscapeLayer {
  Lct ("current land cover type"),
  OakRegen ("presence of regenerative oak vegetation"),
  Aspect ("indication of whether slope faces north or south"),
  Pine ("presence of pine seeds"),
  Oak ("presence of oak seeds"),
  Deciduous ("presence of deciduous seeds"),
  SoilMoisture ("soil moisture measured in mm"),
  TimeInState ("number of years cell in current land cover state"),
  DeltaD ("target land cover type"),
  DeltaT ("target state transition time in years");
  
  private final String description;
  
  private LscapeLayer(String s) {
    description = s;
  }
  
  public String getDescription() {
    return this.description;
  } 

}
