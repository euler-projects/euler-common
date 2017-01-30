package net.eulerframework.common.util.exception;

@SuppressWarnings("serial")
public class NullValueException extends Exception {

    public NullValueException() {
        super();
    }

    public NullValueException(String message) {
        super(message);
    }

    
    public NullValueException(String message, Throwable cause) {
        super(message, cause);
    }

  
    public NullValueException(Throwable cause) {
        super(cause);
    }
}
