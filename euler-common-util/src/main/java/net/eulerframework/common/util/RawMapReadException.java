package net.eulerframework.common.util;

/**
 * @author cFrost
 *
 */
public class RawMapReadException extends IllegalArgumentException {

    public RawMapReadException(String message) {
        super(message);
    }
    
    public RawMapReadException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
