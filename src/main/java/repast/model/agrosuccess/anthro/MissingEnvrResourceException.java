package repast.model.agrosuccess.anthro;

/**
 * {@code MissingEnvrResourceException} is thrown when an agent has attempted to access an environmental
 * resource which isn't present in the environment. Furthermore, the fact that an agent has attempted to
 * access the missing resource indicates that a programming error has taken place.
 *
 * @author Andrew Lane
 *
 */
public class MissingEnvrResourceException extends RuntimeException {

  private static final long serialVersionUID = -2341422551881419257L;

  public MissingEnvrResourceException() {
  }

  public MissingEnvrResourceException(String message) {
    super(message);
  }

  public MissingEnvrResourceException(Throwable cause) {
    super(cause);
  }

  public MissingEnvrResourceException(String message, Throwable cause) {
    super(message, cause);
  }

  public MissingEnvrResourceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
