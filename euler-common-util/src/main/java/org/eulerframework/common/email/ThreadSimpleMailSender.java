/*
 * Copyright 2013-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eulerframework.common.email;

import javax.mail.MessagingException;

import org.eulerframework.common.util.Assert;

public class ThreadSimpleMailSender {

    private final EmailConfig emailConfig;

    protected ThreadSimpleMailSender(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    public void send(String subject, String content, String receiver) {
        Assert.notNull(subject, "邮件主题不能为空");
        Assert.notNull(content, "邮件内容不能为空");
        Assert.notNull(receiver, "收信人不能为空");
        MailSendThread mailSendThread = new MailSendThread(subject, content, receiver);
        mailSendThread.start();
    }

    public void send(String subject, String content, String... receiver) {
        Assert.notNull(subject, "邮件主题不能为空");
        Assert.notNull(content, "邮件内容不能为空");
        Assert.notNull(receiver, "收信人不能为空");
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
                    sender.send(subject, content, receiver);
                } else if (receivers != null) {
                    sender.send(subject, content, receivers);
                }
            } catch (MessagingException e) {
                throw new MailSendException(e);
            }

        }
    }

}
