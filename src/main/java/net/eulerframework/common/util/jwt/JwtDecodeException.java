package net.eulerframework.common.util.jwt;

@SuppressWarnings("serial")
public class JwtDecodeException extends Exception {
    public JwtDecodeException() {
        super();
    }

    public JwtDecodeException(String message) {
        super(message);
    }

    public JwtDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtDecodeException(Throwable cause) {
        super(cause);
    }
    
    protected JwtDecodeException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
