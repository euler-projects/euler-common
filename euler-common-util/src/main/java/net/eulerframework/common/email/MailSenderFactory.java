/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
