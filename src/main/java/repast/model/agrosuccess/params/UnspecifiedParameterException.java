package repast.model.agrosuccess.params;
/**
 * Exception thrown when an expected model parameter hasn't been provided.
 * 
 * @author Andrew Lane
 *
 */
public class UnspecifiedParameterException extends RuntimeException {

  private static final long serialVersionUID = 6837445203748869426L;


  public UnspecifiedParameterException() {
  }

  public UnspecifiedParameterException(String message) {
    super(message);
  }

  public UnspecifiedParameterException(Throwable cause) {
    super(cause);
  }

  public UnspecifiedParameterException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnspecifiedParameterException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
