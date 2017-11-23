package net.eulerframework.common.util.property;

public class EnumPropertyReadException extends RuntimeException {

    public EnumPropertyReadException() {
        super();
    }

    public EnumPropertyReadException(String message) {
        super(message);
    }

    public EnumPropertyReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnumPropertyReadException(Throwable cause) {
        super(cause);
    }
}
