package repast.model.agrosuccess.params;

/**
 * Thrown when a parameter is specified whose value is outside the range permitted by the model.
 * E.g. if a parameter must be greater than 0 but a value of -2.5 is is passed.
 *
 * @author Andrew Lane
 *
 */
public class ParameterOutOfBoundsException extends RuntimeException {

  private static final long serialVersionUID = 2631351515601563642L;

  public ParameterOutOfBoundsException() {}

  public ParameterOutOfBoundsException(String message) {
    super(message);
  }

  public ParameterOutOfBoundsException(Throwable cause) {
    super(cause);
  }

  public ParameterOutOfBoundsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param paramName Name of the parameter which was out of bounds
   * @param param Value of the parameter
   * @param lowerBound Lowest permissible value of the parameter
   * @param upperBound Maximum permissible value of the parameter
   */
  public ParameterOutOfBoundsException(String paramName, double param, double lowerBound,
      double upperBound) {
    this(messageString(paramName, param, lowerBound, upperBound));
  }

  /**
   * @param paramName Name of the parameter which was out of bounds
   * @param param Value of the parameter
   * @param lowerBound Lowest permissible value of the parameter
   * @param upperBound Maximum permissible value of the parameter
   * @param cause Exception which caused this one to be throw
   */
  public ParameterOutOfBoundsException(String paramName, double param, double lowerBound,
      double upperBound, Throwable cause) {
    this(messageString(paramName, param, lowerBound, upperBound), cause);
  }

  public ParameterOutOfBoundsException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * @param paramName
   * @param param
   * @param lowerBound
   * @param upperBound
   * @return String containing information to user explaining why the parameter was out of bounds
   */
  private static String messageString(String paramName, double param, double lowerBound,
      double upperBound) {
    String message = "Parameter " + paramName + " has value " + param
        + " which is outside allowable range " + "[" + lowerBound + ", " + upperBound + "]";
    return message;
  }

}
