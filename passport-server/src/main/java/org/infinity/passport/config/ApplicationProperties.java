package org.infinity.passport.config;

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
public class ApplicationProperties {

    private final Directory directory = new Directory();

    private final Http http = new Http();

    private final Swagger swagger = new Swagger();

    private final Zookeeper zookeeper = new Zookeeper();

    private final Redis redis = new Redis();

    private final Scheduler scheduler = new Scheduler();

    private final Logging logging = new Logging();

    private final Ribbon ribbon = new Ribbon();

    private final HttpClientConnection httpClientConnection = new HttpClientConnection();

    private final UserAuditEvent userAuditEvent = new UserAuditEvent();

    private final Jenkins jenkins = new Jenkins();

    private final FileFtp fileFtp = new FileFtp();

    private final Url url = new Url();

    private final Cache cache = new Cache();

    public Directory getDirectory() {
        return directory;
    }

    public Http getHttp() {
        return http;
    }

    public Swagger getSwagger() {
        return swagger;
    }

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public Redis getRedis() {
        return redis;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Logging getLogging() {
        return logging;
    }

    public Ribbon getRibbon() {
        return ribbon;
    }

    public HttpClientConnection getHttpClientConnection() {
        return httpClientConnection;
    }

    public UserAuditEvent getUserAuditEvent() {
        return userAuditEvent;
    }

    public Jenkins getJenkins() {
        return jenkins;
    }

    public FileFtp getFileFtp() {
        return fileFtp;
    }

    public Url getUrl() {
        return url;
    }

    public Cache getCache() {
        return cache;
    }

    public static class Directory {
        private String config = "classpath:config";

        private String templates = "classpath:templates";

        private String data = "classpath:data";

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public String getTemplates() {
            return templates;
        }

        public void setTemplates(String templates) {
            this.templates = templates;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class Http {

        private final Cache cache = new Cache();

        public Cache getCache() {
            return cache;
        }

        public static class Cache {

            private Long timeToLiveInDays = 31L;

            public Long getTimeToLiveInDays() {
                return timeToLiveInDays;
            }

            public void setTimeToLiveInDays(Long timeToLiveInDays) {
                this.timeToLiveInDays = timeToLiveInDays;
            }
        }
    }

    public static class Swagger {

        private static Api api = new Api();

        private static OpenApi openApi = new OpenApi();

        private String version;

        private String termsOfServiceUrl;

        private String contactName;

        private String contactUrl;

        private String contactEmail;

        private String license;

        private String licenseUrl;

        private String host;

        public Api getApi() {
            return api;
        }

        public OpenApi getOpenApi() {
            return openApi;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getTermsOfServiceUrl() {
            return termsOfServiceUrl;
        }

        public void setTermsOfServiceUrl(String termsOfServiceUrl) {
            this.termsOfServiceUrl = termsOfServiceUrl;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactUrl() {
            return contactUrl;
        }

        public void setContactUrl(String contactUrl) {
            this.contactUrl = contactUrl;
        }

        public String getContactEmail() {
            return contactEmail;
        }

        public void setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public String getLicenseUrl() {
            return licenseUrl;
        }

        public void setLicenseUrl(String licenseUrl) {
            this.licenseUrl = licenseUrl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public static class Api {
            private String title;

            private String description;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public static class OpenApi {
            private String title;

            private String description;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    public static class Dubbo {

        private String adminUrl;

        private String monitorUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }

        public String getMonitorUrl() {
            return monitorUrl;
        }

        public void setMonitorUrl(String monitorUrl) {
            this.monitorUrl = monitorUrl;
        }
    }

    public static class Zookeeper {

        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class Redis {
        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class Scheduler {
        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class Logging {

        private final Logstash logstash = new Logstash();

        public Logstash getLogstash() {
            return logstash;
        }

        public static class Logstash {

            private boolean enabled = false;

            private String host = "localhost";

            private int port = 5000;

            private int queueSize = 512;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getQueueSize() {
                return queueSize;
            }

            public void setQueueSize(int queueSize) {
                this.queueSize = queueSize;
            }
        }

    }

    public static class Ribbon {

        private String[] displayOnActiveProfiles;

        public String[] getDisplayOnActiveProfiles() {
            return displayOnActiveProfiles;
        }

        public void setDisplayOnActiveProfiles(String[] displayOnActiveProfiles) {
            this.displayOnActiveProfiles = displayOnActiveProfiles;
        }
    }

    public static class HttpClientConnection {

        private int globalRetryCount;

        private int globalReadTimeoutInSeconds;

        public int getGlobalRetryCount() {
            return globalRetryCount;
        }

        public void setGlobalRetryCount(int globalRetryCount) {
            this.globalRetryCount = globalRetryCount;
        }

        public int getGlobalReadTimeoutInSeconds() {
            return globalReadTimeoutInSeconds;
        }

        public void setGlobalReadTimeoutInSeconds(int globalReadTimeoutInSeconds) {
            this.globalReadTimeoutInSeconds = globalReadTimeoutInSeconds;
        }
    }

    public static class UserAuditEvent {

        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Jenkins {
        private String adminUrl;

        public String getAdminUrl() {
            return adminUrl;
        }

        public void setAdminUrl(String adminUrl) {
            this.adminUrl = adminUrl;
        }
    }

    public static class FileFtp {

        private String url;

        private String userName;

        private String password;

        private String uploadDir;

        private String httpUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUploadDir() {
            return uploadDir;
        }

        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }

        public String getHttpUrl() {
            return httpUrl;
        }

        public void setHttpUrl(String httpUrl) {
            this.httpUrl = httpUrl;
        }
    }

    public static class Url {
    }

    public static class Cache {

        private String cachePrefix;

        public String getCachePrefix() {
            return cachePrefix;
        }

        public void setCachePrefix(String cachePrefix) {
            this.cachePrefix = cachePrefix;
        }
    }
}
