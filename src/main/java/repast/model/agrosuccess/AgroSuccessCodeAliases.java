package repast.model.agrosuccess;

/**
 * Enumerations used to convert between numerical codes used in the model and the human readable
 * names corresponding to those values. This allows human readable names to be used in the source
 * code more easily where applicable. 
 * 
 * @author Andrew Lane
 * @see <a 
 * href="https://stackoverflow.com/questions/10017729/multiple-enum-classes-in-one-java-file">
 * Multiple Enum Classes in one Java File</a> on Stack Overflow for inspiration for this appoach.
 */
public class AgroSuccessCodeAliases {
  /**
   * Represents succession pathways. 
   * 
   * Regeneration entails there is material in the landscape which resprouting species can use to 
   * regenerate. Secondary succession is contrasted with primary succession.   *
   */
  public enum Succession {
    Regeneration(0), Secondary(1);    
    private int code;    
    Succession(int code) { this.code = code; }    
    int getCode() { return this.code; }
  }
  
  /**
   * Binary aspect, which way slope of land faces.
   */
  public enum Aspect {
    North(0), South(1);    
    private int code;    
    Aspect(int code) { this.code = code; }    
    int getCode() { return this.code; }
  }
  
  /**
   * Presence of oak, pine, or deciduous seeds.
   */
  public enum SeedPresence {
    False(0), True(1);    
    private int code;    
    SeedPresence(int code) { this.code = code; }    
    int getCode() { return this.code; }
  }
  
  /**
   * Discretisation of soil moisture levels.
   */
  public enum Water {
    Xeric(0), Mesic(1), Hydric(2);    
    private int code;    
    Water(int code) { this.code = code; }    
    int getCode() { return this.code; }
  }
  
  /**
   * Land cover types and corresponding codes used in AgroSuccess.
   */
  public enum Lct {
    WaterQuarry(0, "WaterQuarry"), 
    Burnt(1, "Burnt"), 
    Barley(2, "Barley"), 
    Wheat(3, "Wheat"),
    Dal(4, "DAL"),
    Shrubland(5, "Shrubland"),
    Pine(6, "Pine"),
    TransForest(7, "TransForest"),
    Deciduous(8, "Deciduous"),
    Oak(9, "Oak");
    
    private int code;
    private String alias;
    
    Lct(int code, String alias) { 
      this.code = code; 
      this.alias = alias; 
      }    
    
    /**
     * @return Numerical code identifying land cover type
     */
    int getCode() { return this.code; }
    
    /**
     * @return Human readable alias for land cover type used for readability in databases etc.
     */
    String getAlias() { return this.alias; }
  }

}
