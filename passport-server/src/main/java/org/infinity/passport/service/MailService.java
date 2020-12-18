package org.infinity.passport.service;

import org.infinity.passport.domain.User;

public interface MailService {

    void sendEmail(String[] sendTo, String subject, String content, boolean isMultipart, boolean isHtml);

    void sendEmailFromTemplate(User user, String templateName, String titleKey);

    void sendActivationEmail(User user);

    void sendCreationEmail(User user);

    void sendPasswordResetMail(User user);

}
