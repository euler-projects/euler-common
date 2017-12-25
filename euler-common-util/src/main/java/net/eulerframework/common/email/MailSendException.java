package net.eulerframework.common.email;

public class MailSendException extends RuntimeException {
    
    public MailSendException() {
        super();
    }

    public MailSendException(String message) {
        super(message);
    }

    
    public MailSendException(String message, Throwable cause) {
        super(message, cause);
    }

  
    public MailSendException(Throwable cause) {
        super(cause);
    }

}
