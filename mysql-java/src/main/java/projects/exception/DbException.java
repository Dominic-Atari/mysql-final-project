package projects.exception;


@SuppressWarnings("serial")
public class DbException extends RuntimeException {

  
   //Create an exception with a message.
  public DbException(String message) {
    super(message);
  }

  
   //Create an exception with a cause.
  public DbException(Throwable cause) {
    super(cause);
  }

  
   // Create an exception with a message and a cause.
  public DbException(String message, Throwable cause) {
    super(message, cause);
  }
}
