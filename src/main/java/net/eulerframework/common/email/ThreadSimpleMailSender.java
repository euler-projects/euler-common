package net.eulerframework.common.email;

import javax.mail.MessagingException;

import net.eulerframework.common.util.Assert;

public class ThreadSimpleMailSender {

    private final EmailConfig emailConfig;

    protected ThreadSimpleMailSender(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public void send(String subject, String content, String receiver) {
        Assert.isNotNull(subject, "邮件主题不能为空");
        Assert.isNotNull(content, "邮件内容不能为空");
        Assert.isNotNull(receiver, "收信人不能为空");
        MailSendThread mailSendThread = new MailSendThread(subject, content, receiver);
        mailSendThread.start();
    }

    public void send(String subject, String content, String... receiver) {
        Assert.isNotNull(subject, "邮件主题不能为空");
        Assert.isNotNull(content, "邮件内容不能为空");
        Assert.isNotNull(receiver, "收信人不能为空");
        MailSendThread mailSendThread = new MailSendThread(subject, content, receiver);
        mailSendThread.start();
    }

    private class MailSendThread extends Thread {
        private final String subject;
        private final String content;
        private final String receiver;
        private final String[] receivers;

        public MailSendThread(String subject, String content, String receiver) {
            this.subject = subject;
            this.content = content;
            this.receiver = receiver;
            this.receivers = null;
        }

        public MailSendThread(String subject, String content, String... receiver) {
            this.subject = subject;
            this.content = content;
            this.receiver = null;
            this.receivers = receiver;
        }

        @Override
        public void run() {
            SimpleMailSender sender = new SimpleMailSender(emailConfig);
            try {
                if (receiver != null) {
                    sender.send(subject, content, receivers);
                } else if (receivers != null) {
                    sender.send(subject, content, receivers);
                }
            } catch (MessagingException e) {
                throw new MailSendException(e);
            }

        }
    }

}
