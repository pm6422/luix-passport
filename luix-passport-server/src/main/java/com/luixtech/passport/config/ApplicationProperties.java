package com.luixtech.passport.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

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

    private final       CorsConfiguration cors                = new CorsConfiguration();
    private final       UserEventAudit    userEventAudit      = new UserEventAudit();
    private final       Account           account             = new Account();
    private final       Server            server              = new Server();

    @Data
    public static class UserEventAudit {
        private boolean enabled;
    }

    @Data
    public static class Account {
        private String defaultPassword;
    }

    @Data
    public static class Server {
        private String address;
    }
}
