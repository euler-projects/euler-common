package net.eulerframework.common.util.property;

/**
 * @author cFrost
 *
 */
public class InvalidPropertyValueException extends RuntimeException {
    public InvalidPropertyValueException() {
        super();
    }
    
    public InvalidPropertyValueException(String message) {
        super(message);
    }
}
