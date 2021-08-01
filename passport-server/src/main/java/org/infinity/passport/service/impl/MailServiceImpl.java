package org.infinity.passport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.domain.User;
import org.infinity.passport.service.MailService;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private static final String               USER     = "user";
    private static final String               BASE_URL = "baseUrl";
    @Resource
    private              MailProperties       mailProperties;
    @Resource
    private              JavaMailSenderImpl   javaMailSender;
    @Resource
    private              MessageSource        messageSource;
    @Resource
    private              SpringTemplateEngine springTemplateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    @Async
    @Override
    public void sendEmail(String[] sendTo, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
                isHtml, sendTo, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(sendTo);
            message.setFrom(mailProperties.getUsername(), "InfinityTeam");
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", StringUtils.arrayToCommaDelimitedString(sendTo));
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", sendTo, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendEmailFromTemplate(User user, String templateName, String titleKey, String baseUrl) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, baseUrl);
        String content = springTemplateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }

    @Async
    @Override
    public void sendActivationEmail(User user, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/activation-email", "emailActivationTitle", baseUrl);
    }

    @Async
    @Override
    public void sendCreationEmail(User user, String baseUrl) {
        log.debug("Sending creation e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/creation-email", "emailActivationTitle", baseUrl);
    }

    @Async
    @Override
    public void sendPasswordResetMail(User user, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/password-reset-email", "emailResetTitle", baseUrl);
    }
}
