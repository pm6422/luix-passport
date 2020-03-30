package demo;

import demo.utils.NetworkIpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class PassportClientLauncher extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PassportClientLauncher.class);

    /**
     * Entrance method which used to run the application. Spring profiles can be configured with a program arguments
     * --spring.profiles.active=your-active-profile
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(PassportClientLauncher.class);
        Environment env = app.run(args).getEnvironment();
        printAppInfo(env);
    }

    private static void printAppInfo(Environment env) throws IOException {
        String appBanner = StreamUtils.copyToString(new ClassPathResource("config/banner-app.txt").getInputStream(),
                Charset.defaultCharset());
        LOGGER.info(appBanner, env.getProperty("spring.application.name"),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                NetworkIpUtils.INTRANET_IP,
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                StringUtils.isEmpty(env.getProperty("server.ssl.key-store")) ? "http" : "https",
                NetworkIpUtils.INTERNET_IP,
                env.getProperty("server.port"),
                StringUtils.defaultString(env.getProperty("server.servlet.context-path")),
                org.springframework.util.StringUtils.arrayToCommaDelimitedString(env.getActiveProfiles()),
                env.getProperty("PID"),
                Charset.defaultCharset(),
                env.getProperty("LOG_PATH") + "-" + DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()) + ".log");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
            .ignoringAntMatchers("/logout"); // Csrf should ignore logout url
//      .and()
//      .authorizeRequests()
//      .antMatchers("/index.html", "/home.html", "/", "/login").permitAll()
//      .anyRequest().authenticated() // IEngine need uncomment this statement
        // @formatter:on
    }
}
