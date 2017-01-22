package net.eulerframework.common.util.exception;

@SuppressWarnings("serial")
public class PropertyReadException extends Exception {

    public PropertyReadException() {
        super();
    }

    public PropertyReadException(String message) {
        super(message);
    }

    
    public PropertyReadException(String message, Throwable cause) {
        super(message, cause);
    }

  
    public PropertyReadException(Throwable cause) {
        super(cause);
    }
}
