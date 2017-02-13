package net.eulerframework.common.email;

/**
 * 
 */
public class MailSenderFactory {

    public static SimpleMailSender getSimpleSystemMailSender(EmailConfig emailConfig) {
        return new SimpleMailSender(emailConfig);
    }

    public static ThreadSimpleMailSender getThreadSimpleMailSender(EmailConfig emailConfig) {
        return new ThreadSimpleMailSender(emailConfig);
    }

    public static void main(String[] args) throws Exception {
        SimpleMailSender simpleSystemMailSender = MailSenderFactory.getSimpleSystemMailSender(new EmailConfig());
        simpleSystemMailSender.send("密码重置邮件", "<p>请点击下面的链接重置您的密码</p><p><a href=\"http://www.baidu.com\">重置密码</a></p>",
                "cfrostsun@163.com");
    }
}
