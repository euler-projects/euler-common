package net.eulerframework.common.util.jwt;

public class InvalidClaimsException extends RuntimeException {
    public InvalidClaimsException() {
        super();
    }

    public InvalidClaimsException(String message) {
        super(message);
    }

    public InvalidClaimsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClaimsException(Throwable cause) {
        super(cause);
    }
    
    protected InvalidClaimsException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
