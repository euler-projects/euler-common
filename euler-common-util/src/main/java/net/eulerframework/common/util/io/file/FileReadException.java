package net.eulerframework.common.util.io.file;

@SuppressWarnings("serial")
public class FileReadException extends Exception {

    public FileReadException() {
        super();
    }

    public FileReadException(String message) {
        super(message);
    }

    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileReadException(Throwable cause) {
        super(cause);
    }
}
