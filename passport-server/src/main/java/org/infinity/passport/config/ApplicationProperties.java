package org.infinity.passport.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

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
@Data
public class ApplicationProperties {
    private final Directory            directory            = new Directory();
    private final Http                 http                 = new Http();
    private final Swagger              swagger              = new Swagger();
    private final ServiceMetrics       serviceMetrics       = new ServiceMetrics();
    private final Zookeeper            zookeeper            = new Zookeeper();
    private final Redis                redis                = new Redis();
    private final Scheduler            scheduler            = new Scheduler();
    private final Logging              logging              = new Logging();
    private final Ribbon               ribbon               = new Ribbon();
    private final HttpClientConnection httpClientConnection = new HttpClientConnection();
    private final UserAuditEvent       userAuditEvent       = new UserAuditEvent();
    private final Jenkins              jenkins              = new Jenkins();
    private final FileFtp              fileFtp              = new FileFtp();
    private final Url                  url                  = new Url();
    private final Cache                cache                = new Cache();

    @Data
    public static class Directory {
        private String config    = "classpath:config";
        private String templates = "classpath:templates";
        private String data      = "classpath:data";
    }

    @Data
    public static class Http {
        private final Cache cache = new Cache();

        @Data
        public static class Cache {
            private Long timeToLiveInDays = 31L;
        }
    }

    @Data
    public static class Swagger {
        private       boolean enabled;
        private       String  version;
        private       String  contactName;
        private       String  contactEmail;
        private       String  host;
        private final Api     api     = new Api();
        private final OpenApi openApi = new OpenApi();

        @Data
        public static class Api {
            private String title;
            private String description;
        }

        @Data
        public static class OpenApi {
            private String title;
            private String description;
        }
    }

    @Data
    public static class ServiceMetrics {
        private boolean enabled;
        private int     slowExecutionThreshold;
    }

    @Data
    public static class Dubbo {
        private String adminUrl;
        private String monitorUrl;
    }

    @Data
    public static class Zookeeper {
        private String adminUrl;
    }

    @Data
    public static class Redis {
        private String adminUrl;
    }

    @Data
    public static class Scheduler {
        private String adminUrl;
    }

    @Data
    public static class Logging {
        private Logstash logstash = new Logstash();

        @Data
        public static class Logstash {
            private boolean enabled   = false;
            private String  host      = "localhost";
            private int     port      = 5000;
            private int     queueSize = 512;
        }

    }

    @Data
    public static class Ribbon {
        private String[] displayOnActiveProfiles;
    }

    @Data
    public static class HttpClientConnection {
        private int globalRetryCount;
        private int globalReadTimeoutInSeconds;
    }

    @Data
    public static class UserAuditEvent {
        private boolean enabled = false;
    }

    @Data
    public static class Jenkins {
        private String adminUrl;
    }

    @Data
    public static class FileFtp {
        private String url;
        private String userName;
        private String password;
        private String uploadDir;
        private String httpUrl;
    }

    @Data
    public static class Url {
    }

    @Data
    public static class Cache {
        private String cachePrefix;
    }
}
