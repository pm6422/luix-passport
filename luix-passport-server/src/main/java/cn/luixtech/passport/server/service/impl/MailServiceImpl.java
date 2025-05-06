package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.config.ApplicationProperties;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.service.MailService;
import com.luixtech.utilities.exception.ThirdPartyServiceException;
import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

/**
 * Service for sending emails.
 */
@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {
    private static final String                USER             = "user";
    private static final String                BASE_URL         = "baseUrl";
    private static final String                DOMAIN           = "domain";
    private static final String                CONTENT_FRAGMENT = "contentFragment";
    private static final String                EMAIL_LAYOUT     = "emails/layouts/email-layout";
    private final        ApplicationProperties applicationProperties;
    private final        MessageSource         messageSource;
    private final        SpringTemplateEngine  springTemplateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    @Override
    public void sendEmail(String[] sendTo, String subject, String content) {
        log.info("Sending email to [{}] with subject [{}] and content: {}", StringUtils.arrayToCommaDelimitedString(sendTo), subject, content);
        try {
            Resend resend = new Resend(applicationProperties.getMail().getResendApiKey());
            SendEmailRequest sendEmailRequest = SendEmailRequest.builder().from(applicationProperties.getCompany().getName() + " <" + applicationProperties.getMail().getFromUsername() + ">").to(sendTo).subject(subject).html(content).build();
            SendEmailResponse result = resend.emails().send(sendEmailRequest);
            log.info("Successfully sent email to users [{}] with message id [{}]", StringUtils.arrayToCommaDelimitedString(sendTo), result.getId());
        } catch (Exception e) {
            log.error("Failed to send email to users [{}] with exception: {}", StringUtils.arrayToCommaDelimitedString(sendTo), e.getMessage());
            throw new ThirdPartyServiceException(e.getMessage());
        }
    }

    @Override
    public void sendEmailFromTemplate(User user, String[] emailsTo, String contentFragment, String emailSubjectKey, String baseUrl) {
        Locale locale = LocaleUtils.toLocale(user.getLocale());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, baseUrl);
        context.setVariable(DOMAIN, applicationProperties.getCompany().getDomain());
        context.setVariable(CONTENT_FRAGMENT, contentFragment);
        String content = springTemplateEngine.process(EMAIL_LAYOUT, context);
        String subject = messageSource.getMessage(emailSubjectKey, null, locale);
        sendEmail(ArrayUtils.isNotEmpty(emailsTo) ? emailsTo : new String[]{user.getEmail()}, subject, content);
    }

    @Override
    public void sendAccountActivationEmail(User user, String baseUrl) {
        sendEmailFromTemplate(user, null, "emails/activate-account-email", "activate.account.email.subject", baseUrl);
        log.info("Requested sending account activation email to [{}]", user.getEmail());
    }

    @Override
    public void sendUserCreationEmail(User user, String baseUrl) {
        sendEmailFromTemplate(user, null, "emails/create-user-email", "create.user.email.subject", baseUrl);
        log.info("Requested sending user creation email to [{}]", user.getEmail());
    }

    @Override
    public void sendPasswordRecoveryMail(User user, String baseUrl) {
        sendEmailFromTemplate(user, null, "emails/recover-password-email", "reset.password.email.subject", baseUrl);
        log.info("Requested sending password recovery email to [{}]", user.getEmail());
    }

    @Override
    public void sendPasswordChangedMail(User user, String baseUrl) {
        sendEmailFromTemplate(user, null, "emails/changed-password-email", "changed.password.email.subject", baseUrl);
        log.info("Requested sending password changed email to [{}]", user.getEmail());
    }

    @Override
    public void sendVerificationCodeMail(User user, String emailTo, String baseUrl) {
        sendEmailFromTemplate(user, new String[]{emailTo}, "emails/verification-code-email", "verification.code.email.subject", baseUrl);
        log.info("Requested sending verification code email to [{}]", user.getEmail());
    }
}
