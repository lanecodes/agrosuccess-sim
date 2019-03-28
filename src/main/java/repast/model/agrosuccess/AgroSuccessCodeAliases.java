package repast.model.agrosuccess;

import java.awt.Color;

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
    String getAlias() { return this.toString().toLowerCase(); }
  }
  
  
  /**
   * Binary aspect, which way slope of land faces.
   */
  public enum Aspect {
    North(0), South(1);    
    private int code;    
    Aspect(int code) { this.code = code; }    
    int getCode() { return this.code; }
    String getAlias() { return this.toString().toLowerCase(); }
  }
  
  /**
   * Presence of oak, pine, or deciduous seeds.
   */
  public enum SeedPresence {
    False(0), True(1);    
    private int code;    
    SeedPresence(int code) { this.code = code; }    
    int getCode() { return this.code; }
    String getAlias() { return this.toString().toLowerCase(); }
  }
  
  /**
   * Discretisation of soil moisture levels.
   */
  public enum Water {
    Xeric(0), Mesic(1), Hydric(2);    
    private int code;    
    Water(int code) { this.code = code; }    
    int getCode() { return this.code; }
    String getAlias() { return this.toString().toLowerCase(); }
  }
  
  /**
   * Land cover types and corresponding codes used in AgroSuccess.
   */
  public enum Lct {
    WaterQuarry(0, "WaterQuarry", "#0074d9"), // blue
    Burnt(1, "Burnt", "#ff4136"), //red
    Barley(2, "Barley", "#333333"), 
    Wheat(3, "Wheat", "#bfbfff"),
    Dal(4, "DAL", "#ffdc00"),
    Shrubland(5, "Shrubland", "#7fdbff"),
    Pine(6, "Pine", "#2ecc40"), // green
    TransForest(7, "TransForest", "#b10dc9"),
    Deciduous(8, "Deciduous", "#ff851b"),
    Oak(9, "Oak", "#85144b");
    
    private int code;
    private String alias;
    private Color color;
  
    public static Color hex2Rgb(String colorStr) {
      return new Color(
              Integer.valueOf(colorStr.substring( 1, 3 ), 16),
              Integer.valueOf(colorStr.substring( 3, 5 ), 16),
              Integer.valueOf(colorStr.substring( 5, 7 ), 16));
    }
    
    Lct(int code, String alias, String color) { 
      this.code = code; 
      this.alias = alias; 
      this.color = hex2Rgb(color);
    }    
    
    /**
     * @return Numerical code identifying land cover type
     */
    int getCode() { return this.code; }
    
    /**
     * @return Human readable alias for land cover type used for readability in databases etc.
     */
    String getAlias() { return this.alias; }
    
    /**
     * @return Hex string representing the lct's representative colour
     */
    Color getColor() { return this.color; }
  }
  
  public enum SoilType {
    A(0), B(1), C(2), D(3);
    private int code;
    SoilType(int code) { this.code = code; }
    int getCode() { return this.code; }
  }   

}
