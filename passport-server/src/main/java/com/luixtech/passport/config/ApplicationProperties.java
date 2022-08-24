package com.luixtech.passport.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

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
    private final Http               http               = new Http();
    private final ApiDocs            apiDocs            = new ApiDocs();
    private final Metrics            metrics            = new Metrics();
    private final AopLogging         aopLogging         = new AopLogging();
    private final ElapsedTimeLogging elapsedTimeLogging = new ElapsedTimeLogging();
    private final CorsConfiguration  cors               = new CorsConfiguration();
    private final UserEventAudit     userEventAudit     = new UserEventAudit();
    private final Account            account            = new Account();
    private final Server             server             = new Server();
    private final Ribbon             ribbon             = new Ribbon();

    @Data
    public static class Http {
        private final Cache cache = new Cache();

        @Data
        public static class Cache {
            /**
             * Expired days
             */
            private Long expiredAfter = 31L;
        }
    }

    @Data
    public static class ApiDocs {
        private String   apiIncludePattern        = "/api/**";
        private String   openApiIncludePattern    = "/open-api/**";
        private String   managementIncludePattern = "/management/**";
        private String   title;
        private String   description              = "API documentation";
        private String   version;
        private String   termsOfServiceUrl;
        private String   contactName;
        private String   contactUrl;
        private String   contactEmail;
        private String   license;
        private String   licenseUrl;
        private Server[] servers                  = new Server[0];

        @Data
        public static class Server {
            private String url;
            private String description;
        }
    }

    @Data
    public static class Metrics {
        private final Logs     logs     = new Logs();
        private final Graphite graphite = new Graphite();

        @Data
        public static class Spark {
            private boolean enabled = false;
            private String  host    = "localhost";
            private int     port    = 9999;
        }

        @Data
        public static class Graphite {
            private boolean enabled = false;
            private String  host    = "localhost";
            private int     port    = 2003;
            private String  prefix  = "";
        }

        @Data
        public static class Logs {
            private boolean enabled         = false;
            private int     reportFrequency = 60;
        }
    }

    @Data
    public static class AopLogging {
        private boolean      enabled;
        private boolean      methodWhitelistMode;
        private List<String> methodWhitelist;
    }

    @Data
    public static class ElapsedTimeLogging {
        private boolean enabled;
        private int     slowExecutionThreshold;
    }

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

    @Data
    public static class Ribbon {
        private String[] displayOnActiveProfiles;
    }
}
