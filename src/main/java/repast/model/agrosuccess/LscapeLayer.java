package repast.model.agrosuccess;

/**
 * An enumeration of all the landscape layers involved in the model.
 * 
 * See Millington et al. 2009 for details of soil type classifications.
 * 
 * @author Andrew Lane
 *
 */
public enum LscapeLayer {
  Dem ("elevation above sea level in meters."),
  Lct ("current land cover type"),
  OakRegen ("presence of regenerative oak vegetation (succession pathway). "
      + "0=Regeneration, 1=Secondary."),
  Aspect ("indication of whether slope faces north (0) or south (1)"),
  Slope ("slope expressed as percent slope"),
  FlowDir ("flow direction out of cell. 1=E, 2=NE, 3=N,..., 8=SE"),
  SoilType ("soil type. 0=Type A, 1=Type B, 2=Type C, 3=Type D"),
  Pine ("presence of pine seeds"),
  Oak ("presence of oak seeds"),
  Deciduous ("presence of deciduous seeds"),
  SoilMoisture ("soil moisture measured in mm"),
  TimeInState ("number of years cell in current land cover state"),
  DeltaD ("target land cover type"),
  DeltaT ("target state transition time in years"),
  FireCount ("number of times the cells have been burnt during the simulation"),
  OakAge ("number of years mature oak has been present in cells"),
  TimeFarmed ("number of years the land patch has been used for agriculture");

  private final String description;
  
  private LscapeLayer(String s) {
    description = s;
  }
  
  public String getDescription() {
    return this.description;
  } 

}
