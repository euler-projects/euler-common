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

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.eulerframework.common.util.StringUtils;

/**
 * 简单邮件发送器，可单发，群发。
 * 
 * 基于MZULE的SimpleMailSender重写
 * 
 */
public class SimpleMailSender {

    /**
     * 发送邮件的props文件
     */
    private final transient Properties props = System.getProperties();
    /**
     * 邮件服务器登录验证
     */
    private transient MailAuthenticator authenticator;

    /**
     * 邮箱session
     */
    private transient Session session;

    private final String senderEmailAddr;
    private final String senderName;

    protected SimpleMailSender(EmailConfig emailConfig) {
        init(emailConfig.getUsername(), emailConfig.getPassword(), emailConfig.getSmtp());
        this.senderEmailAddr = emailConfig.getSender();
        this.senderName = emailConfig.getSenderName();
    }

    private void init(String username, String password, String smtpHostName) {
        // 初始化props
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtpHostName);
        // 验证
        authenticator = new MailAuthenticator(username, password);
        // 创建session
        session = Session.getInstance(props, authenticator);
    }

    /**
     * 发送邮件
     * 
     * @param subject
     *            邮件主题
     * @param content
     *            邮件内容
     * @param receiver
     *            收件人邮箱地址
     * @throws AddressException The exception thrown when a wrongly formatted address is encountered.
     * @throws MessagingException The base class for all exceptions thrown by the Messaging classes
     */
    public void send(String subject, String content, String receiver) throws AddressException, MessagingException {
        // 创建mime类型邮件
        final MimeMessage message = new MimeMessage(session);
        // 设置发信人
        if(StringUtils.hasText(this.senderName)) {
            try {
                message.setFrom(new InternetAddress(this.senderEmailAddr, this.senderName));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException();
            }
        } else {
            message.setFrom(new InternetAddress(this.senderEmailAddr));
        }
        // 设置收件人
        message.setRecipient(RecipientType.TO, new InternetAddress(receiver));
        // 设置主题
        message.setSubject(subject);
        // 设置邮件内容
        message.setContent(content, "text/html;charset=utf-8");
        // 发送
        Transport.send(message);
    }

    /**
     * 群发邮件
     * 
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param receiver
     *            收件人们
     * @throws AddressException The exception thrown when a wrongly formatted address is encountered.
     * @throws MessagingException The base class for all exceptions thrown by the Messaging classes
     */
    public void send(String subject, String content, String... receiver) throws AddressException, MessagingException {
        // 创建mime类型邮件
        final MimeMessage message = new MimeMessage(session);
        // 设置发信人
        message.setFrom(new InternetAddress(this.senderEmailAddr));
        Set<InternetAddress> addresses = new HashSet<>();
        for (String each : receiver) {
            addresses.add(new InternetAddress(each));
        }
        message.setRecipients(RecipientType.TO, addresses.toArray(new InternetAddress[0]));
        // 设置主题
        message.setSubject(subject);
        // 设置邮件内容
        message.setContent(content, "text/html;charset=utf-8");
        // 发送
        Transport.send(message);
    }

    /**
     * 群发邮件
     * 
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param receivers
     *            收件人们
     * @throws AddressException The exception thrown when a wrongly formatted address is encountered.
     * @throws MessagingException The base class for all exceptions thrown by the Messaging classes
     */
    public void send(String subject, String content, Collection<String> receivers)
            throws AddressException, MessagingException {
        this.send(subject, content, receivers.toArray(new String[0]));
    }

}
