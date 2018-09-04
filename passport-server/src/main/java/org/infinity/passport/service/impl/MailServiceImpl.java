package org.infinity.passport.service.impl;

import org.infinity.passport.domain.User;
import org.infinity.passport.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger  LOGGER   = LoggerFactory.getLogger(MailServiceImpl.class);

    private static final String  USER     = "user";

    private static final String  BASE_URL = "baseUrl";

    @Autowired
    private MailProperties       mailProperties;

    @Autowired
    private JavaMailSenderImpl   javaMailSender;

    @Autowired
    private MessageSource        messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    @Async
    @Override
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        LOGGER.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
                isHtml, to, subject, content);
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(mailProperties.getUsername(), "InfinityTeam");
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            LOGGER.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            LOGGER.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendEmailFromTemplate(User user, String templateName, String titleKey, String baseUrl) {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, baseUrl);
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    @Override
    public void sendActivationEmail(User user, String baseUrl) {
        LOGGER.debug("Sending activation e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/activation-email", "email.activation.title", baseUrl);
    }

    @Async
    @Override
    public void sendCreationEmail(User user, String baseUrl) {
        LOGGER.debug("Sending creation e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/creation-email", "email.activation.title", baseUrl);
    }

    @Async
    @Override
    public void sendPasswordResetMail(User user, String baseUrl) {
        LOGGER.debug("Sending password reset e-mail to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "email/password-reset-email", "email.reset.title", baseUrl);
    }
}
