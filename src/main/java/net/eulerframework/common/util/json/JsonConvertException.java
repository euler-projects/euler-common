package net.eulerframework.common.util.json;

@SuppressWarnings("serial")
public class JsonConvertException extends Exception {
    public JsonConvertException() {
        super();
    }

    public JsonConvertException(String message) {
        super(message);
    }

    public JsonConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonConvertException(Throwable cause) {
        super(cause);
    }
    
    protected JsonConvertException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
