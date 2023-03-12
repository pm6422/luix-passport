package com.luixtech.passport;

import com.luixtech.framework.EnableLuixFramework;
import com.luixtech.passport.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@ServletComponentScan
// https://github.com/spring-projects/spring-retry
@EnableRetry
@EnableLuixFramework
public class PassportServerApplication {
    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PassportServerApplication.class, args);
    }
}
