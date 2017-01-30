package net.eulerframework.common.email;

import javax.mail.MessagingException;

import net.eulerframework.common.util.Assert;

public class ThreadSimpleMailSender {
    
    private final EmailConfig emailConfig;
    
    protected ThreadSimpleMailSender(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public void send(String subject, Object content, String receiver){
        MailSender senderThread = new MailSender(subject, content, receiver);
        senderThread.start();
    }

    public void send(String subject, Object content, String... receiver) {
        MailSender senderThread = new MailSender(subject, content, receiver);
        senderThread.start();
    }
    
    private class MailSender extends Thread {
        private final String subject;
        private final Object content;
        private final String receiver;
        private final String[] receivers;
        
        public MailSender(String subject, Object content, String receiver){
            Assert.isNotNull(subject, "邮件主题不能为空");
            Assert.isNotNull(content, "邮件内容不能为空");
            Assert.isNotNull(receiver, "收信人不能为空");
            
            this.subject = subject;
            this.content = content;
            this.receiver = receiver;
            this.receivers = null;
        }
        
        public MailSender(String subject, Object content, String... receiver){
            Assert.isNotNull(subject, "邮件主题不能为空");
            Assert.isNotNull(content, "邮件内容不能为空");
            Assert.isNotNull(receiver, "收信人不能为空");
            
            this.subject = subject;
            this.content = content;
            this.receiver = null;
            this.receivers = receiver;
        }

        @Override
        public void run() {
            SimpleMailSender sender = new SimpleMailSender(emailConfig);
            try {
                if(receiver != null) {
                    sender.send(subject, content, receivers);
                } else if(receivers != null) {
                    sender.send(subject, content, receivers);                    
                }                
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            
        }
    }

}
