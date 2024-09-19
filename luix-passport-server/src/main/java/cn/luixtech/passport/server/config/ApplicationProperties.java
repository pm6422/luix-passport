package cn.luixtech.passport.server.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;

/**
 * Properties specific to Application.
 *
 * <p>
 * Properties are configured in the application.yml file.
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Validated
@Getter
public class ApplicationProperties {

    private final Account     account     = new Account();
    private final Company     company     = new Company();
    private final Mail        mail        = new Mail();
    private final AllowedCors allowedCors = new AllowedCors();

    @Data
    public static class Account {
        private String defaultPassword;
    }

    @Data
    public static class Company {
        private String  name;
        private String  domain;
        private boolean forceToHttps;
    }

    @Data
    public static class Mail {
        private String resendApiKey;
        private String fromUsername;
        private String adminEmail;
    }

    @Data
    public static class AllowedCors {
        private LinkedHashMap<String, String> mappings = new LinkedHashMap<>();
    }
}
