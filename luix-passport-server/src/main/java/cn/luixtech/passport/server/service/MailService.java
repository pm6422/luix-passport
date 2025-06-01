package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.User;

public interface MailService {

    void sendEmail(String[] sendTo, String subject, String content);

    void sendEmailFromTemplate(User user, String[] emailsTo, String templateName, String subjectKey, String titleKey, String baseUrl);

    void sendAccountActivationEmail(User user, String baseUrl);

    void sendUserCreationEmail(User user, String baseUrl);

    void sendPasswordRecoveryMail(User user, String baseUrl);

    void sendPasswordChangedMail(User user, String baseUrl);

    void sendVerificationCodeMail(User user, String emailTo, String baseUrl);
}
